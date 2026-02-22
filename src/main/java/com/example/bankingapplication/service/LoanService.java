package com.example.bankingapplication.service;

import com.example.bankingapplication.entity.Loan;
import com.example.bankingapplication.repoImplementations.LoanRepositoryImpl;
import com.example.bankingapplication.repository.LoanRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

public class LoanService {
    private final LoanRepository loanRepository;

    public LoanService() {
        this.loanRepository = new LoanRepositoryImpl();
    }

    public Loan applyForLoan(Loan loan) {
        if (loan.getLoanAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Loan amount must be positive");
        }

        // Calculate EMI
        BigDecimal monthlyEmi = calculateEMI(
                loan.getLoanAmount(),
                loan.getInterestRate(),
                loan.getTenureMonths()
        );
        loan.setMonthlyEmi(monthlyEmi);
        loan.setOutstandingAmount(loan.getLoanAmount());
        loan.setStatus("PENDING");

        return loanRepository.save(loan);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public List<Loan> getLoansByAccount(String accountNumber) {
        return loanRepository.findByAccountNumber(accountNumber);
    }

    public void approveLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId);
        if (loan != null) {
            loan.setStatus("APPROVED");
            loan.setDisbursementDate(LocalDate.now());
            loanRepository.update(loan);
        }
    }

    public void payEMI(Long loanId, BigDecimal amount) {
        Loan loan = loanRepository.findById(loanId);
        if (loan != null) {
            BigDecimal newOutstanding = loan.getOutstandingAmount().subtract(amount);
            loan.setOutstandingAmount(newOutstanding);

            if (newOutstanding.compareTo(BigDecimal.ZERO) <= 0) {
                loan.setStatus("CLOSED");
            }

            loanRepository.update(loan);
        }
    }

    private BigDecimal calculateEMI(BigDecimal principal, BigDecimal annualRate, int months) {
        double p = principal.doubleValue();
        double r = annualRate.doubleValue() / 1200; // Monthly rate
        int n = months;

        double emi = (p * r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1);
        return BigDecimal.valueOf(emi).setScale(2, RoundingMode.HALF_UP);
    }
}

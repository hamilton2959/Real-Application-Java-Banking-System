package com.example.bankingapplication.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Loan {
    private Long id;
    private String accountNumber;
    private String loanType; // PERSONAL, HOME, CAR, EDUCATION
    private BigDecimal loanAmount;
    private BigDecimal interestRate;
    private int tenureMonths;
    private BigDecimal monthlyEmi;
    private BigDecimal outstandingAmount;
    private LocalDate disbursementDate;
    private String status; // PENDING, APPROVED, ACTIVE, CLOSED

    public Loan() {}

    public Loan(Long id, String accountNumber, String loanType, BigDecimal loanAmount,
                BigDecimal interestRate, int tenureMonths, BigDecimal monthlyEmi,
                BigDecimal outstandingAmount, LocalDate disbursementDate, String status) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.loanType = loanType;
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.tenureMonths = tenureMonths;
        this.monthlyEmi = monthlyEmi;
        this.outstandingAmount = outstandingAmount;
        this.disbursementDate = disbursementDate;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }

    public BigDecimal getLoanAmount() { return loanAmount; }
    public void setLoanAmount(BigDecimal loanAmount) { this.loanAmount = loanAmount; }

    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }

    public int getTenureMonths() { return tenureMonths; }
    public void setTenureMonths(int tenureMonths) { this.tenureMonths = tenureMonths; }

    public BigDecimal getMonthlyEmi() { return monthlyEmi; }
    public void setMonthlyEmi(BigDecimal monthlyEmi) { this.monthlyEmi = monthlyEmi; }

    public BigDecimal getOutstandingAmount() { return outstandingAmount; }
    public void setOutstandingAmount(BigDecimal outstandingAmount) { this.outstandingAmount = outstandingAmount; }

    public LocalDate getDisbursementDate() { return disbursementDate; }
    public void setDisbursementDate(LocalDate disbursementDate) { this.disbursementDate = disbursementDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

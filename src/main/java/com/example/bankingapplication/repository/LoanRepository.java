package com.example.bankingapplication.repository;

import com.example.bankingapplication.entity.Loan;

import java.util.List;

public interface LoanRepository {
    Loan save(Loan loan);
    Loan findById(Long id);
    List<Loan> findAll();
    List<Loan> findByAccountNumber(String accountNumber);
    void update(Loan loan);
    void deleteById(Long id);
}

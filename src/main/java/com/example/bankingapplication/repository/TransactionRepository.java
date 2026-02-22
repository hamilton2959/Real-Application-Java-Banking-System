package com.example.bankingapplication.repository;

import com.example.bankingapplication.entity.Transaction;

import java.util.List;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    Transaction findById(Long id);
    List<Transaction> findAll();
    List<Transaction> findByAccountNumber(String accountNumber);
    void deleteById(Long id);
}

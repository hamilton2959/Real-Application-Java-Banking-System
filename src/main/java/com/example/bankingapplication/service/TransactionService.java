package com.example.bankingapplication.service;

import com.example.bankingapplication.entity.Account;
import com.example.bankingapplication.entity.Transaction;
import com.example.bankingapplication.repoImplementations.AccountRepositoryImpl;
import com.example.bankingapplication.repoImplementations.TransactionRepositoryImpl;
import com.example.bankingapplication.repository.AccountRepository;
import com.example.bankingapplication.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService() {
        this.transactionRepository = new TransactionRepositoryImpl();
        this.accountRepository = new AccountRepositoryImpl();
    }

    public Transaction deposit(String accountNumber, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }

        // Update balance
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        accountRepository.update(account);

        // Create transaction record
        Transaction transaction = new Transaction(
                null,
                accountNumber,
                "DEPOSIT",
                amount,
                LocalDateTime.now(),
                description,
                generateReferenceNumber(),
                newBalance
        );

        return transactionRepository.save(transaction);
    }

    public Transaction withdraw(String accountNumber, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        // Update balance
        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        accountRepository.update(account);

        // Create transaction record
        Transaction transaction = new Transaction(
                null,
                accountNumber,
                "WITHDRAWAL",
                amount,
                LocalDateTime.now(),
                description,
                generateReferenceNumber(),
                newBalance
        );

        return transactionRepository.save(transaction);
    }

    public void transfer(String fromAccount, String toAccount, BigDecimal amount, String description) {
        withdraw(fromAccount, amount, "Transfer to " + toAccount + " - " + description);
        deposit(toAccount, amount, "Transfer from " + fromAccount + " - " + description);
    }

    public List<Transaction> getTransactionHistory(String accountNumber) {
        return transactionRepository.findByAccountNumber(accountNumber);
    }

    private String generateReferenceNumber() {
        return "TXN" + System.currentTimeMillis();
    }
}

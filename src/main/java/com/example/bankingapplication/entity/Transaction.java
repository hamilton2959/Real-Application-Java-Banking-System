package com.example.bankingapplication.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private Long id;
    private String accountNumber;
    private String transactionType; // DEPOSIT, WITHDRAWAL, TRANSFER
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private String description;
    private String referenceNumber;
    private BigDecimal balanceAfter;

    public Transaction() {}

    public Transaction(Long id, String accountNumber, String transactionType,
                       BigDecimal amount, LocalDateTime transactionDate,
                       String description, String referenceNumber, BigDecimal balanceAfter) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.description = description;
        this.referenceNumber = referenceNumber;
        this.balanceAfter = balanceAfter;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }
}

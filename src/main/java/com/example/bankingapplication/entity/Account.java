package com.example.bankingapplication.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Account {
    private Long id;
    private String accountNumber;
    private String accountType; // SAVINGS, CURRENT, FIXED_DEPOSIT
    private BigDecimal balance;
    private LocalDate openingDate;
    private String status; // ACTIVE, INACTIVE, FROZEN
    private BigDecimal interestRate;

    public Account() {}

    public Account(Long id, String accountNumber, String accountType,
                   BigDecimal balance, LocalDate openingDate, String status,
                   BigDecimal interestRate) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.openingDate = openingDate;
        this.status = status;
        this.interestRate = interestRate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public LocalDate getOpeningDate() { return openingDate; }
    public void setOpeningDate(LocalDate openingDate) { this.openingDate = openingDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }
}

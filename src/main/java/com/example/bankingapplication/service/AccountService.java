package com.example.bankingapplication.service;

import com.example.bankingapplication.entity.Account;
import com.example.bankingapplication.repoImplementations.AccountRepositoryImpl;
import com.example.bankingapplication.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.List;

public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService() {
        this.accountRepository = new AccountRepositoryImpl();
    }

    public Account createAccount(Account account) {
        if (account.getAccountNumber() == null || account.getAccountNumber().isEmpty()) {
            throw new IllegalArgumentException("Account number is required");
        }
        if (account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }

        return accountRepository.save(account);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    public void updateAccount(Account account) {
        accountRepository.update(account);
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }

    public BigDecimal getBalance(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        return account != null ? account.getBalance() : BigDecimal.ZERO;
    }
}

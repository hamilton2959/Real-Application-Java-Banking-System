package com.example.bankingapplication.repository;

import com.example.bankingapplication.entity.Account;

import java.util.List;

public interface AccountRepository {
    Account save(Account account);
    Account findById(Long id);
    Account findByAccountNumber(String accountNumber);
    List<Account> findAll();
    List<Account> findByCustomerAccountNumber(String accountNumber);
    void update(Account account);
    void deleteById(Long id);
}

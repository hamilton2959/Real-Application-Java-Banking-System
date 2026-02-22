package com.example.bankingapplication.repository;

import com.example.bankingapplication.entity.Customer;

import java.util.List;

public interface CustomerRepository {
    Customer save(Customer customer);
    Customer findById(Long id);
    Customer findByAccountNumber(String accountNumber);
    List<Customer> findAll();
    void deleteById(Long id);
    void update(Customer customer);
}

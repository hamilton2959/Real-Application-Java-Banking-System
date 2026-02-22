package com.example.bankingapplication.service;

import com.example.bankingapplication.entity.Customer;
import com.example.bankingapplication.repoImplementations.CustomerRepositoryImpl;
import com.example.bankingapplication.repository.CustomerRepository;

import java.util.List;

public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService() {
        this.customerRepository = new CustomerRepositoryImpl();
    }

    public Customer createCustomer(Customer customer) {
        // Validation
        if (customer.getAccountNumber() == null || customer.getAccountNumber().isEmpty()) {
            throw new IllegalArgumentException("Account number is required");
        }
        if (customer.getFirstName() == null || customer.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (customer.getLastName() == null || customer.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }

        // Check if account number already exists
        Customer existing = customerRepository.findByAccountNumber(customer.getAccountNumber());
        if (existing != null) {
            throw new IllegalArgumentException("Account number already exists");
        }

        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerByAccountNumber(String accountNumber) {
        return customerRepository.findByAccountNumber(accountNumber);
    }

    public void updateCustomer(Customer customer) {
        if (customer.getId() == null) {
            throw new IllegalArgumentException("Customer ID is required for update");
        }
        customerRepository.update(customer);
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
}

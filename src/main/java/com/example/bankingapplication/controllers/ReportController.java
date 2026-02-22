package com.example.bankingapplication.controllers;

import com.example.bankingapplication.entity.Account;
import com.example.bankingapplication.entity.Customer;
import com.example.bankingapplication.entity.Loan;
import com.example.bankingapplication.entity.Transaction;
import com.example.bankingapplication.service.AccountService;
import com.example.bankingapplication.service.CustomerService;
import com.example.bankingapplication.service.LoanService;
import com.example.bankingapplication.service.TransactionService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.util.List;

public class ReportController {
    @FXML
    private ComboBox<String> reportTypeCombo;
    @FXML private TextField accountNumberField;
    @FXML private TextArea reportArea;
    @FXML private Label messageLabel;

    private final CustomerService customerService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final LoanService loanService;

    public ReportController() {
        this.customerService = new CustomerService();
        this.accountService = new AccountService();
        this.transactionService = new TransactionService();
        this.loanService = new LoanService();
    }

    @FXML
    public void initialize() {
        reportTypeCombo.setItems(FXCollections.observableArrayList(
                "Customer Statement",
                "Transaction History",
                "Loan Summary",
                "Account Summary"
        ));
    }

    @FXML
    private void handleGenerateReport() {
        String reportType = reportTypeCombo.getValue();
        String accountNumber = accountNumberField.getText();

        if (reportType == null) {
            showMessage("Please select a report type", "orange");
            return;
        }

        try {
            StringBuilder report = new StringBuilder();
            report.append("=== ").append(reportType.toUpperCase()).append(" ===\n");
            report.append("Generated: ").append(java.time.LocalDateTime.now()).append("\n");
            report.append("Account Number: ").append(accountNumber).append("\n\n");

            switch (reportType) {
                case "Customer Statement":
                    generateCustomerStatement(report, accountNumber);
                    break;
                case "Transaction History":
                    generateTransactionHistory(report, accountNumber);
                    break;
                case "Loan Summary":
                    generateLoanSummary(report, accountNumber);
                    break;
                case "Account Summary":
                    generateAccountSummary(report, accountNumber);
                    break;
            }

            reportArea.setText(report.toString());
            showMessage("Report generated successfully!", "green");

        } catch (Exception e) {
            showMessage("Error: " + e.getMessage(), "red");
        }
    }

    private void generateCustomerStatement(StringBuilder report, String accountNumber) {
        Customer customer = customerService.getCustomerByAccountNumber(accountNumber);
        if (customer != null) {
            report.append("Customer Details:\n");
            report.append("Name: ").append(customer.getFirstName()).append(" ")
                    .append(customer.getLastName()).append("\n");
            report.append("Email: ").append(customer.getEmail()).append("\n");
            report.append("Phone: ").append(customer.getPhoneNumber()).append("\n");
            report.append("Address: ").append(customer.getAddress()).append("\n");
        }
    }

    private void generateTransactionHistory(StringBuilder report, String accountNumber) {
        List<Transaction> transactions = transactionService.getTransactionHistory(accountNumber);
        report.append("Transaction History:\n");
        report.append("-".repeat(80)).append("\n");

        for (Transaction txn : transactions) {
            report.append(txn.getTransactionDate()).append(" | ");
            report.append(txn.getTransactionType()).append(" | ");
            report.append("$").append(txn.getAmount()).append(" | ");
            report.append(txn.getDescription()).append("\n");
        }

        report.append("-".repeat(80)).append("\n");
        report.append("Total Transactions: ").append(transactions.size()).append("\n");
    }

    private void generateLoanSummary(StringBuilder report, String accountNumber) {
        List<Loan> loans = loanService.getLoansByAccount(accountNumber);
        report.append("Loan Summary:\n");
        report.append("-".repeat(80)).append("\n");

        for (Loan loan : loans) {
            report.append("Type: ").append(loan.getLoanType()).append("\n");
            report.append("Amount: $").append(loan.getLoanAmount()).append("\n");
            report.append("EMI: $").append(loan.getMonthlyEmi()).append("\n");
            report.append("Outstanding: $").append(loan.getOutstandingAmount()).append("\n");
            report.append("Status: ").append(loan.getStatus()).append("\n");
            report.append("-".repeat(80)).append("\n");
        }
    }

    private void generateAccountSummary(StringBuilder report, String accountNumber) {
        Account account = accountService.getAccountByNumber(accountNumber);
        if (account != null) {
            report.append("Account Information:\n");
            report.append("Type: ").append(account.getAccountType()).append("\n");
            report.append("Balance: $").append(account.getBalance()).append("\n");
            report.append("Status: ").append(account.getStatus()).append("\n");
            report.append("Interest Rate: ").append(account.getInterestRate()).append("%\n");
            report.append("Opening Date: ").append(account.getOpeningDate()).append("\n");
        }
    }

    @FXML
    private void handleExportReport() {
        // Future implementation: Export to PDF/Excel
        showMessage("Export feature coming soon!", "blue");
    }

    private void showMessage(String message, String color) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
    }
}


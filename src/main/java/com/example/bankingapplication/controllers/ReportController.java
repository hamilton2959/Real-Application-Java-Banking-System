package com.example.bankingapplication.controllers;

import com.example.bankingapplication.entity.Account;
import com.example.bankingapplication.entity.Customer;
import com.example.bankingapplication.entity.Loan;
import com.example.bankingapplication.entity.Transaction;
import com.example.bankingapplication.service.AccountService;
import com.example.bankingapplication.service.CustomerService;
import com.example.bankingapplication.service.LoanService;
import com.example.bankingapplication.service.TransactionService;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportController {
    private String currentReportContent;
    private String currentReportTitle;

    @FXML private ComboBox<String> reportTypeCombo;
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

            // Footer
            report.append("\n").append("═".repeat(80)).append("\n");
            report.append("End of Report - Banking System | Sky Tech Bank\n");
            report.append("═".repeat(80)).append("\n");

            currentReportContent = report.toString();
            reportArea.setText(currentReportContent);
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
        //showMessage("Export feature coming soon!", "blue");
        if (currentReportContent == null || currentReportContent.isEmpty()) {
            showMessage("Please generate a report first!", "orange");
            return;
        }

        try {
            // Create file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Report");

            // Set initial file name
            String timestamp = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "Report_" + accountNumberField.getText() + "_" + timestamp;
            fileChooser.setInitialFileName(fileName);

            // Add extension filters
            fileChooser.getExtensionFilters().addAll(
                    //new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                    //new FileChooser.ExtensionFilter("CSV Files", "*.csv")
                    new FileChooser.ExtensionFilter("HTML Files", "*.html")
            );

            // Show save dialog
            Stage stage = (Stage) reportArea.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                String extension = getFileExtension(file.getName());

                switch (extension.toLowerCase()) {
                    case "txt":
                        exportAsText(file);
                        break;
                    case "csv":
                        exportAsCSV(file);
                        break;
                    case "html":
                        exportAsHTML(file);
                        break;
                    default:
                        exportAsText(file);
                }

                showMessage("Report exported successfully to: " + file.getName(), "green");
            }

        } catch (Exception e) {
            showMessage("Error exporting report: " + e.getMessage(), "red");
            e.printStackTrace();
        }

    }

    private String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "txt"; // default
        }
        return fileName.substring(lastIndexOf + 1);
    }

    private void exportAsHTML(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("<!DOCTYPE html>\n");
            writer.write("<html>\n<head>\n");
            writer.write("<meta charset=\"UTF-8\">\n");
            writer.write("<title>" + currentReportTitle + "</title>\n");
            writer.write("<style>\n");
            writer.write("body { font-family: 'Courier New', monospace; margin: 40px; }\n");
            writer.write("pre { background-color: #f5f5f5; padding: 20px; border: 1px solid #ddd; }\n");
            writer.write("h1 { color: #2c3e50; }\n");
            writer.write(".footer { margin-top: 30px; font-size: 12px; color: #7f8c8d; }\n");
            writer.write("</style>\n");
            writer.write("</head>\n<body>\n");
            writer.write("<h1>" + currentReportTitle + "</h1>\n");
            writer.write("<pre>\n");
            writer.write(currentReportContent.replace("<", "&lt;").replace(">", "&gt;"));
            writer.write("</pre>\n");
            writer.write("<div class=\"footer\">\n");
            writer.write("<p>Generated by Banking System - Sky Tech Bank</p>\n");
            writer.write("<p>Export Date: " + LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "</p>\n");
            writer.write("</div>\n");
            writer.write("</body>\n</html>");
        }
    }

    private void exportAsCSV(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Convert report to CSV format (simplified)
            String[] lines = currentReportContent.split("\n");
            for (String line : lines) {
                // Replace multiple spaces with commas for CSV
                String csvLine = line.replaceAll("\\s{2,}", ",");
                writer.write(csvLine);
                writer.newLine();
            }
        }
    }

    private void exportAsText(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(currentReportContent);
        }
    }

    private void showMessage(String message, String color) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
    }
}


package com.example.bankingapplication.controllers;

import com.example.bankingapplication.entity.Loan;
import com.example.bankingapplication.service.LoanService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ResourceBundle;

public class LoanConroller implements Initializable {
    @FXML private TextField accountNumberField;
    @FXML private ComboBox<String> loanTypeCombo;
    @FXML private TextField loanAmountField;
    @FXML private TextField interestRateField;
    @FXML private TextField tenureField;
    @FXML private Label emiLabel;
    @FXML private TableView<Loan> loanTable;
    @FXML
    private TableColumn<Loan, String> colAccountNumber;
    @FXML private TableColumn<Loan, String> colLoanType;
    @FXML private TableColumn<Loan, String> colLoanAmount;
    @FXML private TableColumn<Loan, String> colInterestRate;
    @FXML private TableColumn<Loan, Integer> colTenure;
    @FXML private TableColumn<Loan, String> colEMI;
    @FXML private TableColumn<Loan, String> colOutstanding;
    @FXML private TableColumn<Loan, String> colStatus;
    @FXML private Label messageLabel;

    private final LoanService loanService;
    private Loan selectedLoan;

    public LoanConroller(){
        this.loanService = new LoanService();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colAccountNumber.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getAccountNumber()));

        colLoanType.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getLoanType()));

        colLoanAmount.setCellValueFactory(data ->
                new SimpleStringProperty("$" + data.getValue().getLoanAmount().toString()));

        colInterestRate.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getInterestRate().toString() + "%"));

        colTenure.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getTenureMonths()).asObject());

        colEMI.setCellValueFactory(data ->
                new SimpleStringProperty("$" + data.getValue().getMonthlyEmi().toString()));

        colOutstanding.setCellValueFactory(data ->
                new SimpleStringProperty("$" + data.getValue().getOutstandingAmount().toString()));

        colStatus.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatus()));

        // Load initial data
        loadLoans();

        // Add selection listener
        loanTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectedLoan = newSelection;
                        populateFields(newSelection);
                    }
                });

    }
    @FXML
    private void handleCalculateEMI() {
        try {
            // Validate inputs
            if (loanAmountField.getText().isEmpty() ||
                    interestRateField.getText().isEmpty() ||
                    tenureField.getText().isEmpty()) {
                showMessage("Please fill all fields to calculate EMI", "orange");
                return;
            }

            BigDecimal amount = new BigDecimal(loanAmountField.getText());
            BigDecimal rate = new BigDecimal(interestRateField.getText());
            int tenure = Integer.parseInt(tenureField.getText());

            // EMI Calculation Formula
            // EMI = [P × r × (1+r)^n] / [(1+r)^n - 1]
            // where P = principal, r = monthly interest rate, n = number of months

            double p = amount.doubleValue();
            double r = rate.doubleValue() / 1200; // Convert annual rate to monthly decimal
            int n = tenure;

            if (r == 0) {
                // If interest rate is 0, EMI is simply principal/tenure
                BigDecimal emiAmount = amount.divide(new BigDecimal(n), 2, RoundingMode.HALF_UP);
                emiLabel.setText("$" + emiAmount.toString());
                emiLabel.setStyle("-fx-text-fill: green; -fx-font-size: 16px; -fx-font-weight: bold;");
            } else {
                double emi = (p * r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1);
                BigDecimal emiAmount = BigDecimal.valueOf(emi).setScale(2, RoundingMode.HALF_UP);

                emiLabel.setText("$" + emiAmount.toString());
                emiLabel.setStyle("-fx-text-fill: green; -fx-font-size: 16px; -fx-font-weight: bold;");
            }

            showMessage("EMI calculated successfully!", "green");

        } catch (NumberFormatException e) {
            showMessage("Error: Please enter valid numbers", "red");
        } catch (Exception e) {
            showMessage("Error calculating EMI: " + e.getMessage(), "red");
        }
    }

    @FXML
    private void handleApplyLoan() {
        try {
            // Validation
            if (accountNumberField.getText().isEmpty()) {
                showMessage("Account number is required", "orange");
                return;
            }

            if (loanTypeCombo.getValue() == null) {
                showMessage("Please select a loan type", "orange");
                return;
            }

            if (loanAmountField.getText().isEmpty() ||
                    interestRateField.getText().isEmpty() ||
                    tenureField.getText().isEmpty()) {
                showMessage("Please fill all loan details", "orange");
                return;
            }

            // Create loan object
            Loan loan = new Loan(
                    null,
                    accountNumberField.getText(),
                    loanTypeCombo.getValue(),
                    new BigDecimal(loanAmountField.getText()),
                    new BigDecimal(interestRateField.getText()),
                    Integer.parseInt(tenureField.getText()),
                    null, // Will be calculated by service
                    null, // Will be set to loan amount by service
                    null, // No disbursement date yet
                    "PENDING"
            );

            // Apply for loan
            Loan savedLoan = loanService.applyForLoan(loan);

            if (savedLoan != null) {
                showMessage("Loan application submitted successfully! (ID: " + savedLoan.getId() + ")", "green");
                clearFields();
                loadLoans();
            } else {
                showMessage("Failed to submit loan application", "red");
            }

        } catch (NumberFormatException e) {
            showMessage("Error: Please enter valid numbers", "red");
        } catch (IllegalArgumentException e) {
            showMessage("Error: " + e.getMessage(), "red");
        } catch (Exception e) {
            showMessage("Error: " + e.getMessage(), "red");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleApproveLoan() {
        if (selectedLoan == null) {
            showMessage("Please select a loan to approve", "orange");
            return;
        }

        if (!"PENDING".equals(selectedLoan.getStatus())) {
            showMessage("Only pending loans can be approved", "orange");
            return;
        }

        try {
            // Confirmation dialog
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Loan Approval");
            confirmAlert.setHeaderText("Approve Loan Application");
            confirmAlert.setContentText("Are you sure you want to approve this loan?\n\n" +
                    "Account: " + selectedLoan.getAccountNumber() + "\n" +
                    "Type: " + selectedLoan.getLoanType() + "\n" +
                    "Amount: $" + selectedLoan.getLoanAmount());

            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    loanService.approveLoan(selectedLoan.getId());
                    showMessage("Loan approved successfully!", "green");
                    clearFields();
                    loadLoans();
                }
            });

        } catch (Exception e) {
            showMessage("Error: " + e.getMessage(), "red");
        }
    }

    @FXML
    private void handlePayEMI() {
        if (selectedLoan == null) {
            showMessage("Please select a loan to pay EMI", "orange");
            return;
        }

        if (!"APPROVED".equals(selectedLoan.getStatus()) &&
                !"ACTIVE".equals(selectedLoan.getStatus())) {
            showMessage("EMI can only be paid for approved/active loans", "orange");
            return;
        }

        try {
            // Create payment dialog
            TextInputDialog dialog = new TextInputDialog(selectedLoan.getMonthlyEmi().toString());
            dialog.setTitle("Pay EMI");
            dialog.setHeaderText("Pay Loan EMI");
            dialog.setContentText("Enter payment amount:\n" +
                    "Monthly EMI: $" + selectedLoan.getMonthlyEmi() + "\n" +
                    "Outstanding: $" + selectedLoan.getOutstandingAmount());

            dialog.showAndWait().ifPresent(amountStr -> {
                try {
                    BigDecimal amount = new BigDecimal(amountStr);

                    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        showMessage("Payment amount must be positive", "red");
                        return;
                    }

                    if (amount.compareTo(selectedLoan.getOutstandingAmount()) > 0) {
                        showMessage("Payment amount cannot exceed outstanding balance", "red");
                        return;
                    }

                    loanService.payEMI(selectedLoan.getId(), amount);
                    showMessage("EMI payment of $" + amount + " successful!", "green");
                    clearFields();
                    loadLoans();

                } catch (NumberFormatException e) {
                    showMessage("Error: Please enter a valid amount", "red");
                } catch (Exception e) {
                    showMessage("Error: " + e.getMessage(), "red");
                }
            });

        } catch (Exception e) {
            showMessage("Error: " + e.getMessage(), "red");
        }
    }

    @FXML
    private void handleClear() {
        clearFields();
    }

    @FXML
    private void handleRefresh() {
        loadLoans();
        showMessage("Loan list refreshed", "blue");
    }

    private void loadLoans() {
        try {
            ObservableList<Loan> loans = FXCollections.observableArrayList(
                    loanService.getAllLoans()
            );
            loanTable.setItems(loans);
        } catch (Exception e) {
            showMessage("Error loading loans: " + e.getMessage(), "red");
        }
    }

    private void populateFields(Loan loan) {
        accountNumberField.setText(loan.getAccountNumber());
        accountNumberField.setDisable(true);
        loanTypeCombo.setValue(loan.getLoanType());
        loanAmountField.setText(loan.getLoanAmount().toString());
        interestRateField.setText(loan.getInterestRate().toString());
        tenureField.setText(String.valueOf(loan.getTenureMonths()));

        if (loan.getMonthlyEmi() != null) {
            emiLabel.setText("$" + loan.getMonthlyEmi().toString());
            emiLabel.setStyle("-fx-text-fill: green; -fx-font-size: 16px; -fx-font-weight: bold;");
        }
    }

    private void clearFields() {
        selectedLoan = null;
        accountNumberField.clear();
        accountNumberField.setDisable(false);
        loanAmountField.clear();
        interestRateField.clear();
        tenureField.clear();
        loanTypeCombo.setValue(null);
        emiLabel.setText("");
        messageLabel.setText("");
    }

    private void showMessage(String message, String color) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 14px; -fx-font-weight: bold;");

        // Auto-clear message after 5 seconds
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                javafx.application.Platform.runLater(() -> messageLabel.setText(""));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}

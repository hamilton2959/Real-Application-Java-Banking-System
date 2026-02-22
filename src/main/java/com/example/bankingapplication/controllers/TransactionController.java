package com.example.bankingapplication.controllers;

import com.example.bankingapplication.entity.Transaction;
import com.example.bankingapplication.service.AccountService;
import com.example.bankingapplication.service.TransactionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class TransactionController implements Initializable {
    @FXML private TableColumn colTransactionType;
    @FXML private TableColumn colAmount;
    @FXML private TableColumn colDate;
    @FXML private TableColumn colDescription;
    @FXML private TableColumn colReference;
    @FXML private TableColumn colBalanceAfter;
    @FXML private TextField accountNumberField;
    @FXML private TextField amountField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> transactionTypeCombo;
    @FXML private TextField toAccountField; // For transfers
    @FXML private TableView<Transaction> transactionTable;
    @FXML private Label balanceLabel;
    @FXML private Label messageLabel;

    private final TransactionService transactionService;
    private final AccountService accountService;

    public TransactionController() {
        this.transactionService = new TransactionService();
        this.accountService = new AccountService();
    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        transactionTypeCombo.setItems(FXCollections.observableArrayList(
                "DEPOSIT", "WITHDRAWAL", "TRANSFER"
        ));

        transactionTypeCombo.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> {
                    toAccountField.setVisible("TRANSFER".equals(newVal));
                });
    }

    public void handleTransaction() {
        try {
            String accountNumber = accountNumberField.getText();
            BigDecimal amount = new BigDecimal(amountField.getText());
            String description = descriptionArea.getText();
            String type = transactionTypeCombo.getValue();

            switch (type) {
                case "DEPOSIT":
                    transactionService.deposit(accountNumber, amount, description);
                    break;
                case "WITHDRAWAL":
                    transactionService.withdraw(accountNumber, amount, description);
                    break;
                case "TRANSFER":
                    String toAccount = toAccountField.getText();
                    transactionService.transfer(accountNumber, toAccount, amount, description);
                    break;
            }

            showMessage("Transaction completed successfully!", "green");
            updateBalance();
            loadTransactions();
            clearFields();

        } catch (Exception e) {
            showMessage("Error: " + e.getMessage(), "red");
        }
    }

    @FXML
    private void handleCheckBalance() {
        try {
            String accountNumber = accountNumberField.getText();
            BigDecimal balance = accountService.getBalance(accountNumber);
            balanceLabel.setText("Current Balance: $" + balance);
        } catch (Exception e) {
            showMessage("Error: " + e.getMessage(), "red");
        }
    }

    public void loadTransactions() {
        String accountNumber = accountNumberField.getText();
        if (accountNumber != null && !accountNumber.isEmpty()) {
            ObservableList<Transaction> transactions = FXCollections.observableArrayList(
                    transactionService.getTransactionHistory(accountNumber)
            );
            transactionTable.setItems(transactions);
        }
    }

    private void updateBalance() {
        handleCheckBalance();
    }

    private void clearFields() {
        amountField.clear();
        descriptionArea.clear();
        toAccountField.clear();
    }

    private void showMessage(String message, String color) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
    }
}

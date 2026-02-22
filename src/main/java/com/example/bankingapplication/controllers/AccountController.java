package com.example.bankingapplication.controllers;

import com.example.bankingapplication.config.DatabaseConfig;
import com.example.bankingapplication.entity.Account;
import com.example.bankingapplication.service.AccountService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;

public class AccountController {
    @FXML private TextField accountNumberField;
    @FXML private ComboBox<String> accountTypeCombo;
    @FXML private TextField balanceField;
    @FXML private TextField interestRateField;
    @FXML private DatePicker openingDatePicker;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TableView<Account> accountTable;
    @FXML private TableColumn<Account, Integer> idColumn;
    @FXML private TableColumn<Account, String> colAccountNumber;
    @FXML private TableColumn<Account, String> colAccountType;
    @FXML private TableColumn<Account, String> colBalance;
    @FXML private TableColumn<Account, String> colStatus;
    @FXML private TableColumn<Account, String> colInterestRate;
    @FXML private TableColumn<Account, String> colOpeningDate;
    @FXML private Label messageLabel;

    private final AccountService accountService;
    private Account selectedAccount;

    public AccountController() {
        this.accountService = new AccountService();
    }

    @FXML
    public void initialize() {
        // Setup combo boxes
        accountTypeCombo.setItems(FXCollections.observableArrayList(
                "SAVINGS", "CURRENT", "FIXED_DEPOSIT"
        ));
        statusCombo.setItems(FXCollections.observableArrayList(
                "ACTIVE", "INACTIVE", "FROZEN"
        ));

         //Setup table columns
        //idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAccountNumber.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getAccountNumber()));
        colAccountType.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getAccountType()));
        colBalance.setCellValueFactory(data ->
                new SimpleStringProperty("$" + data.getValue().getBalance().toString()));
        colStatus.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatus()));
        colInterestRate.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getInterestRate() + "%"));
        colOpeningDate.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getOpeningDate().toString()));

        loadAccounts();

        accountTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        populateFields(newSelection);
                    }
                });
    }

    public void handleCreateAccount() {
        try {
//        try (Connection conn = DatabaseConfig.getConnection()) {
//
//            String sql = "INSERT INTO accounts (account_number, account_type, balance, opening_date, status, interest_rate) VALUES (?, ?, ?, ?, ?, ?)";
//            PreparedStatement stmt = conn.prepareStatement(sql);
//
//            stmt.setString(1, accountNumberField.getText());
//            stmt.setString(2, accountTypeCombo.getValue());
//            stmt.setString(3, balanceField.getText());
//            stmt.setDate(4, Date.valueOf(openingDatePicker.getValue()));
//            stmt.setString(5, statusCombo.getValue());
//            stmt.setString(6, interestRateField.getText());
//
//            stmt.executeUpdate();

            Account account = new Account(
                    null,
                    accountNumberField.getText(),
                    accountTypeCombo.getValue(),
                    new BigDecimal(balanceField.getText()),
                    LocalDate.now(),
                    statusCombo.getValue(),
                    new BigDecimal(interestRateField.getText())
            );

            accountService.createAccount(account);
            showMessage("Account created successfully!", "green");
            clearFields();
            loadAccounts();

        } catch (Exception e) {
            showMessage("Error: " + e.getMessage(), "red");
        }
    }

    public void handleUpdateAccount() {
        if (selectedAccount == null) {
            showMessage("Please select an account to update", "orange");
            return;
        }

        try {
            selectedAccount.setAccountType(accountTypeCombo.getValue());
            selectedAccount.setBalance(new BigDecimal(balanceField.getText()));
            selectedAccount.setStatus(statusCombo.getValue());
            selectedAccount.setInterestRate(new BigDecimal(interestRateField.getText()));

            accountService.updateAccount(selectedAccount);
            showMessage("Account updated successfully!", "green");
            clearFields();
            loadAccounts();

        } catch (Exception e) {
            showMessage("Error: " + e.getMessage(), "red");
        }
    }

    public void handleDeleteAccount() {
        if (selectedAccount == null) {
            showMessage("Please select an account to delete", "orange");
            return;
        }

        try {
            accountService.deleteAccount(selectedAccount.getId());
            showMessage("Account deleted successfully!", "green");
            clearFields();
            loadAccounts();

        } catch (Exception e) {
            showMessage("Error: " + e.getMessage(), "red");
        }
    }

    public void handleClear() {
        clearFields();
    }

    private void loadAccounts() {
        ObservableList<Account> accounts = FXCollections.observableArrayList(
                accountService.getAllAccounts()
        );
        accountTable.setItems(accounts);
    }

    private void populateFields(Account account) {
        selectedAccount = account;
        accountNumberField.setText(account.getAccountNumber());
        accountNumberField.setDisable(true);
        accountTypeCombo.setValue(account.getAccountType());
        balanceField.setText(account.getBalance().toString());
        interestRateField.setText(account.getInterestRate().toString());
        statusCombo.setValue(account.getStatus());
    }

    private void clearFields() {
        selectedAccount = null;
        accountNumberField.clear();
        accountNumberField.setDisable(false);
        balanceField.clear();
        interestRateField.clear();
        accountTypeCombo.setValue(null);
        statusCombo.setValue(null);
        messageLabel.setText("");
    }

    private void showMessage(String message, String color) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
    }
}

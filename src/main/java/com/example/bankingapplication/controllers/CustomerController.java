package com.example.bankingapplication.controllers;

import com.example.bankingapplication.entity.Customer;
import com.example.bankingapplication.service.CustomerService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class CustomerController {
    @FXML private TextField accountNumberField;
    @FXML private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private DatePicker dobPicker;
    @FXML private TextArea addressArea;
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> colAccountNumber;
    @FXML private TableColumn<Customer, String> colFirstName;
    @FXML private TableColumn<Customer, String> colLastName;
    @FXML private TableColumn<Customer, String> colEmail;
    @FXML private TableColumn<Customer, String> colPhone;
    @FXML private Label messageLabel;

    private final CustomerService customerService;
    private Customer selectedCustomer;

    public CustomerController() {
        this.customerService = new CustomerService();
    }

    @FXML
    public void initialize() {
        // Setup table columns
        colAccountNumber.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getAccountNumber()));
        colFirstName.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFirstName()));
        colLastName.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getLastName()));
        colEmail.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEmail()));
        colPhone.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPhoneNumber()));

        // Load initial data
        loadCustomers();

        // Add selection listener
        customerTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        populateFields(newSelection);
                    }
                });
    }

    @FXML
    private void handleAddCustomer() {
        try {
            Customer customer = new Customer(
                    null,
                    accountNumberField.getText(),
                    firstNameField.getText(),
                    lastNameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    dobPicker.getValue(),
                    addressArea.getText(),
                    LocalDate.now()
            );

            customerService.createCustomer(customer);
            showMessage("Customer added successfully!", "green");
            clearFields();
            loadCustomers();

        } catch (Exception e) {
            showMessage("Error: " + e.getMessage(), "red");
        }
    }

    @FXML
    private void handleUpdateCustomer() {
        if (selectedCustomer == null) {
            showMessage("Please select a customer to update", "orange");
            return;
        }

        try {
            selectedCustomer.setFirstName(firstNameField.getText());
            selectedCustomer.setLastName(lastNameField.getText());
            selectedCustomer.setEmail(emailField.getText());
            selectedCustomer.setPhoneNumber(phoneField.getText());
            selectedCustomer.setDateOfBirth(dobPicker.getValue());
            selectedCustomer.setAddress(addressArea.getText());

            customerService.updateCustomer(selectedCustomer);
            showMessage("Customer updated successfully!", "green");
            clearFields();
            loadCustomers();

        } catch (Exception e) {
            showMessage("Error: " + e.getMessage(), "red");
        }
    }

    @FXML
    private void handleDeleteCustomer() {
        if (selectedCustomer == null) {
            showMessage("Please select a customer to delete", "orange");
            return;
        }

        try {
            customerService.deleteCustomer(selectedCustomer.getId());
            showMessage("Customer deleted successfully!", "green");
            clearFields();
            loadCustomers();

        } catch (Exception e) {
            showMessage("Error: " + e.getMessage(), "red");
        }
    }

    @FXML
    private void handleClear() {
        clearFields();
    }

    private void loadCustomers() {
        ObservableList<Customer> customers = FXCollections.observableArrayList(
                customerService.getAllCustomers()
        );
        customerTable.setItems(customers);
    }

    private void populateFields(Customer customer) {
        selectedCustomer = customer;
        accountNumberField.setText(customer.getAccountNumber());
        accountNumberField.setDisable(true); // Cannot change account number
        firstNameField.setText(customer.getFirstName());
        lastNameField.setText(customer.getLastName());
        emailField.setText(customer.getEmail());
        phoneField.setText(customer.getPhoneNumber());
        dobPicker.setValue(customer.getDateOfBirth());
        addressArea.setText(customer.getAddress());
    }

    private void clearFields() {
        selectedCustomer = null;
        accountNumberField.clear();
        accountNumberField.setDisable(false);
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneField.clear();
        dobPicker.setValue(null);
        addressArea.clear();
        messageLabel.setText("");
    }

    private void showMessage(String message, String color) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
    }
}

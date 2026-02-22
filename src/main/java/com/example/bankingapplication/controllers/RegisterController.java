package com.example.bankingapplication.controllers;

import com.example.bankingapplication.HelloApplication;
import com.example.bankingapplication.config.DatabaseConfig;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleBox;
    @FXML private Label messageLabel;
    private Stage stage;

    @FXML
    public void initialize() {
        roleBox.getItems().addAll("ADMIN", "USER");
    }

    @FXML
    private void handleRegister() {

        try (Connection conn = DatabaseConfig.getConnection()) {

            String hashed = BCrypt.hashpw(
                    passwordField.getText(),
                    BCrypt.gensalt()
            );

            String sql = "INSERT INTO users(username,password,role) VALUES(?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, usernameField.getText());
            ps.setString(2, hashed);
            ps.setString(3, roleBox.getValue());

            ps.executeUpdate();

            messageLabel.setText("User Registered Successfully!");

        } catch (Exception e) {
            messageLabel.setText("Registration Failed");
        }
    }

    public void goToLogin(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Login.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        //Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }
}
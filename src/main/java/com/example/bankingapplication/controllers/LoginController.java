package com.example.bankingapplication.controllers;

import com.example.bankingapplication.HelloApplication;
import com.example.bankingapplication.config.SessionManager;
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
import java.sql.ResultSet;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberCheck;
    @FXML private Label messageLabel;
    private Stage stage;

    @FXML
    public void initialize() {
        usernameField.setText(SessionManager.getRememberedUser());
    }

    public void handleLogin() {

        try (Connection conn = DatabaseConfig.getConnection()) {

            String sql = "SELECT * FROM users WHERE username=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, usernameField.getText());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String storedHash = rs.getString("password");

                if (BCrypt.checkpw(passwordField.getText(), storedHash)) {

                    String role = rs.getString("role");
                    SessionManager.setUser(usernameField.getText(), role);

                    if (rememberCheck.isSelected()) {
                        SessionManager.rememberUser(usernameField.getText());
                    } else {
                        SessionManager.clearRememberedUser();
                    }

                    //openDashboard();
                    FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("MainViews.fxml"));

                    Stage stage = new Stage();
                    stage.setScene(new Scene(loader.load()));
                    stage.show();

                } else {
                    messageLabel.setText("Invalid Credentials");
                }
            } else {
                messageLabel.setText("User Not Found");
            }

        } catch (Exception e) {
            messageLabel.setText("Database Error");
        }
    }

    public void goToRegister(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Register.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        //Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }
}
package com.example.bankingapplication.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "banking_system_db";
    private static final String USER = "root";
    private static final String PASS = ""; // Change as needed

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL + DB_NAME, USER, PASS);
    }

    public static void initializeDatabase() {
        // Connect to MySQL server without specifying database
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {

            // Create database if not exists
            String createDbSql = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
            stmt.executeUpdate(createDbSql);
            System.out.println("Database created/verified: " + DB_NAME);

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        // Now connect to the specific database and create tables
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create customers table
            String createCustomersTable = """
                CREATE TABLE IF NOT EXISTS customers (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    account_number VARCHAR(20) UNIQUE NOT NULL,
                    first_name VARCHAR(50) NOT NULL,
                    last_name VARCHAR(50) NOT NULL,
                    email VARCHAR(100),
                    phone_number VARCHAR(20),
                    date_of_birth DATE,
                    address VARCHAR(255),
                    registration_date DATE NOT NULL
                )
            """;
            stmt.executeUpdate(createCustomersTable);
            System.out.println("Customers table created/verified");

            // Create accounts table
            String createAccountsTable = """
                CREATE TABLE IF NOT EXISTS accounts (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    account_number VARCHAR(20) UNIQUE NOT NULL,
                    account_type VARCHAR(20) NOT NULL,
                    balance DECIMAL(15,2) DEFAULT 0.00,
                    opening_date DATE NOT NULL,
                    status VARCHAR(20) DEFAULT 'ACTIVE',
                    interest_rate DECIMAL(5,2) DEFAULT 0.00,
                    FOREIGN KEY (account_number) REFERENCES customers(account_number)
                )
            """;
            stmt.executeUpdate(createAccountsTable);
            System.out.println("Accounts table created/verified");

            // Create transactions table
            String createTransactionsTable = """
                CREATE TABLE IF NOT EXISTS transactions (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    account_number VARCHAR(20) NOT NULL,
                    transaction_type VARCHAR(20) NOT NULL,
                    amount DECIMAL(15,2) NOT NULL,
                    transaction_date DATETIME NOT NULL,
                    description VARCHAR(255),
                    reference_number VARCHAR(50) UNIQUE,
                    balance_after DECIMAL(15,2),
                    FOREIGN KEY (account_number) REFERENCES customers(account_number)
                )
            """;
            stmt.executeUpdate(createTransactionsTable);
            System.out.println("Transactions table created/verified");

            // Create loans table
            String createLoansTable = """
                CREATE TABLE IF NOT EXISTS loans (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    account_number VARCHAR(20) NOT NULL,
                    loan_type VARCHAR(20) NOT NULL,
                    loan_amount DECIMAL(15,2) NOT NULL,
                    interest_rate DECIMAL(5,2) NOT NULL,
                    tenure_months INT NOT NULL,
                    monthly_emi DECIMAL(15,2) NOT NULL,
                    outstanding_amount DECIMAL(15,2) NOT NULL,
                    disbursement_date DATE,
                    status VARCHAR(20) DEFAULT 'PENDING',
                    FOREIGN KEY (account_number) REFERENCES customers(account_number)
                )
            """;
            stmt.executeUpdate(createLoansTable);
            System.out.println("Loans table created/verified");

            String createPasswordTable = """
                    CREATE TABLE IF NOT EXISTS users (
                        id INT AUTO_INCREMENT,
                        username VARCHAR(50) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL,
                        role ENUM('ADMIN','USER') NOT NULL DEFAULT 'USER',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        PRIMARY KEY (id)
                    );
                    """;
            stmt.executeUpdate(createPasswordTable);
            System.out.println("Passwords table created/verified");

            System.out.println("All tables created successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

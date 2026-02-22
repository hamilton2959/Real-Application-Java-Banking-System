package com.example.bankingapplication.repoImplementations;

import com.example.bankingapplication.config.DatabaseConfig;
import com.example.bankingapplication.entity.Transaction;
import com.example.bankingapplication.repository.TransactionRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepositoryImpl implements TransactionRepository {
    @Override
    public Transaction save(Transaction transaction) {
        String sql = """
            INSERT INTO transactions (account_number, transaction_type, amount, 
            transaction_date, description, reference_number, balance_after) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, transaction.getAccountNumber());
            pstmt.setString(2, transaction.getTransactionType());
            pstmt.setBigDecimal(3, transaction.getAmount());
            pstmt.setTimestamp(4, Timestamp.valueOf(transaction.getTransactionDate()));
            pstmt.setString(5, transaction.getDescription());
            pstmt.setString(6, transaction.getReferenceNumber());
            pstmt.setBigDecimal(7, transaction.getBalanceAfter());

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                transaction.setId(rs.getLong(1));
            }

            return transaction;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Transaction findById(Long id) {
        String sql = "SELECT * FROM transactions WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractTransactionFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY transaction_date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    @Override
    public List<Transaction> findByAccountNumber(String accountNumber) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY transaction_date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM transactions WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Transaction extractTransactionFromResultSet(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getLong("id"),
                rs.getString("account_number"),
                rs.getString("transaction_type"),
                rs.getBigDecimal("amount"),
                rs.getTimestamp("transaction_date").toLocalDateTime(),
                rs.getString("description"),
                rs.getString("reference_number"),
                rs.getBigDecimal("balance_after")
        );
    }
}

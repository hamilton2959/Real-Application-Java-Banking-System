package com.example.bankingapplication.repoImplementations;

import com.example.bankingapplication.config.DatabaseConfig;
import com.example.bankingapplication.entity.Account;
import com.example.bankingapplication.repository.AccountRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountRepositoryImpl implements AccountRepository {
    @Override
    public Account save(Account account) {
        String sql = """
            INSERT INTO accounts (account_number, account_type, balance, 
            opening_date, status, interest_rate) 
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, account.getAccountNumber());
            pstmt.setString(2, account.getAccountType());
            pstmt.setBigDecimal(3, account.getBalance());
            pstmt.setDate(4, Date.valueOf(account.getOpeningDate()));
            pstmt.setString(5, account.getStatus());
            pstmt.setBigDecimal(6, account.getInterestRate());

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                account.setId(rs.getLong(1));
            }

            return account;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Account findById(Long id) {
        String sql = "SELECT * FROM accounts WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractAccountFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Account findByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractAccountFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                accounts.add(extractAccountFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    @Override
    public List<Account> findByCustomerAccountNumber(String accountNumber) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE account_number = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                accounts.add(extractAccountFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    @Override
    public void update(Account account) {
        String sql = """
            UPDATE accounts SET account_type=?, balance=?, status=?, 
            interest_rate=? WHERE id=?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, account.getAccountType());
            pstmt.setBigDecimal(2, account.getBalance());
            pstmt.setString(3, account.getStatus());
            pstmt.setBigDecimal(4, account.getInterestRate());
            pstmt.setLong(5, account.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM accounts WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Account extractAccountFromResultSet(ResultSet rs) throws SQLException {
        return new Account(
                rs.getLong("id"),
                rs.getString("account_number"),
                rs.getString("account_type"),
                rs.getBigDecimal("balance"),
                rs.getDate("opening_date").toLocalDate(),
                rs.getString("status"),
                rs.getBigDecimal("interest_rate")
        );
    }
}

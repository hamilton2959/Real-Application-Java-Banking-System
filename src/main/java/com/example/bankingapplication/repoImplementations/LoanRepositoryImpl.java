package com.example.bankingapplication.repoImplementations;

import com.example.bankingapplication.config.DatabaseConfig;
import com.example.bankingapplication.entity.Loan;
import com.example.bankingapplication.repository.LoanRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanRepositoryImpl implements LoanRepository {
    @Override
    public Loan save(Loan loan) {
        String sql = """
            INSERT INTO loans (account_number, loan_type, loan_amount, interest_rate, 
            tenure_months, monthly_emi, outstanding_amount, disbursement_date, status) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, loan.getAccountNumber());
            pstmt.setString(2, loan.getLoanType());
            pstmt.setBigDecimal(3, loan.getLoanAmount());
            pstmt.setBigDecimal(4, loan.getInterestRate());
            pstmt.setInt(5, loan.getTenureMonths());
            pstmt.setBigDecimal(6, loan.getMonthlyEmi());
            pstmt.setBigDecimal(7, loan.getOutstandingAmount());

            if (loan.getDisbursementDate() != null) {
                pstmt.setDate(8, Date.valueOf(loan.getDisbursementDate()));
            } else {
                pstmt.setNull(8, Types.DATE);
            }

            pstmt.setString(9, loan.getStatus());

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                loan.setId(rs.getLong(1));
            }

            return loan;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Loan findById(Long id) {
        String sql = "SELECT * FROM loans WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractLoanFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Loan> findAll() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                loans.add(extractLoanFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    @Override
    public List<Loan> findByAccountNumber(String accountNumber) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE account_number = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                loans.add(extractLoanFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    @Override
    public void update(Loan loan) {
        String sql = """
            UPDATE loans SET loan_type=?, loan_amount=?, interest_rate=?, 
            tenure_months=?, monthly_emi=?, outstanding_amount=?, 
            disbursement_date=?, status=? WHERE id=?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, loan.getLoanType());
            pstmt.setBigDecimal(2, loan.getLoanAmount());
            pstmt.setBigDecimal(3, loan.getInterestRate());
            pstmt.setInt(4, loan.getTenureMonths());
            pstmt.setBigDecimal(5, loan.getMonthlyEmi());
            pstmt.setBigDecimal(6, loan.getOutstandingAmount());

            if (loan.getDisbursementDate() != null) {
                pstmt.setDate(7, Date.valueOf(loan.getDisbursementDate()));
            } else {
                pstmt.setNull(7, Types.DATE);
            }

            pstmt.setString(8, loan.getStatus());
            pstmt.setLong(9, loan.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM loans WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Loan extractLoanFromResultSet(ResultSet rs) throws SQLException {
        Date disbursementDate = rs.getDate("disbursement_date");
        return new Loan(
                rs.getLong("id"),
                rs.getString("account_number"),
                rs.getString("loan_type"),
                rs.getBigDecimal("loan_amount"),
                rs.getBigDecimal("interest_rate"),
                rs.getInt("tenure_months"),
                rs.getBigDecimal("monthly_emi"),
                rs.getBigDecimal("outstanding_amount"),
                disbursementDate != null ? disbursementDate.toLocalDate() : null,
                rs.getString("status")
        );
    }
}

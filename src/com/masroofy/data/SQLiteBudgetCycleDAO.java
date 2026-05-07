package com.masroofy.data;

import com.masroofy.domain.BudgetCycle;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Sq lite budget cycle dao.
 */
public class SQLiteBudgetCycleDAO implements IBudgetCycleDAO {

    private final Connection dbConnection;

    /**
     * Instantiates a new Sq lite budget cycle dao.
     */
    public SQLiteBudgetCycleDAO() {
        this.dbConnection = DatabaseHelper.getInstance().getConnection();
    }

    @Override
    public boolean saveCycle(BudgetCycle cycle) {
        if (cycle.getCycleId() == 0) {
            return insertCycle(cycle);
        } else {
            return updateCycle(cycle);
        }
    }

    @Override
    public BudgetCycle getCurrentCycle() {
        String sql =
                "SELECT * FROM " + DatabaseHelper.TABLE_CYCLES
                        + " ORDER BY " + DatabaseHelper.CYCLES_COL_ID + " DESC"
                        + " LIMIT 1;";

        try (PreparedStatement pstmt = dbConnection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRowToCycle(rs);
            }
        } catch (SQLException e) {
            System.err.println("getCurrentCycle failed: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<BudgetCycle> getAllCycles() {
        List<BudgetCycle> cycles = new ArrayList<>();
        String sql =
                "SELECT * FROM " + DatabaseHelper.TABLE_CYCLES
                        + " ORDER BY " + DatabaseHelper.CYCLES_COL_ID + " DESC;";

        try (PreparedStatement pstmt = dbConnection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                cycles.add(mapRowToCycle(rs));
            }
        } catch (SQLException e) {
            System.err.println("getAllCycles failed: " + e.getMessage());
        }
        return cycles;
    }

    @Override
    public boolean deleteCycle(int cycleId) {
        String deleteTransactions = "DELETE FROM " + DatabaseHelper.TABLE_TRANSACTIONS + " WHERE " + DatabaseHelper.TRANS_COL_CYCLE_ID + " = ?;";
        String deleteCycle = "DELETE FROM " + DatabaseHelper.TABLE_CYCLES + " WHERE " + DatabaseHelper.CYCLES_COL_ID + " = ?;";

        try {
            dbConnection.setAutoCommit(false);
            try (PreparedStatement delTrans = dbConnection.prepareStatement(deleteTransactions)) {
                delTrans.setInt(1, cycleId);
                delTrans.executeUpdate();
            }
            int rowsAffected;
            try (PreparedStatement delCycle = dbConnection.prepareStatement(deleteCycle)) {
                delCycle.setInt(1, cycleId);
                rowsAffected = delCycle.executeUpdate();
            }
            dbConnection.commit();
            dbConnection.setAutoCommit(true);
            return rowsAffected == 1;
        } catch (SQLException e) {
            rollbackQuietly();
            return false;
        }
    }

    // clearing data for seq diag 6
    @Override
    public boolean deleteAllData() {
        String deleteTransactions = "DELETE FROM " + DatabaseHelper.TABLE_TRANSACTIONS + ";";
        String deleteCycles = "DELETE FROM " + DatabaseHelper.TABLE_CYCLES + ";";

        try {
            dbConnection.setAutoCommit(false);
            try (Statement stmt = dbConnection.createStatement()) {
                // diag 3
                stmt.executeUpdate(deleteTransactions);

                // diag 5
                stmt.executeUpdate(deleteCycles);
            }
            dbConnection.commit();
            dbConnection.setAutoCommit(true);
            System.out.println("All records deleted from the database.");
            return true;
        } catch (SQLException e) {
            System.err.println("deleteAllData failed: " + e.getMessage());
            rollbackQuietly();
            return false;
        }
    }

    private void rollbackQuietly() {
        try {
            dbConnection.rollback();
            dbConnection.setAutoCommit(true);
        } catch (SQLException ex) { }
    }

    private boolean insertCycle(BudgetCycle cycle) {
        String sql = "INSERT INTO " + DatabaseHelper.TABLE_CYCLES + " (" + DatabaseHelper.CYCLES_COL_START_DATE + ", " + DatabaseHelper.CYCLES_COL_END_DATE + ", " + DatabaseHelper.CYCLES_COL_ALLOWANCE + ", " + DatabaseHelper.CYCLES_COL_BALANCE + ") VALUES (?, ?, ?, ?);";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, cycle.getStartDate().toString());
            pstmt.setString(2, cycle.getEndDate().toString());
            pstmt.setDouble(3, cycle.getTotalAllowance());
            pstmt.setDouble(4, cycle.getRemainingBalance());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 1) {
                ResultSet keys = pstmt.getGeneratedKeys();
                if (keys.next()) { cycle.setCycleId(keys.getInt(1)); }
                return true;
            }
            return false;
        } catch (SQLException e) { return false; }
    }

    private boolean updateCycle(BudgetCycle cycle) {
        String sql = "UPDATE " + DatabaseHelper.TABLE_CYCLES + " SET " + DatabaseHelper.CYCLES_COL_START_DATE + " = ?, " + DatabaseHelper.CYCLES_COL_END_DATE + " = ?, " + DatabaseHelper.CYCLES_COL_ALLOWANCE + " = ?, " + DatabaseHelper.CYCLES_COL_BALANCE + " = ? WHERE " + DatabaseHelper.CYCLES_COL_ID + " = ?;";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(sql)) {
            pstmt.setString(1, cycle.getStartDate().toString());
            pstmt.setString(2, cycle.getEndDate().toString());
            pstmt.setDouble(3, cycle.getTotalAllowance());
            pstmt.setDouble(4, cycle.getRemainingBalance());
            pstmt.setInt(5, cycle.getCycleId());
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) { return false; }
    }

    private BudgetCycle mapRowToCycle(ResultSet rs) throws SQLException {
        int id = rs.getInt(DatabaseHelper.CYCLES_COL_ID);
        LocalDate startDate = LocalDate.parse(rs.getString(DatabaseHelper.CYCLES_COL_START_DATE));
        LocalDate endDate = LocalDate.parse(rs.getString(DatabaseHelper.CYCLES_COL_END_DATE));
        double allowance = rs.getDouble(DatabaseHelper.CYCLES_COL_ALLOWANCE);
        double balance = rs.getDouble(DatabaseHelper.CYCLES_COL_BALANCE);
        BudgetCycle cycle = new BudgetCycle(id, startDate, endDate, allowance);
        double spent = allowance - balance;
        if (spent > 0) cycle.deductAmount(spent);
        return cycle;
    }
}
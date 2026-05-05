package com.masroofy.data;

import com.masroofy.domain.BudgetCycle;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList; // NEW CHANGE: Imported ArrayList
import java.util.List;      // NEW CHANGE: Imported List

/**
 * SQLiteBudgetCycleDAO
 *
 * Concrete implementation of IBudgetCycleDAO.
 * Handles all persistence operations for BudgetCycle objects in SQLite.
 *
 * Design note: saveCycle() acts as an upsert — it inserts a new cycle
 * if cycleId == 0 (new), or updates the existing row if cycleId > 0.
 * This matches how CycleManager and ExpenseTracker use it.
 */
public class SQLiteBudgetCycleDAO implements IBudgetCycleDAO {

    private final Connection dbConnection;

    public SQLiteBudgetCycleDAO() {
        this.dbConnection = DatabaseHelper.getInstance().getConnection();
    }

    // -----------------------------------------------------------------------
    // IBudgetCycleDAO implementation
    // -----------------------------------------------------------------------

    /**
     * Inserts or updates a BudgetCycle in the database.
     *
     * - If cycleId == 0  → INSERT (new cycle), generated ID is written back.
     * - If cycleId  > 0  → UPDATE (e.g. balance changed after an expense).
     *
     * @param cycle The BudgetCycle to persist.
     * @return true if the operation succeeded, false otherwise.
     */
    @Override
    public boolean saveCycle(BudgetCycle cycle) {
        if (cycle.getCycleId() == 0) {
            return insertCycle(cycle);
        } else {
            return updateCycle(cycle);
        }
    }

    /**
     * Returns the most recently created budget cycle.
     * "Current" is defined as the cycle with the highest cycle_id.
     *
     * @return The active BudgetCycle, or null if no cycle exists yet.
     */
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

    // NEW CHANGE: Added implementation to query the DB for all historical budget cycles.
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

    /**
     * Deletes a budget cycle and all its associated transactions.
     * Called by CycleManager.resetCycle() (Sequence Diagram 6).
     *
     * @param cycleId The ID of the cycle to delete.
     * @return true if the cycle row was deleted, false otherwise.
     */
    @Override
    public boolean deleteCycle(int cycleId) {
        // First delete child transactions to respect foreign key constraint
        String deleteTransactions =
                "DELETE FROM " + DatabaseHelper.TABLE_TRANSACTIONS
                        + " WHERE " + DatabaseHelper.TRANS_COL_CYCLE_ID + " = ?;";

        String deleteCycle =
                "DELETE FROM " + DatabaseHelper.TABLE_CYCLES
                        + " WHERE " + DatabaseHelper.CYCLES_COL_ID + " = ?;";

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

            System.out.println("All data cleared for cycle " + cycleId + ".");
            return rowsAffected == 1;

        } catch (SQLException e) {
            System.err.println("deleteCycle failed: " + e.getMessage());
            try { dbConnection.rollback(); dbConnection.setAutoCommit(true); }
            catch (SQLException rollbackEx) { System.err.println("Rollback failed: " + rollbackEx.getMessage()); }
            return false;
        }
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    private boolean insertCycle(BudgetCycle cycle) {
        String sql =
                "INSERT INTO " + DatabaseHelper.TABLE_CYCLES
                        + " (" + DatabaseHelper.CYCLES_COL_START_DATE + ", "
                        + DatabaseHelper.CYCLES_COL_END_DATE   + ", "
                        + DatabaseHelper.CYCLES_COL_ALLOWANCE  + ", "
                        + DatabaseHelper.CYCLES_COL_BALANCE    + ") "
                        + "VALUES (?, ?, ?, ?);";

        try (PreparedStatement pstmt = dbConnection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, cycle.getStartDate().toString());
            pstmt.setString(2, cycle.getEndDate().toString());
            pstmt.setDouble(3, cycle.getTotalAllowance());
            pstmt.setDouble(4, cycle.getRemainingBalance());

            int rowsAffected = pstmt.executeUpdate();

            // Write the auto-generated ID back into the domain object
            if (rowsAffected == 1) {
                ResultSet keys = pstmt.getGeneratedKeys();
                if (keys.next()) {
                    cycle.setCycleId(keys.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("insertCycle failed: " + e.getMessage());
            return false;
        }
    }

    private boolean updateCycle(BudgetCycle cycle) {
        String sql =
                "UPDATE " + DatabaseHelper.TABLE_CYCLES + " SET "
                        + DatabaseHelper.CYCLES_COL_START_DATE + " = ?, "
                        + DatabaseHelper.CYCLES_COL_END_DATE   + " = ?, "
                        + DatabaseHelper.CYCLES_COL_ALLOWANCE  + " = ?, "
                        + DatabaseHelper.CYCLES_COL_BALANCE    + " = ? "
                        + "WHERE " + DatabaseHelper.CYCLES_COL_ID + " = ?;";

        try (PreparedStatement pstmt = dbConnection.prepareStatement(sql)) {
            pstmt.setString(1, cycle.getStartDate().toString());
            pstmt.setString(2, cycle.getEndDate().toString());
            pstmt.setDouble(3, cycle.getTotalAllowance());
            pstmt.setDouble(4, cycle.getRemainingBalance());
            pstmt.setInt   (5, cycle.getCycleId());

            return pstmt.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("updateCycle failed: " + e.getMessage());
            return false;
        }
    }

    private BudgetCycle mapRowToCycle(ResultSet rs) throws SQLException {
        int       id        = rs.getInt   (DatabaseHelper.CYCLES_COL_ID);
        LocalDate startDate = LocalDate.parse(rs.getString(DatabaseHelper.CYCLES_COL_START_DATE));
        LocalDate endDate   = LocalDate.parse(rs.getString(DatabaseHelper.CYCLES_COL_END_DATE));
        double    allowance = rs.getDouble(DatabaseHelper.CYCLES_COL_ALLOWANCE);
        double    balance   = rs.getDouble(DatabaseHelper.CYCLES_COL_BALANCE);

        BudgetCycle cycle = new BudgetCycle(id, startDate, endDate, allowance);
        // Restore the actual persisted balance (may differ from allowance after expenses)
        double spent = allowance - balance;
        if (spent > 0) cycle.deductAmount(spent);
        return cycle;
    }
}
package com.masroofy.data;

import com.masroofy.domain.Transaction;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SQLiteTransactionDAO (DBC014)
 *
 * Concrete implementation of ITransactionDAO.
 * Interacts directly with the local SQLite database to perform all
 * CRUD operations on the transactions table.
 *
 * Obtains its Connection from the DatabaseHelper singleton, keeping
 * database configuration in one place (Open/Closed Principle).
 */
public class SQLiteTransactionDAO implements ITransactionDAO {

    private final Connection dbConnection;

    public SQLiteTransactionDAO() {
        this.dbConnection = DatabaseHelper.getInstance().getConnection();
    }

    // -----------------------------------------------------------------------
    // ITransactionDAO implementation
    // -----------------------------------------------------------------------

    /**
     * Inserts a new transaction record into the database.
     * The cycleId links this transaction to the currently active BudgetCycle.
     *
     * @param t      The Transaction to persist.
     * @return true if exactly one row was inserted, false otherwise.
     */
    @Override
    public boolean saveTransaction(Transaction t) {
        String sql =
            "INSERT INTO " + DatabaseHelper.TABLE_TRANSACTIONS
            + " (" + DatabaseHelper.TRANS_COL_CYCLE_ID    + ", "
                   + DatabaseHelper.TRANS_COL_AMOUNT       + ", "
                   + DatabaseHelper.TRANS_COL_CATEGORY_ID  + ", "
                   + DatabaseHelper.TRANS_COL_NOTE         + ", "
                   + DatabaseHelper.TRANS_COL_TIMESTAMP    + ") "
            + "VALUES (?, ?, ?, ?, ?);";

        try (PreparedStatement pstmt = dbConnection.prepareStatement(sql)) {
            pstmt.setInt   (1, t.getCycleId());
            pstmt.setDouble(2, t.getAmount());
            pstmt.setInt   (3, t.getCategoryId());
            pstmt.setString(4, t.getNote());
            pstmt.setString(5, t.getTimestamp().toString());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected == 1;

        } catch (SQLException e) {
            System.err.println("saveTransaction failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a transaction by its unique ID.
     *
     * @param transactionId The ID of the transaction to remove.
     * @return true if exactly one row was deleted, false otherwise.
     */
    @Override
    public boolean deleteTransaction(int transactionId) {
        String sql =
            "DELETE FROM " + DatabaseHelper.TABLE_TRANSACTIONS
            + " WHERE " + DatabaseHelper.TRANS_COL_ID + " = ?;";

        try (PreparedStatement pstmt = dbConnection.prepareStatement(sql)) {
            pstmt.setInt(1, transactionId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected == 1;

        } catch (SQLException e) {
            System.err.println("deleteTransaction failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all transactions belonging to a specific budget cycle,
     * ordered from newest to oldest (matches HistoryUI sort requirement).
     *
     * @param cycleId The ID of the BudgetCycle to query.
     * @return A list of Transaction objects, or an empty list if none found.
     */
    @Override
    public List<Transaction> getAllTransactionsByCycle(int cycleId) {
        List<Transaction> transactions = new ArrayList<>();

        String sql =
            "SELECT * FROM " + DatabaseHelper.TABLE_TRANSACTIONS
            + " WHERE " + DatabaseHelper.TRANS_COL_CYCLE_ID + " = ?"
            + " ORDER BY " + DatabaseHelper.TRANS_COL_TIMESTAMP + " DESC;";

        try (PreparedStatement pstmt = dbConnection.prepareStatement(sql)) {
            pstmt.setInt(1, cycleId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Transaction t = mapRowToTransaction(rs);
                transactions.add(t);
            }
        } catch (SQLException e) {
            System.err.println("getAllTransactionsByCycle failed: " + e.getMessage());
        }

        return transactions;
    }

    // -----------------------------------------------------------------------
    // Helper: map a ResultSet row → Transaction object
    // -----------------------------------------------------------------------

    private Transaction mapRowToTransaction(ResultSet rs) throws SQLException {
        int    id         = rs.getInt   (DatabaseHelper.TRANS_COL_ID);
        double amount     = rs.getDouble(DatabaseHelper.TRANS_COL_AMOUNT);
        int    categoryId = rs.getInt   (DatabaseHelper.TRANS_COL_CATEGORY_ID);
        String note       = rs.getString(DatabaseHelper.TRANS_COL_NOTE);
        int    cycleId    = rs.getInt   (DatabaseHelper.TRANS_COL_CYCLE_ID);
        String timestamp  = rs.getString(DatabaseHelper.TRANS_COL_TIMESTAMP);

        Transaction t = new Transaction(id, amount, categoryId, note);
        t.setCycleId(cycleId);
        t.setTimestamp(LocalDateTime.parse(timestamp));
        return t;
    }
}

package com.masroofy.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseHelper
 *
 * Manages the creation and lifecycle of the local SQLite database.
 * Acts as the single source of truth for the schema: table names,
 * column names, and seed data. All DAO classes obtain their
 * Connection through this class.
 *
 * Uses a singleton pattern so only one connection is open at a time.
 */
public class DatabaseHelper {

    // -----------------------------------------------------------------------
    // Database file
    // -----------------------------------------------------------------------
    private static final String DB_URL = "jdbc:sqlite:masroofy.db";

    // -----------------------------------------------------------------------
    // Table: budget_cycles
    // -----------------------------------------------------------------------
    public static final String TABLE_CYCLES          = "budget_cycles";
    public static final String CYCLES_COL_ID         = "cycle_id";
    public static final String CYCLES_COL_START_DATE = "start_date";
    public static final String CYCLES_COL_END_DATE   = "end_date";
    public static final String CYCLES_COL_ALLOWANCE  = "total_allowance";
    public static final String CYCLES_COL_BALANCE    = "remaining_balance";

    // -----------------------------------------------------------------------
    // Table: transactions
    // -----------------------------------------------------------------------
    public static final String TABLE_TRANSACTIONS      = "transactions";
    public static final String TRANS_COL_ID            = "transaction_id";
    public static final String TRANS_COL_CYCLE_ID      = "cycle_id";
    public static final String TRANS_COL_AMOUNT        = "amount";
    public static final String TRANS_COL_CATEGORY_ID   = "category_id";
    public static final String TRANS_COL_NOTE          = "note";
    public static final String TRANS_COL_TIMESTAMP     = "timestamp";

    // -----------------------------------------------------------------------
    // Singleton
    // -----------------------------------------------------------------------
    private static DatabaseHelper instance;
    private Connection connection;

    private DatabaseHelper() {
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            // Enable foreign key enforcement
            connection.createStatement().execute("PRAGMA foreign_keys = ON;");
            initSchema();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found. Add sqlite-jdbc to your classpath.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to open database: " + e.getMessage(), e);
        }
    }

    /** Returns the singleton instance, creating it on first call. */
    public static synchronized DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    /** Returns the open database connection for use by DAOs. */
    public Connection getConnection() {
        return connection;
    }

    // -----------------------------------------------------------------------
    // Schema creation
    // -----------------------------------------------------------------------

    /**
     * Creates all tables if they do not already exist.
     * Safe to call on every app launch (idempotent).
     */
    private void initSchema() throws SQLException {
        Statement stmt = connection.createStatement();

        // budget_cycles table
        stmt.execute(
            "CREATE TABLE IF NOT EXISTS " + TABLE_CYCLES + " ("
            + CYCLES_COL_ID         + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CYCLES_COL_START_DATE + " TEXT NOT NULL, "
            + CYCLES_COL_END_DATE   + " TEXT NOT NULL, "
            + CYCLES_COL_ALLOWANCE  + " REAL NOT NULL, "
            + CYCLES_COL_BALANCE    + " REAL NOT NULL"
            + ");"
        );

        // transactions table
        stmt.execute(
            "CREATE TABLE IF NOT EXISTS " + TABLE_TRANSACTIONS + " ("
            + TRANS_COL_ID          + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TRANS_COL_CYCLE_ID    + " INTEGER NOT NULL, "
            + TRANS_COL_AMOUNT      + " REAL NOT NULL, "
            + TRANS_COL_CATEGORY_ID + " INTEGER NOT NULL, "
            + TRANS_COL_NOTE        + " TEXT, "
            + TRANS_COL_TIMESTAMP   + " TEXT NOT NULL, "
            + "FOREIGN KEY (" + TRANS_COL_CYCLE_ID + ") REFERENCES "
                + TABLE_CYCLES + "(" + CYCLES_COL_ID + ")"
            + ");"
        );

        stmt.close();
        System.out.println("Database schema initialized successfully.");
    }

    // -----------------------------------------------------------------------
    // Cleanup
    // -----------------------------------------------------------------------

    /** Closes the database connection. Call this on application shutdown. */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}

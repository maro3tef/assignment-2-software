package com.masroofy;

import com.masroofy.business.AlertingSystem;
import com.masroofy.business.CalculationEngine;
import com.masroofy.business.CycleManager;
import com.masroofy.business.ExpenseTracker;
import com.masroofy.data.DatabaseHelper;
import com.masroofy.data.SQLiteBudgetCycleDAO;
import com.masroofy.data.SQLiteTransactionDAO;
import com.masroofy.domain.BudgetCycle;
import com.masroofy.domain.UserProfile;
import com.masroofy.presentation.AuthUI;

import javax.swing.SwingUtilities;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Masroofy Initialized ===\n");

        // --- Wire up the data layer (Dependency Injection) ---
        SQLiteBudgetCycleDAO cycleDAO       = new SQLiteBudgetCycleDAO();
        SQLiteTransactionDAO transactionDAO  = new SQLiteTransactionDAO();
        AlertingSystem       alertingSystem  = new AlertingSystem();
        CalculationEngine    calcEngine      = new CalculationEngine(cycleDAO);

        CycleManager   cycleManager   = new CycleManager(cycleDAO, calcEngine);
        ExpenseTracker expenseTracker = new ExpenseTracker(transactionDAO, cycleDAO, alertingSystem);

        // Console testing code remains intact...
        System.out.println("--- All Historical Cycles ---");
        List<BudgetCycle> cycleHistory = cycleManager.getCycleHistory();
        if (cycleHistory.isEmpty()) {
            System.out.println("No history found. The database is empty.");
        } else {
            for (BudgetCycle c : cycleHistory) {
                System.out.println("Cycle ID: " + c.getCycleId() +
                        " | Start: " + c.getStartDate() +
                        " | End: " + c.getEndDate() +
                        " | Allowance: " + c.getTotalAllowance() +
                        " | Remaining Balance: " + c.getRemainingBalance());
            }
        }

        // NOTE: The hardcoded setup logic was removed from here.
        // It is now correctly handled by the SetupCycleUI in the presentation layer.

        // Add a JVM Shutdown Hook to safely close the DB when the user closes the app window.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseHelper.getInstance().closeConnection();
            System.out.println("\n=== Masroofy session complete ===");
        }));

        // Launch the UI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Create a dummy user profile for testing (PIN is "1234")
            UserProfile dummyUser = new UserProfile(1, "1234", false);

            // Pass all required dependencies to the presentation layer
            new AuthUI(dummyUser, cycleManager, calcEngine, expenseTracker, transactionDAO);
        });
    }
}
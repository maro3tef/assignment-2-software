package com.masroofy;

import com.masroofy.business.AlertingSystem;
import com.masroofy.business.CalculationEngine;
import com.masroofy.business.CycleManager;
import com.masroofy.business.ExpenseTracker;
import com.masroofy.data.DatabaseHelper;
import com.masroofy.data.SQLiteBudgetCycleDAO;
import com.masroofy.data.SQLiteTransactionDAO;
import com.masroofy.domain.BudgetCycle;
import com.masroofy.domain.Transaction;
import com.masroofy.domain.UserProfile;
import com.masroofy.presentation.AuthUI; // FIX: Imported AuthUI

import javax.swing.SwingUtilities; // FIX: Imported SwingUtilities
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Masroofy Initialized ===\n");

        // --- Wire up the data layer (Dependency Injection) ---
        SQLiteBudgetCycleDAO cycleDAO       = new SQLiteBudgetCycleDAO();
        SQLiteTransactionDAO transactionDAO  = new SQLiteTransactionDAO();
        AlertingSystem       alertingSystem  = new AlertingSystem();
        CalculationEngine    calcEngine      = new CalculationEngine();

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

        // Check if there is an active cycle for the UI to use, if not, create one
        BudgetCycle current = cycleDAO.getCurrentCycle();
        if(current == null) {
            System.out.println("\n--- Setting up a new budget cycle for UI ---");
            LocalDate start = LocalDate.now();
            LocalDate end   = start.plusDays(9);
            cycleManager.initCycle(start, end, 1000.0);
        }

        // FIX: Add a JVM Shutdown Hook to safely close the DB when the user closes the app window.
        // We cannot close it synchronously at the end of main() because the UI runs on a separate thread.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseHelper.getInstance().closeConnection();
            System.out.println("\n=== Masroofy session complete ===");
        }));

        // FIX: Launch the UI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Create a dummy user profile for testing (PIN is "1234")
            UserProfile dummyUser = new UserProfile(1, "1234", false);

            // Pass all required dependencies to the presentation layer
            new AuthUI(dummyUser, cycleManager, calcEngine, expenseTracker, transactionDAO);
        });
    }
}
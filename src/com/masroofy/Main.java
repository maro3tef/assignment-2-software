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

/**
 * The type Main.
 */
public class Main {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        System.out.println("=== Masroofy Initialized ===\n");

        //   init the data layer applying Dependency Injection
        SQLiteBudgetCycleDAO cycleDAO       = new SQLiteBudgetCycleDAO();
        SQLiteTransactionDAO transactionDAO  = new SQLiteTransactionDAO();
        AlertingSystem       alertingSystem  = new AlertingSystem();
        CalculationEngine    calcEngine      = new CalculationEngine(cycleDAO);

        CycleManager   cycleManager   = new CycleManager(cycleDAO, calcEngine);
        ExpenseTracker expenseTracker = new ExpenseTracker(transactionDAO, cycleDAO, alertingSystem);

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

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseHelper.getInstance().closeConnection();
            System.out.println("\n=== Masroofy session complete ===");
        }));

        SwingUtilities.invokeLater(() -> {
            UserProfile dummyUser = new UserProfile(1, "1234", false);
    new AuthUI(dummyUser, cycleManager, calcEngine, expenseTracker, transactionDAO);
        });
    }
}
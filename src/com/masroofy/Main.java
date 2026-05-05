package com.masroofy;
 
import com.masroofy.business.AlertingSystem;
import com.masroofy.business.CalculationEngine;
import com.masroofy.business.CycleManager;
import com.masroofy.business.ExpenseTracker;
import com.masroofy.data.DatabaseHelper;
import com.masroofy.data.IBudgetCycleDAO;
import com.masroofy.data.SQLiteBudgetCycleDAO;
import com.masroofy.data.SQLiteTransactionDAO;
import com.masroofy.domain.BudgetCycle;
import com.masroofy.domain.Transaction;
 
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
        System.out.println();
        // --- Sequence Diagram 1: Set Initial Budget Cycle ---
        System.out.println("--- Setting up a new budget cycle ---");
        LocalDate start = LocalDate.now();
        LocalDate end   = start.plusDays(9);
        cycleManager.initCycle(start, end, 1000.0);
 
        // --- Sequence Diagram 2: Dynamic Daily Limit View ---
        System.out.println("\n--- Viewing dynamic daily limit ---");
        BudgetCycle current = cycleDAO.getCurrentCycle();
        if (current != null) {
            double limit = calcEngine.calcSafeDailyLimit(current);
            System.out.printf("Safe Daily Limit: %.2f EGP%n", limit);
            System.out.printf("Remaining Balance: %.2f EGP | Days left: %d%n",
                current.getRemainingBalance(), current.getRemainingDays());
        }
 
        // --- Sequence Diagram 4: Log some transactions ---
        System.out.println("\n--- Logging transactions ---");
        expenseTracker.logTransaction(120.0, 1, "Lunch at cafeteria");
        expenseTracker.logTransaction(50.0,  2, "Metro ticket");
        expenseTracker.logTransaction(200.0, 3, "New headphones");
 
        // --- Transaction History Review ---
        System.out.println("\n--- Transaction History ---");
        current = cycleDAO.getCurrentCycle();
        if (current != null) {
            List<Transaction> history = transactionDAO.getAllTransactionsByCycle(current.getCycleId());
            for (Transaction t : history) {
                System.out.println(t.getTransactionDetails());
            }
        }
 
        // --- Sequence Diagram 3: Midnight Rollover ---
        System.out.println("\n--- Daily Rollover ---");
        current = cycleDAO.getCurrentCycle();
        if (current != null) calcEngine.triggerMidnightRollover(current);
 
        // --- Sequence Diagram 6: Reset cycle ---
        /*System.out.println("\n--- Resetting cycle ---");
        current = cycleDAO.getCurrentCycle();
        if (current != null) {
            boolean reset = cycleManager.resetCycle(current.getCycleId());
            System.out.println("Reset successful: " + reset);
        }*/
 
        // Cleanup
        DatabaseHelper.getInstance().closeConnection();
        System.out.println("\n=== Masroofy session complete ===");
    }
}
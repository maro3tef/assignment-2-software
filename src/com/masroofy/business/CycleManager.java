package com.masroofy.business;
import com.masroofy.data.IBudgetCycleDAO;
import com.masroofy.domain.BudgetCycle;
import java.time.LocalDate;
import java.util.List; // NEW CHANGE: Imported List for the getCycleHistory method

public class CycleManager {
    private IBudgetCycleDAO cycleDAO;
    private CalculationEngine calcEngine;

    // Dependency Injection
    public CycleManager(IBudgetCycleDAO cycleDAO, CalculationEngine calcEngine) {
        this.cycleDAO = cycleDAO;
        this.calcEngine = calcEngine;
    }

    // Sequence Diagram 1: Set Initial Budget Cycle
    public boolean initCycle(LocalDate startDate, LocalDate endDate, double amount) {
        // NEW CHANGE: Changed hardcoded ID from 1 to 0 so the database knows to auto-increment it.
        BudgetCycle newCycle = new BudgetCycle(0, startDate, endDate, amount);

        boolean saved = cycleDAO.saveCycle(newCycle);
        if (saved) {
            double initialLimit = calcEngine.calcSafeDailyLimit(newCycle);
            System.out.println("Cycle successfully created! Initial Daily Limit: " + initialLimit);
        }
        return saved;
    }

    // NEW CHANGE: Added this new method to allow retrieving all historical cycles
    public List<BudgetCycle> getCycleHistory() {
        return cycleDAO.getAllCycles();
    }
    public boolean resetCycle(int cycleId) {
        return cycleDAO.deleteCycle(cycleId);
    }

    // NEW: Sequence Diagram 6 - triggers a complete reset of all tables
    public boolean triggerDataReset() {
        System.out.println("Triggering complete data reset...");
        return cycleDAO.deleteAllData();
    }
}
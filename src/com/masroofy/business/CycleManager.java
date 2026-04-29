package com.masroofy.business;
import com.masroofy.data.IBudgetCycleDAO;
import com.masroofy.domain.BudgetCycle;
import java.time.LocalDate;
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
        // Create a new cycle (Using dummy ID 1 for now)
        BudgetCycle newCycle = new BudgetCycle(1, startDate, endDate, amount);

        boolean saved = cycleDAO.saveCycle(newCycle);
        if (saved) {
            double initialLimit = calcEngine.calcSafeDailyLimit(newCycle);
            System.out.println("Cycle successfully created! Initial Daily Limit: " + initialLimit);
        }
        return saved;
    }
    // Sequence Diagram 6
    public boolean resetCycle(int cycleId) {
        System.out.println("Triggering data reset...");
        return cycleDAO.deleteCycle(cycleId);
    }
}

package com.masroofy.business;
import com.masroofy.data.IBudgetCycleDAO;
import com.masroofy.domain.BudgetCycle;
import java.time.LocalDate;
import java.util.List; // NEW CHANGE: Imported List for the getCycleHistory method

/**
 * The type Cycle manager.
 */
public class CycleManager {
    private IBudgetCycleDAO cycleDAO;
    private CalculationEngine calcEngine;

    /**
     * Instantiates a new Cycle manager.
     *
     * @param cycleDAO   the cycle dao
     * @param calcEngine the calc engine
     */
// Dependency Injection
    public CycleManager(IBudgetCycleDAO cycleDAO, CalculationEngine calcEngine) {
        this.cycleDAO = cycleDAO;
        this.calcEngine = calcEngine;
    }

    /**
     * Init cycle boolean.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @param amount    the amount
     * @return the boolean
     */
// 1# Set Initial Budget Cycle
    public boolean initCycle(LocalDate startDate, LocalDate endDate, double amount) {
        BudgetCycle newCycle = new BudgetCycle(0, startDate, endDate, amount);

        boolean saved = cycleDAO.saveCycle(newCycle);
        if (saved) {
            double initialLimit = calcEngine.calcSafeDailyLimit(newCycle);
            System.out.println("Cycle successfully created! Initial Daily Limit: " + initialLimit);
        }
        return saved;
    }

    /**
     * Gets cycle history.
     *
     * @return the cycle history
     */

    public List<BudgetCycle> getCycleHistory() {
        return cycleDAO.getAllCycles();
    }

    /**
     * Reset cycle boolean.
     *
     * @param cycleId the cycle id
     * @return the boolean
     */
    public boolean resetCycle(int cycleId) {
        return cycleDAO.deleteCycle(cycleId);
    }

    /**
     * Trigger data reset boolean.
     *
     * @return the boolean
     */
//   6 # triggers a complete reset of all tables
    public boolean triggerDataReset() {
        System.out.println("Triggering complete data reset...");
        return cycleDAO.deleteAllData();
    }
}
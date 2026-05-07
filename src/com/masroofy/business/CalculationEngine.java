package com.masroofy.business;

import com.masroofy.domain.BudgetCycle;
import com.masroofy.data.IBudgetCycleDAO;
import java.time.LocalDate;

/**
 * The type Calculation engine.
 */
public class CalculationEngine {

    private IBudgetCycleDAO cycleDAO;

    /**
     * Instantiates a new Calculation engine.
     *
     * @param cycleDAO the cycle dao
     */


    public CalculationEngine(IBudgetCycleDAO cycleDAO) {
        this.cycleDAO = cycleDAO;
    }

    /**
     * Calc safe daily limit double.
     *
     * @param cycle the cycle
     * @return the double
     */
    public double calcSafeDailyLimit(BudgetCycle cycle) {
        if (cycle == null) return 0.0;

        int remainingDays = cycle.getRemainingDays();
        if (remainingDays <= 0) {
            return cycle.getRemainingBalance();
        }

        return cycle.getRemainingBalance() / remainingDays;
    }

    /**
     * Request current status double.
     *
     * @param cycle the cycle
     * @return the double
     */
// 3# Daily Rollover Management
    public double requestCurrentStatus(BudgetCycle cycle) {
        if (cycle == null) return 0.0;

        LocalDate today = LocalDate.now();
        LocalDate lastRollover = cycle.getLastRolloverDate();


        double currentLimit = calcSafeDailyLimit(cycle);


        if (lastRollover == null || lastRollover.isBefore(today)) {
            System.out.println("System: App opened on a new day. Triggering Midnight Rollover...");

            // Identify unspent funds , Recalculate Safe Daily Limit
            cycle.setSafeDailyLimit(currentLimit);
            cycle.setLastRolloverDate(today);


            if (cycleDAO != null) {
                cycleDAO.saveCycle(cycle);
                System.out.println("System: Unspent funds rolled over! Saved new Safe Limit to DB: " + currentLimit);
            }
        }


        return currentLimit;
    }
}
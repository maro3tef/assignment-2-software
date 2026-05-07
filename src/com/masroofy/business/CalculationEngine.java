package com.masroofy.business;

import com.masroofy.domain.BudgetCycle;
import com.masroofy.data.IBudgetCycleDAO;
import java.time.LocalDate;

public class CalculationEngine {

    private IBudgetCycleDAO cycleDAO;

    // INJECTED: Diagram 3 explicitly shows the Engine talking directly to SQLite Database
    public CalculationEngine(IBudgetCycleDAO cycleDAO) {
        this.cycleDAO = cycleDAO;
    }

    public double calcSafeDailyLimit(BudgetCycle cycle) {
        if (cycle == null) return 0.0;

        int remainingDays = cycle.getRemainingDays();
        if (remainingDays <= 0) {
            return cycle.getRemainingBalance();
        }

        return cycle.getRemainingBalance() / remainingDays;
    }

    // SEQUENCE DIAGRAM 3 IMPLEMENTATION: Daily Rollover Management
    public double requestCurrentStatus(BudgetCycle cycle) {
        if (cycle == null) return 0.0;

        LocalDate today = LocalDate.now();
        LocalDate lastRollover = cycle.getLastRolloverDate();

        // Calculate limit dynamically to account for any expenses added today
        double currentLimit = calcSafeDailyLimit(cycle);

        // Step: "Check last login & current balance"
        if (lastRollover == null || lastRollover.isBefore(today)) {
            System.out.println("System: App opened on a new day. Triggering Midnight Rollover...");

            // Step: "Identify unspent funds" & "Recalculate Safe Daily Limit"
            cycle.setSafeDailyLimit(currentLimit);
            cycle.setLastRolloverDate(today);

            // Step: "Update new Safe Daily Limit" -> SQLite Database
            if (cycleDAO != null) {
                cycleDAO.saveCycle(cycle);
                System.out.println("System: Unspent funds rolled over! Saved new Safe Limit to DB: " + currentLimit);
            }
        }

        // Step: "Return new Safe Daily Limit"
        return currentLimit;
    }
}
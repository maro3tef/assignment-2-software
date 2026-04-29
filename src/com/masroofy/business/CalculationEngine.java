package com.masroofy.business;
import com.masroofy.domain.BudgetCycle;

public class CalculationEngine {
    // Sequence Diagram 2: Dynamic Daily Limit View
    public double calcSafeDailyLimit(BudgetCycle cycle) {
        if (cycle == null) return 0.0;

        int remainingDays = cycle.getRemainingDays();
        if (remainingDays <= 0) {
            return cycle.getRemainingBalance();
        }

        return cycle.getRemainingBalance() / remainingDays;
    }
    // Sequence Diagram 3: Daily Rollover Management
    public void triggerMidnightRollover(BudgetCycle cycle) {
        System.out.println("System: Recalculating rollover funds...");
        double newLimit = calcSafeDailyLimit(cycle);
        System.out.println("System: New Safe Daily Limit updated to: " + String.format("%.2f", newLimit));
    }

}

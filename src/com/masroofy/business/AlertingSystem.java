package com.masroofy.business;

public class AlertingSystem {
    // Checks spending against total allowance
    public void checkThreshold(double spent, double totalAllowance) {
        if (totalAllowance <= 0) return;

        double percentageSpent = (spent / totalAllowance) * 100;

        if (percentageSpent >= 100.0) {
            pushNotification("CRITICAL: You have consumed 100% of your budget!");
        } else if (percentageSpent >= 80.0) {
            pushNotification("WARNING: You have consumed " + String.format("%.1f", percentageSpent) + "% of your budget.");
        }
    }

    private void pushNotification(String message) {
        System.out.println("\n[ALERT] " + message + "\n");
    }
}

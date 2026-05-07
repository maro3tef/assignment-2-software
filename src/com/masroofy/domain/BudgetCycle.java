package com.masroofy.domain;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * The type Budget cycle.
 */
public class BudgetCycle {
    private int cycleId;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalAllowance;
    private double remainingBalance;

    // NEW: Added to support Diagram 3 (Daily Rollover Tracking)
    private LocalDate lastRolloverDate;
    private double safeDailyLimit;

    /**
     * Instantiates a new Budget cycle.
     *
     * @param cycleId        the cycle id
     * @param startDate      the start date
     * @param endDate        the end date
     * @param totalAllowance the total allowance
     */
    public BudgetCycle(int cycleId, LocalDate startDate, LocalDate endDate, double totalAllowance) {
        this.cycleId = cycleId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalAllowance = totalAllowance;
        this.remainingBalance = totalAllowance;
    }

    /**
     * Gets remaining days.
     *
     * @return the remaining days
     */
    public int getRemainingDays() {
        LocalDate today = LocalDate.now();
        if (today.isAfter(endDate)) return 0;
        if (today.isBefore(startDate)) return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        return (int) ChronoUnit.DAYS.between(today, endDate) + 1;
    }

    /**
     * Deduct amount.
     *
     * @param amount the amount
     */
    public void deductAmount(double amount) {
        this.remainingBalance -= amount;
    }

    /**
     * Add amount.
     *
     * @param amount the amount
     */
    public void addAmount(double amount) {
        this.remainingBalance += amount;
    }

    /**
     * Gets cycle id.
     *
     * @return the cycle id
     */
    public int getCycleId()              { return cycleId; }

    /**
     * Gets total allowance.
     *
     * @return the total allowance
     */
    public double getTotalAllowance()    { return totalAllowance; }

    /**
     * Gets remaining balance.
     *
     * @return the remaining balance
     */
    public double getRemainingBalance()  { return remainingBalance; }

    /**
     * Gets start date.
     *
     * @return the start date
     */
    public LocalDate getStartDate()      { return startDate; }

    /**
     * Gets end date.
     *
     * @return the end date
     */
    public LocalDate getEndDate()        { return endDate; }

    /**
     * Sets cycle id.
     *
     * @param cycleId the cycle id
     */
    public void setCycleId(int cycleId)  { this.cycleId = cycleId; }

    /**
     * Gets last rollover date.
     *
     * @return the last rollover date
     */
// NEW Getters and Setters
    public LocalDate getLastRolloverDate() { return lastRolloverDate; }

    /**
     * Sets last rollover date.
     *
     * @param lastRolloverDate the last rollover date
     */
    public void setLastRolloverDate(LocalDate lastRolloverDate) { this.lastRolloverDate = lastRolloverDate; }

    /**
     * Gets safe daily limit.
     *
     * @return the safe daily limit
     */
    public double getSafeDailyLimit() { return safeDailyLimit; }

    /**
     * Sets safe daily limit.
     *
     * @param safeDailyLimit the safe daily limit
     */
    public void setSafeDailyLimit(double safeDailyLimit) { this.safeDailyLimit = safeDailyLimit; }
}
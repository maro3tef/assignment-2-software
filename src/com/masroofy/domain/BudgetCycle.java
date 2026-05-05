package com.masroofy.domain;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
 
public class BudgetCycle {
    private int cycleId;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalAllowance;
    private double remainingBalance;
    public BudgetCycle(int cycleId, LocalDate startDate, LocalDate endDate, double totalAllowance) {
        this.cycleId = cycleId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalAllowance = totalAllowance;
        this.remainingBalance = totalAllowance;
    }
    public int getRemainingDays() {
        LocalDate today = LocalDate.now();
        if (today.isAfter(endDate)) return 0;
        if (today.isBefore(startDate)) return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        return (int) ChronoUnit.DAYS.between(today, endDate) + 1;
    }
    public void deductAmount(double amount) {
        this.remainingBalance -= amount;
    }
 
    public void addAmount(double amount) {
        this.remainingBalance += amount;
    }
    public int getCycleId()              { return cycleId; }
    public double getTotalAllowance()    { return totalAllowance; }
    public double getRemainingBalance()  { return remainingBalance; }
    public LocalDate getStartDate()      { return startDate; }
    public LocalDate getEndDate()        { return endDate; }
    public void setCycleId(int cycleId)  { this.cycleId = cycleId; }
}
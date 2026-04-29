package com.masroofy.business;
import com.masroofy.data.IBudgetCycleDAO;
import com.masroofy.data.ITransactionDAO;
import com.masroofy.domain.BudgetCycle;
import com.masroofy.domain.Transaction;

public class ExpenseTracker {
    private ITransactionDAO transactionDAO;
    private IBudgetCycleDAO cycleDAO;
    private AlertingSystem alertingSystem;

    public ExpenseTracker(ITransactionDAO transactionDAO,
                          IBudgetCycleDAO cycleDAO,
                          AlertingSystem alertingSystem) {
        this.transactionDAO = transactionDAO;
        this.cycleDAO = cycleDAO;
        this.alertingSystem = alertingSystem;
    }
    public boolean logTransaction(double amount, int categoryId, String note) {
        BudgetCycle currentCycle = cycleDAO.getCurrentCycle();
        if (currentCycle == null) {
            System.out.println("Error: No active budget cycle found.");
            return false;
        }

        // Generate a random ID for console simulation
        int randomId = (int)(Math.random() * 1000);
        Transaction newExpense = new Transaction(randomId, amount, categoryId, note);

        if (transactionDAO.saveTransaction(newExpense)) {
            currentCycle.deductAmount(amount);
            cycleDAO.saveCycle(currentCycle); // update balance in DB

            // Check thresholds after adding expense
            double spent = currentCycle.getTotalAllowance() - currentCycle.getRemainingBalance();
            alertingSystem.checkThreshold(spent, currentCycle.getTotalAllowance());

            return true;
        }
        return false;
    }
    // Sequence Diagram 5
    public boolean deleteTransaction(int transactionId, double refundAmount) {
        if (transactionDAO.deleteTransaction(transactionId)) {
            BudgetCycle currentCycle = cycleDAO.getCurrentCycle();
            if (currentCycle != null) {
                currentCycle.addAmount(refundAmount);
                cycleDAO.saveCycle(currentCycle); // update balance in DB
            }
            return true;
        }
        return false;
    }
}

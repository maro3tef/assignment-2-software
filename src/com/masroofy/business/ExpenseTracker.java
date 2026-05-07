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
        if (currentCycle == null) return false;

        int randomId = (int)(Math.random() * 1000);
        Transaction newExpense = new Transaction(randomId, amount, categoryId, note);
        newExpense.setCycleId(currentCycle.getCycleId());

        if (transactionDAO.saveTransaction(newExpense)) {
            currentCycle.deductAmount(amount);
            cycleDAO.saveCycle(currentCycle);

            double spent = currentCycle.getTotalAllowance() - currentCycle.getRemainingBalance();
            alertingSystem.checkThreshold(spent, currentCycle.getTotalAllowance());
            return true;
        }
        return false;
    }

    public boolean deleteTransaction(int transactionId, double refundAmount) {
        if (transactionDAO.deleteTransaction(transactionId)) {
            BudgetCycle currentCycle = cycleDAO.getCurrentCycle();
            if (currentCycle != null) {
                currentCycle.addAmount(refundAmount);
                cycleDAO.saveCycle(currentCycle);
            }
            return true;
        }
        return false;
    }

    // NEW: Diagram 5 - Handles balance recalculation when editing an amount
    public boolean editTransaction(Transaction updatedTransaction, double oldAmount) {
        // Step 1: Update the transaction record in the DB
        if (transactionDAO.updateTransaction(updatedTransaction)) {

            BudgetCycle currentCycle = cycleDAO.getCurrentCycle();
            if (currentCycle != null) {
                // Math logic to adjust the cycle's balance
                // E.g. If old expense was $100 and new is $60. Difference is $40 to add back.
                double difference = oldAmount - updatedTransaction.getAmount();
                currentCycle.addAmount(difference);

                // Step 2: Save updated limit/balance
                cycleDAO.saveCycle(currentCycle);
            }
            return true;
        }
        return false;
    }
}
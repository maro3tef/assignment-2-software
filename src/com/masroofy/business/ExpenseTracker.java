package com.masroofy.business;
import com.masroofy.data.IBudgetCycleDAO;
import com.masroofy.data.ITransactionDAO;
import com.masroofy.domain.BudgetCycle;
import com.masroofy.domain.Transaction;

/**
 * The type Expense tracker.
 */
public class ExpenseTracker {
    private ITransactionDAO transactionDAO;
    private IBudgetCycleDAO cycleDAO;
    private AlertingSystem alertingSystem;

    /**
     * Instantiates a new Expense tracker.
     *
     * @param transactionDAO the transaction dao
     * @param cycleDAO       the cycle dao
     * @param alertingSystem the alerting system
     */
    public ExpenseTracker(ITransactionDAO transactionDAO,
                          IBudgetCycleDAO cycleDAO,
                          AlertingSystem alertingSystem) {
        this.transactionDAO = transactionDAO;
        this.cycleDAO = cycleDAO;
        this.alertingSystem = alertingSystem;
    }

    /**
     * Log transaction boolean.
     *
     * @param amount     the amount
     * @param categoryId the category id
     * @param note       the note
     * @return the boolean
     */
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

    /**
     * Delete transaction boolean.
     *
     * @param transactionId the transaction id
     * @param refundAmount  the refund amount
     * @return the boolean
     */
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

    /**
     * Edit transaction boolean.
     *
     * @param updatedTransaction the updated transaction
     * @param oldAmount          the old amount
     * @return the boolean
     */
//   5# Handles balance recalculation when editing an amount
    public boolean editTransaction(Transaction updatedTransaction, double oldAmount) {

        if (transactionDAO.updateTransaction(updatedTransaction)) {

            BudgetCycle currentCycle = cycleDAO.getCurrentCycle();
            if (currentCycle != null) {
                double difference = oldAmount - updatedTransaction.getAmount();
                currentCycle.addAmount(difference);


                cycleDAO.saveCycle(currentCycle);
            }
            return true;
        }
        return false;
    }
}
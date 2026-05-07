package com.masroofy.data;
import com.masroofy.domain.Transaction;
import java.util.List;

/**
 * The interface Transaction dao.
 */
public interface ITransactionDAO {
    /**
     * Save transaction boolean.
     *
     * @param t the t
     * @return the boolean
     */
    boolean saveTransaction(Transaction t);

    /**
     * Delete transaction boolean.
     *
     * @param transactionId the transaction id
     * @return the boolean
     */
    boolean deleteTransaction(int transactionId);

    /**
     * Gets all transactions by cycle.
     *
     * @param cycleId the cycle id
     * @return the all transactions by cycle
     */
    List<Transaction> getAllTransactionsByCycle(int cycleId);

    /**
     * Update transaction boolean.
     *
     * @param t the t
     * @return the boolean
     */

    boolean updateTransaction(Transaction t);
}
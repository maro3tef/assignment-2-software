package com.masroofy.data;
import com.masroofy.domain.Transaction;
import java.util.List;

public interface ITransactionDAO {
    boolean saveTransaction(Transaction t);
    boolean deleteTransaction(int transactionId);
    List<Transaction> getAllTransactionsByCycle(int cycleId);

    // NEW: Added to support Sequence Diagram 5
    boolean updateTransaction(Transaction t);
}
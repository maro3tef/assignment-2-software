package com.masroofy.data;
import com.masroofy.domain.Transaction;
import java.util.List;

// DIP
public interface ITransactionDAO {
    boolean saveTransaction(Transaction t);
    boolean deleteTransaction(int transactionId);
    List<Transaction> getAllTransactionsByCycle(int cycleId);
}

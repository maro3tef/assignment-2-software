package com.masroofy.domain;
import java.time.LocalDateTime;
public class Transaction {
    private int transactionId;
    private int cycleId;
    private double amount;
    private int categoryId;
    private LocalDateTime timestamp;
    private String note;
 
    public Transaction(int transactionId, double amount, int categoryId, String note) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.categoryId = categoryId;
        this.note = note;
        this.timestamp = LocalDateTime.now();
    }
 
    public double getAmount()         { return amount; }
    public int getTransactionId()     { return transactionId; }
    public int getCycleId()           { return cycleId; }
    public int getCategoryId()        { return categoryId; }
    public String getNote()           { return note; }
    public LocalDateTime getTimestamp() { return timestamp; }
 
    public void setCycleId(int cycleId)             { this.cycleId = cycleId; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
 
    public String getTransactionDetails() {
        return "ID: " + transactionId + "     | Amount:        " + amount + "      | Note:         " + note + "     | Date:         " + timestamp;
    }
}
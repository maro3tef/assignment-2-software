package com.masroofy.domain;
import java.time.LocalDateTime;

/**
 * The type Transaction.
 */
public class Transaction {
    private int transactionId;
    private int cycleId;
    private double amount;
    private int categoryId;
    private LocalDateTime timestamp;
    private String note;

    /**
     * Instantiates a new Transaction.
     *
     * @param transactionId the transaction id
     * @param amount        the amount
     * @param categoryId    the category id
     * @param note          the note
     */
    public Transaction(int transactionId, double amount, int categoryId, String note) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.categoryId = categoryId;
        this.note = note;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Gets amount.
     *
     * @return the amount
     */
    public double getAmount()         { return amount; }

    /**
     * Gets transaction id.
     *
     * @return the transaction id
     */
    public int getTransactionId()     { return transactionId; }

    /**
     * Gets cycle id.
     *
     * @return the cycle id
     */
    public int getCycleId()           { return cycleId; }

    /**
     * Gets category id.
     *
     * @return the category id
     */
    public int getCategoryId()        { return categoryId; }

    /**
     * Gets note.
     *
     * @return the note
     */
    public String getNote()           { return note; }

    /**
     * Gets timestamp.
     *
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() { return timestamp; }

    /**
     * Sets cycle id.
     *
     * @param cycleId the cycle id
     */
    public void setCycleId(int cycleId)               { this.cycleId = cycleId; }

    /**
     * Sets timestamp.
     *
     * @param timestamp the timestamp
     */
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    /**
     * Sets amount.
     *
     * @param amount the amount
     */
    public void setAmount(double amount)              { this.amount = amount; }

    /**
     * Sets note.
     *
     * @param note the note
     */
    public void setNote(String note)                  { this.note = note; }

    /**
     * Sets category id.
     *
     * @param categoryId the category id
     */
    public void setCategoryId(int categoryId)         { this.categoryId = categoryId; }

    /**
     * Gets transaction details.
     *
     * @return the transaction details
     */
    public String getTransactionDetails() {
        return "ID: " + transactionId + "  |  Amount: " + String.format("%.2f", amount) + "  |  Note: " + note + "  |  Date: " + timestamp.toLocalDate();
    }
}
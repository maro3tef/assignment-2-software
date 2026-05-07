package com.masroofy.presentation;

import javax.swing.*;
import java.awt.*;
import com.masroofy.business.ExpenseTracker;
import com.masroofy.domain.Transaction;

public class EditTransactionUI extends JFrame {

    private JTextField amountField;
    private JTextField categoryField;
    private JLabel messageLabel;

    private ExpenseTracker expenseTracker;
    private Transaction transaction;
    private HistoryUI historyUI;
    private DashboardUI dashboardUI;
    private double oldAmount;

    public EditTransactionUI(Transaction transaction, ExpenseTracker expenseTracker,
                             HistoryUI historyUI, DashboardUI dashboardUI) {
        this.transaction = transaction;
        this.expenseTracker = expenseTracker;
        this.historyUI = historyUI;
        this.dashboardUI = dashboardUI;

        // We capture the old amount so we know how much to refund/deduct the budget later
        this.oldAmount = transaction.getAmount();

        setTitle("Edit Transaction");
        setSize(300, 250);
        setLayout(new FlowLayout());
        setLocationRelativeTo(null);

        // Pre-fill the details from the selected transaction
        amountField = new JTextField(String.valueOf(transaction.getAmount()), 10);
        categoryField = new JTextField(transaction.getNote(), 10);
        messageLabel = new JLabel(" ");

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        add(new JLabel("Amount"));
        add(amountField);
        add(new JLabel("Category/Note"));
        add(categoryField);
        add(saveBtn);
        add(cancelBtn);
        add(messageLabel);

        saveBtn.addActionListener(e -> saveChanges());
        cancelBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void saveChanges() {
        try {
            double newAmount = Double.parseDouble(amountField.getText().trim());
            String newNote = categoryField.getText().trim();

            if (newAmount <= 0) throw new Exception();

            // Update the object in memory
            transaction.setAmount(newAmount);
            transaction.setNote(newNote);

            // Execute Business logic (Validate and update data -> Update record)
            boolean success = expenseTracker.editTransaction(transaction, oldAmount);

            if (success) {
                messageLabel.setText("Transaction Updated");
                messageLabel.setForeground(Color.GREEN);

                // Triggers "Recalculate Safe Daily limit" via the UI reload
                if (historyUI != null) historyUI.refreshHistory();
                if (dashboardUI != null) dashboardUI.refreshAfterTransaction();

                Timer timer = new Timer(1000, e -> dispose());
                timer.setRepeats(false);
                timer.start();
            } else {
                messageLabel.setText("Error updating");
                messageLabel.setForeground(Color.RED);
            }
        } catch (Exception ex) {
            messageLabel.setText("Invalid input");
            messageLabel.setForeground(Color.RED);
        }
    }
}
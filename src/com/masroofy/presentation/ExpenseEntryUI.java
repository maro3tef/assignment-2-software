package com.masroofy.presentation;

import javax.swing.*;
import java.awt.*;
import com.masroofy.business.ExpenseTracker;
import com.masroofy.domain.BudgetCycle;
import com.masroofy.domain.UserProfile;

/**
 * The type Expense entry ui.
 */
public class ExpenseEntryUI extends JFrame {

    private JTextField amountField;
    private JTextField categoryField;
    private JLabel messageLabel;

    private ExpenseTracker expenseTracker;
    private BudgetCycle currentCycle;
    private DashboardUI dashboardUI;

    /**
     * Instantiates a new Expense entry ui.
     *
     * @param userProfile    the user profile
     * @param currentCycle   the current cycle
     * @param dashboardUI    the dashboard ui
     * @param expenseTracker the expense tracker
     */
 public ExpenseEntryUI(UserProfile userProfile, BudgetCycle currentCycle,
                          DashboardUI dashboardUI, ExpenseTracker expenseTracker) {

        this.expenseTracker = expenseTracker;
        this.currentCycle = currentCycle;
        this.dashboardUI = dashboardUI;

        setTitle("Add Expense");
        setSize(300, 250);
        setLayout(new FlowLayout());
        setLocationRelativeTo(null);

        amountField = new JTextField(10);
        categoryField = new JTextField(10);
        messageLabel = new JLabel(" ");

        JButton addBtn = new JButton("Add");
        JButton cancelBtn = new JButton("Cancel");

        add(new JLabel("Amount"));
        add(amountField);
        add(new JLabel("Category/Note"));
        add(categoryField);
        add(addBtn);
        add(cancelBtn);
        add(messageLabel);

        addBtn.addActionListener(e -> captureExpenseDetails());
        cancelBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    /**
     * Capture expense details.
     */
    public void captureExpenseDetails() {
        String amountText = amountField.getText().trim();
        String note = categoryField.getText().trim();
        if (amountText.isEmpty() || note.isEmpty()) {
            showErrorMessage("All fields required");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) throw new Exception();
        } catch (Exception e) {
            showErrorMessage("Invalid amount");
            return;
        }

        boolean success = expenseTracker.logTransaction(amount, 1, note);

        if (success) {
            showSuccessMessage("Expense added");
            if (dashboardUI != null) {
                dashboardUI.refreshAfterTransaction();
            }
            Timer timer = new Timer(1000, e -> dispose());
            timer.setRepeats(false);
            timer.start();
        } else {
            showErrorMessage("Error saving expense");
        }
    }

    /**
     * Show success message.
     *
     * @param msg the msg
     */
    public void showSuccessMessage(String msg) {
        messageLabel.setText(msg);
        messageLabel.setForeground(Color.GREEN);
    }

    /**
     * Show error message.
     *
     * @param msg the msg
     */
    public void showErrorMessage(String msg) {
        messageLabel.setText(msg);
        messageLabel.setForeground(Color.RED);
    }
}
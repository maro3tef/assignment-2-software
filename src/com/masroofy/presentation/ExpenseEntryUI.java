package com.masroofy.presentation;

import javax.swing.*;
import java.awt.*;
import com.masroofy.business.ExpenseTracker;
import com.masroofy.domain.BudgetCycle;
import com.masroofy.domain.UserProfile;

public class ExpenseEntryUI extends JFrame {

    private JTextField amountField;
    private JTextField categoryField;
    private JLabel messageLabel;

    private ExpenseTracker expenseTracker;
    private BudgetCycle currentCycle;
    private DashboardUI dashboardUI;

    public ExpenseEntryUI(UserProfile userProfile, BudgetCycle currentCycle, DashboardUI dashboardUI) {

        this.expenseTracker = new ExpenseTracker();
        this.currentCycle = currentCycle;
        this.dashboardUI = dashboardUI;

        setTitle("Add Expense");
        setSize(300, 250);
        setLayout(new FlowLayout());

        amountField = new JTextField(10);
        categoryField = new JTextField(10);
        messageLabel = new JLabel(" ");

        JButton addBtn = new JButton("Add");
        JButton cancelBtn = new JButton("Cancel");

        add(new JLabel("Amount"));
        add(amountField);
        add(new JLabel("Category"));
        add(categoryField);
        add(addBtn);
        add(cancelBtn);
        add(messageLabel);

        addBtn.addActionListener(e -> captureExpenseDetails());
        cancelBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    public void captureExpenseDetails() {
        String amountText = amountField.getText().trim();
        String category = categoryField.getText().trim();

        if (amountText.isEmpty() || category.isEmpty()) {
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

        boolean success = expenseTracker.addExpense(amount, category);

        if (success) {
            showSuccessMessage("Expense added");
            if (dashboardUI != null) {
                dashboardUI.refreshAfterTransaction();
            }
        } else {
            showErrorMessage("Error saving expense");
        }
    }

    public void showSuccessMessage(String msg) {
        messageLabel.setText(msg);
        messageLabel.setForeground(Color.GREEN);
    }

    public void showErrorMessage(String msg) {
        messageLabel.setText(msg);
        messageLabel.setForeground(Color.RED);
    }
}
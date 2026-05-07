package com.masroofy.presentation;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import com.masroofy.business.CalculationEngine;
import com.masroofy.business.CycleManager;
import com.masroofy.business.ExpenseTracker;
import com.masroofy.data.ITransactionDAO;
import com.masroofy.domain.BudgetCycle;
import com.masroofy.domain.UserProfile;

public class AuthUI extends JFrame {

    private JPasswordField pinField;
    private JLabel messageLabel;
    private UserProfile userProfile;

    private CycleManager cycleManager;
    private CalculationEngine calcEngine;
    private ExpenseTracker expenseTracker;
    private ITransactionDAO transactionDAO;

    public AuthUI(UserProfile userProfile, CycleManager cycleManager, CalculationEngine calcEngine,
                  ExpenseTracker expenseTracker, ITransactionDAO transactionDAO) {

        this.userProfile = userProfile;
        this.cycleManager = cycleManager;
        this.calcEngine = calcEngine;
        this.expenseTracker = expenseTracker;
        this.transactionDAO = transactionDAO;

        setTitle("Login");
        setSize(300, 200);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        pinField = new JPasswordField(10);
        messageLabel = new JLabel(" ");

        JButton btn = new JButton("Login");

        add(new JLabel("PIN (Hint: 1234):"));
        add(pinField);
        add(btn);
        add(messageLabel);

        btn.addActionListener(e -> verify());

        setVisible(true);
    }

    public void PromptForPIN() {
        setVisible(true);
    }

    private void verify() {
        String pin = new String(pinField.getPassword());

        if (userProfile.verifyPIN(pin)) {
            // Check if there are any existing budget cycles
            List<BudgetCycle> history = cycleManager.getCycleHistory();

            if (history == null || history.isEmpty()) {
                // Database is empty. Route user to Setup Initial Cycle UI
                new SetupCycleUI(userProfile, cycleManager, calcEngine, expenseTracker, transactionDAO);
            } else {
                // Existing cycle found. Route straight to Dashboard
                new DashboardUI(userProfile, cycleManager, calcEngine, expenseTracker, transactionDAO);
            }
            dispose(); // Close Auth window
        } else {
            ShowValidationError("Wrong PIN");
        }
    }

    public void ShowValidationError(String msg) {
        messageLabel.setText(msg);
        messageLabel.setForeground(Color.RED);
    }
}
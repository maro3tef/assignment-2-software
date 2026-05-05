package com.masroofy.presentation;

import javax.swing.*;
import java.awt.*;

import com.masroofy.business.CalculationEngine;
import com.masroofy.business.CycleManager;
import com.masroofy.business.ExpenseTracker;
import com.masroofy.data.ITransactionDAO;
import com.masroofy.domain.UserProfile;

public class AuthUI extends JFrame {

    private JPasswordField pinField;
    private JLabel messageLabel;
    private UserProfile userProfile;

    // FIX: Added dependencies to pass down to DashboardUI
    private CycleManager cycleManager;
    private CalculationEngine calcEngine;
    private ExpenseTracker expenseTracker;
    private ITransactionDAO transactionDAO;

    // FIX: Updated constructor to accept business dependencies
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
        setLocationRelativeTo(null); // FIX: Centers the window on screen

        pinField = new JPasswordField(10);
        messageLabel = new JLabel(" ");

        JButton btn = new JButton("Login");

        add(new JLabel("PIN (Hint: 1234):")); // FIX: Added hint for easier testing
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

        // FIX: Changed VerifyPIN to verifyPIN (case-sensitive fix)
        if (userProfile.verifyPIN(pin)) {
            // FIX: Passed dependencies to DashboardUI
            new DashboardUI(userProfile, cycleManager, calcEngine, expenseTracker, transactionDAO);
            dispose();
        } else {
            ShowValidationError("Wrong PIN");
        }
    }

    public void ShowValidationError(String msg) {
        messageLabel.setText(msg);
        messageLabel.setForeground(Color.RED);
    }
}
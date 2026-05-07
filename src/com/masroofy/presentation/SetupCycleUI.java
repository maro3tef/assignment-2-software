package com.masroofy.presentation;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.masroofy.business.CalculationEngine;
import com.masroofy.business.CycleManager;
import com.masroofy.business.ExpenseTracker;
import com.masroofy.data.ITransactionDAO;
import com.masroofy.domain.UserProfile;

public class SetupCycleUI extends JFrame {

    private JTextField amountField;
    private JTextField startDateField;
    private JTextField endDateField;
    private JLabel messageLabel;

    private UserProfile userProfile;
    private CycleManager cycleManager;
    private CalculationEngine calcEngine;
    private ExpenseTracker expenseTracker;
    private ITransactionDAO transactionDAO;

    public SetupCycleUI(UserProfile userProfile, CycleManager cycleManager, CalculationEngine calcEngine,
                        ExpenseTracker expenseTracker, ITransactionDAO transactionDAO) {

        this.userProfile = userProfile;
        this.cycleManager = cycleManager;
        this.calcEngine = calcEngine;
        this.expenseTracker = expenseTracker;
        this.transactionDAO = transactionDAO;

        setTitle("Set Initial Budget Cycle");
        setSize(350, 250);
        setLayout(new GridLayout(5, 2, 10, 10)); // Grid layout for neat form alignment
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        amountField = new JTextField();
        // Pre-fill with today's date to help the user with formatting
        startDateField = new JTextField(LocalDate.now().toString());
        endDateField = new JTextField(LocalDate.now().plusDays(30).toString());
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);

        JButton saveBtn = new JButton("Start Cycle");

        add(new JLabel("  Total Amount:"));
        add(amountField);
        add(new JLabel("  Start Date (YYYY-MM-DD):"));
        add(startDateField);
        add(new JLabel("  End Date (YYYY-MM-DD):"));
        add(endDateField);
        add(new JLabel("")); // Spacer
        add(saveBtn);
        add(new JLabel("")); // Spacer
        add(messageLabel);

        saveBtn.addActionListener(e -> captureCycleDetails());

        setVisible(true);
    }

    private void captureCycleDetails() {
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            LocalDate start = LocalDate.parse(startDateField.getText().trim());
            LocalDate end = LocalDate.parse(endDateField.getText().trim());

            if (amount <= 0) {
                messageLabel.setText("Amount must be > 0");
                return;
            }
            if (end.isBefore(start)) {
                messageLabel.setText("End date must be after start");
                return;
            }

            // Calls Step 2 in Sequence Diagram 1
            boolean success = cycleManager.initCycle(start, end, amount);

            if (success) {
                // Proceed to Dashboard
                new DashboardUI(userProfile, cycleManager, calcEngine, expenseTracker, transactionDAO);
                dispose(); // Close setup window
            } else {
                messageLabel.setText("Database error saving cycle.");
            }

        } catch (NumberFormatException ex) {
            messageLabel.setText("Invalid amount format.");
        } catch (DateTimeParseException ex) {
            messageLabel.setText("Invalid date. Use YYYY-MM-DD.");
        }
    }
}
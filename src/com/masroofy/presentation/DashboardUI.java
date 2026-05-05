package com.masroofy.presentation;

import javax.swing.*;
import java.awt.*;
import com.masroofy.business.CycleManager;
import com.masroofy.business.CalculationEngine;
import com.masroofy.business.ExpenseTracker;
import com.masroofy.data.ITransactionDAO;
import com.masroofy.domain.BudgetCycle;
import com.masroofy.domain.UserProfile;
import java.util.List;

public class DashboardUI extends JFrame {

    private JLabel balanceLabel;
    private JLabel dailyLimitLabel;

    private CycleManager cycleManager;
    private CalculationEngine calculationEngine;
    private ExpenseTracker expenseTracker; // FIX: Added to pass to ExpenseEntryUI
    private ITransactionDAO transactionDAO; // FIX: Added to pass to HistoryUI

    private UserProfile userProfile;
    private BudgetCycle currentCycle;

    // FIX: Constructor now accepts dependencies instead of trying to instantiate them without args
    public DashboardUI(UserProfile userProfile, CycleManager cycleManager,
                       CalculationEngine calculationEngine, ExpenseTracker expenseTracker,
                       ITransactionDAO transactionDAO) {

        this.userProfile = userProfile;
        this.cycleManager = cycleManager;
        this.calculationEngine = calculationEngine;
        this.expenseTracker = expenseTracker;
        this.transactionDAO = transactionDAO;

        setTitle("Dashboard");
        setSize(400, 300);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // FIX: Center on screen

        balanceLabel = new JLabel("Remaining Balance: 0");
        dailyLimitLabel = new JLabel("Daily Limit: 0");

        JButton addBtn = new JButton("Add Expense");
        JButton historyBtn = new JButton("History");

        add(balanceLabel);
        add(dailyLimitLabel);
        add(addBtn);
        add(historyBtn);

        // FIX: Pass appropriate dependencies to child UIs
        addBtn.addActionListener(e -> new ExpenseEntryUI(userProfile, currentCycle, this, expenseTracker));

        historyBtn.addActionListener(e -> {
            if (currentCycle != null) {
                new HistoryUI(userProfile, transactionDAO).show(currentCycle.getCycleId());
            } else {
                JOptionPane.showMessageDialog(this, "No active cycle available.");
            }
        });

        loadCycle();

        setVisible(true);
    }

    private void loadCycle() {
        // FIX: Ensure cycle manager is getting the latest cycle from DB history
        List<BudgetCycle> cycles = cycleManager.getCycleHistory();
        if (!cycles.isEmpty()) {
            currentCycle = cycles.get(0); // Assuming the first item is the latest
        }

        if (currentCycle != null) {
            // FIX: Changed CalcSafeDailyLimit to calcSafeDailyLimit (case-sensitive fix)
            double limit = calculationEngine.calcSafeDailyLimit(currentCycle);
            UpdateDailyLimitDisplay(limit);
            ShowRemainingBalance(currentCycle.getRemainingBalance());
        }
    }

    public void UpdateDailyLimitDisplay(double limit) {
        dailyLimitLabel.setText("Daily Limit: " + String.format("%.2f", limit)); // FIX: Formatting to 2 decimal places
    }

    public void ShowRemainingBalance(double amount) {
        balanceLabel.setText("Remaining Balance: " + String.format("%.2f", amount)); // FIX: Formatting
    }

    public void refreshAfterTransaction() {
        loadCycle();
    }
}
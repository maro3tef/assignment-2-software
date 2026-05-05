package com.masroofy.presentation;

import javax.swing.*;
import java.awt.*;
import com.masroofy.business.CycleManager;
import com.masroofy.business.CalculationEngine;
import com.masroofy.domain.BudgetCycle;
import com.masroofy.domain.UserProfile;

public class DashboardUI extends JFrame {

    private JLabel balanceLabel;
    private JLabel dailyLimitLabel;

    private CycleManager cycleManager;
    private CalculationEngine calculationEngine;
    private UserProfile userProfile;
    private BudgetCycle currentCycle;

    public DashboardUI(UserProfile userProfile) {
        this.userProfile = userProfile;
        this.cycleManager = new CycleManager();
        this.calculationEngine = new CalculationEngine();

        setTitle("Dashboard");
        setSize(400, 300);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        balanceLabel = new JLabel("Remaining Balance: 0");
        dailyLimitLabel = new JLabel("Daily Limit: 0");

        JButton addBtn = new JButton("Add Expense");
        JButton historyBtn = new JButton("History");

        add(balanceLabel);
        add(dailyLimitLabel);
        add(addBtn);
        add(historyBtn);

        addBtn.addActionListener(e -> new ExpenseEntryUI(userProfile, currentCycle, this));
        historyBtn.addActionListener(e -> new HistoryUI(userProfile).show(currentCycle.getCycleId()));

        loadCycle();

        setVisible(true);
    }

    private void loadCycle() {
        currentCycle = cycleManager.getCurrentCycle();
        if (currentCycle != null) {
            double limit = calculationEngine.CalcSafeDailyLimit(currentCycle);
            UpdateDailyLimitDisplay(limit);
            ShowRemainingBalance(currentCycle.getRemainingBalance());
        }
    }

    public void UpdateDailyLimitDisplay(double limit) {
        dailyLimitLabel.setText("Daily Limit: " + limit);
    }

    public void ShowRemainingBalance(double amount) {
        balanceLabel.setText("Remaining Balance: " + amount);
    }

    public void refreshAfterTransaction() {
        loadCycle();
    }
}
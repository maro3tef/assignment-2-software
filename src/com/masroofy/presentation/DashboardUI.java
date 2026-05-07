package com.masroofy.presentation;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.masroofy.business.CycleManager;
import com.masroofy.business.CalculationEngine;
import com.masroofy.business.ExpenseTracker;
import com.masroofy.data.ITransactionDAO;
import com.masroofy.domain.BudgetCycle;
import com.masroofy.domain.Transaction;
import com.masroofy.domain.UserProfile;

/**
 * The type Dashboard ui.
 */
public class DashboardUI extends JFrame {

    private JLabel balanceLabel;
    private JLabel dailyLimitLabel;
    private PieChartPanel chartPanel; // NEW: The pie chart component

    private CycleManager cycleManager;
    private CalculationEngine calculationEngine;
    private ExpenseTracker expenseTracker;
    private ITransactionDAO transactionDAO;

    private UserProfile userProfile;
    private BudgetCycle currentCycle;

    /**
     * Instantiates a new Dashboard ui.
     *
     * @param userProfile       the user profile
     * @param cycleManager      the cycle manager
     * @param calculationEngine the calculation engine
     * @param expenseTracker    the expense tracker
     * @param transactionDAO    the transaction dao
     */
    public DashboardUI(UserProfile userProfile, CycleManager cycleManager,
                       CalculationEngine calculationEngine, ExpenseTracker expenseTracker,
                       ITransactionDAO transactionDAO) {

        this.userProfile = userProfile;
        this.cycleManager = cycleManager;
        this.calculationEngine = calculationEngine;
        this.expenseTracker = expenseTracker;
        this.transactionDAO = transactionDAO;

        setTitle("Dashboard");
        setSize(450, 450); // Increased size to fit the chart
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        balanceLabel = new JLabel("Remaining Balance: 0");
        dailyLimitLabel = new JLabel("Daily Limit: 0");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        dailyLimitLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JButton addBtn = new JButton("Add Expense");
        JButton historyBtn = new JButton("History");
        JButton resetBtn = new JButton("Reset All Data");
        resetBtn.setForeground(Color.RED);

        chartPanel = new PieChartPanel();


        add(balanceLabel);
        add(dailyLimitLabel);
        add(chartPanel);
        add(addBtn);
        add(historyBtn);
        add(resetBtn);

        addBtn.addActionListener(e -> new ExpenseEntryUI(userProfile, currentCycle, this, expenseTracker));

        historyBtn.addActionListener(e -> {
            if (currentCycle != null) {
                new HistoryUI(userProfile, transactionDAO,expenseTracker,this).show(currentCycle.getCycleId());
            } else {
                JOptionPane.showMessageDialog(this, "No active cycle available.");
            }
        });

        resetBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to permanently delete all budget cycles and transactions?",
                    "Confirm Reset", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = cycleManager.triggerDataReset();
                if (success) {
                    JOptionPane.showMessageDialog(this, "All data cleared successfully.", "Reset Complete", JOptionPane.INFORMATION_MESSAGE);
                    new SetupCycleUI(userProfile, cycleManager, calculationEngine, expenseTracker, transactionDAO);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to clear data. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        loadCycle();

        setVisible(true);
    }

    private void loadCycle() {
        List<BudgetCycle> cycles = cycleManager.getCycleHistory();
        currentCycle = cycles.isEmpty() ? null : cycles.get(0);

        if (currentCycle != null) {
            double limit = calculationEngine.calcSafeDailyLimit(currentCycle);
            UpdateDailyLimitDisplay(limit);
            ShowRemainingBalance(currentCycle.getRemainingBalance());


            updateChartData();
        }
    }

    private void updateChartData() {
        List<Transaction> transactions = transactionDAO.getAllTransactionsByCycle(currentCycle.getCycleId());
        Map<String, Double> categoryTotals = new HashMap<>();

        for (Transaction t : transactions) {

            String categoryName = t.getNote() != null ? t.getNote() : "Uncategorized";
            categoryTotals.put(categoryName, categoryTotals.getOrDefault(categoryName, 0.0) + t.getAmount());
        }

        chartPanel.setTotals(categoryTotals);
    }

    /**
     * Update daily limit display.
     *
     * @param limit the limit
     */
    public void UpdateDailyLimitDisplay(double limit) {
        dailyLimitLabel.setText("Daily Limit: " + String.format("%.2f", limit));
    }

    /**
     * Show remaining balance.
     *
     * @param amount the amount
     */
    public void ShowRemainingBalance(double amount) {
        balanceLabel.setText("Remaining Balance: " + String.format("%.2f", amount));
    }

    /**
     * Refresh after transaction.
     */
    public void refreshAfterTransaction() {
        loadCycle(); // Reloads cycle math and refreshes the chart data
    }
}
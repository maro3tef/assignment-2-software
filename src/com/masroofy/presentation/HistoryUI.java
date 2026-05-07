package com.masroofy.presentation;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import com.masroofy.domain.Transaction;
import com.masroofy.domain.UserProfile;
import com.masroofy.data.ITransactionDAO;
import com.masroofy.business.ExpenseTracker;

/**
 * The type History ui.
 */
public class HistoryUI extends JFrame {

    private DefaultListModel<Transaction> listModel;
    private JList<Transaction> transactionList;

    private UserProfile userProfile;
    private ITransactionDAO transactionDAO;
    private ExpenseTracker expenseTracker;
    private DashboardUI dashboardUI;

    private int currentCycleId;

    /**
     * Instantiates a new History ui.
     *
     * @param userProfile    the user profile
     * @param transactionDAO the transaction dao
     * @param expenseTracker the expense tracker
     * @param dashboardUI    the dashboard ui
     */
    public HistoryUI(UserProfile userProfile, ITransactionDAO transactionDAO,
                     ExpenseTracker expenseTracker, DashboardUI dashboardUI) {
        this.userProfile = userProfile;
        this.transactionDAO = transactionDAO;
        this.expenseTracker = expenseTracker;
        this.dashboardUI = dashboardUI;

        setTitle("Transaction History");
        setSize(500, 400);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);

        listModel = new DefaultListModel<>();
        transactionList = new JList<>(listModel);
        transactionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        transactionList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Transaction) {
                    setText(((Transaction)value).getTransactionDetails());
                }
                return this;
            }
        });

        add(new JScrollPane(transactionList), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");

        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        add(btnPanel, BorderLayout.SOUTH);

        editBtn.addActionListener(e -> {
            Transaction selected = transactionList.getSelectedValue();
            if (selected != null) {
                new EditTransactionUI(selected, expenseTracker, this, dashboardUI);
            } else {
                JOptionPane.showMessageDialog(this, "Please select an entry to edit.");
            }
        });

        deleteBtn.addActionListener(e -> {
            Transaction selected = transactionList.getSelectedValue();
            if (selected != null) {
                int confirm = JOptionPane.showConfirmDialog(this, "Delete this transaction?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = expenseTracker.deleteTransaction(selected.getTransactionId(), selected.getAmount());
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Transaction deleted successfully.");
                        refreshHistory();
                        dashboardUI.refreshAfterTransaction();
                    } else {
                        JOptionPane.showMessageDialog(this, "Error deleting.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an entry to delete.");
            }
        });
    }

    /**
     * Show.
     *
     * @param cycleId the cycle id
     */
    public void show(int cycleId) {
        this.currentCycleId = cycleId;
        refreshHistory();
        setVisible(true);
    }

    /**
     * Refresh history.
     */
    public void refreshHistory() {
        listModel.clear();
        List<Transaction> list = transactionDAO.getAllTransactionsByCycle(currentCycleId);
        if (list != null && !list.isEmpty()) {
            for (Transaction t : list) {
                listModel.addElement(t);
            }
        }
    }
}
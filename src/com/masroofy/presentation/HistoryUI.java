package com.masroofy.presentation;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import com.masroofy.domain.Transaction;
import com.masroofy.domain.UserProfile;
import com.masroofy.data.ITransactionDAO; // FIX: Import DAO

public class HistoryUI extends JFrame {

    private JTextArea area;
    private UserProfile userProfile;
    private ITransactionDAO transactionDAO; // FIX: Added DAO dependency

    // FIX: Constructor requires ITransactionDAO now
    public HistoryUI(UserProfile userProfile, ITransactionDAO transactionDAO) {
        this.userProfile = userProfile;
        this.transactionDAO = transactionDAO;

        setTitle("History");
        setSize(400, 300);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null); // FIX: Center on screen

        area = new JTextArea();
        area.setEditable(false); // FIX: Make history read-only
        add(new JScrollPane(area), BorderLayout.CENTER);
    }

    public void getTransactionHistory(int cycleId) {
        // FIX: Stop calling userProfile for history. Use the DAO instead.
        List<Transaction> list = transactionDAO.getAllTransactionsByCycle(cycleId);
        renderList(list);
    }

    public void renderList(List<Transaction> list) {
        area.setText("");
        if (list == null || list.isEmpty()) {
            area.setText("No transactions found for this cycle.");
            return;
        }

        for (Transaction t : list) {
            area.append(t.getTransactionDetails() + "\n");
        }
    }

    public void show(int cycleId) {
        getTransactionHistory(cycleId);
        setVisible(true);
    }
}
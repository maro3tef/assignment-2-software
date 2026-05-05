package com.masroofy.presentation;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import com.masroofy.domain.Transaction;
import com.masroofy.domain.UserProfile;

public class HistoryUI extends JFrame {

    private JTextArea area;
    private UserProfile userProfile;

    public HistoryUI(UserProfile userProfile) {
        this.userProfile = userProfile;

        setTitle("History");
        setSize(400, 300);
        setLayout(new BorderLayout());

        area = new JTextArea();
        add(new JScrollPane(area), BorderLayout.CENTER);
    }

    public void getTransactionHistory(int cycleId) {
        List<Transaction> list = userProfile.getTransactionHistory(cycleId);
        renderList(list);
    }

    public void renderList(List<Transaction> list) {
        area.setText("");
        if (list == null || list.isEmpty()) {
            area.setText("No transactions");
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
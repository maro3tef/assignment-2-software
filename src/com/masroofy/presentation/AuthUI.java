package com.masroofy.presentation;

import javax.swing.*;
import java.awt.*;
import com.masroofy.domain.UserProfile;

public class AuthUI extends JFrame {

    private JPasswordField pinField;
    private JLabel messageLabel;
    private UserProfile userProfile;

    public AuthUI(UserProfile userProfile) {

        this.userProfile = userProfile;

        setTitle("Login");
        setSize(300, 200);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pinField = new JPasswordField(10);
        messageLabel = new JLabel(" ");

        JButton btn = new JButton("Login");

        add(new JLabel("PIN"));
        add(pinField);
        add(btn);
        add(messageLabel);

        btn.addActionListener(e -> verify());

        setVisible(true);
    }

    public void PromptForPIN() {
        setVisible(true);
    }

//    private void verify() {
//        String pin = new String(pinField.getPassword());
//
//        if (userProfile.VerifyPIN(pin)) {
//            new DashboardUI(userProfile);
//            dispose();
//        } else {
//            ShowValidationError("Wrong PIN");
//        }
//    }

    public void ShowValidationError(String msg) {
        messageLabel.setText(msg);
        messageLabel.setForeground(Color.RED);
    }
}
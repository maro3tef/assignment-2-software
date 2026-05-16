package com.masroofy.presentation.fx;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class CycleSetupView extends VBox {

    public CycleSetupView(SceneManager sceneManager) {
        setSpacing(0);

        Label header = new Label("Set Up Your Budget");
        header.getStyleClass().add("page-header");

        Label subheader = new Label(
            "You don't have an active budget cycle. Create one to get started."
        );
        subheader.getStyleClass().add("page-subheader");

        VBox form = new VBox();
        form.getStyleClass().add("form-container");
        form.setAlignment(Pos.TOP_LEFT);

        // Total Allowance
        Label amountLabel = new Label("Total Budget Amount");
        amountLabel.getStyleClass().add("form-label");

        TextField amountField = new TextField();
        amountField.getStyleClass().add("form-field");
        amountField.setPromptText("e.g. 2000.00");

        // Start Date
        Label startLabel = new Label("Start Date");
        startLabel.getStyleClass().add("form-label");

        DatePicker startPicker = new DatePicker(LocalDate.now());
        startPicker.getStyleClass().add("form-field");
        startPicker.setMaxWidth(Double.MAX_VALUE);

        // End Date
        Label endLabel = new Label("End Date");
        endLabel.getStyleClass().add("form-label");

        DatePicker endPicker = new DatePicker(LocalDate.now().plusDays(30));
        endPicker.getStyleClass().add("form-field");
        endPicker.setMaxWidth(Double.MAX_VALUE);

        // Message
        Label messageLabel = new Label();
        messageLabel.setVisible(false);

        // Buttons
        HBox buttonRow = new HBox();
        buttonRow.setSpacing(12);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        Button startBtn = new Button("Start Budget Cycle");
        startBtn.getStyleClass().add("primary-btn");

        buttonRow.getChildren().add(startBtn);

        form.getChildren().addAll(
            amountLabel, amountField,
            startLabel, startPicker,
            endLabel, endPicker,
            messageLabel,
            buttonRow
        );

        VBox.setVgrow(form, Priority.ALWAYS);
        getChildren().addAll(header, subheader, form);

        startBtn.setOnAction(e -> {
            String amountText = amountField.getText().trim();
            LocalDate startDate = startPicker.getValue();
            LocalDate endDate = endPicker.getValue();

            if (amountText.isEmpty()) {
                messageLabel.setText("Please enter a budget amount.");
                messageLabel.getStyleClass().setAll("error-label");
                messageLabel.setVisible(true);
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountText);
                if (amount <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                messageLabel.setText("Please enter a valid positive amount.");
                messageLabel.getStyleClass().setAll("error-label");
                messageLabel.setVisible(true);
                return;
            }

            if (startDate == null || endDate == null) {
                messageLabel.setText("Please select both start and end dates.");
                messageLabel.getStyleClass().setAll("error-label");
                messageLabel.setVisible(true);
                return;
            }

            if (endDate.isBefore(startDate)) {
                messageLabel.setText("End date must be after start date.");
                messageLabel.getStyleClass().setAll("error-label");
                messageLabel.setVisible(true);
                return;
            }

            boolean success = sceneManager.getCycleManager().initCycle(startDate, endDate, amount);
            if (success) {
                sceneManager.navigateToDashboard();
            } else {
                messageLabel.setText("Failed to create budget cycle. Check database.");
                messageLabel.getStyleClass().setAll("error-label");
                messageLabel.setVisible(true);
            }
        });
    }
}

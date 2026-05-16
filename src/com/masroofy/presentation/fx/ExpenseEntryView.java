package com.masroofy.presentation.fx;

import com.masroofy.domain.Transaction;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ExpenseEntryView extends VBox {

    private final SceneManager sceneManager;
    private final Transaction editTarget;

    public ExpenseEntryView(SceneManager sceneManager, Transaction editTarget) {
        this.sceneManager = sceneManager;
        this.editTarget = editTarget;

        setSpacing(0);

        boolean isEdit = editTarget != null;

        Label header = new Label(isEdit ? "Edit Expense" : "Add Expense");
        header.getStyleClass().add("page-header");

        Label subheader = new Label(
            isEdit ? "Update the transaction details below"
                   : "Log a new expense to your current budget cycle"
        );
        subheader.getStyleClass().add("page-subheader");

        VBox form = new VBox();
        form.getStyleClass().add("form-container");

        // Amount
        Label amountLabel = new Label("Amount");
        amountLabel.getStyleClass().add("form-label");

        TextField amountField = new TextField();
        amountField.getStyleClass().add("form-field");
        amountField.setPromptText("0.00");
        if (isEdit) {
            amountField.setText(String.format("%.2f", editTarget.getAmount()));
        }

        // Category
        Label categoryLabel = new Label("Category");
        categoryLabel.getStyleClass().add("form-label");

        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getStyleClass().add("form-field");
        categoryCombo.setPromptText("Select a category");
        categoryCombo.getItems().addAll(
            "Food & Dining", "Transportation", "Shopping",
            "Bills & Utilities", "Entertainment", "Other"
        );
        categoryCombo.setMaxWidth(Double.MAX_VALUE);
        if (isEdit) {
            String currentCat = SceneManager.CATEGORY_NAMES
                .getOrDefault(editTarget.getCategoryId(), "Other");
            categoryCombo.setValue(currentCat);
        }

        // Note
        Label noteLabel = new Label("Note (optional)");
        noteLabel.getStyleClass().add("form-label");

        TextField noteField = new TextField();
        noteField.getStyleClass().add("form-field");
        noteField.setPromptText("e.g. Grocery run");
        if (isEdit) {
            noteField.setText(editTarget.getNote());
        }

        // Message
        Label messageLabel = new Label();
        messageLabel.setVisible(false);

        // Buttons
        HBox buttonRow = new HBox();
        buttonRow.setSpacing(12);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        Button saveBtn = new Button(isEdit ? "Save Changes" : "Add Expense");
        saveBtn.getStyleClass().add("primary-btn");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("secondary-btn");
        cancelBtn.setOnAction(e -> sceneManager.navigateToDashboard());

        buttonRow.getChildren().addAll(saveBtn, cancelBtn);

        form.getChildren().addAll(
            amountLabel, amountField,
            categoryLabel, categoryCombo,
            noteLabel, noteField,
            messageLabel,
            buttonRow
        );

        VBox.setVgrow(form, Priority.ALWAYS);
        form.setAlignment(Pos.TOP_LEFT);

        getChildren().addAll(header, subheader, form);

        // --- Save Action ---
        saveBtn.setOnAction(e -> {
            String amountText = amountField.getText().trim();
            String categoryName = categoryCombo.getValue();
            String note = noteField.getText().trim();

            if (amountText.isEmpty() || categoryName == null) {
                messageLabel.setText("Please fill in amount and category.");
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

            int categoryId = SceneManager.CATEGORY_IDS.getOrDefault(categoryName, 6);

            boolean success;
            if (isEdit) {
                double oldAmount = editTarget.getAmount();
                editTarget.setAmount(amount);
                editTarget.setCategoryId(categoryId);
                editTarget.setNote(note);
                success = sceneManager.getExpenseTracker().editTransaction(editTarget, oldAmount);
            } else {
                success = sceneManager.getExpenseTracker().logTransaction(amount, categoryId, note);
            }

            if (success) {
                messageLabel.setText(isEdit ? "Transaction updated!" : "Expense added!");
                messageLabel.getStyleClass().setAll("success-label");
                messageLabel.setVisible(true);

                sceneManager.navigateToHistory();
            } else {
                messageLabel.setText("Error saving transaction. Is there an active cycle?");
                messageLabel.getStyleClass().setAll("error-label");
                messageLabel.setVisible(true);
            }
        });
    }
}

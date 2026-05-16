package com.masroofy.presentation.fx;

import com.masroofy.domain.UserProfile;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class AuthView extends BorderPane {

    public AuthView(SceneManager sceneManager) {
        getStyleClass().add("auth-bg");

        VBox glassCard = new VBox();
        glassCard.getStyleClass().add("glass-card");

        Label title = new Label("Masroofy");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold;");

        Label subtitle = new Label("Your Smart Budget Vault");
        subtitle.setStyle("-fx-text-fill: rgba(255,255,255,0.6); -fx-font-size: 14px;");

        Label pinLabel = new Label("Enter PIN to unlock");
        pinLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.5); -fx-font-size: 13px; -fx-padding: 16px 0 4px 0;");

        PasswordField pinField = new PasswordField();
        pinField.setPromptText("••••");
        pinField.setStyle(
            "-fx-background-color: rgba(255,255,255,0.1);" +
            "-fx-background-radius: 12px;" +
            "-fx-border-color: rgba(255,255,255,0.2);" +
            "-fx-border-radius: 12px;" +
            "-fx-padding: 12px 16px;" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: rgba(255,255,255,0.3);" +
            "-fx-font-size: 16px;" +
            "-fx-alignment: center;" +
            "-fx-min-height: 48px;" +
            "-fx-max-width: 200px;"
        );

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);

        Button unlockBtn = new Button("Unlock Vault");
        unlockBtn.getStyleClass().add("primary-btn");
        unlockBtn.setMaxWidth(Double.MAX_VALUE);

        UserProfile userProfile = sceneManager.getUserProfile();

        unlockBtn.setOnAction(e -> {
            String pin = pinField.getText();
            if (userProfile.verifyPIN(pin)) {
                sceneManager.showAppShell();
            } else {
                errorLabel.setText("Incorrect PIN. Try 1234.");
                errorLabel.setVisible(true);
                pinField.clear();
            }
        });

        pinField.setOnAction(unlockBtn.getOnAction());

        glassCard.getChildren().addAll(title, subtitle, pinLabel, pinField, errorLabel, unlockBtn);
        setCenter(glassCard);
        setAlignment(glassCard, Pos.CENTER);
    }
}

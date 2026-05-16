package com.masroofy.presentation.fx;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;

public class AppShell extends HBox {

    private final StackPane contentArea;

    public AppShell(SceneManager sceneManager) {
        setSpacing(0);

        // --- Sidebar ---
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");

        Label logo = new Label("Masroofy");
        logo.getStyleClass().add("sidebar-title");

        Label logoSub = new Label("BUDGET TRACKER");
        logoSub.getStyleClass().add("sidebar-subtitle");

        Button navDashboard = createNavButton("  Dashboard");
        Button navHistory = createNavButton("  History");
        Button navAddExpense = createNavButton("  Add Expense");

        navDashboard.setOnAction(e -> {
            setActiveButton(navDashboard, navHistory, navAddExpense);
            sceneManager.navigateToDashboard();
        });
        navHistory.setOnAction(e -> {
            setActiveButton(navHistory, navDashboard, navAddExpense);
            sceneManager.navigateToHistory();
        });
        navAddExpense.setOnAction(e -> {
            setActiveButton(navAddExpense, navDashboard, navHistory);
            sceneManager.navigateToAddExpense();
        });

        Line separator = new Line(0, 0, 208, 0);
        separator.getStyleClass().add("separator-line");
        separator.getStyleClass().add("sidebar-separator");

        Button resetBtn = new Button("  Reset All Data");
        resetBtn.getStyleClass().addAll("sidebar-nav-btn", "danger-btn");
        resetBtn.setMinWidth(208);
        resetBtn.setAlignment(Pos.CENTER_LEFT);
        resetBtn.setOnAction(e -> {
            boolean success = sceneManager.getCycleManager().triggerDataReset();
            if (success) {
                sceneManager.navigateToCycleSetup();
            }
        });

        VBox spacer = new VBox();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(
            logo, logoSub, navDashboard, navHistory, navAddExpense,
            spacer, separator, resetBtn
        );

        // --- Content Area ---
        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");
        HBox.setHgrow(contentArea, Priority.ALWAYS);

        getChildren().addAll(sidebar, contentArea);
    }

    public void setContent(javafx.scene.Node view) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
    }

    private Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("sidebar-nav-btn");
        return btn;
    }

    private void setActiveButton(Button active, Button... others) {
        active.getStyleClass().add("sidebar-nav-btn--active");
        for (Button btn : others) {
            btn.getStyleClass().remove("sidebar-nav-btn--active");
        }
    }
}

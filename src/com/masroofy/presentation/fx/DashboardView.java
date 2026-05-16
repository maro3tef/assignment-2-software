package com.masroofy.presentation.fx;

import com.masroofy.domain.BudgetCycle;
import com.masroofy.domain.Transaction;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardView extends ScrollPane {

    private static final String[] DONUT_COLORS = {
        "#6366f1", "#f43f5e", "#eab308", "#10b981",
        "#3b82f6", "#a855f7", "#ec4899", "#14b8a6"
    };

    public DashboardView(SceneManager sceneManager) {
        getStyleClass().add("scroll-pane");
        setFitToWidth(true);

        BudgetCycle cycle = sceneManager.getCurrentCycle();
        VBox root = new VBox();
        root.setSpacing(24);
        root.setPadding(new Insets(0, 0, 32, 0));

        // --- Row 1: Balance Card + Safe Limit ---
        HBox row1 = new HBox();
        row1.setSpacing(24);

        VBox balanceCard = new VBox();
        balanceCard.getStyleClass().add("balance-card");
        HBox.setHgrow(balanceCard, Priority.ALWAYS);

        Label balanceLabel = new Label("Available Balance");
        balanceLabel.getStyleClass().add("balance-card-label");

        double balance = (cycle != null) ? cycle.getRemainingBalance() : 0.0;
        Label balanceAmount = new Label(String.format("$%,.2f", balance));
        balanceAmount.getStyleClass().add("balance-card-amount");

        String cycleInfo = (cycle != null)
            ? "Cycle ends in " + cycle.getRemainingDays() + " days"
            : "No active budget cycle";
        Label balanceSub = new Label(cycleInfo);
        balanceSub.getStyleClass().add("balance-card-sub");

        balanceCard.getChildren().addAll(balanceLabel, balanceAmount, balanceSub);

        VBox limitCard = new VBox();
        limitCard.getStyleClass().add("white-card");
        limitCard.setPrefWidth(300);

        Label limitLabel = new Label("Safe Daily Limit");
        limitLabel.getStyleClass().add("white-card-label");

        double dailyLimit = (cycle != null)
            ? sceneManager.getCalcEngine().calcSafeDailyLimit(cycle)
            : 0.0;
        Label limitAmount = new Label(String.format("$%,.2f", dailyLimit));
        limitAmount.getStyleClass().add("white-card-value-green");

        String limitSubText = (cycle != null)
            ? "Based on " + cycle.getRemainingDays() + " remaining days"
            : "Set up a cycle to see your limit";
        Label limitSub = new Label(limitSubText);
        limitSub.getStyleClass().add("white-card-sub");

        limitCard.getChildren().addAll(limitLabel, limitAmount, limitSub);

        row1.getChildren().addAll(balanceCard, limitCard);

        // --- Row 2: Donut Chart + Category Breakdown ---
        HBox row2 = new HBox();
        row2.setSpacing(24);

        VBox donutSection = new VBox();
        donutSection.getStyleClass().add("donut-container");
        HBox.setHgrow(donutSection, Priority.ALWAYS);

        Label chartHeader = new Label("Spending Breakdown");
        chartHeader.getStyleClass().add("white-card-label");
        chartHeader.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        StackPane donutWrapper = new StackPane();
        donutWrapper.setAlignment(Pos.CENTER);
        donutWrapper.setMinHeight(240);

        // Load transactions and build pie chart data
        List<Transaction> transactions = (cycle != null)
            ? sceneManager.getTransactionDAO().getAllTransactionsByCycle(cycle.getCycleId())
            : List.of();

        Map<String, Double> categoryTotals = aggregateByCategory(transactions);
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        int colorIdx = 0;
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            PieChart.Data slice = new PieChart.Data(entry.getKey(), entry.getValue());
            pieData.add(slice);
            colorIdx++;
        }

        PieChart pieChart = new PieChart(pieData);
        pieChart.setLegendVisible(false);
        pieChart.setLabelsVisible(false);
        pieChart.setPrefSize(220, 220);
        pieChart.setMaxSize(220, 220);
        pieChart.setAnimated(false);
        pieChart.setStyle("-fx-background-color: transparent;");

        // style each slice
        colorIdx = 0;
        for (PieChart.Data data : pieData) {
            String color = DONUT_COLORS[colorIdx % DONUT_COLORS.length];
            data.getNode().setStyle(
                "-fx-pie-color: " + color + ";"
            );
            colorIdx++;
        }

        Circle whiteHole = new Circle(70);
        whiteHole.setStyle("-fx-fill: white;");

        double totalSpent = categoryTotals.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalAllowance = (cycle != null) ? cycle.getTotalAllowance() : 0;
        double remaining = totalAllowance - totalSpent;

        VBox donutCenter = new VBox();
        donutCenter.setAlignment(Pos.CENTER);
        donutCenter.setSpacing(2);
        Label spentLabel = new Label(String.format("$%,.2f", totalSpent));
        spentLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        Label spentDesc = new Label("Total Spent");
        spentDesc.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");

        if (pieData.isEmpty()) {
            spentLabel.setText("$0.00");
            Label emptyHint = new Label("Add expenses to\nsee your breakdown");
            emptyHint.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8; -fx-text-alignment: center;");
            donutCenter.getChildren().addAll(spentLabel, emptyHint);
        } else {
            donutCenter.getChildren().addAll(spentLabel, spentDesc);
        }

        donutWrapper.getChildren().addAll(pieChart, whiteHole, donutCenter);

        donutSection.getChildren().addAll(chartHeader, donutWrapper);

        // --- Category legend (right side) ---
        VBox legendSection = new VBox();
        legendSection.getStyleClass().add("white-card");
        legendSection.setPrefWidth(280);
        legendSection.setSpacing(12);

        Label legendHeader = new Label("By Category");
        legendHeader.getStyleClass().add("white-card-label");
        legendHeader.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        Label remainingLabel = new Label();
        remainingLabel.getStyleClass().add("white-card-sub");
        if (cycle != null) {
            double spentPct = totalAllowance > 0 ? (totalSpent / totalAllowance) * 100 : 0;
            remainingLabel.setText(
                String.format("$%,.2f remaining  •  %.1f%% used", remaining, spentPct)
            );
        } else {
            remainingLabel.setText("No active cycle");
        }

        legendSection.getChildren().addAll(legendHeader, remainingLabel);

        colorIdx = 0;
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            HBox item = new HBox();
            item.getStyleClass().add("legend-item");
            item.setSpacing(10);

            Circle dot = new Circle(5);
            dot.setStyle("-fx-fill: " + DONUT_COLORS[colorIdx % DONUT_COLORS.length] + ";");

            Label name = new Label(entry.getKey());
            name.getStyleClass().add("legend-label");
            HBox.setHgrow(name, Priority.ALWAYS);

            Label amount = new Label(String.format("$%,.2f", entry.getValue()));
            amount.getStyleClass().add("legend-amount");

            item.getChildren().addAll(dot, name, amount);
            legendSection.getChildren().add(item);
            colorIdx++;
        }

        if (categoryTotals.isEmpty()) {
            Label emptyLegend = new Label("No expenses recorded yet");
            emptyLegend.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px; -fx-padding: 8px 0;");
            legendSection.getChildren().add(emptyLegend);
        }

        row2.getChildren().addAll(donutSection, legendSection);

        root.getChildren().addAll(row1, row2);
        setContent(root);
    }

    private Map<String, Double> aggregateByCategory(List<Transaction> transactions) {
        Map<String, Double> totals = new HashMap<>();
        for (Transaction t : transactions) {
            String category = SceneManager.CATEGORY_NAMES
                .getOrDefault(t.getCategoryId(), "Other");
            totals.merge(category, t.getAmount(), Double::sum);
        }
        return totals;
    }
}

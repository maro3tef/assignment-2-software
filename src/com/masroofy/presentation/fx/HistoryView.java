package com.masroofy.presentation.fx;

import com.masroofy.domain.BudgetCycle;
import com.masroofy.domain.Transaction;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistoryView extends VBox {

    private final SceneManager sceneManager;
    private final TableView<Transaction> tableView;

    public HistoryView(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        setSpacing(0);

        Label header = new Label("Transaction History");
        header.getStyleClass().add("page-header");

        BudgetCycle cycle = sceneManager.getCurrentCycle();
        String subText = (cycle != null)
            ? "All transactions for current budget cycle"
            : "No active budget cycle";
        Label subheader = new Label(subText);
        subheader.getStyleClass().add("page-subheader");

        tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cd ->
            new ReadOnlyObjectWrapper<>(
                cd.getValue().getTimestamp()
                    .format(DateTimeFormatter.ofPattern("MMM dd, yyyy  hh:mm a"))
            )
        );
        dateCol.setPrefWidth(180);

        TableColumn<Transaction, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(cd ->
            new ReadOnlyObjectWrapper<>(cd.getValue().getAmount())
        );
        amountCol.setPrefWidth(120);
        amountCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%,.2f", item));
                    setStyle("-fx-text-fill: #f43f5e; -fx-font-weight: bold;");
                }
            }
        });

        TableColumn<Transaction, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cd ->
            new ReadOnlyObjectWrapper<>(
                SceneManager.CATEGORY_NAMES
                    .getOrDefault(cd.getValue().getCategoryId(), "Other")
            )
        );
        categoryCol.setPrefWidth(160);

        TableColumn<Transaction, String> noteCol = new TableColumn<>("Note");
        noteCol.setCellValueFactory(cd ->
            new ReadOnlyObjectWrapper<>(cd.getValue().getNote())
        );
        noteCol.setPrefWidth(200);

        TableColumn<Transaction, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(100);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = createEditButton();
            private final Button deleteBtn = createDeleteButton();
            private final HBox pane = new HBox(6, editBtn, deleteBtn);

            {
                pane.setAlignment(Pos.CENTER);
                editBtn.getStyleClass().addAll("icon-btn", "icon-btn-edit");
                deleteBtn.getStyleClass().addAll("icon-btn", "icon-btn-delete");

                editBtn.setOnAction(e -> {
                    Transaction t = getTableView().getItems().get(getIndex());
                    sceneManager.navigateToEditExpense(t);
                });

                deleteBtn.setOnAction(e -> {
                    Transaction t = getTableView().getItems().get(getIndex());
                    boolean ok = sceneManager.getExpenseTracker()
                        .deleteTransaction(t.getTransactionId(), t.getAmount());
                    if (ok) {
                        refreshData();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        tableView.getColumns().addAll(dateCol, amountCol, categoryCol, noteCol, actionsCol);
        getChildren().addAll(header, subheader, tableView);

        refreshData();
    }

    private static Button createEditButton() {
        SVGPath pencil = new SVGPath();
        pencil.setContent("M16 3 L21 8 L8 21 L2 23 L3 17 Z M16 3 L11 8");
        pencil.setFill(null);
        pencil.setStroke(Color.valueOf("#6366f1"));
        pencil.setStrokeWidth(2);
        pencil.setStrokeLineCap(StrokeLineCap.ROUND);
        pencil.setStrokeLineJoin(StrokeLineJoin.ROUND);
        Button btn = new Button();
        btn.setGraphic(pencil);
        btn.setPrefSize(32, 32);
        btn.setMinSize(32, 32);
        return btn;
    }

    private static Button createDeleteButton() {
        SVGPath trash = new SVGPath();
        trash.setContent(
            "M4 7h16" +
            "M7 7v10a2 2 0 002 2h6a2 2 0 002-2V7" +
            "M10 7V5a1 1 0 011-1h2a1 1 0 011 1v2" +
            "M9 11v4" +
            "M15 11v4"
        );
        trash.setFill(null);
        trash.setStroke(Color.valueOf("#f43f5e"));
        trash.setStrokeWidth(2);
        trash.setStrokeLineCap(StrokeLineCap.ROUND);
        trash.setStrokeLineJoin(StrokeLineJoin.ROUND);
        Button btn = new Button();
        btn.setGraphic(trash);
        btn.setPrefSize(32, 32);
        btn.setMinSize(32, 32);
        return btn;
    }

    public void refreshData() {
        BudgetCycle cycle = sceneManager.getCurrentCycle();
        if (cycle != null) {
            List<Transaction> transactions =
                sceneManager.getTransactionDAO().getAllTransactionsByCycle(cycle.getCycleId());
            ObservableList<Transaction> items = FXCollections.observableArrayList(transactions);
            tableView.setItems(items);
        } else {
            tableView.setItems(FXCollections.observableArrayList());
        }
    }
}

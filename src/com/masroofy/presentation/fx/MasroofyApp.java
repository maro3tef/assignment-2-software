package com.masroofy.presentation.fx;

import com.masroofy.business.AlertingSystem;
import com.masroofy.business.CalculationEngine;
import com.masroofy.business.CycleManager;
import com.masroofy.business.ExpenseTracker;
import com.masroofy.data.DatabaseHelper;
import com.masroofy.data.SQLiteBudgetCycleDAO;
import com.masroofy.data.SQLiteTransactionDAO;
import com.masroofy.domain.UserProfile;

import javafx.application.Application;
import javafx.stage.Stage;

public class MasroofyApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        SQLiteBudgetCycleDAO cycleDAO = new SQLiteBudgetCycleDAO();
        SQLiteTransactionDAO transactionDAO = new SQLiteTransactionDAO();
        AlertingSystem alertingSystem = new AlertingSystem();
        CalculationEngine calcEngine = new CalculationEngine(cycleDAO);

        CycleManager cycleManager = new CycleManager(cycleDAO, calcEngine);
        ExpenseTracker expenseTracker = new ExpenseTracker(transactionDAO, cycleDAO, alertingSystem);
        UserProfile userProfile = new UserProfile(1, "1234", false);

        SceneManager sceneManager = new SceneManager(
            primaryStage, cycleManager, calcEngine, expenseTracker,
            transactionDAO, userProfile
        );

        primaryStage.setTitle("Masroofy");
        primaryStage.setResizable(false);
        sceneManager.showAuth();
        primaryStage.show();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseHelper.getInstance().closeConnection();
            System.out.println("\n=== Masroofy session complete ===");
        }));
    }

    public static void main(String[] args) {
        System.out.println("=== Masroofy Initialized ===\n");
        launch(args);
    }
}

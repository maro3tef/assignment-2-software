package com.masroofy.presentation.fx;

import com.masroofy.business.CalculationEngine;
import com.masroofy.business.CycleManager;
import com.masroofy.business.ExpenseTracker;
import com.masroofy.data.ITransactionDAO;
import com.masroofy.domain.BudgetCycle;
import com.masroofy.domain.Transaction;
import com.masroofy.domain.UserProfile;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class SceneManager {

    private static final int WIDTH = 1050;
    private static final int HEIGHT = 700;

    private final Stage stage;
    private final CycleManager cycleManager;
    private final CalculationEngine calcEngine;
    private final ExpenseTracker expenseTracker;
    private final ITransactionDAO transactionDAO;
    private final UserProfile userProfile;

    private AppShell appShell;
    private DashboardView dashboardView;
    private HistoryView historyView;
    private ExpenseEntryView expenseEntryView;
    private CycleSetupView cycleSetupView;

    private BudgetCycle currentCycle;

    // --- Category mappings (hardcoded) ---
    public static final Map<Integer, String> CATEGORY_NAMES = Map.of(
        1, "Food & Dining",
        2, "Transportation",
        3, "Shopping",
        4, "Bills & Utilities",
        5, "Entertainment",
        6, "Other"
    );

    public static final Map<String, Integer> CATEGORY_IDS = Map.of(
        "Food & Dining", 1,
        "Transportation", 2,
        "Shopping", 3,
        "Bills & Utilities", 4,
        "Entertainment", 5,
        "Other", 6
    );

    public SceneManager(Stage stage, CycleManager cycleManager,
                        CalculationEngine calcEngine, ExpenseTracker expenseTracker,
                        ITransactionDAO transactionDAO, UserProfile userProfile) {
        this.stage = stage;
        this.cycleManager = cycleManager;
        this.calcEngine = calcEngine;
        this.expenseTracker = expenseTracker;
        this.transactionDAO = transactionDAO;
        this.userProfile = userProfile;
    }

    public Stage getStage() { return stage; }
    public CycleManager getCycleManager() { return cycleManager; }
    public CalculationEngine getCalcEngine() { return calcEngine; }
    public ExpenseTracker getExpenseTracker() { return expenseTracker; }
    public ITransactionDAO getTransactionDAO() { return transactionDAO; }
    public UserProfile getUserProfile() { return userProfile; }

    public BudgetCycle getCurrentCycle() { return currentCycle; }

    public void refreshCurrentCycle() {
        List<BudgetCycle> cycles = cycleManager.getCycleHistory();
        currentCycle = cycles.isEmpty() ? null : cycles.get(0);
    }

    // --- Navigation ---

    public void showAuth() {
        AuthView authView = new AuthView(this);
        Scene scene = new Scene(authView, WIDTH, HEIGHT);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setScene(scene);
    }

    public void showAppShell() {
        refreshCurrentCycle();
        appShell = new AppShell(this);
        Scene scene = new Scene(appShell, WIDTH, HEIGHT);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setScene(scene);

        if (currentCycle == null) {
            navigateToCycleSetup();
        } else {
            navigateToDashboard();
        }
    }

    public void navigateToDashboard() {
        refreshCurrentCycle();
        dashboardView = new DashboardView(this);
        appShell.setContent(dashboardView);
    }

    public void navigateToHistory() {
        refreshCurrentCycle();
        historyView = new HistoryView(this);
        appShell.setContent(historyView);
    }

    public void navigateToAddExpense() {
        expenseEntryView = new ExpenseEntryView(this, null);
        appShell.setContent(expenseEntryView);
    }

    public void navigateToEditExpense(Transaction transaction) {
        expenseEntryView = new ExpenseEntryView(this, transaction);
        appShell.setContent(expenseEntryView);
    }

    public void navigateToCycleSetup() {
        cycleSetupView = new CycleSetupView(this);
        appShell.setContent(cycleSetupView);
    }
}

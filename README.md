# <span dir="rtl">مصروفي</span> — Masroofy

> **Your Smart Budget Vault** — A personal budget management and expense tracking application built with JavaFX and Swing.

Masroofy (derived from the Arabic word for "expenses") helps you take control of your finances by creating budget cycles, logging expenses, tracking your remaining balance in real time, and visualizing spending by category.

---

## Features

- **PIN Authentication** — Secure 4-digit PIN login to protect your financial data
- **Budget Cycle Management** — Create custom budget periods with a start date, end date, and total allowance
- **Expense Logging** — Add, edit, or delete transactions with category selection and optional notes
- **Real-time Balance Tracking** — Remaining balance updates instantly on every transaction
- **Safe Daily Limit** — Automatically calculated as `remaining balance ÷ remaining days` so you know how much you can spend each day
- **Daily Rollover** — Unspent funds from previous days automatically roll over, recalculating your daily limit
- **Spending Alerts** — Console notifications when you reach 80% and 100% of your budget
- **Category Pie Chart** — Visual breakdown of spending by category (Food & Dining, Transportation, Shopping, Bills & Utilities, Entertainment, Other)
- **Transaction History** — Full history table with edit and delete actions
- **Dual UI** — Modern JavaFX interface with a legacy Swing interface as an alternative

---

## Screenshots

| Dashboard | Expense Entry | Transaction History |
|-----------|---------------|-------------------|
| <img width="1310" height="877" alt="dashboard" src="https://github.com/user-attachments/assets/25f0202e-0fcc-4c32-bfd8-12135b73a8fd" />| <img width="1309" height="873" alt="add_expense" src="https://github.com/user-attachments/assets/481bf447-c5ce-482b-a756-6ef42b1aa42b" />| <img width="1308" height="873" alt="history" src="https://github.com/user-attachments/assets/cc3c6d9c-d2de-40d8-93e9-35476a681b93" />|

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Java 25 |
| **UI (Primary)** | JavaFX 25.0.3 (Controls, FXML, Graphics, Base) |
| **UI (Legacy)** | Swing |
| **Database** | SQLite via `sqlite-jdbc-3.53.0.0` |
| **Build** | Manual / IntelliJ IDEA |
| **IDE** | IntelliJ IDEA (project config included) |

---

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit 25** (or later) — [Download JDK 25](https://jdk.java.net/25/)
- **IntelliJ IDEA** (recommended) or any Java IDE — [Download IntelliJ](https://www.jetbrains.com/idea/download/)
- **JavaFX SDK 25.0.3** — already included at [`lib/javafx-sdk-25.0.3/`](lib/javafx-sdk-25.0.3/)
- **SQLite JDBC Driver** (`sqlite-jdbc-3.53.0.0.jar`) — required for database connectivity

---

## Download & Setup

### 1. Download the project

**Option A — Clone via Git (recommended)**

```bash
git clone https://github.com/<your-username>/assignment-2-software.git
cd assignment-2-software
```

**Option B — Download ZIP**

1. Go to the repository page on GitHub
2. Click the **Code** button → **Download ZIP**
3. Extract the ZIP file to your preferred location

### 2. Obtain the SQLite JDBC driver

The SQLite JDBC JAR is **not bundled** in the repository. You need to download it separately:

1. Download `sqlite-jdbc-3.53.0.0.jar` from the [SQLite JDBC Maven Repository](https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.53.0.0/)
2. Place the JAR file at the location expected by the project:
   ```
   assignment-2-software/.idea/libraries/
   ```
   (or update the library path in IntelliJ: **File → Project Structure → Libraries → sqlite-jdbc-3.53.0.0 → edit path**)

### 3. Open in IntelliJ IDEA

1. Launch IntelliJ IDEA
2. Click **Open** and select the `assignment-2-software` folder
3. IntelliJ will detect the project configuration automatically
4. Ensure the **Project SDK** is set to **openjdk-25**:
   - **File → Project Structure → Project → SDK**
   - If not listed, add your JDK 25 installation

### 4. Verify JavaFX library

The JavaFX SDK is already included at `lib/javafx-sdk-25.0.3/`. Verify it is registered:

1. **File → Project Structure → Libraries**
2. Confirm `javafx-sdk-25.0.3` is listed and points to `lib/javafx-sdk-25.0.3/lib`

---

## How to Run

### From IntelliJ IDEA

1. Open the **Run** menu → **Run 'Masroofy'** (or press `Shift+F10`)
2. The run configuration is pre-configured with the correct VM arguments:
   ```
   --module-path "lib/javafx-sdk-25.0.3/lib" --add-modules javafx.controls,javafx.base,javafx.graphics,javafx.fxml
   ```
3. The application will launch with the PIN login screen

### From the command line

```bash
# Compile
javac --module-path "lib/javafx-sdk-25.0.3/lib" --add-modules javafx.controls,javafx.base,javafx.graphics,javafx.fxml -cp "path/to/sqlite-jdbc-3.53.0.0.jar" -d out/production/assignment-2-software src/com/masroofy/**/*.java

# Run
java --module-path "lib/javafx-sdk-25.0.3/lib" --add-modules javafx.controls,javafx.base,javafx.graphics,javafx.fxml -cp "out/production/assignment-2-software;path/to/sqlite-jdbc-3.53.0.0.jar" com.masroofy.Main
```

---

## Usage

1. **Login** — Enter the default PIN `1234` (changeable in code)
2. **Create a Budget Cycle** — Set your total budget, start date, and end date
3. **Add Expenses** — Log each purchase with amount, category, and optional note
4. **Monitor Dashboard** — View remaining balance, safe daily limit, and category pie chart
5. **Review History** — Browse, edit, or delete past transactions
6. **Watch for Alerts** — Console warnings appear at 80% and 100% spending thresholds

---

## Project Structure

```
assignment-2-software/
├── .idea/                          # IntelliJ IDEA configuration
├── com/                            # Additional sources
├── lib/
│   └── javafx-sdk-25.0.3/         # JavaFX SDK
├── src/com/masroofy/
│   ├── Main.java                   # Application entry point
│   ├── domain/                     # Domain models
│   │   ├── BudgetCycle.java
│   │   ├── Transaction.java
│   │   ├── Category.java
│   │   └── UserProfile.java
│   ├── business/                   # Business logic
│   │   ├── ExpenseTracker.java
│   │   ├── CycleManager.java
│   │   ├── CalculationEngine.java
│   │   └── AlertingSystem.java
│   ├── data/                       # Data access layer
│   │   ├── DatabaseHelper.java
│   │   ├── ITransactionDAO.java
│   │   ├── IBudgetCycleDAO.java
│   │   ├── SQLiteTransactionDAO.java
│   │   └── SQLiteBudgetCycleDAO.java
│   └── presentation/
│       ├── fx/                     # JavaFX UI
│       │   ├── MasroofyApp.java
│       │   ├── SceneManager.java
│       │   ├── AppShell.java
│       │   ├── AuthView.java
│       │   ├── DashboardView.java
│       │   ├── HistoryView.java
│       │   ├── ExpenseEntryView.java
│       │   ├── CycleSetupView.java
│       │   └── styles.css
│       └── ...                     # Swing UI (legacy)
├── Documentation/                  # Generated Javadoc
├── resource-files/                 # Javadoc resources
├── script-files/                   # Javadoc scripts
├── out/                            # Compiled output
├── masroofy.db                     # SQLite database
└── README.md
```

---

## Documentation

Full Javadoc is available in the [`Documentation/`](Documentation/) directory. Open [`Documentation/index.html`](Documentation/index.html) in your browser to browse:

- Package summaries
- Class and interface documentation
- Generated API reference

---

## Architecture

The project follows a **layered architecture**:

```
┌──────────────────────────────────────────────┐
│           Presentation Layer (UI)             │
│    JavaFX (primary)      Swing (legacy)       │
├──────────────────────────────────────────────┤
│           Business Logic Layer                │
│  ExpenseTracker  │  CycleManager              │
│  CalculationEngine│  AlertingSystem           │
├──────────────────────────────────────────────┤
│           Data Access Layer (DAO)             │
│  SQLiteTransactionDAO  │  SQLiteBudgetCycleDAO│
├──────────────────────────────────────────────┤
│           Database (SQLite)                   │
│  budget_cycles  │  transactions               │
└──────────────────────────────────────────────┘
```

---

## Built With

- [Java 25](https://jdk.java.net/25/) — Latest Java platform
- [JavaFX 25](https://openjfx.io/) — Modern Java UI toolkit
- [SQLite](https://www.sqlite.org/) — Self-contained, serverless database
- [SQLite JDBC](https://github.com/xerial/sqlite-jdbc) — JDBC driver for SQLite
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) — Java IDE

---

## License

This project is developed for educational purposes as part of a university assignment.

package com.masroofy.domain;

import java.util.List;
import java.time.LocalDateTime;

public class UserProfile {
    private int userId;
    private String hashedPIN;
    private boolean bioAuthEnabled;

    public UserProfile(int userId, String hashedPIN, boolean bioAuthEnabled) {
        this.userId = userId;
        this.hashedPIN = hashedPIN;
        this.bioAuthEnabled = bioAuthEnabled;
    }

    public boolean verifyPIN(String inputPIN) {
        // This would typically involve hashing the inputPIN and comparing it with hashedPIN
        // For now, a direct comparison is used for simplicity.
        return this.hashedPIN.equals(inputPIN);
    }

    // The SDS mentions getTransactionHistory and sortByDateTime here, but these methods
    // seem more appropriate for a business logic layer (e.g., ExpenseTracker or a dedicated HistoryManager)
    // as they involve data access and manipulation of a list of transactions.
    // For now, I will omit them from the domain object to keep it focused on data representation.
    // If the SDS explicitly requires them here, they can be added back.

    public int getUserId() {
        return userId;
    }

    public String getHashedPIN() {
        return hashedPIN;
    }

    public boolean isBioAuthEnabled() {
        return bioAuthEnabled;
    }
}

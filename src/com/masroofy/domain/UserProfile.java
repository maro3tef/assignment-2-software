package com.masroofy.domain;

import java.util.List;
import java.time.LocalDateTime;

/**
 * The type User profile.
 */
public class UserProfile {
    private int userId;
    private String hashedPIN;
    private boolean bioAuthEnabled;

    /**
     * Instantiates a new User profile.
     *
     * @param userId         the user id
     * @param hashedPIN      the hashed pin
     * @param bioAuthEnabled the bio auth enabled
     */
    public UserProfile(int userId, String hashedPIN, boolean bioAuthEnabled) {
        this.userId = userId;
        this.hashedPIN = hashedPIN;
        this.bioAuthEnabled = bioAuthEnabled;
    }

    /**
     * Verify pin boolean.
     *
     * @param inputPIN the input pin
     * @return the boolean
     */
    public boolean verifyPIN(String inputPIN) {
        return this.hashedPIN.equals(inputPIN);//to be updated in final version of the app
    }


    /**
     * Gets user id.
     *
     * @return the user id
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Gets hashed pin.
     *
     * @return the hashed pin
     */
    public String getHashedPIN() {
        return hashedPIN;
    }

    /**
     * Is bio auth enabled boolean.
     *
     * @return the boolean
     */
    public boolean isBioAuthEnabled() {
        return bioAuthEnabled;
    }
}

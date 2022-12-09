package edu.uiuc.cs427app.model;

import java.io.Serializable;

/**
 * UserPreferences to store UI Themes and other user specific preferences
 */
public class UserPreferences implements Serializable {

    private UITheme uiThemeId = UITheme.THEME_SYSTEM;

    /**
     * this method is used for fetching the uiTheme
     * @return - UITheme for the user preference
     */
    public UITheme getUiThemeId() {
        return uiThemeId;
    }

    /**
     * Used for storing the uiTheme
     * @param uiThemeId - Id of the UITheme
     */
    public void setUiThemeId(UITheme uiThemeId) {
        this.uiThemeId = uiThemeId;
    }

    /**
     * Parameterized constructor for initializing user preferences
     * @param uiThemeId
     */
    public UserPreferences(UITheme uiThemeId) {
        this.uiThemeId = uiThemeId;
    }

    /**
     * Default constructor to create user preferences object
     */
    public UserPreferences(){}

    /**
     * To string method to print user preferences
     * @return - string representation of user preferences
     */
    @Override
    public String toString() {
        return "UserPreferences{" +
                "uiThemeId=" + uiThemeId +
                '}';
    }
}

package edu.uiuc.cs427app.model;

import androidx.appcompat.app.AppCompatDelegate;

import java.io.Serializable;

/**
 * Enum for mapping the theme option with their corresponding AppCompatDelegate Mode
 */
public enum UITheme implements Serializable {
    THEME_UNDEFINED(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM),
    THEME_LIGHT(AppCompatDelegate.MODE_NIGHT_NO),
    THEME_DARK(AppCompatDelegate.MODE_NIGHT_YES),
    THEME_SYSTEM(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

    private Integer id;

    /**
     * Used to fetch the corresponding AppCompatDelegate Mode value
     * @return - AppCompatDelegate Mode Integer value
     */
    public Integer getId() {
        return id;
    }

    /**
     * Used to update the corresponding AppCompatDelegate Mode value
     * @param id - AppCompatDelegate Mode Integer value
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Parameterized constructor for setting the corresponding AppCompatDelegate Mode value
     * @param id
     */
    UITheme(Integer id) {
        this.id = id;
    }
}

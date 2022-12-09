package edu.uiuc.cs427app;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NavUtils;

import edu.uiuc.cs427app.database.user.UserDBHelper;
import edu.uiuc.cs427app.model.UITheme;
import edu.uiuc.cs427app.model.User;
import edu.uiuc.cs427app.model.UserPreferences;

/**
 * Activity for selecting ui theme while registering the user
 */
public class SelectUIThemeActivity extends AppCompatActivity implements View.OnClickListener {

    private RadioGroup radioGroup;
    private RadioButton themeLight,themeDark,themeSystem;
    private String userName;
    private User user;
    private String TAG="SelectUITheme";
    private final UserDBHelper dbHelper;

    /**
     * Default Constructor to initialize the user database helper
     */
    public SelectUIThemeActivity() {
        dbHelper = new UserDBHelper(this);
    }

    /**
     * This method is used to initialize the Select_UI_Theme activity and set the corresponding default theme for the activity
     * @param savedInstanceState - current activity state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_theme);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        radioGroup = findViewById(R.id.themeGroup);
        Button signup = (Button) findViewById(R.id.signup_confirm);

        userName = getIntent().getStringExtra("username").toString();
        user = (User) getIntent().getSerializableExtra("user");
        initTheme();
        initThemeListener(radioGroup);
        signup.setOnClickListener(this);

    }

    /**
     * This method is used to initialize the theme for the activity based on the current configurations of the system
     */
    private void initTheme() {
        themeLight = (RadioButton)findViewById(R.id.themeLight);
        themeDark = (RadioButton)findViewById(R.id.themeDark);
        themeSystem = (RadioButton)findViewById(R.id.themeSystem);
        UITheme uiTheme = getSavedTheme();
        switch (uiTheme) {
            case THEME_LIGHT:
                themeLight.setChecked(true);
                break;
            case THEME_DARK:
                themeDark.setChecked(true);
                break;
            case THEME_SYSTEM:
                themeSystem.setChecked(true);
                break;
            case THEME_UNDEFINED:
                int currentNightMode = getResources().getConfiguration().uiMode
                        & Configuration.UI_MODE_NIGHT_MASK;
                switch (currentNightMode) {
                    case Configuration.UI_MODE_NIGHT_NO:
                        themeLight.setChecked(true);
                        break;
                    case Configuration.UI_MODE_NIGHT_YES:
                        themeDark.setChecked(true);
                        break;
                    case Configuration.UI_MODE_NIGHT_UNDEFINED:
                        themeSystem.setChecked(true);
                        break;
                }
                break;

        }
    }

    /**
     * This method is used to change the theme of the activity based on the user selected option so that user can have a feel
     * of how the system looks after changing the theme
     * @param radioGroup - group of radio buttons
     */
    private void initThemeListener(RadioGroup radioGroup) {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId){
                    case R.id.themeLight:
                        Log.d(TAG,"Setting Light Theme");
                        setTheme(AppCompatDelegate.MODE_NIGHT_NO,UITheme.THEME_LIGHT);
                        break;
                    case R.id.themeDark:
                        Log.d(TAG,"Setting Dark Theme");
                        setTheme(AppCompatDelegate.MODE_NIGHT_YES,UITheme.THEME_DARK);
                        break;
                    case R.id.themeSystem:
                        Log.d(TAG,"Setting System Theme");
                        setTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,UITheme.THEME_SYSTEM);
                    default:
                        Log.d(TAG,"Invalid Theme setting system theme");
                        setTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,UITheme.THEME_SYSTEM);
                        break;
                }
            }
        });
    }

    /**
     * OnClick Listener to confirm user signup and save the user details to database
     * @param v - current view
     */
    @Override
    public void onClick(View v) {
            // To go the User Login page
            saveUser(user);
            Toast.makeText(this, "User Registered Successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, UserLogin.class);
            startActivity(intent);
            finish();
    }

    /**
     * this method is used to update the theme in user preferences and update the theme of the current activity
     * @param themeMode - the AppCompatDelegate theme mode
     * @param uiTheme - the uiTheme selected by user
     */
    private void setTheme(Integer themeMode, UITheme uiTheme){
        getDelegate().setLocalNightMode(themeMode);
        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setUiThemeId(uiTheme);
        user.setUserPreferences(userPreferences);
    }

    /**
     * this method is used to fetch the saved theme for user. Will default to system theme if there is no user details
     * or selected user preferences
     * @return - UITheme details
     */
    private UITheme getSavedTheme(){
        UserPreferences userPreferences = dbHelper.getUserPreferences(user.getUserName());
        return userPreferences!=null?userPreferences.getUiThemeId():UITheme.THEME_SYSTEM;
    }


    /**
     * this method is used to save user details and preferences to database
     * @param userInfo
     */
    private void saveUser(User userInfo){
        // Create the user account and add it to the database
        dbHelper.insertUserData(userInfo,"");
    }
}
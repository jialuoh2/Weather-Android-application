package edu.uiuc.cs427app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import edu.uiuc.cs427app.database.user.UserDBHelper;
import edu.uiuc.cs427app.model.User;
import edu.uiuc.cs427app.model.UserPreferences;

/**
 * Activity for registering and validating user details
 */
public class UserRegister extends AppCompatActivity implements View.OnClickListener {

    private EditText username_text;
    private EditText password_text;
    private EditText password_text_2;
    private Button select_ui_theme;
    private final UserDBHelper dbHelper;

    /**
     * Default constructor to initialize database helper
     */
    public UserRegister(){
        dbHelper = new UserDBHelper(this);
    }

    /**
     * this method is used to initialize the sign in page
     * @param savedInstanceState - current activity state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //run in light mode for login page
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username_text = (EditText) findViewById(R.id.username_text);
        password_text = (EditText) findViewById(R.id.password_text);
        password_text_2 = (EditText) findViewById(R.id.password_text_2);

        //Button username_clear = (Button) findViewById(R.id.username_clear);
        //Button password_clear = (Button) findViewById(R.id.password_clear);
        select_ui_theme = (Button) findViewById(R.id.select_ui_theme);

        //username_clear.setOnClickListener(this);
        //password_clear.setOnClickListener(this);
        select_ui_theme.setOnClickListener(this);
    }

    /**
     * OnClick method to set user details in user object and redirect to next page for selecting ui theme
     * @param v - current view
     */
    @Override
    public void onClick(View v) {
            User user = new User();
            user.setUserName(username_text.getText().toString().trim());
            user.setPassword(password_text.getText().toString().trim());
            user.setUserSelectedLocations(new ArrayList<>());
            user.setUserPreferences(new UserPreferences());
            if (checkRegister(user)) {
                Intent intent = new Intent(this, SelectUIThemeActivity.class);
                intent.putExtra("username", username_text.getText().toString().trim());
                intent.putExtra("user",user);
                startActivity(intent);
            }
    }

    /**
     * this method is used to validate user entered details and alert the user in case the details entered is incorrect or empty
     * @param userInfo - user details like username and password
     * @return - true if it passes all validation otherwise false
     */
    private boolean checkRegister(User userInfo) {
        String username = username_text.getText().toString().trim();
        String password1 = password_text.getText().toString().trim();
        String password2 = password_text_2.getText().toString().trim();
        if (username.isEmpty()) {
            username_text.setError("DO NOT Enter An Empty Username!");
            username_text.requestFocus();
            return false;
        }

        if (password1.isEmpty()) {
            password_text.setError("DO NOT Enter An Empty Password!");
            password_text.requestFocus();
            return false;
        }

        if (password1.length() < 6) {
            password_text.setError(("The Minimum Length For Password Is 6!"));
            password_text.requestFocus();
            return false;
        }

        if (password2.isEmpty()) {
            password_text_2.setError("Please Confirm Your Password!");
            password_text_2.requestFocus();
            return false;
        }

        if (!password1.equals(password2)) {
            password_text_2.setError("Please Enter The Same Password!");
            password_text_2.requestFocus();
            return false;
        }

        return verifyRegisteredUser(userInfo.getUserName());

    }

    /**
     * Validate if the username is already taken
     * @param userName - username entered by user
     * @return - true if the username has not been used and false otherwise
     */
    private boolean verifyRegisteredUser(String userName){
        if (dbHelper.verifyRegisterData(userName)) {
            username_text.setError("This Username has been used!");
            username_text.requestFocus();
            return false;
        }
        return true;
    }
}
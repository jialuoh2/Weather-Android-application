package edu.uiuc.cs427app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.uiuc.cs427app.database.user.UserDBHelper;
import edu.uiuc.cs427app.databinding.ActivityMainBinding;
import edu.uiuc.cs427app.model.UITheme;
import edu.uiuc.cs427app.model.UserPreferences;
import edu.uiuc.cs427app.model.UserSelectedLocation;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Main Activity to display user selected locations, add new locations and sign out
 */
public class MainActivity extends AppCompatActivity  {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    //activityResultLauncher is for resuming MainActivity upon other activities such as AddLocationActivity
    private ActivityResultLauncher<Intent> activityResultLauncher;
    //add location and logout buttons
    private Button buttonNew;
    private ImageButton logoutButton;
    //Recycler view for showing location list of the user
    private RecyclerView locationRecView;
    //text view to show the user name
    private TextView teamUserName;
    //user name is the logged-in user on this MainActivity, to decide what user name and location list to show on Main page
    private String userName;


    private final UserDBHelper dbHelper;

    /**
     * Default constructor to initialize the database helper
     */
    public MainActivity() {
        dbHelper = new UserDBHelper(this);
    }

    /**
     * this method is used to initialize the main activity and set the necessary text and also display the user selected location and theme
     * @param savedInstanceState - activity instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize "add location" button, "location list" recycler view, and the user
        buttonNew = findViewById(R.id.buttonAddLocation);
        locationRecView = findViewById(R.id.locationRecView);
        teamUserName = findViewById(R.id.textView3);
        logoutButton = findViewById(R.id.logoutButton);

        // In our finished version, the MainActivity will be started by "LoginActivity"
        // (Now in my version, by fake login activity),
        // which should pass MainActivity the user name or user id
        // (Now in my version, the user name is passed in)
        // then MainActivity can get the user selected locations from the database.
        // And show user name, user selected locations on the screen correctly.
        // Here I am processing the Intent payload that contains the user name.
        userName = getIntent().getStringExtra("username").toString();

        //set the user name displayed on top of the screen
        teamUserName.setText("CS427 Project App - Team 23-" + userName);
        this.setTitle("Team 23-" + userName);
        //set the top of screen after login in
        //set up an arraylist to store the user selected locations.
        //UserSelectedLocation is a customized class for user selected locations.
        ArrayList<UserSelectedLocation> locationsRec = new ArrayList<>();

        //connect to the database and fetch location list of the current user

        //find out the string of the locations stored for the user,
        //multiple locations are stored with split \t
        Cursor cursor = dbHelper.getLocations(userName);
        cursor.moveToFirst();
        String locationString = cursor.getString(0);

        //split the string into string array, and add the locations to the ArrayList
        // for later showing in recycler view
        if(locationString.length()>0) {
            String[] locationTemp = locationString.split("\t");
            for (String s : locationTemp) {
                locationsRec.add(new UserSelectedLocation(s));
            }
        }

        //set the adapter of recyclerview so that the list of locations can be shown correctly
        LocationRecViewAdapter adapter = new LocationRecViewAdapter(this);
        adapter.setUserSelectedLocations(locationsRec);
        locationRecView.setAdapter(adapter);
        locationRecView.setLayoutManager(new LinearLayoutManager(this));
        //set theme selected by user
        initTheme(userName);

        //interactions with AddLocationActivity
        //set up button click listener, to receive the message(intent) sent back from AddLocationActivity
        buttonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddLocationActivity.class);
                activityResultLauncher.launch(intent);
            }
        });

        //when new location is added, put the new location in the user selected locations arraylist
        //and notify the adapter that there is data changed, so the shown list can be refreshed.
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    String newLocationName = data.getExtras().getString("location").toString();
                    Double newLat = data.getExtras().getDouble("latitude");
                    Double newLng = data.getExtras().getDouble("longitude");
                    UserSelectedLocation newLocation = new UserSelectedLocation(newLocationName,
                            String.format(Locale.ENGLISH, "%f", newLat),
                            String.format(Locale.ENGLISH, "%f", newLng));
                    locationsRec.add(newLocation);
                    adapter.setUserSelectedLocations(locationsRec);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        //Logout from Main page. will jump to login page (Now jumping to fake login page)
        // And save the location list to the database when logging out
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save current location list to the database, convert location names to a string, split by \t
                StringBuilder userLocationsToSaveBuilder = new StringBuilder("");
                for(UserSelectedLocation l: locationsRec) {
                    userLocationsToSaveBuilder.append(l.serialize()).append("\t");
                }
                dbHelper.updateUserLocation(userName,userLocationsToSaveBuilder.toString());
                //jump to fake login page
                Intent intent = new Intent(view.getContext(), UserLogin.class);
                startActivity(intent);
                finish();
            }
        });


    }

    /**
     * This method is used to initialize the ui theme based on user preferences
     * @param userName - username of user
     */
    private void initTheme(String userName) {
        UITheme uiTheme = getSavedTheme(userName);
        AppCompatDelegate.setDefaultNightMode(uiTheme.getId());
    }

    /**
     * This method is used to fetch the ui theme saved for the given user
     * @param userName - username of user
     * @return
     */
    private UITheme getSavedTheme(String userName){
        UserPreferences userPreferences = dbHelper.getUserPreferences(userName);
        return userPreferences!=null?userPreferences.getUiThemeId():UITheme.THEME_UNDEFINED;
    }


    /**
     * overriding the onDestroy method to close the opened database connection
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}


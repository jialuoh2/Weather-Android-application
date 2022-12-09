package edu.uiuc.cs427app;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Used for showing details of the user selected location - will be expanded further in coming milestones
 */
public class DetailsActivity extends AppCompatActivity implements View.OnClickListener{

    /**
     * This method is used to initialize the Details activity with basic details like welcome to the city etc
     * @param savedInstanceState - activity instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Process the Intent payload that has opened this Activity and show the information accordingly
        String cityName = getIntent().getStringExtra("city").toString();
        String welcome = "Welcome to the "+cityName;
        String cityWeatherInfo = "Detailed information about the weather of "+cityName;

        // Initializing the GUI elements
        TextView welcomeMessage = findViewById(R.id.welcomeText);
        TextView cityInfoMessage = findViewById(R.id.cityInfo);

        welcomeMessage.setText(welcome);
        cityInfoMessage.setText(cityWeatherInfo);
        // Get the weather information from a Service that connects to a weather server and show the results

        Button buttonMap = findViewById(R.id.mapButton);
        buttonMap.setOnClickListener(this);

    }

    /**
     * This method will be expanded further in coming milestones
     * @param view - current view
     */
    @Override
    public void onClick(View view) {
        //Implement this (create an Intent that goes to a new Activity, which shows the map)
    }
}


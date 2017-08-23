package com.favoriteplace.jakubwitczak.thefavoriteplace;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class YourLocationActivity extends AppCompatActivity {

    private final static String CITY_NAME = "cityName";
    private final static String LATITUDE = "latitude";
    private final static String LONGITUDE = "longitude";

    private TextView cityNameTextView;
    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private Button backButton;
    private String cityName;
    private String latitude;
    private String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_location);
        initializeComponents();

        Bundle extras = getIntent().getExtras();
        if (!extras.isEmpty()) {
            cityName = extras.getString(CITY_NAME);
            latitude = extras.getString(LATITUDE);
            longitude = extras.getString(LONGITUDE);
        }
        setLocations();
        backToMainActivity();
    }

    private void setLocations() {
        cityNameTextView.setText(cityName);
        latitudeTextView.setText(latitude);
        longitudeTextView.setText(longitude);
    }

    private void backToMainActivity() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initializeComponents() {
        cityNameTextView = (TextView) findViewById(R.id.textViewCityName);
        latitudeTextView = (TextView) findViewById(R.id.textViewYourLatitude);
        longitudeTextView = (TextView) findViewById(R.id.textViewYourLongitude);
        backButton = (Button) findViewById(R.id.backButton);
    }
}

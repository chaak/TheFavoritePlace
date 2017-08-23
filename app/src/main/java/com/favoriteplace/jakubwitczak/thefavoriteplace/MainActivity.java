package com.favoriteplace.jakubwitczak.thefavoriteplace;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final static String CITY_NAME = "cityName";
    private final static String LATITUDE = "latitude";
    private final static String LONGITUDE = "longitude";

    private final static int ZERO = 0;
    private final static int PERMISSION_REQUEST_LOCATION = 1;

    private Button getLocationButton;
    private Button deleteLocationButton;
    private ListView locationListView;

    private List<CurrentLocation> locations;
    private LocationsAdapter locationsListAdapter;

    private LocationsDataBaseAdapter locationsDataBaseAdapter;
    private Cursor locationsCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();
        fillListView();
        getNewLocation();
        getYourLocation();
        deleteLocations();

        locationListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                CurrentLocation currentLocation = locations.get(i);
                if (currentLocation.isToDelete()) {
                    locationsDataBaseAdapter.updateLocation(currentLocation.getId(), currentLocation.getLatitude(), currentLocation.getLongitude(), currentLocation.getCityName(), false);
                } else {
                    locationsDataBaseAdapter.updateLocation(currentLocation.getId(), currentLocation.getLatitude(), currentLocation.getLongitude(), currentLocation.getCityName(), true);
                }
                updateListViewData();
                return true;
            }
        });
    }

    private void getYourLocation() {
        locationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CurrentLocation currentLocation = locations.get(i);
                Intent intent = new Intent(MainActivity.this, YourLocationActivity.class);
                Bundle extras = new Bundle();
                extras.putString(CITY_NAME, currentLocation.getCityName());
                extras.putString(LATITUDE, String.valueOf(currentLocation.getLatitude()));
                extras.putString(LONGITUDE, String.valueOf(currentLocation.getLongitude()));
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
    }

    public void deleteLocations() {
        deleteLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locationsCursor != null && locationsCursor.moveToFirst()) {
                    do {
                        if (locationsCursor.getInt(LocationsDataBaseAdapter.TO_DELETE_COLUMN) == 1) {
                            long id = locationsCursor.getLong(LocationsDataBaseAdapter.ID_COLUMN);
                            locationsDataBaseAdapter.deleteLocation(id);
                        }
                    } while (locationsCursor.moveToNext());
                }
                updateListViewData();
            }
        });
    }

    private void getNewLocation() {
        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_LOCATION);
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_LOCATION);
                    }
                } else {
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    try {
                        locationsDataBaseAdapter.insertLocation(location.getLatitude(), location.getLongitude(), getCityName(location.getLatitude(), location.getLongitude()));
                        updateListViewData();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Location not found!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void initializeComponents() {
        getLocationButton = (Button) findViewById(R.id.getLocationButton);
        deleteLocationButton = (Button) findViewById(R.id.deleteLocationButton);
        locationListView = (ListView) findViewById(R.id.locationListView);
    }

    private void fillListView() {
        locationsDataBaseAdapter = new LocationsDataBaseAdapter(getApplicationContext());
        locationsDataBaseAdapter.openDatabaseConnection();
        getAllLocations();
        locationsListAdapter = new LocationsAdapter(this, locations);
        locationListView.setAdapter(locationsListAdapter);
    }

    private void getAllLocations() {
        locations = new ArrayList<CurrentLocation>();
        locationsCursor = getAllEntriesFromDB();
        updateLocationList();
    }

    private Cursor getAllEntriesFromDB() {
        locationsCursor = locationsDataBaseAdapter.getAllLocations();
        if (locationsCursor != null) {
            startManagingCursor(locationsCursor);
            locationsCursor.moveToFirst();
        }
        return locationsCursor;
    }

    private void updateLocationList() {
        if (locationsCursor != null && locationsCursor.moveToFirst()) {
            do {
                long id = locationsCursor.getLong(LocationsDataBaseAdapter.ID_COLUMN);
                Double latitude = locationsCursor.getDouble(LocationsDataBaseAdapter.LATITUDE_COLUMN);
                Double longitude = locationsCursor.getDouble(LocationsDataBaseAdapter.LONGITUDE_COLUMN);
                String cityName = locationsCursor.getString(LocationsDataBaseAdapter.CITY_NAME_COLUMN);
                boolean toDelete = locationsCursor.getInt(LocationsDataBaseAdapter.TO_DELETE_COLUMN) > 0;

                locations.add(new CurrentLocation(id, latitude, longitude, cityName, toDelete));
            } while (locationsCursor.moveToNext());
        }
    }

    @Override
    protected void onDestroy() {
        if (locationsDataBaseAdapter != null)
            locationsDataBaseAdapter.closeDatabseConnection();
        super.onDestroy();
    }

    private void updateListViewData() {
        locationsCursor.requery();
        locations.clear();
        updateLocationList();
        locationsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION: {
                if (grantResults.length > ZERO && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        try {
                            locationsDataBaseAdapter.insertLocation(location.getLatitude(), location.getLongitude(), getCityName(location.getLatitude(), location.getLongitude()));
                            updateListViewData();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Location not found!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "No permission granted !", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String getCityName(double latitude, double longitude) {
        String currentCity = "";
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList.size() > ZERO) currentCity = addressList.get(ZERO).getLocality();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentCity;
    }
}

package com.favoriteplace.jakubwitczak.thefavoriteplace;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocationsDataBaseAdapter {

    private static final String DEBUG_TAG = "SqliteLocationsDB";

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "current_locations_test1.db";
    private static final String DB_LOCATIONS_TABLE = "locations";

    public static final String KEY_ID = "_id";
    public static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final int ID_COLUMN = 0;

    public static final String KEY_LATITUDE = "latitude";
    public static final int LATITUDE_COLUMN = 1;

    public static final String KEY_LONGITUDE = "longitude";
    public static final int LONGITUDE_COLUMN = 2;

    public static final String KEY_CITY_NAME = "city_name";
    public static final int CITY_NAME_COLUMN = 3;

    public static final String KEY_TO_DELETE = "to_delete";
    public static final int TO_DELETE_COLUMN = 4;
    public static final String TO_DELETE_OPTIONS = "INTEGER DEFAULT 0";

    public static final String DB_CREATE_LOCATIONS_TABLE =
            "CREATE TABLE " + DB_LOCATIONS_TABLE + "( " +
                    KEY_ID + " " + ID_OPTIONS + ", " +
                    KEY_LATITUDE + ", " +
                    KEY_LONGITUDE + ", " +
                    KEY_CITY_NAME + "," +
                    KEY_TO_DELETE + " " + TO_DELETE_OPTIONS +
                    ");";

    private static final String DROP_LOCATIONS_TABLE =
            "DROP TABLE IF EXISTS " + DB_LOCATIONS_TABLE;

    private SQLiteDatabase database;
    private Context context;
    private DatabaseHelper databaseHelper;

    LocationsDataBaseAdapter(Context context) {
        this.context = context;
    }

    public LocationsDataBaseAdapter openDatabaseConnection() {
        databaseHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
        try {
            database = databaseHelper.getWritableDatabase();
        } catch (SQLException e) {
            database = databaseHelper.getReadableDatabase();
        }
        return this;
    }

    public void closeDatabseConnection() {
        databaseHelper.close();
    }

    public long insertLocation(double latitude, double longitude, String cityName) {
        ContentValues newLocation = new ContentValues();
        newLocation.put(KEY_LATITUDE, latitude);
        newLocation.put(KEY_LONGITUDE, longitude);
        newLocation.put(KEY_CITY_NAME, cityName);
        return database.insert(DB_LOCATIONS_TABLE, null, newLocation);
    }

    public boolean updateLocation(long id, double latitude, double longitude, String cityName, boolean toDelete) {
        String where = KEY_ID + "=" + id;
        int isDeleted = toDelete ? 1 : 0;
        ContentValues updateLocationValues = new ContentValues();
        updateLocationValues.put(KEY_LATITUDE, latitude);
        updateLocationValues.put(KEY_LONGITUDE, longitude);
        updateLocationValues.put(KEY_CITY_NAME, cityName);
        updateLocationValues.put(KEY_TO_DELETE, isDeleted);
        return database.update(DB_LOCATIONS_TABLE, updateLocationValues, where, null) > 0;
    }

    public void deleteLocation(long id) {
        String where = KEY_ID + "=" + id;
        System.out.println("location number: " + id + " was deleted");
        database.delete(DB_LOCATIONS_TABLE, where, null);
    }

    public Cursor getAllLocations() {
        String[] columns = {KEY_ID, KEY_LATITUDE, KEY_LONGITUDE, KEY_CITY_NAME, KEY_TO_DELETE};
        return database.query(DB_LOCATIONS_TABLE, columns, null, null, null, null, null);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(DB_CREATE_LOCATIONS_TABLE);
            Log.d(DEBUG_TAG, "Creating database...");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(DROP_LOCATIONS_TABLE);
            Log.d(DEBUG_TAG, "All data is lost.");
            onCreate(sqLiteDatabase);
        }
    }
}

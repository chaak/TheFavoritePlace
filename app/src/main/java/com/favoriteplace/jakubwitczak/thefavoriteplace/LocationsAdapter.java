package com.favoriteplace.jakubwitczak.thefavoriteplace;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class LocationsAdapter extends ArrayAdapter<CurrentLocation> {

    private Activity context;
    private List<CurrentLocation> locations;

    public LocationsAdapter(Activity context, List<CurrentLocation> locations) {
        super(context, android.R.layout.simple_list_item_1, locations);
        this.context = context;
        this.locations = locations;
    }

    static class ViewHolder {
        public TextView cityNameTextView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            rowView = layoutInflater.inflate(R.layout.location_list_item, null, true);
            viewHolder = new ViewHolder();
            viewHolder.cityNameTextView = rowView.findViewById(R.id.textViewCityName);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        final CurrentLocation currentLocation = locations.get(position);
        viewHolder.cityNameTextView.setText(currentLocation.getCityName());

        if (currentLocation.isToDelete()) {
            rowView.setBackgroundColor(Color.parseColor("#FFA4A4"));
        } else {
            rowView.setBackgroundColor(Color.WHITE);
        }
        return rowView;
    }
}

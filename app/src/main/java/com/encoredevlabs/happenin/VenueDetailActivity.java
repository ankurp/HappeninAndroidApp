package com.encoredevlabs.happenin;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class VenueDetailActivity extends ActionBarActivity implements View.OnClickListener {

    private Venue venue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_detail);

        Bundle extras = getIntent().getExtras();
        this.venue = MapActivity.getVenueForId(extras.getString(MapActivity.VENUE));


        TextView venueNameTextView = (TextView) findViewById(R.id.venueNameTextView);
        venueNameTextView.setText(this.venue.getName());
        TextView venueStreetTextView = (TextView) findViewById(R.id.streetAddressTextView);
        venueStreetTextView.setText(this.venue.getStreetAddress());
        TextView venueCityStateTextView = (TextView) findViewById(R.id.cityStateTextView);
        venueCityStateTextView.setText(this.venue.getCityState());
        TextView venueCategoriesTextView = (TextView) findViewById(R.id.categoriesTextView);
        venueCategoriesTextView.setText(this.venue.getCategories());
        TextView venueDistanceTextView = (TextView) findViewById(R.id.distanceTextView);
        venueDistanceTextView.setText(this.venue.distanceFromLatLng(MapActivity.getCurrentLocation()) + " miles away");
        Button button = (Button) findViewById(R.id.directionButton);
        button.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.venue_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        Location currentLocation = MapActivity.getCurrentLocation();
        if (currentLocation != null) {
            LatLng venueLatLng = this.venue.getLatLng();
            String url = "http://maps.google.com/maps?saddr=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + "&daddr=" + venueLatLng.latitude + "," + venueLatLng.longitude;
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);
        } else {
            Toast.makeText(this, "Your current location is unknown", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_venue_detail, container, false);
            return rootView;
        }
    }

}

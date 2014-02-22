package com.encoredevlabs.happenin;

import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
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
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.android.gms.maps.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

public class MapActivity extends ActionBarActivity implements GoogleMap.OnCameraChangeListener, GoogleMap.OnMyLocationChangeListener, GoogleMap.OnInfoWindowClickListener {

    private static Location currentLocation;
    private static HashMap<String, Venue> mapVenueIdToVenue;
    private static HashMap<Marker, String> mapMarkerToVenueId;
    private GoogleMap map;

    public static String VENUE = "com.encoredevlabs.happenin.VENUE";

    public static Location getCurrentLocation() {
        return currentLocation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        this.map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();
        this.map.setMyLocationEnabled(true);
        this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(40.6700, -73.9400), 11));
        this.map.setOnMyLocationChangeListener(this);
        this.map.setOnCameraChangeListener(this);
        this.map.setOnInfoWindowClickListener(this);

        this.mapVenueIdToVenue = new HashMap<String, Venue>();
        this.mapMarkerToVenueId = new HashMap<Marker, String>();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        getMenuInflater().inflate(R.menu.map, menu);
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
    public void onCameraChange(final CameraPosition cameraPosition) {
        final LatLng center = cameraPosition.target;
        String foursqURL = "https://api.foursquare.com/v2/venues/trending?radius=48280&ll=" + center.latitude + "," + center.longitude + "&oauth_token=GF51LPBOS5LJMC41HSWKSJQ3411GZWB4H5VH1HUVQV35RZJV&v=20140216";
        new TrendingData().execute(foursqURL);
    }

    @Override
    public void onMyLocationChange(Location location) {
        currentLocation = location;
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 13));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(this, VenueDetailActivity.class);
        intent.putExtra(MapActivity.VENUE, this.mapMarkerToVenueId.get(marker));
        startActivity(intent);
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
            View rootView = inflater.inflate(R.layout.fragment_map, container, false);
            return rootView;
        }
    }

    public static Venue getVenueForId(String id) {
        return mapVenueIdToVenue.get(id);
    }

    private class TrendingData extends AsyncTask <String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... strings) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet getRequest = new HttpGet(strings[0]);
                HttpResponse response = httpClient.execute(getRequest);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null ; ) {
                    builder.append(line).append("\n");
                }
                JSONObject jsonObject = new JSONObject(builder.toString());
                JSONArray venues = jsonObject.getJSONObject("response").getJSONArray("venues");
                return venues;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONArray venues) {
            if (venues != null && map != null) {
                for (int i = 0; i < venues.length(); i++) {
                    try {
                        Venue venue = new Venue(venues.getJSONObject(i));
                        if (mapVenueIdToVenue.get(venue.getId()) == null) {
                            mapVenueIdToVenue.put(venue.getId(), venue);
                            Marker marker = map.addMarker(new MarkerOptions()
                                    .title(venue.getName())
                                    .snippet(venue.hereNowDescription())
                                    .position(venue.getLatLng())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
                            mapMarkerToVenueId.put(marker, venue.getId());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}

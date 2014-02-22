package com.encoredevlabs.happenin;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by ankur on 2/22/14.
 */
public class Venue {

    private String id;
    private String name;
    private int hereNowCount;
    private LatLng latLng;
    private JSONObject object;

    public Venue(JSONObject venue) {
        try {
            this.id = venue.get("id").toString();
            this.name = venue.get("name").toString();
            JSONObject location = venue.getJSONObject("location");
            this.latLng = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
            this.hereNowCount = venue.getJSONObject("hereNow").getInt("count");
            this.object = venue;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return this.id;
    }

    public LatLng getLatLng() {
        return this.latLng;
    }

    public String getName() {
        return this.name;
    }

    public String hereNowDescription() {
        if (this.hereNowCount == 0) {
            return this.hereNowCount + " person here now";
        } else {
            return this.hereNowCount + " people here now";
        }
    }

    public String getStreetAddress() {
        try {
            JSONObject location = this.object.getJSONObject("location");
            return location.getString("address");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getCityState() {
        try {
            JSONObject location = this.object.getJSONObject("location");
            return location.getString("city") + ", " + location.getString("state") + " " + location.getString("postalCode");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getCategories() {
        try {
            StringBuilder builder = new StringBuilder("Type: ");
            JSONArray categories = this.object.getJSONArray("categories");
            for (int i = 0; i < categories.length(); i++) {
                JSONObject category = categories.getJSONObject(i);
                builder.append(category.getString("shortName") + " ");
            }
            return builder.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String distanceFromLatLng(Location loc) {
        if (loc != null) {
            double radius = 3958.75;
            double dlat = ToRadians(loc.getLatitude() - this.latLng.latitude);
            double dlon = ToRadians(loc.getLongitude() - this.latLng.longitude);
            double a = Math.sin(dlat / 2)
                    * Math.sin(dlat / 2)
                    + Math.cos(ToRadians(loc.getLatitude()))
                    * Math.cos(ToRadians(this.latLng.latitude)) * Math.sin(dlon / 2)
                    * Math.sin(dlon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double d = radius * c;
            double meterConversion = 1.60934;
            DecimalFormat df = new DecimalFormat("#.#");
            return df.format(d * meterConversion);
        } else {
            return "";
        }
    }

    private static double ToRadians(double degrees) {
        double radians = degrees * Math.PI / 180;
        return radians;
    }
}

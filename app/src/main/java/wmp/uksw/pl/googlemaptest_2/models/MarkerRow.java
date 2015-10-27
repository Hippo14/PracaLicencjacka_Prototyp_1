package wmp.uksw.pl.googlemaptest_2.models;

import com.google.android.gms.maps.model.Marker;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MSI on 2015-10-20.
 */
public class MarkerRow {

    private int id;
    private double latitude;
    private double longitude;
    private String title;

    public MarkerRow() {

    }

    public MarkerRow(int id, double latitude, double longitude, String title) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
    }

    public MarkerRow(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt("1");
        this.latitude = jsonObject.getDouble("2");
        this.longitude = jsonObject.getDouble("3");
        this.title = jsonObject.getString("4");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

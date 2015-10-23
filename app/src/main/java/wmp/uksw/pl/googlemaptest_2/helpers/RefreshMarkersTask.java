package wmp.uksw.pl.googlemaptest_2.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.Marker;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wmp.uksw.pl.googlemaptest_2.database.DbHelper;
import wmp.uksw.pl.googlemaptest_2.models.MarkerRow;

/**
 * Created by MSI on 2015-10-20.
 */
public class RefreshMarkersTask extends AsyncTask<Void, Void, Boolean> {

    private List<NameValuePair> list;
    private Context context;
    private JSONObject json;
    private JSONParser jsonParser;
    private List<MarkerRow> markersList;
    private DbHelper dbHelper;

    public RefreshMarkersTask(Context context) {
        this.context = context;
        this.list = new ArrayList<>();
        this.markersList = new ArrayList<>();
        this.jsonParser = new JSONParser();
        this.dbHelper = new DbHelper(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        IsConnected isConnected = new IsConnected(context);

        if (isConnected.check()) {
            json = jsonParser.getJSONFromUrl(AppConfig.URL_API_REFRESH_MARKER, list);
        }
        else {
            jsonParser.setError("No internet connection!");
        }

        return null;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        try {
            // Delete markers from database
            this.dbHelper.clearMarkers();
            // Add markers to SQLite database
            for (int i = 0; i < json.length() - 2; i++) {
                this.dbHelper.insertMarker(Double.parseDouble(json.getJSONObject(Integer.toString(i)).getString("latitude")), Double.parseDouble(json.getJSONObject(Integer.toString(i)).getString("longitude")), json.getJSONObject(Integer.toString(i)).getString("title"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("TEST", "Pobrano markery");
    }



}
package wmp.uksw.pl.googlemaptest_2.helpers;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.Marker;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
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

    private HashSet<MarkerRow> hashSet = new HashSet<>();

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

        for (int i = 0; i < json.length() - 2; i++) {
            try {
                JSONObject jsonObject = json.getJSONObject(Integer.toString(i));
                hashSet.add(new MarkerRow(
                                Integer.parseInt(jsonObject.getString("id")),
                                Double.parseDouble(jsonObject.getString("latitude")),
                                Double.parseDouble(jsonObject.getString("longitude")),
                                jsonObject.getString("title")
                        )
                );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


//        try {
//            // Delete markers from database
//            this.dbHelper.clearMarkers();
//
//            // Get markers list
//            //List<MarkerRow> markerRows = this.dbHelper.getAllMarkers();
//
//            // Add markers to SQLite database
//            for (int i = 0; i < json.length() - 2; i++) {
//                this.dbHelper.insertMarker(
//                        Integer.parseInt(json.getJSONObject(Integer.toString(i)).getString("id")),
//                        Double.parseDouble(json.getJSONObject(Integer.toString(i)).getString("latitude")),
//                        Double.parseDouble(json.getJSONObject(Integer.toString(i)).getString("longitude")),
//                        json.getJSONObject(Integer.toString(i)).getString("title")
//                );
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    public HashSet<MarkerRow> getHashSet() {
        return hashSet;
    }
}
package wmp.uksw.pl.googlemaptest_2.helpers;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MSI on 2015-10-19.
 */
public class DeleteAllMarkersTask extends AsyncTask<Void, Void, Boolean> {

    private List<NameValuePair> list;
    private Context context;
    private JSONObject json;
    private JSONParser jsonParser;

    public DeleteAllMarkersTask(Context context) {
        this.context = context;
        this.list = new ArrayList<>();
        this.jsonParser = new JSONParser();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        IsConnected isConnected = new IsConnected(context);

        if (isConnected.check()) {
            json = jsonParser.getJSONFromUrl(AppConfig.URL_API_DELETE_ALL_MARKERS, list);
        }
        else {
            jsonParser.setError("No internet connection!");
        }


        return null;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
    }

}
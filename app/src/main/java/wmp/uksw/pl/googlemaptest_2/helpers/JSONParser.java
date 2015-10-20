package wmp.uksw.pl.googlemaptest_2.helpers;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by MSI on 2015-10-19.
 */
public class JSONParser {
    // LogCat tag
    private static String TAG = JSONParser.class.getSimpleName();

    private InputStream is;
    private JSONObject jObj;
    private String json;

    private boolean error;
    private String errorMsg;

    // constructor
    public JSONParser() {
        this.is = null;
        this.jObj = null;
        this.json = "";
        this.error = false;
    }

    public JSONObject getJSONFromUrl(String url, List<NameValuePair> params) {

        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            //add data
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
        } catch (Exception e) {
            setError("Something went wrong!");
            return null;
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.d(TAG, "Error converting result " + e.toString());
            setError("Error converting result!");
            return null;
        }

        // try parse the string to a JSON object
        try {
            this.jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.d(TAG, "Error parsing data " + e.toString());
            setError("Error parsing data!");
            return null;
        }

        // check for errors from server
        try {
            if (this.jObj.getBoolean("error"))
                setError(this.jObj.getString("errorMsg"));
        } catch (JSONException e) {
            setError("JSON Exception!");
            return null;
        }


        // return JSON String
        return jObj;

    }

    public void setError(String errorMsg) {
        this.error = true;
        this.errorMsg = errorMsg;
    }

    public boolean getError() { return this.error; }

    public String getErrorMsg() { return this.errorMsg; }

}

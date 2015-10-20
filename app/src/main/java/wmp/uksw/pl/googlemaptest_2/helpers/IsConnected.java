package wmp.uksw.pl.googlemaptest_2.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by MSI on 2015-10-19.
 */
public class IsConnected {

    private Context context;

    public IsConnected(Context context) {
        this.context = context;
    }

    /**
     * If internet service is running, return true or false.
     *
     * @return boolean
     */
    public boolean check() {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
            }
        }
        return false;
    }

}
package wmp.uksw.pl.googlemaptest_2;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import wmp.uksw.pl.googlemaptest_2.database.Contract;
import wmp.uksw.pl.googlemaptest_2.database.DbHelper;
import wmp.uksw.pl.googlemaptest_2.helpers.AddMarkersTask;
import wmp.uksw.pl.googlemaptest_2.helpers.RefreshMarkersTask;
import wmp.uksw.pl.googlemaptest_2.models.MarkerRow;

public class MapsActivity extends FragmentActivity implements LocationListener {

    private static final int LOADER = 0x01;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager locationManager;
    private static final long MIN_TIME = 1 * 60 /* 1000 */; // 1 minute
    private boolean whileLoop = true;
    private Marker marker;
    private Button addMarker;

    private LatLng myLocation;


    private TextView longitude, latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        longitude = (TextView) findViewById(R.id.textView);
        latitude = (TextView) findViewById(R.id.textView2);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, 0, this);

//        Runnable runnableMarker = new Runnable() {
//            @Override
//            public void run() {
//                while (whileLoop) {
//                    try {
//                        Thread.sleep(1000); //1000 ms = 1s
//                        final Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                        if (location != null) {
//                            longitude.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    longitude.setText(Double.toString(location.getLongitude()));
//                                }
//                            });
//
//                            latitude.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    latitude.setText(Double.toString(location.getLatitude()));
//                                }
//                            });
//
//                            Handler handler = new Handler(Looper.getMainLooper());
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if (marker != null)
//                                        marker.remove();
//
//                                    MarkerOptions options = new MarkerOptions();
//                                    options.position(new LatLng(location.getLatitude(), location.getLongitude()));
//                                    options.title("IM HERE!!!");
//
//                                    marker = mMap.addMarker(options);
//                                }
//                            });
//                        }
//
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        };

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();

                LatLng coordinate = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                CameraUpdate center = CameraUpdateFactory.newLatLngZoom(coordinate, 15);

                mMap.animateCamera(center);

                return true;
            }
        });

        Runnable runnableAddMarkersToMap = new Runnable() {
            @Override
            public void run() {
                while (whileLoop) {
                    try {
                        Thread.sleep(1000 * 60 * 2); //1000ms - 1 s
                        DbHelper dbHelper = new DbHelper(getApplicationContext());

                        List<MarkerRow> markerRows = new ArrayList<>();
                        markerRows = dbHelper.getAllMarkers();

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mMap.clear();
                            }
                        });

                        for(int i = 0; i < markerRows.size(); i++) {
                            MarkerOptions options = new MarkerOptions();
                            options.position(new LatLng(markerRows.get(i).getLatitude(), markerRows.get(i).getLongitude()));
                            options.title(markerRows.get(i).getTitle());

                            Marker marker = mMap.addMarker(options);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Runnable runnableRefreshMarkers = new Runnable() {
            @Override
            public void run() {
                //while (whileLoop) {
                    try {
                        Thread.sleep(1000);
                        RefreshMarkersTask refreshMarkersTask = new RefreshMarkersTask(getApplicationContext());
                        refreshMarkersTask.execute();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                //}
            }
        };

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                myLocation = latLng;

                CameraUpdate center = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                mMap.animateCamera(center);
            }
        });

        addMarker = (Button) findViewById(R.id.btnAddMarker);
        addMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latitude != null && longitude != null) {
                    double latitude = myLocation.latitude;
                    double longitude = myLocation.longitude;
                    String title = "IM HERE!!";

                    List<NameValuePair> list = new ArrayList<>();
                    list.add(new BasicNameValuePair("latitude", Double.toString(latitude)));
                    list.add(new BasicNameValuePair("longitude", Double.toString(longitude)));
                    list.add(new BasicNameValuePair("title", title));

                    AddMarkersTask addMarkersTask = new AddMarkersTask(getApplicationContext(), list);
                    addMarkersTask.execute();
                }
            }
        });

        Thread threadAddMarkersToMap = new Thread(runnableAddMarkersToMap);
        //Thread threadMarker = new Thread(runnableMarker);
        Thread threadRefresh = new Thread(runnableRefreshMarkers);
        //threadMarker.start();
        threadRefresh.start();
        threadAddMarkersToMap.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

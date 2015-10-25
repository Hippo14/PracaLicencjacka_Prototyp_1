package wmp.uksw.pl.googlemaptest_2;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import wmp.uksw.pl.googlemaptest_2.database.DbHelper;
import wmp.uksw.pl.googlemaptest_2.helpers.AddMarkersTask;
import wmp.uksw.pl.googlemaptest_2.helpers.DeleteAllMarkersTask;
import wmp.uksw.pl.googlemaptest_2.helpers.RefreshMarkersTask;
import wmp.uksw.pl.googlemaptest_2.models.MarkerRow;

public class MapsActivity extends AppCompatActivity implements LocationListener {
    private static final int LOADER = 0x01;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager locationManager;
    private static final long MIN_TIME = 1 * 60 /* 1000 */; // 1 minute
    private boolean whileLoop = true;
    private Marker marker, auxMarker;
    private List<Marker> markerList;
    private FloatingActionButton addMarker, delMarkers;
    private DbHelper dbHelper;


    private TextView longitude, latitude;
    private View.OnClickListener mOnClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dbHelper = new DbHelper(getApplicationContext());
        this.markerList = new ArrayList<>();
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        // Set textView
        //longitude = (TextView) findViewById(R.id.textView);
        //latitude = (TextView) findViewById(R.id.textView2);

        // Find my location from GPS
        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, 0, this);

        // Set marker listener onClick
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // If temporary marker for adding was here...
                if (auxMarker != null) auxMarker.remove();
                // Show marker info
                marker.showInfoWindow();
                // Set camera to clicked marker
                LatLng coordinate = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                CameraUpdate center = CameraUpdateFactory.newLatLngZoom(coordinate, mMap.getCameraPosition().zoom);
                mMap.animateCamera(center);

                return true;
            }
        });

        // Set map listener onClick
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // If temporary marker for adding was here...
                if (auxMarker != null) auxMarker.remove();

                // Set up temporary marker for user
                final MarkerOptions options = new MarkerOptions();
                options.position(new LatLng(latLng.latitude, latLng.longitude));
                options.title("Pls.. add me...");
                auxMarker = mMap.addMarker(options);
                auxMarker.showInfoWindow();
                // Set camera to temporary marker
                CameraUpdate center = CameraUpdateFactory.newLatLngZoom(latLng, mMap.getCameraPosition().zoom);
                mMap.animateCamera(center);
            }
        });

        // Reset all markers
        delMarkers = (FloatingActionButton) findViewById(R.id.fabBtnDel);
        delMarkers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteAllMarkersTask deleteAllMarkersTask = new DeleteAllMarkersTask(getApplicationContext());
                deleteAllMarkersTask.execute();

                final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.root);

                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "All markers exterminated.", Snackbar.LENGTH_LONG);
                snackbar.setAction("Undo", mOnClickListener)
                        .setActionTextColor(Color.YELLOW);
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(Color.DKGRAY);
                TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.show();
            }
        });

        // Button addMarker to database
        addMarker = (FloatingActionButton) findViewById(R.id.fabBtnAdd);
        addMarker.setFocusable(false);
        addMarker.setFocusableInTouchMode(false);
        addMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auxMarker != null) {
                    // Setting up data for send to database
                    double latitude = auxMarker.getPosition().latitude;
                    double longitude = auxMarker.getPosition().longitude;
                    Random random = new Random();
                    String title = Double.toString(random.nextGaussian());

                    List<NameValuePair> list = new ArrayList<>();
                    list.add(new BasicNameValuePair("latitude", Double.toString(latitude)));
                    list.add(new BasicNameValuePair("longitude", Double.toString(longitude)));
                    list.add(new BasicNameValuePair("title", title));

                    AddMarkersTask addMarkersTask = new AddMarkersTask(getApplicationContext(), list);
                    addMarkersTask.execute();

                    final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.root);

                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Mark added.", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Undo", mOnClickListener)
                            .setActionTextColor(Color.YELLOW);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(Color.DKGRAY);
                    TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    snackbar.show();
                }
                else {
                    final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.root);

                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "But first, let me take a selfie.", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Undo", mOnClickListener)
                            .setActionTextColor(Color.YELLOW);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(Color.DKGRAY);
                    TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    snackbar.show();
                }
            }
        });

        // Threads
        threadRefresh();
        threadAddMarkersToMap();
    }

    public void threadMarker() {
        Runnable runnableMarker = new Runnable() {
            @Override
            public void run() {
                while (whileLoop) {
                    try {
                        Thread.sleep(1000 * 5); //1000 ms = 1s
                        final Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            longitude.post(new Runnable() {
                                @Override
                                public void run() {
                                    longitude.setText(Double.toString(location.getLongitude()));
                                }
                            });

                            latitude.post(new Runnable() {
                                @Override
                                public void run() {
                                    latitude.setText(Double.toString(location.getLatitude()));
                                }
                            });

                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (marker != null)
                                        marker.remove();

                                    MarkerOptions options = new MarkerOptions();
                                    options.position(new LatLng(location.getLatitude(), location.getLongitude()));
                                    options.title("IM HERE!!!");

                                    marker = mMap.addMarker(options);
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread threadMarker = new Thread(runnableMarker);
        threadMarker.start();
    }

    public void threadRefresh() {
        Runnable runnableRefreshMarkers = new Runnable() {
            @Override
            public void run() {
                while (whileLoop) {
                try {
                    Thread.sleep(1000 * 5);
                    RefreshMarkersTask refreshMarkersTask = new RefreshMarkersTask(getApplicationContext());
                    refreshMarkersTask.execute();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                }
            }
        };

        Thread threadRefresh = new Thread(runnableRefreshMarkers);
        threadRefresh.start();
    }

    public void threadAddMarkersToMap() {
        Runnable runnableAddMarkersToMap = new Runnable() {
            @Override
            public void run() {
                while (whileLoop) {
                    try {
                        Thread.sleep(1000 * 5); //1000ms - 1 s

                        List<MarkerRow> markerRows = new ArrayList<>();
                        markerRows = dbHelper.getAllMarkers();

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //mMap.clear();
                                for (int i = 0; i < markerList.size(); i++) {
                                    markerList.get(i).remove();
                                }
                            }
                        });

                        for(int i = 0; i < markerRows.size(); i++) {
                            final MarkerOptions options = new MarkerOptions();
                            options.position(new LatLng(markerRows.get(i).getLatitude(), markerRows.get(i).getLongitude()));
                            options.title(markerRows.get(i).getTitle());

                            Handler handler1 = new Handler(Looper.getMainLooper());
                            handler1.post(new Runnable() {
                                @Override
                                public void run() {
                                    Marker marker = mMap.addMarker(options);
                                    markerList.add(marker);
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread threadAddMarkersToMap = new Thread(runnableAddMarkersToMap);
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

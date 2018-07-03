package com.example.darlokh.test_smartwatch;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import info.metadude.java.library.overpass.models.Element;

/*
TODO:
DONE 1. get own location to request landmarks in a radius form it
2. transform latitude and longitudes to some sort of x-y-coordinates such that canvas can draw them
3. write function that places the TARGETLOCATION on the border if it is not inside the visible/shown area (on the smartwatch)
*/

public class MainActivity extends AppCompatActivity {
    final String TAG = "MainActivity";
    private Integer PLACE_PICKER_REQUEST = 42;
    private Integer MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 43;
    PlacePicker.IntentBuilder builder;
    private permissionHelper mPermissionHelper;
    private Activity thisActivity;
    private List<Element> mElementList;
    private ResponseReceiver receiver;
    private String json;
    private JSONArray jsonArray = new JSONArray();
    private JSONObject jsonObject = new JSONObject();
    private IntentFilter filter;
    private static final String LANDMARKDATA_KEY = "com.example.key.landmarkdata";
    private DataClient mDataClient;
    private GoogleApiClient mGoogleApiClient;
    private LatLngBounds targetLocation;
    private double myCurrentLatitude = 0;
    private double myCurrentLongitude = 0;
    private LocationManager mLocationManager;
    private static final long LOCATION_REFRESH_TIME =  5000;
    private static final float LOCATION_REFRESH_DISTANCE = 5;
    private LandmarkContainer lmContainer = new LandmarkContainer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity thisActivity = this;
        builder = new PlacePicker.IntentBuilder();
        mPermissionHelper = new permissionHelper();
        receiver = new ResponseReceiver();
        filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, filter);

        mPermissionHelper.checkPermission(thisActivity);
        mPermissionHelper.checkGooglePlayServices(getApplicationContext(), thisActivity);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE,
                mLocationListener);

        connectToWearable();
        initLandmarkData();

        setContentView(R.layout.activity_main);

        final Button buttonFoo = findViewById(R.id.button);
        final Button buttonBar = findViewById(R.id.button2);
        final Button buttonFoobar = findViewById(R.id.button3);

        final queryHelper mQueryHelper = new queryHelper();
        buttonFoo.setOnClickListener(mQueryHelper.handleClick);

        final Intent queryIntent = new Intent(this, queryService.class);
        buttonFoobar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(queryIntent);
            }
        });

        buttonBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTargetLocation();
            }

        });

    }

    void popToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            myCurrentLatitude = location.getLatitude();
            myCurrentLongitude = location.getLongitude();
            lmContainer.setMyLocation(new Landmark(myCurrentLongitude, myCurrentLatitude, "myLocation"));
            String msg = Double.toString(myCurrentLatitude) + ' ' + Double.toString(myCurrentLongitude);
            popToast(msg);
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
    };


    private void initLandmarkData() {
        String initLandmark = "";
        putLandmarkData(initLandmark);
    }

    private void putLandmarkData(String jsonLandmarkData) {
        if(mGoogleApiClient == null)
            return;
        final PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/landmarkData");
        final DataMap map = putDataMapReq.getDataMap();
        map.putString(LANDMARKDATA_KEY, jsonLandmarkData);
        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataMapReq.asPutDataRequest());
    }

    private void connectToWearable() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP =
                "com.example.darlokh.test_smartwatch.intent.action.MESSAGE_PROCESSED";

        @Override
        public void onReceive(Context context, Intent intent) {
            json = intent.getStringExtra(queryService.PARAM_OUT_MSG);
            Gson gson = new Gson();
            mElementList = gson.fromJson(json, List.class);
            System.out.println("OSM data received in mainActivity.");
            try {
                jsonObject = new JSONObject("{locations:" + intent.getStringExtra(queryService.PARAM_OUT_MSG) + "}");
                jsonArray = jsonObject.getJSONArray("locations");
            } catch (Exception e ){
                e.printStackTrace();
            }
            foobar();
        }
    }

    @Override
    protected void onPause() {
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        registerReceiver(receiver, filter);
        super.onResume();
    }

    // do something with the data from OSM
    protected void foobar() {
        lmContainer.clearLmArray();
        try {
//            String tag = String.format("FOOBAR: %s", jsonArray.getJSONObject(1).get("lat").toString());
//            Toast.makeText(this.getApplicationContext(), tag, Toast.LENGTH_SHORT).show();
            lmContainer.setMyLocation(new Landmark(myCurrentLongitude, myCurrentLatitude, "myLocation"));
            lmContainer.distanceLandmarksToMyLocation();
            for (int i = 0; i < jsonArray.length(); i++) {
                double lat = jsonArray.getJSONObject(i).getDouble("lat");
                double lon = jsonArray.getJSONObject(i).getDouble("lon");
                JSONObject jsonObjectTags = (JSONObject) jsonArray.getJSONObject(i).get("tags");
                String tags = jsonObjectTags.getString("amenity");
                Landmark tmpLm = new Landmark(lon, lat, tags);
                lmContainer.addLandmark(tmpLm);
                Log.d(TAG, "foobar: filling lmContainer with landmarks from OSM.");
            }
//            lmContainer.sortByDistance();
            Log.d(TAG, "foobar: lmArr size: " + lmContainer.getLmArr().size());
            for(int i = 0; i < lmContainer.getLmArr().size(); i++){
                System.out.println(lmContainer.getLmArr().get(i).dist);
            }
            putLandmarkData(lmContainer.containerToJSONObject().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    // save the targetLocation
    // blog entry about PlacePicker
    // https://medium.com/exploring-android/exploring-play-services-place-picker-autocomplete-150809f739fe
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String toastMsg = String.format("Place: %s", place.getName());
                targetLocation = PlacePicker.getLatLngBounds(data);
                double lon = targetLocation.getCenter().longitude;
                double lat = targetLocation.getCenter().latitude;
                lmContainer.setTargetLocation(new Landmark(lon, lat, "myTarget"));

                Log.d(TAG, "onActivityResult: " + targetLocation);
//                String toastMsg2 = String.format("Place: %s", place.getName());

                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    // let user select his target location -> 'onActivityResult' saves the selected location
    private void getTargetLocation(){
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            mPermissionHelper.checkGooglePlayServices(getApplicationContext(), thisActivity);
            Log.d(TAG, "getTargetLocation: FAILED");
        }
    }
}

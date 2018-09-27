package com.example.darlokh.test_smartwatch;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
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

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import info.metadude.java.library.overpass.models.Element;

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
    private static double myCurrentLatitude = 0;
    private static double myCurrentLongitude = 0;
    private LocationManager mLocationManager;
    private static final long LOCATION_REFRESH_TIME =  5000;
    private static final float LOCATION_REFRESH_DISTANCE = 5;
    private LandmarkContainer lmContainer = new LandmarkContainer();
    private Integer maxAmountOfIcons = 20;
    private double viewRadiusInKilometers = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity thisActivity = this;
        mPermissionHelper = new permissionHelper();
        mPermissionHelper.checkPermission(thisActivity);
        mPermissionHelper.checkGooglePlayServices(getApplicationContext(), thisActivity);

        builder = new PlacePicker.IntentBuilder();
        receiver = new ResponseReceiver();
        filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        mDataClient = Wearable.getDataClient(this);
        registerReceiver(receiver, filter);

        if (ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE,
                    mLocationListener);
        }

        connectToWearable();
        initLandmarkData();
        initTargetLocation();

        setContentView(R.layout.activity_main);

//        final Button buttonFoo = findViewById(R.id.button);
        final Button buttonBar = findViewById(R.id.button2);
        final Button buttonFoobar = findViewById(R.id.button3);

        final Intent queryIntent = new Intent(this, queryService.class);
        queryService.myLat = myCurrentLatitude;
        queryService.myLon = myCurrentLongitude;

        buttonFoobar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryService.myLat = myCurrentLatitude;
                queryService.myLon = myCurrentLongitude;

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
//            popToast(msg);
            updateData();
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
        String initLandmark = "[{\"x\":0,\"y\":0,\"tag\":\"myLocation\"}]";
        putLandmarkData(initLandmark);
    }
    private void initTargetLocation() {
        targetLocation = new LatLngBounds(new LatLng(0.0, 0.0),new LatLng(0.0, 0.0));
    }

    private void putLandmarkData(String jsonLandmarkData) {
        if(mGoogleApiClient == null)
            return;
        final PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/landmarkData");
        final DataMap map = putDataMapReq.getDataMap();
        map.putString(LANDMARKDATA_KEY, jsonLandmarkData);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        Task<DataItem> putDataItem = mDataClient.putDataItem(putDataReq);
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
            try {
                jsonObject = new JSONObject("{locations:" + intent.getStringExtra(queryService.PARAM_OUT_MSG) + "}");
                jsonArray = jsonObject.getJSONArray("locations");
            } catch (Exception e ){
                e.printStackTrace();
            }
            updateData();
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

    // update the data from OSM with respect to the users location
    protected void updateData() {
        lmContainer.clearLmArray();
        try {
            lmContainer.setMyLocation(new Landmark(myCurrentLongitude, myCurrentLatitude, "myLocation"));
            double lonTarget = targetLocation.getCenter().longitude;
            double latTarget = targetLocation.getCenter().latitude;
            int amountOfIcons;
            if (jsonArray.length() < maxAmountOfIcons) {
                amountOfIcons = jsonArray.length();
            } else {
                amountOfIcons = maxAmountOfIcons;
            }
            lmContainer.setTargetLocation(new Landmark(lonTarget, latTarget, "myTarget"));
            for (int i = 0; i < jsonArray.length(); i++) {
                double lon = jsonArray.getJSONObject(i).getDouble("lon");
                double lat = jsonArray.getJSONObject(i).getDouble("lat");
                JSONObject jsonObjectTags = (JSONObject) jsonArray.getJSONObject(i).get("tags");
                String tags = "";
                if (jsonObjectTags.has("natural")) {
                    tags = "natural";
                } else if (jsonObjectTags.has("historic")) {
                    tags = "historic";
                } else if (jsonObjectTags.has("man_made")) {
                    tags = "man_made";
                } else if (jsonObjectTags.has("waterway")) {
                    tags = "waterway";
                } else if (jsonObjectTags.has("building")) {
                    tags = "building";
                }
                Landmark tmpLm = new Landmark(lon, lat, tags);
                lmContainer.addLandmark(tmpLm);
            }

            lmContainer.translateLatLonIntoXY();
            lmContainer.setLandmarksIntoLocalCoords();
            lmContainer.distanceLandmarksToMyLocation();
            lmContainer.sortByDistance();
            for (int i = lmContainer.getLmArr().size() - 1; i >= amountOfIcons; i--){
                lmContainer.getLmArr().remove(i);
            }
            lmContainer.calcFactor(viewRadiusInKilometers);
            lmContainer.transformCoordsIntoCanvasResolution();
            putLandmarkData(lmContainer.containerToJSONObject().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
//        try {
//            unregisterReceiver(receiver);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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
        }
    }
}
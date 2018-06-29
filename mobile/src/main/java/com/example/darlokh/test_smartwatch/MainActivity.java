package com.example.darlokh.test_smartwatch;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import info.metadude.java.library.overpass.models.Element;

/*TODO:
1. get own location to request landmarks in a radius form it
2. transform latitude and longitudes to some sort of x-y-coordinates such that canvas can draw them
3.
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity thisActivity = this;
        builder = new PlacePicker.IntentBuilder();
        mPermissionHelper = new permissionHelper();
        filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, filter);

//        final Intent intentQueryService = new Intent(this, queryService.class);
        mPermissionHelper.checkPermission(thisActivity);
        mPermissionHelper.checkGooglePlayServices(getApplicationContext(), thisActivity);
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
                prepareData();
            }

        });

    }

    public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP =
                "com.example.darlokh.test_smartwatch.intent.action.MESSAGE_PROCESSED";

        @Override
        public void onReceive(Context context, Intent intent) {
            json = intent.getStringExtra(queryService.PARAM_OUT_MSG);// getParcelableExtra(queryService.PARAM_OUT_MSG);
            Gson gson = new Gson();
            mElementList = gson.fromJson(json, List.class);
            System.out.println("HELP");
            try {
                jsonObject = new JSONObject("{locations:" + intent.getStringExtra(queryService.PARAM_OUT_MSG) + "}");
                jsonArray = jsonObject.getJSONArray("locations");
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    System.out.println(jsonArray.getJSONObject(i));
//                }
            } catch (Exception e ){
                e.printStackTrace();
            }
            foobar();
        }
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        registerReceiver(receiver, filter);
        super.onResume();
    }

    protected void foobar() {
        try {
            String tag = String.format("FOOBAR: %s", jsonArray.getJSONObject(1).get("lat").toString());
            Toast.makeText(this.getApplicationContext(), tag, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    // blog entry about PlacePicker
    // https://medium.com/exploring-android/exploring-play-services-place-picker-autocomplete-150809f739fe
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String toastMsg = String.format("Place: %s", place.getName());
                LatLngBounds latlngPlace = PlacePicker.getLatLngBounds(data);
                Log.d(TAG, "onActivityResult: " + latlngPlace);
//                String toastMsg2 = String.format("Place: %s", place.getName());

                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void prepareData(){
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            mPermissionHelper.checkGooglePlayServices(getApplicationContext(), thisActivity);
            Log.d(TAG, "prepareData: FAILED");
        }
    }
}

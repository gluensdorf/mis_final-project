package com.example.darlokh.test_smartwatch;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class permissionHelper {
    final String TAG = "permissionHelper";
    private Integer MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 43;

    public void checkPermission(Activity thisActivity) {
        // followed this guide https://developer.android.com/training/permissions/requesting.html
        //permission added to manifest
        if (ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(thisActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            );
//            }
        } else {
            // just nope
        }
    }

    // https://stackoverflow.com/questions/46632651/exception-googleplayservices-not-available-due-to-error-2
    public void checkGooglePlayServices(Context givenContext, Activity givenActivity){
        switch (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(givenContext)){
            case ConnectionResult.SERVICE_MISSING:
                GoogleApiAvailability.getInstance().getErrorDialog(givenActivity,ConnectionResult.SERVICE_MISSING,0).show();
                Log.d(TAG, "checkGooglePlayServices: SERVICE_MISSING");
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                GoogleApiAvailability.getInstance().getErrorDialog(givenActivity,ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED,0).show();
                Log.d(TAG, "checkGooglePlayServices: SERVICE_VERSION_UPDATE_REQUIRED");
                break;
            case ConnectionResult.SERVICE_DISABLED:
                GoogleApiAvailability.getInstance().getErrorDialog(givenActivity, ConnectionResult.SERVICE_DISABLED,0).show();
                Log.d(TAG, "checkGooglePlayServices: SERVICE_DISABLED");
                break;
        }
    }
}

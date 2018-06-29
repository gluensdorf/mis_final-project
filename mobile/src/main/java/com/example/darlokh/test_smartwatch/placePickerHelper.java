package com.example.darlokh.test_smartwatch;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLngBounds;

public class placePickerHelper extends MainActivity {
    final String TAG = "placePickerHelper";
    private Integer PLACE_PICKER_REQUEST = 42;
    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

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

    public void whatever(){
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
//            Intent intent =
//                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
//                            .setFilter(cityFilter)
//                            .build(getActivity());
//            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
//            mPermissionHelper.checkGooglePlayServices();
            Log.d(TAG, "whatever: NARF");
        }

    }
}

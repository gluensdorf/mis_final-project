package com.example.darlokh.test_smartwatch;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import java.util.List;
import info.metadude.java.library.overpass.ApiModule;
import info.metadude.java.library.overpass.OverpassService;
import info.metadude.java.library.overpass.models.Element;
import info.metadude.java.library.overpass.models.OverpassResponse;
import retrofit2.Call;
import retrofit2.Response;

import static junit.framework.Assert.fail;

public class queryService extends IntentService {
    private static final String TAG = "queryService";
    public static List<Element> elementsList;
    public static final String PARAM_OUT_MSG = "omsg";
    public static String elementsListToString = "noElements";
    public static double myLat = 0;
    public static double myLon = 0;

    public queryService(){
        super("queryService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            String OSMformatTimeout = "[out:json][timeout:30];";
            String OSMaround = "(around:25000,"
                    + Double.toString(myLat)
                    + ","
                    + Double.toString(myLon)
                    + ");";
            String OSMqueryEnding = ");(._;>;); out body center qt;";

            String OSMtower = "node[man_made=tower]";
            String OSMnatural = "node[natural=peak]";
            String OSMbuilding = "node[building=church]";
            String OSMwaterway = "node[waterway=weir]";
            String OSMhistoric = "node[historic=monument]";

            String query = OSMformatTimeout
                    + "("
                    + OSMtower + OSMaround
                    + OSMnatural + OSMaround
                    + OSMbuilding + OSMaround
                    + OSMwaterway + OSMaround
                    + OSMhistoric + OSMaround
                    + OSMqueryEnding;
            OverpassService streamsService = ApiModule.provideOverpassService();

            Call<OverpassResponse> streamsResponseCall = streamsService.getOverpassResponse(query);

            Response<OverpassResponse> response = streamsResponseCall.execute();
            if(response.isSuccessful()){
                Gson gson = new Gson();

                Log.d(TAG, "onHandleIntent: it was successful");
                OverpassResponse overpassResponse = response.body();

                overpassResponse.elements.toArray();
                elementsList = overpassResponse.elements;
                String json = gson.toJson(elementsList);
                elementsListToString = json;
            } else {
                fail("Query failed.");
            }
            Intent broadCastIntent = new Intent();

            broadCastIntent.setAction(MainActivity.ResponseReceiver.ACTION_RESP);
            broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadCastIntent.putExtra(PARAM_OUT_MSG, elementsListToString);
            sendBroadcast(broadCastIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

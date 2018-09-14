package com.example.darlokh.test_smartwatch;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.metadude.java.library.overpass.ApiModule;
import info.metadude.java.library.overpass.OverpassService;
import info.metadude.java.library.overpass.models.Element;
import info.metadude.java.library.overpass.models.OverpassResponse;
import info.metadude.java.library.overpass.utils.NodesQuery;
import retrofit2.Call;
import retrofit2.Response;

import static junit.framework.Assert.fail;

public class queryService extends IntentService {
    private static final String TAG = "queryService";
    public static List<Element> elementsList;
    public static final String PARAM_OUT_MSG = "omsg";
    public static String elementsListToString = "WOLOLOL ";
    public static double myLat = 0;
    public static double myLon = 0;

    public queryService(){
        super("queryService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            OverpassService streamsService = ApiModule.provideOverpassService();
            Map<String, String> tags = new HashMap<String, String>() {
                {
                    put("amenity", "place_of_worship");
                }
            };
            Log.d(TAG, "onHandleIntent: lat, lon" + myLat + ", " + myLon);
            NodesQuery nodesQuery = new NodesQuery(3000, myLat, myLon, tags, true, 13);
            Call<OverpassResponse> streamsResponseCall = streamsService.getOverpassResponse(
            nodesQuery.getFormattedDataQuery());
            Response<OverpassResponse> response = streamsResponseCall.execute();
            if(response.isSuccessful()){
                Gson gson = new Gson();
                OverpassResponse overpassResponse = response.body();

                overpassResponse.elements.toArray();
                elementsList = overpassResponse.elements;
                String json = gson.toJson(elementsList);
                elementsListToString = json;
                System.out.println(elementsListToString);
            } else {
                fail("Query failed.");
            }
//            System.out.println(elementsListToString);
            Intent broadCastIntent = new Intent();
            // TODO: need to 'implements' Parcelable or some other interface (are those interfaces?)

            broadCastIntent.setAction(MainActivity.ResponseReceiver.ACTION_RESP);
            broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadCastIntent.putExtra(PARAM_OUT_MSG, elementsListToString);
            sendBroadcast(broadCastIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//    }
//
////    public queryService() {
////    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Thread foo = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        OverpassService streamsService = ApiModule.provideOverpassService();
//                        Map<String, String> tags = new HashMap<String, String>() {
//                            {
//                                put("amenity", "post_box");
//                            }
//                        };
//                        NodesQuery nodesQuery = new NodesQuery(600, 52.516667, 13.383333, tags, true, 13);
//                        Call<OverpassResponse> streamsResponseCall = streamsService.getOverpassResponse(
//                                nodesQuery.getFormattedDataQuery());
//                        Response<OverpassResponse> response = streamsResponseCall.execute();
//                        if(response.isSuccessful()){
//                            OverpassResponse overpassResponse = response.body();
//                            elementsList = overpassResponse.elements;
//                            Log.d(TAG, "run: response.isSuccessful");
//                            for(int i = 0; i < elementsList.size(); i++){
//                                Log.d(TAG, "elements: " + elementsList.get(i));
//                            }
//                        } else {
//                            fail("Query failed.");
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        foo.start();
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    @Override
//    public void onDestroy() {
//
//        super.onDestroy();
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return aBinder;
//    }
}

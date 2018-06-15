package com.example.darlokh.test_smartwatch;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.metadude.java.library.overpass.OverpassService;
import info.metadude.java.library.overpass.models.Element;
import info.metadude.java.library.overpass.models.OverpassResponse;
import info.metadude.java.library.overpass.utils.NodesQuery;
import info.metadude.java.library.overpass.ApiModule;
import retrofit2.Call;
import retrofit2.Response;

import static junit.framework.Assert.fail;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);

        final Button buttonFoo = findViewById(R.id.button);
        final String TAG = "TOASTBROT";

        buttonFoo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OverpassService streamsService = ApiModule.provideOverpassService();
                            Map<String, String> tags = new HashMap<String, String>() {
                                {
                                    put("amenity", "post_box");
                                }
                            };
                            NodesQuery nodesQuery = new NodesQuery(600, 52.516667, 13.383333, tags, true, 13);
                            Call<OverpassResponse> streamsResponseCall = streamsService.getOverpassResponse(
                                    nodesQuery.getFormattedDataQuery());
                            Response<OverpassResponse> response = streamsResponseCall.execute();
                            if(response.isSuccessful()){
                                OverpassResponse overpassResponse = response.body();
                                List<Element> elements = overpassResponse.elements;
                                Log.d(TAG, "run: response.isSuccessful");
                                for(int i = 0; i < elements.size(); i++){
                                    Log.d(TAG, "elements: " + elements.get(i));
                                }
                            } else {
                                fail("Query failed.");
                            }} catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

    }
}

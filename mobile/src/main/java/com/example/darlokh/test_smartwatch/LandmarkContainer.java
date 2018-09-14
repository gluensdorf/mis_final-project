package com.example.darlokh.test_smartwatch;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LandmarkContainer {
    private ArrayList<Landmark> lmArr = new ArrayList<Landmark>();
    private Landmark myLocation;
    private Landmark targetLocation;
    private String TAG = "lmContainer";

    public LandmarkContainer(){}

    public void addLandmark(Landmark newLandmark){
        lmArr.add(newLandmark);
    }

    public void distanceLandmarksToMyLocation(){
        for(int i=0; i < lmArr.size(); i++){
            lmArr.get(i).euclideanDist(myLocation);
            System.out.println(lmArr.get(i).dist);
        }
        targetLocation.euclideanDist(myLocation);
//        myLocation.x = myLocation.x - myLocation.x;
//        myLocation.y = myLocation.y - myLocation.y;
    }

    public void clearLmArray(){
        lmArr = new ArrayList<Landmark>();
    }

    public void sortByDistance(){
        Collections.sort(lmArr, new Comparator<Landmark>() {
            @Override
            public int compare(Landmark o1, Landmark o2) {
                double compResult = (o1.dist - o2.dist);
                if(compResult > 0){
                    return 1;
                } else if (compResult < 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }

    public JSONObject myLocationToJSONObject(){
        JSONObject tmp = new JSONObject();
        try {
            tmp.put("x", myLocation.x);
            tmp.put("y", myLocation.y);
            tmp.put("tag", myLocation.tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmp;
    }
    public JSONObject myTargetToJSONObject(){
        JSONObject tmp = new JSONObject();
        try {
            tmp.put("x", targetLocation.x);
            tmp.put("y", targetLocation.y);
            tmp.put("tag", targetLocation.tag);
            tmp.put("dist", targetLocation.dist);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmp;
    }

    // builds a JSONObject such that myLocation is at index 0, targetLocation at index 1 and
    // landmarks from OSM are added sorted by 'dist' (euclidean distance to myLocation)
    public JSONArray containerToJSONObject(){
        JSONObject tmpLocation = myLocationToJSONObject();
        JSONObject tmpTarget = myTargetToJSONObject();
        JSONArray tmpArr = new JSONArray();
        tmpArr.put(tmpLocation);
        tmpArr.put(tmpTarget);
        sortByDistance();

        for(int i=0; i < lmArr.size(); i++){
            JSONObject tmp = new JSONObject();
            try {
                tmp.put("x", lmArr.get(i).x);
                tmp.put("y", lmArr.get(i).y);
                tmp.put("tag", lmArr.get(i).tag);
                tmp.put("dist", lmArr.get(i).dist);
                tmpArr.put(tmp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tmpArr;
    }

    public void JSONObjectToContainer(JSONObject obj){
//        try {
//            obj.getJSONArray()
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public ArrayList<Double> getMinMaxCoords(){
        double tmpMaxLat = -99999;
        double tmpMinLat = 99999;
        double tmpMaxLon = -99999;
        double tmpMinLon = 99999;
        Log.d(TAG, "getMinMaxCoords: lmArr.size()" + lmArr.size());
        for (int i=0; i<lmArr.size(); i++){
            // landmark(lon, lat)
            Log.d(TAG, "getMinMaxCoords max lmArr.x/lon: " + lmArr.get(i).x);
            tmpMaxLon = Math.max(lmArr.get(i).x, tmpMaxLon);
            tmpMinLon = Math.min(lmArr.get(i).x, tmpMinLon);
            Log.d(TAG, "getMinMaxCoords max lmArr.y/lat: " + lmArr.get(i).y);
            tmpMaxLat = Math.max(lmArr.get(i).y, tmpMaxLat);
            tmpMinLat = Math.min(lmArr.get(i).y, tmpMinLat);
//            lmArr.get(i).x = lmArr.get(i).x - myLocation.x;
//            lmArr.get(i).y = lmArr.get(i).y - myLocation.y;
        }
        ArrayList<Double> result = new ArrayList<Double>();
        result.add(tmpMaxLon);
        result.add(tmpMinLon);
        result.add(tmpMaxLat);
        result.add(tmpMinLat);
        // select the value which is furthest away from myLocation, times 1000 to get meters
        result.add(Math.max(Math.abs(tmpMaxLon) - Math.abs(tmpMinLon),
                Math.abs(tmpMaxLat) - Math.abs(tmpMinLat)));
        return result;
    }

    public void setLandmarksIntoLocalCoords(){
        for (int i=0; i<lmArr.size(); i++){
            lmArr.get(i).x = lmArr.get(i).x - myLocation.x;
            lmArr.get(i).y = lmArr.get(i).y - myLocation.y;
        }
        targetLocation.x -= myLocation.x;
        targetLocation.y -= myLocation.y;
        myLocation.x -= myLocation.x;
        myLocation.y -= myLocation.y;
    }

    public void translateLatLonIntoXY(){
        int radiusEarth = 6371; // km
        for (int i=0; i<lmArr.size(); i++) {
            lmArr.get(i).x = lmArr.get(i).x * radiusEarth * Math.cos(myLocation.y);
            lmArr.get(i).y = lmArr.get(i).y * radiusEarth;
        }
        Log.d(TAG, "translateLatLonIntoXY: " + targetLocation.x);
        targetLocation.x = targetLocation.x * radiusEarth * Math.cos(myLocation.y);
        targetLocation.y = targetLocation.y * radiusEarth;
        myLocation.x = myLocation.x * radiusEarth * Math.cos(myLocation.y);
        myLocation.y = myLocation.y * radiusEarth;

    }

    public void setCoordsIntoCanvasResolution(double factor){
        for (int i=0; i<lmArr.size(); i++) {
            lmArr.get(i).x = 160 + lmArr.get(i).x * (factor/320);
            lmArr.get(i).y = 160 + lmArr.get(i).y * (factor/320);
        }
        targetLocation.x = 160 + targetLocation.x * (factor/320);
        targetLocation.y = 160 + targetLocation.y * (factor/320);
        myLocation.x = 160 + myLocation.x;
        myLocation.y = 160 + myLocation.y;
    }

    public Landmark getMyLocation(){
        return myLocation;
    }

    public ArrayList<Landmark> getLmArr() {
        return lmArr;
    }

    public Landmark getTargetLocation(){
        return targetLocation;
    }
    public void setMyLocation(Landmark myLoc){
        myLocation = myLoc;
    }
    public void setTargetLocation(Landmark targetLoc){
        targetLocation = targetLoc;
    }
}

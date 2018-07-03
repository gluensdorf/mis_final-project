package com.example.darlokh.test_smartwatch;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LandmarkContainer {
    private ArrayList<Landmark> lmArr = new ArrayList<Landmark>();
    private Landmark myLocation;
    private Landmark targetLocation;

    public LandmarkContainer(){}

    public void addLandmark(Landmark newLandmark){
        lmArr.add(newLandmark);
    }
    public void distanceLandmarksToMyLocation(){
        for(int i=0; i < lmArr.size(); i++){
            lmArr.get(i).euclideanDist(myLocation);
        }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmp;
    }

    // JSONObject -> JSONArray -> multiple JSONObject
    public JSONArray containerToJSONObject(){
        JSONObject tmpLocation = myLocationToJSONObject();
        JSONObject tmpTarget = myTargetToJSONObject();
        JSONArray tmpArr = new JSONArray();
        tmpArr.put(tmpLocation);
        tmpArr.put(tmpTarget);

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

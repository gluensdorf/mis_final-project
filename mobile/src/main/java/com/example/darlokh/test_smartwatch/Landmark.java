package com.example.darlokh.test_smartwatch;

public class Landmark {
    public double x;
    public double y;
    public String tag;
    public double dist;

    public Landmark(double newX, double newY, String newTag){
        x = newX;
        y = newY;
        tag = newTag;
    }

    //TODO: distance is still too large
    public void euclideanDist(Landmark otherLandmark){
        dist = Math.sqrt(Math.pow(otherLandmark.x - x, 2) + Math.pow(otherLandmark.y - y, 2));
        x -= otherLandmark.x;
        y -= otherLandmark.y;
        System.out.println(x);
    }

//
//    //get-Functions
//    int getX(){return x;}
//    int getY(){return y;}
//    String getTag(){return tag;}
//
//    //set-Functions
//    void setX(int newX){x = newX;}
//    void setY(int newY){y = newY;}
//    void setTag(String newTag){tag = newTag;}
}
package com.example.darlokh.test_smartwatch;

public class Landmark {
    public double x;
    public double y;
    public String tag;
    public double dist;

    public Landmark(double newX, double newY, String newTag){
        x = newX; //Longitude
        y = newY; //Latitude
        tag = newTag;
    }

    public void euclideanDist(Landmark otherLandmark){
        dist = Math.sqrt(Math.pow(otherLandmark.x - x, 2) + Math.pow(otherLandmark.y - y, 2));
    }
}
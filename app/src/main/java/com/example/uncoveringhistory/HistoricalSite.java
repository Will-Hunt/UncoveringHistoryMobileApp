package com.example.uncoveringhistory;

import com.google.android.gms.maps.model.LatLng;

public class HistoricalSite {
    String name;
    String description;
    String type;
    String location;
    String imageName;

    public HistoricalSite(String name, String description, String type, String location, String imageName) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.location = location;
        this.imageName = imageName;
    }

    public String getName() {
        return name;
    }
    public String getDescription(){
        return description;
    }
    public String getLocation() {
        return location;
    }
    public String getType(){
        return type;
    }
    public String getImageName() {
        return imageName;
    }
}
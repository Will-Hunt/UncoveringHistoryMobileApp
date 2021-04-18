package com.example.uncoveringhistory;

import com.google.android.gms.maps.model.LatLng;

public class HistoricalSite {
    String name;
    String description;
    String type;
    LatLng location;
    String imageName;

    public HistoricalSite(String name, String description, String type, LatLng location, String imageName) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.location = location;
        this.imageName = imageName;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
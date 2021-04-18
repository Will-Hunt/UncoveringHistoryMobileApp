package com.example.uncoveringhistory;

import com.google.android.gms.maps.model.LatLng;

public class HistoricalSite {
    String name;
    String description;
    LatLng location;

    public HistoricalSite(String name, String description, LatLng location) {
        this.name = name;
        this.description = description;
        this.location = location;
    }

   public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LatLng getLocation() {
        return location;
    }

}
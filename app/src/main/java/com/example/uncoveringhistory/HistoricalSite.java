package com.example.uncoveringhistory;

public class HistoricalSite {
    String name;
    String description;
    String location;

    public HistoricalSite(String name, String description, String location) {
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

    public String getLocation() {
        return location;
    }
}
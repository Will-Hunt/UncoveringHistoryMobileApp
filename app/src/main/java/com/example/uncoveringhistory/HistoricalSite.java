package com.example.uncoveringhistory;

public class HistoricalSite {
    String name;
    String description;
    String location;
//    String imageName;

    public HistoricalSite(String name, String description, String location, String imageName) {
        this.name = name;
        this.description = description;
        this.location = location;
//        this.imageName = imageName;
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

//    public String getImage() { return imageName; }
}
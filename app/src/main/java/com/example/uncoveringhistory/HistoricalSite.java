package com.example.uncoveringhistory;

public class HistoricalSite {
    String name;
    String description;
    String type;
    String location;
    String imageName;
    Boolean favourite;

    public HistoricalSite(String name, String description, String type, String location, String imageName, Boolean favourite) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.location = location;
        this.imageName = imageName;
        this.favourite = favourite;
    }

    public String getName() {
        return name;
    }

    public String getDescription() { return description;}

    public String getLocation() {
        return location;
    }

    public String getType() {return type; }

    public String getImageName() {
        return imageName;
    }

    public Boolean getFavourite() {
        return favourite;
    }
}
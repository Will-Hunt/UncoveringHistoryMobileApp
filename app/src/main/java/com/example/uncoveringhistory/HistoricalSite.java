package com.example.uncoveringhistory;

public class HistoricalSite {
    String name;
    String description;
    String type;
    String location;
    String imageName;
    Boolean checked;

    public HistoricalSite(String name, String description, String type, String location, String imageName, Boolean checked) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.location = location;
        this.imageName = imageName;
        this.checked = checked;
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

    public Boolean getChecked() {
        return checked;
    }
}
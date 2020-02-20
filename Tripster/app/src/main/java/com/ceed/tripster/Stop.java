package com.ceed.tripster;

public class Stop {

    private String name;
    private String type;
    private String address;
    private double latitude;
    private double longitude;

    private int index;

    public Stop() {

    }

    public Stop(String name, String type, String address, double latitude, double longitude, int index) {
        this.name = name;
        this.type = type;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.index = index;
    }

    public Stop(Stop c) {
        this.name = c.name;
        this.type = c.type;
        this.address  = c.address;
        this.latitude = c.latitude;
        this.longitude = c.longitude;
        this.index = c.index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getIndex() { return index; }

    public void setIndex(int index) { this.index = index; }

}
package com.ceed.tripster;

import java.util.HashMap;

public class Trip {

    public Trip() {

    }

    public Trip(String name, HashMap<String, Stop> stops, HashMap<String, Integer> memberIds) {
        this.name = name;
        this.stops = stops;
        this.memberIds = memberIds;
    }

    private String name;

    private HashMap<String, Stop> stops;
    private HashMap<String, Integer> memberIds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Stop> getStops() {
        return stops;
    }

    public void setStops(HashMap<String, Stop> stops) {
        this.stops = stops;
    }

    public HashMap<String, Integer> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(HashMap<String, Integer> memberIds) {
        this.memberIds = memberIds;
    }


}
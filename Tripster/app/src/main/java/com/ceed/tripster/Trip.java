package com.ceed.tripster;

import java.util.HashMap;

public class Trip {

    public Trip() {

    }

    public Trip(String name, String start, String destination, HashMap<String, Stop> stops, HashMap<String, String> memberIds) {
        this.name = name;
        this.start = start;
        this.destination = destination;
        this.stops = stops;
        this.memberIds = memberIds;
    }

    private String name;

    private String start;
    private String destination;

    private HashMap<String, Stop> stops;
    private HashMap<String, String> memberIds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart() { return start; }

    public void setStart(String start) { this.start = start; }

    public String getDestination() { return destination; }

    public void setDestination(String destination) { this.destination = destination; }

    public HashMap<String, Stop> getStops() { return stops; }

    public void setStops(HashMap<String, Stop> stops) { this.stops = stops; }

    public HashMap<String, String> getMemberIds() { return memberIds; }

    public void setMemberIds(HashMap<String, String> memberIds) { this.memberIds = memberIds; }


}
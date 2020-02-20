package com.ceed.tripster;

import java.util.HashMap;
import java.util.Map;

public class Trip {

    private String name;

    private String start;
    private String destination;

    private HashMap<String, Stop> stops;
    private HashMap<String, String> memberIds;

    public Trip() {

    }

    public Trip(String name, String start, String destination, HashMap<String, Stop> stops, HashMap<String, String> memberIds) {
        this.name = name;
        this.start = start;
        this.destination = destination;
        this.stops = stops;
        this.memberIds = memberIds;
    }

    public Trip(Trip c) {
        this.name = c.name;
        this.start = c.start;
        this.destination = c.destination;

        for (Map.Entry mapElement : c.getStops().entrySet()) {
            Stop newStop = new Stop((Stop) mapElement.getValue());
            String newKey = new String((String) mapElement.getKey());
            this.stops.put(newKey, newStop);
        }
        for (Map.Entry mapElement : c.getMemberIds().entrySet()) {
            String newMemberId = new String((String) mapElement.getValue());
            String newMemberState = new String((String) mapElement.getKey());
            this.memberIds.put(newMemberId, newMemberState);
        }
    }

    public void copy(Trip c) {
        this.name = c.name;
        this.start = c.start;
        this.destination = c.destination;

        for (Map.Entry mapElement : c.getStops().entrySet()) {
            Stop newStop = new Stop((Stop) mapElement.getValue());
            String newKey = new String((String) mapElement.getKey());
            this.stops.put(newKey, newStop);
        }
        for (Map.Entry mapElement : c.getMemberIds().entrySet()) {
            String newMemberId = new String((String) mapElement.getValue());
            String newMemberState = new String((String) mapElement.getKey());
            this.memberIds.put(newMemberId, newMemberState);
        }
    }

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
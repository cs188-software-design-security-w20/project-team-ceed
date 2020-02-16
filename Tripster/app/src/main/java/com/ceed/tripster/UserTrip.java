package com.ceed.tripster;

public class UserTrip {

    private String userId;
    private String state;

    public UserTrip() {

    }

    public UserTrip(String userId, String state) {
        this.userId = userId;
        this.state = state;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}

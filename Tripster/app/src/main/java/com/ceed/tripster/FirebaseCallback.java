package com.ceed.tripster;

public interface FirebaseCallback<T> {
    void onCallback(T dataItem);
}

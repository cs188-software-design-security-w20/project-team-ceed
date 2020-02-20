package com.ceed.tripster;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class GalleryPhoto {
    public String uploadTimestamp;
    public String userId;
    public String imageUrl;

    public GalleryPhoto() {}

    public GalleryPhoto(String imageUrl, String userId, String uploadTimestamp) {
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.uploadTimestamp = uploadTimestamp;
    }
}

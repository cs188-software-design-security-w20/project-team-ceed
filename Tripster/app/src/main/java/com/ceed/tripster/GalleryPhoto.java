package com.ceed.tripster;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class GalleryPhoto {
    public Long uploadTimestamp;
    public String userId;
    public String imageUrl;

    public GalleryPhoto() {}

    public GalleryPhoto(String imageUrl, String userId, Long uploadTimestamp) {
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.uploadTimestamp = uploadTimestamp;
    }
}

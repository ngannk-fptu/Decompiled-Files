/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.image.effects.analytics;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="confluence.imageeffects.rotation.exif")
public class ImageRotationByExifEvent {
    private final long imageSize;
    private final String errorMsg;
    private final String cacheEntryName;
    private final boolean rotationOnly;
    private final boolean rotationAndThumbnailOnly;
    private final boolean success;
    private final long elapsedTime;

    public ImageRotationByExifEvent(long imageSize, String errorMsg, String cacheEntryName, boolean rotationOnly, boolean rotationAndThumbnailOnly, boolean success, long elapsedTime) {
        this.imageSize = imageSize;
        this.errorMsg = errorMsg;
        this.cacheEntryName = cacheEntryName;
        this.rotationOnly = rotationOnly;
        this.rotationAndThumbnailOnly = rotationAndThumbnailOnly;
        this.success = success;
        this.elapsedTime = elapsedTime;
    }

    public long getImageSize() {
        return this.imageSize;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public String getCacheEntryName() {
        return this.cacheEntryName;
    }

    public boolean isRotationOnly() {
        return this.rotationOnly;
    }

    public boolean isRotationAndThumbnailOnly() {
        return this.rotationAndThumbnailOnly;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public long getElapsedTime() {
        return this.elapsedTime;
    }
}


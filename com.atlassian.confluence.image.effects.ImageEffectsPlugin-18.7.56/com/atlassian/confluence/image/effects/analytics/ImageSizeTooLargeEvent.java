/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.image.effects.analytics;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="confluence.imageeffects.imagesize.toolarge")
public class ImageSizeTooLargeEvent {
    private final long imageSize;
    private final String cacheEntryName;

    public ImageSizeTooLargeEvent(long imageSize, String cacheEntryName) {
        this.imageSize = imageSize;
        this.cacheEntryName = cacheEntryName;
    }

    public long getImageSize() {
        return this.imageSize;
    }

    public String getCacheEntryName() {
        return this.cacheEntryName;
    }
}


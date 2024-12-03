/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.image.effects.analytics;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="confluence.imageeffects.processingoutcome")
public class ImageProcessingOutcomeEvent {
    private final boolean inMemoryProcessing;
    private final boolean successful;
    private final String reason;
    private final String cacheEntryName;
    private final long elapsedTime;

    public ImageProcessingOutcomeEvent(boolean inMemoryProcessing, boolean successful, String reason, String cacheEntryName, long elapsedTime) {
        this.inMemoryProcessing = inMemoryProcessing;
        this.successful = successful;
        this.reason = reason;
        this.cacheEntryName = cacheEntryName;
        this.elapsedTime = elapsedTime;
    }

    public boolean isInMemoryProcessing() {
        return this.inMemoryProcessing;
    }

    public boolean isSuccessful() {
        return this.successful;
    }

    public String getReason() {
        return this.reason;
    }

    public String getCacheEntryName() {
        return this.cacheEntryName;
    }

    public long getElapsedTime() {
        return this.elapsedTime;
    }
}


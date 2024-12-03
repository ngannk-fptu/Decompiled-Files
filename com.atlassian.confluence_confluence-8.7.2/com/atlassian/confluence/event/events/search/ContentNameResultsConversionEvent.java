/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.event.events.search;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="confluence.search.contentNameResultConversion")
public class ContentNameResultsConversionEvent {
    private final long durationMillis;
    private final boolean categorised;

    public ContentNameResultsConversionEvent(long startMillis, long endMillis, boolean categorised) {
        this.durationMillis = endMillis - startMillis;
        this.categorised = categorised;
    }

    public long getDurationMillis() {
        return this.durationMillis;
    }

    public boolean categorised() {
        return this.categorised;
    }
}


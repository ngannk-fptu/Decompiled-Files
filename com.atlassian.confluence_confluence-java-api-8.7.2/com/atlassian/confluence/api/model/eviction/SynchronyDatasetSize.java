/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.eviction;

import com.atlassian.annotations.ExperimentalApi;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
public class SynchronyDatasetSize {
    private final long eventsCount;
    private final long snapshotsCount;

    @JsonCreator
    public SynchronyDatasetSize(@JsonProperty(value="eventsCount") long eventsCount, @JsonProperty(value="snapshotsCount") long snapshotsCount) {
        this.eventsCount = eventsCount;
        this.snapshotsCount = snapshotsCount;
    }

    @JsonProperty(value="eventsCount")
    public long getEventsCount() {
        return this.eventsCount;
    }

    @JsonProperty(value="snapshotsCount")
    public long getSnapshotsCount() {
        return this.snapshotsCount;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.model.stats;

import java.time.Instant;
import org.codehaus.jackson.annotate.JsonProperty;

public class CachedStats<T> {
    @JsonProperty
    private final T data;
    @JsonProperty
    private final Instant collectedType;

    public CachedStats(T data, Instant collectedType) {
        this.data = data;
        this.collectedType = collectedType;
    }

    public T getData() {
        return this.data;
    }

    public Instant getCollectedType() {
        return this.collectedType;
    }
}


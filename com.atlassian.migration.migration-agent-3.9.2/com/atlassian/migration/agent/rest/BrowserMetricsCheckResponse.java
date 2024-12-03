/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonAutoDetect
 *  com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility
 *  com.fasterxml.jackson.annotation.JsonCreator
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.atlassian.migration.agent.rest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class BrowserMetricsCheckResponse {
    @JsonProperty
    boolean shouldCollect;

    public boolean isShouldCollect() {
        return this.shouldCollect;
    }

    public void setShouldCollect(boolean shouldCollect) {
        this.shouldCollect = shouldCollect;
    }

    @JsonCreator
    public BrowserMetricsCheckResponse(@JsonProperty(value="shouldCollect") boolean shouldCollect) {
        this.shouldCollect = shouldCollect;
    }
}


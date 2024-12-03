/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.rest.api.model;

import com.atlassian.annotations.ExperimentalApi;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
public class SoftEvictionParams {
    private final int thresholdHours;
    private final int limit;

    @JsonCreator
    public SoftEvictionParams(@JsonProperty(value="thresholdHours") int thresholdHours, @JsonProperty(value="limit") int limit) {
        this.thresholdHours = thresholdHours;
        this.limit = limit;
    }

    public int getThresholdHours() {
        return this.thresholdHours;
    }

    public int getLimit() {
        return this.limit;
    }
}


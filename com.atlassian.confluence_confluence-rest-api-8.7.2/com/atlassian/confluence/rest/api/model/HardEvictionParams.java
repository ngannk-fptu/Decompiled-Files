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
public class HardEvictionParams {
    private final int thresholdHours;

    @JsonCreator
    public HardEvictionParams(@JsonProperty(value="thresholdHours") int thresholdHours) {
        this.thresholdHours = thresholdHours;
    }

    public int getThresholdHours() {
        return this.thresholdHours;
    }
}


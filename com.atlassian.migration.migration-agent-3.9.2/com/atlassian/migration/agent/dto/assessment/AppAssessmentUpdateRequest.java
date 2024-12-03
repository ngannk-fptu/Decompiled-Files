/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto.assessment;

import org.codehaus.jackson.annotate.JsonProperty;

public class AppAssessmentUpdateRequest {
    private final String appKey;
    private final String appProperty;
    private final String value;

    public AppAssessmentUpdateRequest(@JsonProperty(value="appKey") String appKey, @JsonProperty(value="appProperty") String appProperty, @JsonProperty(value="value") String value) {
        this.appKey = appKey;
        this.appProperty = appProperty;
        this.value = value;
    }

    public String getAppProperty() {
        return this.appProperty;
    }

    public String getValue() {
        return this.value;
    }

    public String getAppKey() {
        return this.appKey;
    }
}


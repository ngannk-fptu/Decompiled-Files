/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto.assessment;

import org.codehaus.jackson.annotate.JsonProperty;

public class AppAssessmentStatsResponse {
    @JsonProperty
    private long count;

    public AppAssessmentStatsResponse() {
    }

    public AppAssessmentStatsResponse(long count) {
        this.count = count;
    }

    public long getCount() {
        return this.count;
    }
}


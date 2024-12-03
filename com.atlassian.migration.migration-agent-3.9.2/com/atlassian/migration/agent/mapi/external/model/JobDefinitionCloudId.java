/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.mapi.external.model;

import com.atlassian.migration.agent.mapi.external.model.JobDetails;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class JobDefinitionCloudId
implements JobDetails {
    @JsonProperty
    private String expectedCloudId;

    @Generated
    public JobDefinitionCloudId(String expectedCloudId) {
        this.expectedCloudId = expectedCloudId;
    }

    @Generated
    public JobDefinitionCloudId() {
    }

    @Generated
    public String getExpectedCloudId() {
        return this.expectedCloudId;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.mapi.job.scope;

import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class CloudDetails {
    @JsonProperty
    private String cloudId;
    @JsonProperty
    private String url;

    @Generated
    public CloudDetails(String cloudId, String url) {
        this.cloudId = cloudId;
        this.url = url;
    }

    @Generated
    public CloudDetails() {
    }

    @Generated
    public String getCloudId() {
        return this.cloudId;
    }

    @Generated
    public String getUrl() {
        return this.url;
    }

    @Generated
    public void setCloudId(String cloudId) {
        this.cloudId = cloudId;
    }
}


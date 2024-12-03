/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.mapi.entity;

import java.time.Instant;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class AttachCloudResponse {
    @JsonProperty
    String cloudUrl;
    @JsonProperty
    Instant expiryTime;

    @Generated
    public AttachCloudResponse(String cloudUrl, Instant expiryTime) {
        this.cloudUrl = cloudUrl;
        this.expiryTime = expiryTime;
    }

    @Generated
    public String getCloudUrl() {
        return this.cloudUrl;
    }

    @Generated
    public Instant getExpiryTime() {
        return this.expiryTime;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.migration.agent.mma.model;

import lombok.Generated;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ServerInstanceCreateResponse {
    private String status;

    @Generated
    public ServerInstanceCreateResponse(String status) {
        this.status = status;
    }

    @Generated
    public ServerInstanceCreateResponse() {
    }

    @Generated
    public String getStatus() {
        return this.status;
    }
}


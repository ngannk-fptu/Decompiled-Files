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

public class ServerDetails {
    @JsonProperty
    private String serverId;
    @JsonProperty
    private String url;

    @Generated
    public ServerDetails(String serverId, String url) {
        this.serverId = serverId;
        this.url = url;
    }

    @Generated
    public ServerDetails() {
    }

    @Generated
    public String getServerId() {
        return this.serverId;
    }

    @Generated
    public String getUrl() {
        return this.url;
    }
}


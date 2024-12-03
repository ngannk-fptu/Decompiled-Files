/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.portfolioanalyzer.model;

import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class AnalysisMetadata {
    @JsonProperty
    private final String type;
    @JsonProperty
    private final String baseUrl;
    @JsonProperty
    private final String serverId;

    public AnalysisMetadata(@JsonProperty(value="type") String type, @JsonProperty(value="baseUrl") String baseUrl, @JsonProperty(value="serverId") String serverId) {
        this.type = type;
        this.baseUrl = baseUrl;
        this.serverId = serverId;
    }

    @Generated
    public String getType() {
        return this.type;
    }

    @Generated
    public String getBaseUrl() {
        return this.baseUrl;
    }

    @Generated
    public String getServerId() {
        return this.serverId;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AnalysisMetadata)) {
            return false;
        }
        AnalysisMetadata other = (AnalysisMetadata)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$type = this.getType();
        String other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) {
            return false;
        }
        String this$baseUrl = this.getBaseUrl();
        String other$baseUrl = other.getBaseUrl();
        if (this$baseUrl == null ? other$baseUrl != null : !this$baseUrl.equals(other$baseUrl)) {
            return false;
        }
        String this$serverId = this.getServerId();
        String other$serverId = other.getServerId();
        return !(this$serverId == null ? other$serverId != null : !this$serverId.equals(other$serverId));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof AnalysisMetadata;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $type = this.getType();
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        String $baseUrl = this.getBaseUrl();
        result = result * 59 + ($baseUrl == null ? 43 : $baseUrl.hashCode());
        String $serverId = this.getServerId();
        result = result * 59 + ($serverId == null ? 43 : $serverId.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "AnalysisMetadata(type=" + this.getType() + ", baseUrl=" + this.getBaseUrl() + ", serverId=" + this.getServerId() + ")";
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto;

import java.util.List;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class AppInstallInfoRequest {
    private final String cloudId;
    private final List<String> appKeys;

    @JsonCreator
    public AppInstallInfoRequest(@JsonProperty(value="cloudId") String cloudId, @JsonProperty(value="appKeys") List<String> appKeys) {
        this.cloudId = cloudId;
        this.appKeys = appKeys;
    }

    public String getCloudId() {
        return this.cloudId;
    }

    public List<String> getAppKeys() {
        return this.appKeys;
    }
}


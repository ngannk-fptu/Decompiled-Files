/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonIgnoreProperties
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class MediaConfig {
    @JsonProperty
    final String clientId;

    @JsonCreator
    public MediaConfig(@JsonProperty(value="clientId") String clientId) {
        this.clientId = clientId;
    }

    @Generated
    public String getClientId() {
        return this.clientId;
    }
}


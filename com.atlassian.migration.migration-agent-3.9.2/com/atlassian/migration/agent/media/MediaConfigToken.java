/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.constraints.NotNull
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.media;

import com.atlassian.migration.agent.media.MediaConfig;
import javax.validation.constraints.NotNull;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class MediaConfigToken {
    @NotNull
    @JsonProperty
    private final MediaConfig config;
    @JsonProperty
    private final String token;

    @Generated
    public MediaConfig getConfig() {
        return this.config;
    }

    @Generated
    public String getToken() {
        return this.token;
    }

    @Generated
    public MediaConfigToken(MediaConfig config, String token) {
        this.config = config;
        this.token = token;
    }
}


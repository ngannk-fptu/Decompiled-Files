/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize$Inclusion
 */
package com.atlassian.plugins.impl.rest;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonSerialize(include=JsonSerialize.Inclusion.ALWAYS)
public class CdnConfigurationEntity {
    @JsonProperty
    private boolean enabled;
    @JsonProperty
    private String url;

    public CdnConfigurationEntity(@JsonProperty(value="enabled") boolean enabled, @JsonProperty(value="url") String url) {
        this.enabled = enabled;
        this.url = url;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getUrl() {
        return this.url;
    }
}


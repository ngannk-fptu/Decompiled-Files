/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Version
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.recentlyviewed.cql.metadata;

import com.atlassian.confluence.api.model.content.Version;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class LastModifiedSummary {
    @JsonProperty
    private final Version version;
    @JsonProperty
    private final String friendlyLastModified;

    public LastModifiedSummary(@JsonProperty(value="version") Version version, @JsonProperty(value="friendlyLastModified") String friendlyLastModified) {
        this.version = version;
        this.friendlyLastModified = friendlyLastModified;
    }

    public Version getVersion() {
        return this.version;
    }

    public String getFriendlyLastModified() {
        return this.friendlyLastModified;
    }
}


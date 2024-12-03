/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.directory.rest.entity.delta;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class GraphDeltaQueryRemoved {
    @JsonProperty
    private final String reason;

    public GraphDeltaQueryRemoved(String reason) {
        this.reason = reason;
    }

    private GraphDeltaQueryRemoved() {
        this.reason = null;
    }

    public String getReason() {
        return this.reason;
    }
}


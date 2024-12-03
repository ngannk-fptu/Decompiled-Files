/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.directory.rest.entity.group;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class GraphGroup {
    @JsonProperty(value="displayName")
    private final String displayName;
    @JsonProperty(value="id")
    private final String id;
    @JsonProperty(value="description")
    private final String description;

    public GraphGroup() {
        this.id = null;
        this.displayName = null;
        this.description = null;
    }

    public GraphGroup(String displayName) {
        this.id = null;
        this.description = null;
        this.displayName = displayName;
    }

    public GraphGroup(String id, String displayName, String description) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getId() {
        return this.id;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GraphGroup [displayName=");
        builder.append(this.displayName);
        builder.append(", id=");
        builder.append(this.id);
        builder.append(", description=");
        builder.append(this.description);
        builder.append("]");
        return builder.toString();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.directory.rest.entity.delta;

import com.atlassian.crowd.directory.rest.entity.delta.GraphDeletableObject;
import com.atlassian.crowd.directory.rest.entity.delta.GraphDeltaQueryRemoved;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class GraphDeltaQueryMembership
implements GraphDeletableObject {
    @JsonProperty(value="@odata.type")
    private final String type;
    @JsonProperty(value="id")
    private final String id;
    @JsonProperty(value="@removed")
    private final GraphDeltaQueryRemoved removed;

    public GraphDeltaQueryMembership(String type, String id, GraphDeltaQueryRemoved removed) {
        this.type = type;
        this.id = id;
        this.removed = removed;
    }

    private GraphDeltaQueryMembership() {
        this.type = null;
        this.id = null;
        this.removed = null;
    }

    public String getType() {
        return this.type;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public GraphDeltaQueryRemoved getRemoved() {
        return this.removed;
    }
}


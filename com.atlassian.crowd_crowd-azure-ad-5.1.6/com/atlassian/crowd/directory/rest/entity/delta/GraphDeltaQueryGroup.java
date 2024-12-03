/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.directory.rest.entity.delta;

import com.atlassian.crowd.directory.rest.entity.delta.GraphDeletableObject;
import com.atlassian.crowd.directory.rest.entity.delta.GraphDeltaQueryMembership;
import com.atlassian.crowd.directory.rest.entity.delta.GraphDeltaQueryRemoved;
import com.atlassian.crowd.directory.rest.entity.group.GraphGroup;
import java.util.Collections;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

public class GraphDeltaQueryGroup
extends GraphGroup
implements GraphDeletableObject {
    @JsonProperty(value="members@delta")
    private final List<GraphDeltaQueryMembership> members;
    @JsonProperty(value="@removed")
    private final GraphDeltaQueryRemoved removed;

    private GraphDeltaQueryGroup() {
        this.members = Collections.emptyList();
        this.removed = null;
    }

    public GraphDeltaQueryGroup(String displayName) {
        super(displayName);
        this.members = Collections.emptyList();
        this.removed = null;
    }

    public GraphDeltaQueryGroup(String id, String displayName, String description) {
        super(id, displayName, description);
        this.members = Collections.emptyList();
        this.removed = null;
    }

    public GraphDeltaQueryGroup(String id, String displayName, String description, List<GraphDeltaQueryMembership> members, GraphDeltaQueryRemoved removed) {
        super(id, displayName, description);
        this.members = members;
        this.removed = removed;
    }

    public List<GraphDeltaQueryMembership> getMembers() {
        return this.members;
    }

    @Override
    public GraphDeltaQueryRemoved getRemoved() {
        return this.removed;
    }
}


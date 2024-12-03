/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonCreator
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.atlassian.confluence.impl.system.task;

import com.atlassian.confluence.impl.system.task.SystemMaintenanceTask;
import com.atlassian.confluence.internal.index.Index;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class CreateIndexSnapshotMaintenanceTask
implements SystemMaintenanceTask {
    private String sourceNodeId;
    private Collection<Index> indices;

    @JsonCreator
    public CreateIndexSnapshotMaintenanceTask(@JsonProperty(value="sourceNodeId") String sourceNodeId, @JsonProperty(value="indices") Collection<Index> indices) {
        this.sourceNodeId = Objects.requireNonNull(sourceNodeId);
        this.indices = new ArrayList<Index>(indices);
    }

    @JsonProperty(value="sourceNodeId")
    public String getSourceNodeId() {
        return this.sourceNodeId;
    }

    @JsonProperty(value="indices")
    public Collection<Index> getIndices() {
        return Collections.unmodifiableCollection(this.indices);
    }
}


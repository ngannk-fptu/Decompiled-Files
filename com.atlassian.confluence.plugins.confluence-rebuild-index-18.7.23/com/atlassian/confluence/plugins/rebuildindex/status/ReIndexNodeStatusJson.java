/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.index.status.ReIndexError
 *  com.atlassian.confluence.index.status.ReIndexNodeStatus
 *  com.atlassian.confluence.index.status.ReIndexNodeStatus$State
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize$Inclusion
 */
package com.atlassian.confluence.plugins.rebuildindex.status;

import com.atlassian.confluence.index.status.ReIndexError;
import com.atlassian.confluence.index.status.ReIndexNodeStatus;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class ReIndexNodeStatusJson {
    private final String nodeId;
    private final ReIndexNodeStatus.State state;
    private final ReIndexError error;
    private final long progress;

    @JsonCreator
    public ReIndexNodeStatusJson(@JsonProperty(value="nodeId") String nodeId, @JsonProperty(value="state") ReIndexNodeStatus.State state, @JsonProperty(value="errorCode") ReIndexError error, @JsonProperty(value="progress") long progress) {
        this.nodeId = nodeId;
        this.state = state;
        this.error = error;
        this.progress = progress;
    }

    public ReIndexNodeStatusJson(ReIndexNodeStatus reIndexStatus) {
        this.nodeId = reIndexStatus.getNodeId();
        this.state = reIndexStatus.getState();
        this.error = reIndexStatus.getError();
        this.progress = reIndexStatus.getProgress().getPercentage();
    }

    @JsonProperty(value="nodeId")
    public String getNodeId() {
        return this.nodeId;
    }

    @JsonProperty(value="state")
    public ReIndexNodeStatus.State getState() {
        return this.state;
    }

    @JsonProperty(value="errorCode")
    public @Nullable ReIndexError getReIndexError() {
        return this.error;
    }

    @JsonProperty(value="progress")
    public long getProgress() {
        return this.progress;
    }
}


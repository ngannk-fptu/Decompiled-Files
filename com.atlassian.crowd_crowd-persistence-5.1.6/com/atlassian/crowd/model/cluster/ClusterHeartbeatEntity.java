/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.crowd.model.cluster;

import com.google.common.base.MoreObjects;
import java.util.Objects;

public class ClusterHeartbeatEntity {
    private String nodeId;
    private String nodeName;
    private long timestamp;

    protected ClusterHeartbeatEntity() {
    }

    public ClusterHeartbeatEntity(String nodeId, String nodeName, long timestamp) {
        this.nodeId = nodeId;
        this.timestamp = timestamp;
        this.nodeName = nodeName;
    }

    public String getNodeId() {
        return this.nodeId;
    }

    protected void setNodeId(String nodeid) {
        this.nodeId = nodeid;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    protected void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getNodeName() {
        return this.nodeName;
    }

    protected void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ClusterHeartbeatEntity that = (ClusterHeartbeatEntity)o;
        return this.timestamp == that.timestamp && Objects.equals(this.nodeId, that.nodeId) && Objects.equals(this.nodeName, that.nodeName);
    }

    public int hashCode() {
        return Objects.hash(this.nodeId, this.nodeName, this.timestamp);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("nodeId", (Object)this.nodeId).add("nodeName", (Object)this.nodeName).add("timestamp", this.timestamp).toString();
    }
}


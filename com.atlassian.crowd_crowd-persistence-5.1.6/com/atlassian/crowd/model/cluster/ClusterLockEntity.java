/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.cluster;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ClusterLockEntity {
    private String lockName;
    private long timestamp;
    @Nullable
    private String nodeId;

    protected ClusterLockEntity() {
    }

    public ClusterLockEntity(@Nonnull String lockName, @Nullable String nodeId, long timestamp) {
        this.lockName = lockName;
        this.timestamp = timestamp;
        this.nodeId = nodeId;
    }

    public String getLockName() {
        return this.lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getNodeId() {
        return this.nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ClusterLockEntity that = (ClusterLockEntity)o;
        return this.timestamp == that.timestamp && Objects.equals(this.lockName, that.lockName) && Objects.equals(this.nodeId, that.nodeId);
    }

    public int hashCode() {
        return Objects.hash(this.lockName, this.timestamp, this.nodeId);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("lockName", (Object)this.lockName).add("timestamp", this.timestamp).add("nodeId", (Object)this.nodeId).toString();
    }
}


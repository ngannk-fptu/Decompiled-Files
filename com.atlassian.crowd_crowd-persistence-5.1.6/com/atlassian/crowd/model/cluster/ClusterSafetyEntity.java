/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.cluster;

import java.util.Objects;
import javax.annotation.Nullable;

public class ClusterSafetyEntity {
    private String key;
    private String value;
    private String nodeId;
    private String ipAddress;
    private long timestamp;

    protected ClusterSafetyEntity() {
    }

    public ClusterSafetyEntity(String key, String value, String nodeId, String ipAddress, long timestamp) {
        this.key = key;
        this.value = value;
        this.nodeId = nodeId;
        this.ipAddress = ipAddress;
        this.timestamp = timestamp;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Nullable
    public String getNodeId() {
        return this.nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    @Nullable
    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ClusterSafetyEntity that = (ClusterSafetyEntity)o;
        return this.timestamp == that.timestamp && Objects.equals(this.key, that.key) && Objects.equals(this.value, that.value) && Objects.equals(this.nodeId, that.nodeId) && Objects.equals(this.ipAddress, that.ipAddress);
    }

    public int hashCode() {
        return Objects.hash(this.key, this.value, this.nodeId, this.ipAddress, this.timestamp);
    }
}


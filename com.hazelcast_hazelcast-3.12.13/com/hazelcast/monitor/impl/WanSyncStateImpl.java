/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.monitor.WanSyncState;
import com.hazelcast.util.Clock;
import com.hazelcast.wan.WanSyncStatus;

public class WanSyncStateImpl
implements WanSyncState {
    private long creationTime;
    private WanSyncStatus status = WanSyncStatus.READY;
    private int syncedPartitionCount;
    private String activeWanConfigName;
    private String activePublisherName;

    public WanSyncStateImpl() {
    }

    public WanSyncStateImpl(WanSyncStatus status, int syncedPartitionCount, String activeWanConfigName, String activePublisherName) {
        this.creationTime = Clock.currentTimeMillis();
        this.status = status;
        this.syncedPartitionCount = syncedPartitionCount;
        this.activeWanConfigName = activeWanConfigName;
        this.activePublisherName = activePublisherName;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public WanSyncStatus getStatus() {
        return this.status;
    }

    @Override
    public int getSyncedPartitionCount() {
        return this.syncedPartitionCount;
    }

    @Override
    public String getActiveWanConfigName() {
        return this.activeWanConfigName;
    }

    @Override
    public String getActivePublisherName() {
        return this.activePublisherName;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("creationTime", this.creationTime);
        root.add("status", this.status.getStatus());
        root.add("syncedPartitionCount", this.syncedPartitionCount);
        if (this.activeWanConfigName != null) {
            root.add("activeWanConfigName", this.activeWanConfigName);
        }
        if (this.activePublisherName != null) {
            root.add("activePublisherName", this.activePublisherName);
        }
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.creationTime = json.getLong("creationTime", -1L);
        int status = json.getInt("status", WanSyncStatus.READY.getStatus());
        this.status = WanSyncStatus.getByStatus(status);
        this.syncedPartitionCount = json.getInt("syncedPartitionCount", 0);
        this.activeWanConfigName = json.getString("activeWanConfigName", null);
        this.activePublisherName = json.getString("activePublisherName", null);
    }

    public String toString() {
        return "WanSyncStateImpl{wanSyncStatus=" + (Object)((Object)this.status) + ", syncedPartitionCount=" + this.syncedPartitionCount + ", activeWanConfigName=" + this.activeWanConfigName + ", activePublisherName=" + this.activePublisherName + '}';
    }
}


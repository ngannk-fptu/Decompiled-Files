/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.monitor.LocalTopicStats;
import com.hazelcast.util.Clock;
import com.hazelcast.util.JsonUtil;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class LocalTopicStatsImpl
implements LocalTopicStats {
    private static final AtomicLongFieldUpdater<LocalTopicStatsImpl> TOTAL_PUBLISHES = AtomicLongFieldUpdater.newUpdater(LocalTopicStatsImpl.class, "totalPublishes");
    private static final AtomicLongFieldUpdater<LocalTopicStatsImpl> TOTAL_RECEIVED_MESSAGES = AtomicLongFieldUpdater.newUpdater(LocalTopicStatsImpl.class, "totalReceivedMessages");
    @Probe
    private long creationTime = Clock.currentTimeMillis();
    @Probe
    private volatile long totalPublishes;
    @Probe
    private volatile long totalReceivedMessages;

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public long getPublishOperationCount() {
        return this.totalPublishes;
    }

    public void incrementPublishes() {
        TOTAL_PUBLISHES.incrementAndGet(this);
    }

    @Override
    public long getReceiveOperationCount() {
        return this.totalReceivedMessages;
    }

    public void incrementReceives() {
        TOTAL_RECEIVED_MESSAGES.incrementAndGet(this);
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("creationTime", this.creationTime);
        root.add("totalPublishes", this.totalPublishes);
        root.add("totalReceivedMessages", this.totalReceivedMessages);
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.creationTime = JsonUtil.getLong(json, "creationTime", -1L);
        this.totalPublishes = JsonUtil.getLong(json, "totalPublishes", -1L);
        this.totalReceivedMessages = JsonUtil.getLong(json, "totalReceivedMessages", -1L);
    }

    public String toString() {
        return "LocalTopicStatsImpl{creationTime=" + this.creationTime + ", totalPublishes=" + this.totalPublishes + ", totalReceivedMessages=" + this.totalReceivedMessages + '}';
    }
}


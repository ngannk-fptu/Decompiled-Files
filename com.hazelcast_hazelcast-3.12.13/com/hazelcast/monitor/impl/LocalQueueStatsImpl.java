/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.monitor.LocalQueueStats;
import com.hazelcast.util.Clock;
import com.hazelcast.util.JsonUtil;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class LocalQueueStatsImpl
implements LocalQueueStats {
    private static final AtomicLongFieldUpdater<LocalQueueStatsImpl> NUMBER_OF_OFFERS = AtomicLongFieldUpdater.newUpdater(LocalQueueStatsImpl.class, "numberOfOffers");
    private static final AtomicLongFieldUpdater<LocalQueueStatsImpl> NUMBER_OF_REJECTED_OFFERS = AtomicLongFieldUpdater.newUpdater(LocalQueueStatsImpl.class, "numberOfRejectedOffers");
    private static final AtomicLongFieldUpdater<LocalQueueStatsImpl> NUMBER_OF_POLLS = AtomicLongFieldUpdater.newUpdater(LocalQueueStatsImpl.class, "numberOfPolls");
    private static final AtomicLongFieldUpdater<LocalQueueStatsImpl> NUMBER_OF_EMPTY_POLLS = AtomicLongFieldUpdater.newUpdater(LocalQueueStatsImpl.class, "numberOfEmptyPolls");
    private static final AtomicLongFieldUpdater<LocalQueueStatsImpl> NUMBER_OF_OTHER_OPERATIONS = AtomicLongFieldUpdater.newUpdater(LocalQueueStatsImpl.class, "numberOfOtherOperations");
    private static final AtomicLongFieldUpdater<LocalQueueStatsImpl> NUMBER_OF_EVENTS = AtomicLongFieldUpdater.newUpdater(LocalQueueStatsImpl.class, "numberOfEvents");
    @Probe
    private int ownedItemCount;
    @Probe
    private int backupItemCount;
    @Probe
    private long minAge;
    @Probe
    private long maxAge;
    @Probe
    private long aveAge;
    @Probe
    private long creationTime = Clock.currentTimeMillis();
    @Probe
    private volatile long numberOfOffers;
    @Probe
    private volatile long numberOfRejectedOffers;
    @Probe
    private volatile long numberOfPolls;
    @Probe
    private volatile long numberOfEmptyPolls;
    @Probe
    private volatile long numberOfOtherOperations;
    @Probe
    private volatile long numberOfEvents;

    @Override
    public long getMinAge() {
        return this.minAge;
    }

    public void setMinAge(long minAge) {
        this.minAge = minAge;
    }

    @Override
    public long getMaxAge() {
        return this.maxAge;
    }

    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }

    @Override
    public long getAvgAge() {
        return this.aveAge;
    }

    public void setAveAge(long aveAge) {
        this.aveAge = aveAge;
    }

    @Override
    public long getOwnedItemCount() {
        return this.ownedItemCount;
    }

    public void setOwnedItemCount(int ownedItemCount) {
        this.ownedItemCount = ownedItemCount;
    }

    @Override
    public long getBackupItemCount() {
        return this.backupItemCount;
    }

    public void setBackupItemCount(int backupItemCount) {
        this.backupItemCount = backupItemCount;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Probe
    public long total() {
        return this.numberOfOffers + this.numberOfPolls + this.numberOfOtherOperations;
    }

    @Override
    public long getOfferOperationCount() {
        return this.numberOfOffers;
    }

    @Override
    public long getRejectedOfferOperationCount() {
        return this.numberOfRejectedOffers;
    }

    @Override
    public long getPollOperationCount() {
        return this.numberOfPolls;
    }

    @Override
    public long getEmptyPollOperationCount() {
        return this.numberOfEmptyPolls;
    }

    @Override
    public long getOtherOperationsCount() {
        return this.numberOfOtherOperations;
    }

    public void incrementOtherOperations() {
        NUMBER_OF_OTHER_OPERATIONS.incrementAndGet(this);
    }

    public void incrementOffers() {
        NUMBER_OF_OFFERS.incrementAndGet(this);
    }

    public void incrementRejectedOffers() {
        NUMBER_OF_REJECTED_OFFERS.incrementAndGet(this);
    }

    public void incrementPolls() {
        NUMBER_OF_POLLS.incrementAndGet(this);
    }

    public void incrementEmptyPolls() {
        NUMBER_OF_EMPTY_POLLS.incrementAndGet(this);
    }

    public void incrementReceivedEvents() {
        NUMBER_OF_EVENTS.incrementAndGet(this);
    }

    @Override
    @Probe
    public long getEventOperationCount() {
        return this.numberOfEvents;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("ownedItemCount", this.ownedItemCount);
        root.add("backupItemCount", this.backupItemCount);
        root.add("minAge", this.minAge);
        root.add("maxAge", this.maxAge);
        root.add("aveAge", this.aveAge);
        root.add("creationTime", this.creationTime);
        root.add("numberOfOffers", this.numberOfOffers);
        root.add("numberOfPolls", this.numberOfPolls);
        root.add("numberOfRejectedOffers", this.numberOfRejectedOffers);
        root.add("numberOfEmptyPolls", this.numberOfEmptyPolls);
        root.add("numberOfOtherOperations", this.numberOfOtherOperations);
        root.add("numberOfEvents", this.numberOfEvents);
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.ownedItemCount = JsonUtil.getInt(json, "ownedItemCount", -1);
        this.backupItemCount = JsonUtil.getInt(json, "backupItemCount", -1);
        this.minAge = JsonUtil.getLong(json, "minAge", -1L);
        this.maxAge = JsonUtil.getLong(json, "maxAge", -1L);
        this.aveAge = JsonUtil.getLong(json, "aveAge", -1L);
        this.creationTime = JsonUtil.getLong(json, "creationTime", -1L);
        NUMBER_OF_OFFERS.set(this, JsonUtil.getLong(json, "numberOfOffers", -1L));
        NUMBER_OF_POLLS.set(this, JsonUtil.getLong(json, "numberOfPolls", -1L));
        NUMBER_OF_REJECTED_OFFERS.set(this, JsonUtil.getLong(json, "numberOfRejectedOffers", -1L));
        NUMBER_OF_EMPTY_POLLS.set(this, JsonUtil.getLong(json, "numberOfEmptyPolls", -1L));
        NUMBER_OF_OTHER_OPERATIONS.set(this, JsonUtil.getLong(json, "numberOfOtherOperations", -1L));
        NUMBER_OF_EVENTS.set(this, JsonUtil.getLong(json, "numberOfEvents", -1L));
    }

    public String toString() {
        return "LocalQueueStatsImpl{ownedItemCount=" + this.ownedItemCount + ", backupItemCount=" + this.backupItemCount + ", minAge=" + this.minAge + ", maxAge=" + this.maxAge + ", aveAge=" + this.aveAge + ", creationTime=" + this.creationTime + ", numberOfOffers=" + this.numberOfOffers + ", numberOfRejectedOffers=" + this.numberOfRejectedOffers + ", numberOfPolls=" + this.numberOfPolls + ", numberOfEmptyPolls=" + this.numberOfEmptyPolls + ", numberOfOtherOperations=" + this.numberOfOtherOperations + ", numberOfEvents=" + this.numberOfEvents + '}';
    }
}


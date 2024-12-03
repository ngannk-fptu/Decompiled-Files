/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.internal.cluster.ClusterClock;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.logging.ILogger;
import com.hazelcast.util.Clock;

public class ClusterClockImpl
implements ClusterClock {
    private final ILogger logger;
    private volatile long clusterTimeDiff;
    private volatile long clusterStartTime = Long.MIN_VALUE;
    @Probe(level=ProbeLevel.MANDATORY)
    private volatile long maxClusterTimeDiff;

    public ClusterClockImpl(ILogger logger) {
        this.logger = logger;
    }

    @Override
    @Probe
    public long getClusterTime() {
        return Clock.currentTimeMillis() + this.clusterTimeDiff;
    }

    public void setMasterTime(long masterTime) {
        long diff = masterTime - Clock.currentTimeMillis();
        this.setClusterTimeDiff(diff);
    }

    void setClusterTimeDiff(long diff) {
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Setting cluster time diff to " + diff + "ms.");
        }
        if (Math.abs(diff) > Math.abs(this.maxClusterTimeDiff)) {
            this.maxClusterTimeDiff = diff;
        }
        this.clusterTimeDiff = diff;
    }

    @Probe(level=ProbeLevel.MANDATORY)
    long getClusterTimeDiff() {
        return this.clusterTimeDiff;
    }

    @Override
    @Probe
    public long getClusterUpTime() {
        return Clock.currentTimeMillis() - this.clusterStartTime;
    }

    public void setClusterStartTime(long startTime) {
        if (this.clusterStartTime == Long.MIN_VALUE) {
            this.clusterStartTime = startTime;
        }
    }

    @Probe
    private long getLocalClockTime() {
        return Clock.currentTimeMillis();
    }

    @Probe
    public long getClusterStartTime() {
        return this.clusterStartTime;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigurationException;

public class CRDTReplicationConfig {
    public static final int DEFAULT_REPLICATION_PERIOD_MILLIS = 1000;
    public static final int DEFAULT_MAX_CONCURRENT_REPLICATION_TARGETS = 1;
    private int replicationPeriodMillis = 1000;
    private int maxConcurrentReplicationTargets = 1;

    public int getReplicationPeriodMillis() {
        return this.replicationPeriodMillis;
    }

    public CRDTReplicationConfig setReplicationPeriodMillis(int replicationPeriodMillis) {
        if (replicationPeriodMillis <= 0) {
            throw new ConfigurationException("The value of replicationPeriodMillis must be a non-null positive integer");
        }
        this.replicationPeriodMillis = replicationPeriodMillis;
        return this;
    }

    public int getMaxConcurrentReplicationTargets() {
        return this.maxConcurrentReplicationTargets;
    }

    public CRDTReplicationConfig setMaxConcurrentReplicationTargets(int maxConcurrentReplicationTargets) {
        if (maxConcurrentReplicationTargets <= 0) {
            throw new ConfigurationException("The value of maxConcurrentReplicationTargets must be a non-null positive integer");
        }
        this.maxConcurrentReplicationTargets = maxConcurrentReplicationTargets;
        return this;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster;

public interface ClusterClock {
    public long getClusterTime();

    public long getClusterUpTime();
}


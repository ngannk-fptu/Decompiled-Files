/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster;

import com.hazelcast.cluster.ClusterState;

public interface ClusterStateListener {
    public void onClusterStateChange(ClusterState var1);
}


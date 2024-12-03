/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster;

import com.hazelcast.version.Version;

public interface ClusterVersionListener {
    public void onClusterVersionChange(Version var1);
}


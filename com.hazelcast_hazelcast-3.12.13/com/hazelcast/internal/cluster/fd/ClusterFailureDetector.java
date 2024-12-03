/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.fd;

import com.hazelcast.core.Member;

public interface ClusterFailureDetector {
    public void heartbeat(Member var1, long var2);

    public boolean isAlive(Member var1, long var2);

    public long lastHeartbeat(Member var1);

    public double suspicionLevel(Member var1, long var2);

    public void remove(Member var1);

    public void reset();
}


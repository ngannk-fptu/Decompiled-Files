/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.fd;

public interface FailureDetector {
    public void heartbeat(long var1);

    public boolean isAlive(long var1);

    public long lastHeartbeat();

    public double suspicionLevel(long var1);
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2.impl;

public class EvictionConfig {
    private final long idleEvictTime;
    private final long idleSoftEvictTime;
    private final int minIdle;

    public EvictionConfig(long poolIdleEvictTime, long poolIdleSoftEvictTime, int minIdle) {
        this.idleEvictTime = poolIdleEvictTime > 0L ? poolIdleEvictTime : Long.MAX_VALUE;
        this.idleSoftEvictTime = poolIdleSoftEvictTime > 0L ? poolIdleSoftEvictTime : Long.MAX_VALUE;
        this.minIdle = minIdle;
    }

    public long getIdleEvictTime() {
        return this.idleEvictTime;
    }

    public long getIdleSoftEvictTime() {
        return this.idleSoftEvictTime;
    }

    public int getMinIdle() {
        return this.minIdle;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EvictionConfig [idleEvictTime=");
        builder.append(this.idleEvictTime);
        builder.append(", idleSoftEvictTime=");
        builder.append(this.idleSoftEvictTime);
        builder.append(", minIdle=");
        builder.append(this.minIdle);
        builder.append("]");
        return builder.toString();
    }
}


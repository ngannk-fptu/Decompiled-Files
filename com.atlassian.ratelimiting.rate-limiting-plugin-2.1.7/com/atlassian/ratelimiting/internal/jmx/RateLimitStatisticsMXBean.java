/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.internal.jmx;

public interface RateLimitStatisticsMXBean {
    public long getRejectedRequestCount();

    public int getUserMapSize();
}


/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.eviction;

public interface Expirable {
    public long getExpirationTime();

    public void setExpirationTime(long var1);

    public boolean isExpiredAt(long var1);
}


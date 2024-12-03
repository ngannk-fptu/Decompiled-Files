/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock;

import com.hazelcast.nio.serialization.Data;

public interface LockResource {
    public Data getKey();

    public boolean isLocked();

    public boolean isLockedBy(String var1, long var2);

    public String getOwner();

    public boolean isTransactional();

    public boolean isLocal();

    public boolean shouldBlockReads();

    public long getThreadId();

    public int getLockCount();

    public long getAcquireTime();

    public long getRemainingLeaseTime();

    public long getExpirationTime();

    public int getVersion();
}


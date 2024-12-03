/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock;

import com.hazelcast.nio.serialization.Data;
import java.util.Set;

public interface LockStore {
    public boolean lock(Data var1, String var2, long var3, long var5, long var7);

    public boolean localLock(Data var1, String var2, long var3, long var5, long var7);

    public boolean txnLock(Data var1, String var2, long var3, long var5, long var7, boolean var9);

    public boolean extendLeaseTime(Data var1, String var2, long var3, long var5);

    public boolean unlock(Data var1, String var2, long var3, long var5);

    public boolean isLocked(Data var1);

    public boolean isLockedBy(Data var1, String var2, long var3);

    public int getLockCount(Data var1);

    public int getLockedEntryCount();

    public long getRemainingLeaseTime(Data var1);

    public boolean canAcquireLock(Data var1, String var2, long var3);

    public boolean shouldBlockReads(Data var1);

    public Set<Data> getLockedKeys();

    public boolean forceUnlock(Data var1);

    public String getOwnerInfo(Data var1);
}


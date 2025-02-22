/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock;

import com.hazelcast.concurrent.lock.LockStore;
import com.hazelcast.concurrent.lock.LockStoreContainer;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.ObjectNamespace;
import java.util.Collections;
import java.util.Set;

public final class LockStoreProxy
implements LockStore {
    static final String NOT_LOCKED = "<not-locked>";
    private final LockStoreContainer container;
    private final ObjectNamespace namespace;

    public LockStoreProxy(LockStoreContainer container, ObjectNamespace namespace) {
        this.container = container;
        this.namespace = namespace;
    }

    @Override
    public boolean lock(Data key, String caller, long threadId, long referenceId, long leaseTime) {
        LockStore lockStore = this.getOrCreateLockStore();
        return lockStore != null && lockStore.lock(key, caller, threadId, referenceId, leaseTime);
    }

    @Override
    public boolean localLock(Data key, String caller, long threadId, long referenceId, long leaseTime) {
        LockStore lockStore = this.getOrCreateLockStore();
        return lockStore != null && lockStore.localLock(key, caller, threadId, referenceId, leaseTime);
    }

    @Override
    public boolean txnLock(Data key, String caller, long threadId, long referenceId, long leaseTime, boolean blockReads) {
        LockStore lockStore = this.getOrCreateLockStore();
        return lockStore != null && lockStore.txnLock(key, caller, threadId, referenceId, leaseTime, blockReads);
    }

    @Override
    public boolean extendLeaseTime(Data key, String caller, long threadId, long leaseTime) {
        LockStore lockStore = this.getLockStoreOrNull();
        return lockStore != null && lockStore.extendLeaseTime(key, caller, threadId, leaseTime);
    }

    @Override
    public boolean unlock(Data key, String caller, long threadId, long referenceId) {
        LockStore lockStore = this.getLockStoreOrNull();
        return lockStore != null && lockStore.unlock(key, caller, threadId, referenceId);
    }

    @Override
    public boolean isLocked(Data key) {
        LockStore lockStore = this.getLockStoreOrNull();
        return lockStore != null && lockStore.isLocked(key);
    }

    @Override
    public boolean isLockedBy(Data key, String caller, long threadId) {
        LockStore lockStore = this.getLockStoreOrNull();
        return lockStore != null && lockStore.isLockedBy(key, caller, threadId);
    }

    @Override
    public int getLockCount(Data key) {
        LockStore lockStore = this.getLockStoreOrNull();
        if (lockStore == null) {
            return 0;
        }
        return lockStore.getLockCount(key);
    }

    @Override
    public int getLockedEntryCount() {
        LockStore lockStore = this.getLockStoreOrNull();
        if (lockStore == null) {
            return 0;
        }
        return lockStore.getLockedEntryCount();
    }

    @Override
    public long getRemainingLeaseTime(Data key) {
        LockStore lockStore = this.getLockStoreOrNull();
        if (lockStore == null) {
            return 0L;
        }
        return lockStore.getRemainingLeaseTime(key);
    }

    @Override
    public boolean canAcquireLock(Data key, String caller, long threadId) {
        LockStore lockStore = this.getLockStoreOrNull();
        return lockStore == null || lockStore.canAcquireLock(key, caller, threadId);
    }

    @Override
    public boolean shouldBlockReads(Data key) {
        LockStore lockStore = this.getLockStoreOrNull();
        return lockStore != null && lockStore.shouldBlockReads(key);
    }

    @Override
    public Set<Data> getLockedKeys() {
        LockStore lockStore = this.getLockStoreOrNull();
        if (lockStore == null) {
            return Collections.emptySet();
        }
        return lockStore.getLockedKeys();
    }

    @Override
    public boolean forceUnlock(Data key) {
        LockStore lockStore = this.getLockStoreOrNull();
        return lockStore != null && lockStore.forceUnlock(key);
    }

    @Override
    public String getOwnerInfo(Data dataKey) {
        LockStore lockStore = this.getLockStoreOrNull();
        if (lockStore == null) {
            return NOT_LOCKED;
        }
        return lockStore.getOwnerInfo(dataKey);
    }

    private LockStore getOrCreateLockStore() {
        return this.container.getOrCreateLockStore(this.namespace);
    }

    private LockStore getLockStoreOrNull() {
        return this.container.getLockStore(this.namespace);
    }
}


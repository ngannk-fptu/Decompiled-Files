/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock;

import com.hazelcast.concurrent.lock.ConditionKey;
import com.hazelcast.concurrent.lock.LockDataSerializerHook;
import com.hazelcast.concurrent.lock.LockResource;
import com.hazelcast.concurrent.lock.LockResourceImpl;
import com.hazelcast.concurrent.lock.LockService;
import com.hazelcast.concurrent.lock.LockServiceImpl;
import com.hazelcast.concurrent.lock.LockStore;
import com.hazelcast.concurrent.lock.LockWaitNotifyKey;
import com.hazelcast.concurrent.lock.ObjectNamespaceSerializationHelper;
import com.hazelcast.concurrent.lock.operations.AwaitOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.SetUtil;
import com.hazelcast.util.scheduler.EntryTaskScheduler;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class LockStoreImpl
implements IdentifiedDataSerializable,
LockStore,
Versioned {
    private final transient ConstructorFunction<Data, LockResourceImpl> lockConstructor = new ConstructorFunction<Data, LockResourceImpl>(){

        @Override
        public LockResourceImpl createNew(Data key) {
            return new LockResourceImpl(key, LockStoreImpl.this);
        }
    };
    private final ConcurrentMap<Data, LockResourceImpl> locks = new ConcurrentHashMap<Data, LockResourceImpl>();
    private ObjectNamespace namespace;
    private int backupCount;
    private int asyncBackupCount;
    private LockService lockService;
    private EntryTaskScheduler<Data, Integer> entryTaskScheduler;

    public LockStoreImpl() {
    }

    public LockStoreImpl(LockService lockService, ObjectNamespace name, EntryTaskScheduler<Data, Integer> entryTaskScheduler, int backupCount, int asyncBackupCount) {
        this.lockService = lockService;
        this.namespace = name;
        this.entryTaskScheduler = entryTaskScheduler;
        this.backupCount = backupCount;
        this.asyncBackupCount = asyncBackupCount;
    }

    @Override
    public boolean lock(Data key, String caller, long threadId, long referenceId, long leaseTime) {
        leaseTime = this.getLeaseTime(leaseTime);
        LockResourceImpl lock = this.getLock(key);
        return lock.lock(caller, threadId, referenceId, leaseTime, false, false, false);
    }

    @Override
    public boolean localLock(Data key, String caller, long threadId, long referenceId, long leaseTime) {
        LockResourceImpl lock = this.getLock(key);
        return lock.lock(caller, threadId, referenceId, leaseTime, false, false, true);
    }

    private long getLeaseTime(long leaseTime) {
        long maxLeaseTimeInMillis = this.lockService.getMaxLeaseTimeInMillis();
        if (leaseTime > maxLeaseTimeInMillis) {
            throw new IllegalArgumentException("Max allowed lease time: " + maxLeaseTimeInMillis + "ms. Given lease time: " + leaseTime + "ms.");
        }
        if (leaseTime < 0L) {
            leaseTime = maxLeaseTimeInMillis;
        }
        return leaseTime;
    }

    @Override
    public boolean txnLock(Data key, String caller, long threadId, long referenceId, long leaseTime, boolean blockReads) {
        LockResourceImpl lock = this.getLock(key);
        return lock.lock(caller, threadId, referenceId, leaseTime, true, blockReads, false);
    }

    @Override
    public boolean extendLeaseTime(Data key, String caller, long threadId, long leaseTime) {
        LockResourceImpl lock = (LockResourceImpl)this.locks.get(key);
        return lock != null && lock.extendLeaseTime(caller, threadId, leaseTime);
    }

    public LockResourceImpl getLock(Data key) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.locks, key, this.lockConstructor);
    }

    @Override
    public boolean isLocked(Data key) {
        LockResource lock = (LockResource)this.locks.get(key);
        return lock != null && lock.isLocked();
    }

    @Override
    public boolean isLockedBy(Data key, String caller, long threadId) {
        LockResource lock = (LockResource)this.locks.get(key);
        return lock != null && lock.isLockedBy(caller, threadId);
    }

    @Override
    public int getLockCount(Data key) {
        LockResource lock = (LockResource)this.locks.get(key);
        if (lock == null) {
            return 0;
        }
        return lock.getLockCount();
    }

    @Override
    public int getLockedEntryCount() {
        return this.locks.size();
    }

    @Override
    public long getRemainingLeaseTime(Data key) {
        LockResource lock = (LockResource)this.locks.get(key);
        if (lock == null) {
            return -1L;
        }
        return lock.getRemainingLeaseTime();
    }

    @Override
    public boolean canAcquireLock(Data key, String caller, long threadId) {
        LockResourceImpl lock = (LockResourceImpl)this.locks.get(key);
        return lock == null || lock.canAcquireLock(caller, threadId);
    }

    @Override
    public boolean shouldBlockReads(Data key) {
        LockResourceImpl lock = (LockResourceImpl)this.locks.get(key);
        return lock != null && lock.shouldBlockReads() && lock.isLocked();
    }

    @Override
    public boolean unlock(Data key, String caller, long threadId, long referenceId) {
        LockResourceImpl lock = (LockResourceImpl)this.locks.get(key);
        if (lock == null) {
            return false;
        }
        boolean result = false;
        if (lock.canAcquireLock(caller, threadId) && lock.unlock(caller, threadId, referenceId)) {
            result = true;
        }
        if (lock.isRemovable()) {
            this.locks.remove(key);
        }
        return result;
    }

    @Override
    public boolean forceUnlock(Data key) {
        LockResourceImpl lock = (LockResourceImpl)this.locks.get(key);
        if (lock == null) {
            return false;
        }
        lock.clear();
        if (lock.isRemovable()) {
            this.locks.remove(key);
            lock.cancelEviction();
        }
        return true;
    }

    void cleanWaitersAndSignalsFor(Data key, String uuid) {
        LockResourceImpl lockResource = (LockResourceImpl)this.locks.get(key);
        if (lockResource != null) {
            lockResource.cleanWaitersAndSignalsFor(uuid);
        }
    }

    public int getVersion(Data key) {
        LockResourceImpl lock = (LockResourceImpl)this.locks.get(key);
        if (lock != null) {
            return lock.getVersion();
        }
        return -1;
    }

    public Collection<LockResource> getLocks() {
        return Collections.unmodifiableCollection(this.locks.values());
    }

    public void removeLocalLocks() {
        Iterator iterator = this.locks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            if (!((LockResourceImpl)entry.getValue()).isLocal()) continue;
            iterator.remove();
        }
    }

    @Override
    public Set<Data> getLockedKeys() {
        Set<Data> keySet = SetUtil.createHashSet(this.locks.size());
        for (Map.Entry entry : this.locks.entrySet()) {
            Data key = (Data)entry.getKey();
            LockResource lock = (LockResource)entry.getValue();
            if (!lock.isLocked()) continue;
            keySet.add(key);
        }
        return keySet;
    }

    public boolean hasLock() {
        return !this.locks.isEmpty();
    }

    void scheduleEviction(Data key, int version, long leaseTime) {
        this.entryTaskScheduler.schedule(leaseTime, key, version);
    }

    void cancelEviction(Data key) {
        this.entryTaskScheduler.cancel(key);
    }

    void setLockService(LockServiceImpl lockService) {
        this.lockService = lockService;
    }

    void setEntryTaskScheduler(EntryTaskScheduler<Data, Integer> entryTaskScheduler) {
        this.entryTaskScheduler = entryTaskScheduler;
    }

    public void clear() {
        this.locks.clear();
        this.entryTaskScheduler.cancelAll();
    }

    public ObjectNamespace getNamespace() {
        return this.namespace;
    }

    public int getBackupCount() {
        return this.backupCount;
    }

    public int getAsyncBackupCount() {
        return this.asyncBackupCount;
    }

    public int getTotalBackupCount() {
        return this.backupCount + this.asyncBackupCount;
    }

    public void addAwait(Data key, String conditionId, String caller, long threadId) {
        LockResourceImpl lock = this.getLock(key);
        lock.addAwait(conditionId, caller, threadId);
    }

    public void removeAwait(Data key, String conditionId, String caller, long threadId) {
        LockResourceImpl lock = this.getLock(key);
        lock.removeAwait(conditionId, caller, threadId);
    }

    public void signal(Data key, String conditionId, int maxSignalCount, String objectName) {
        LockResourceImpl lock = this.getLock(key);
        lock.signal(conditionId, maxSignalCount, objectName);
    }

    public WaitNotifyKey getNotifiedKey(Data key) {
        ConditionKey conditionKey = this.getSignalKey(key);
        if (conditionKey == null) {
            return new LockWaitNotifyKey(this.namespace, key);
        }
        return conditionKey;
    }

    private ConditionKey getSignalKey(Data key) {
        LockResourceImpl lock = (LockResourceImpl)this.locks.get(key);
        if (lock == null) {
            return null;
        }
        return lock.getSignalKey();
    }

    public void removeSignalKey(ConditionKey conditionKey) {
        LockResourceImpl lock = (LockResourceImpl)this.locks.get(conditionKey.getKey());
        if (lock != null) {
            lock.removeSignalKey(conditionKey);
        }
    }

    public boolean hasSignalKey(ConditionKey conditionKey) {
        LockResourceImpl lock = (LockResourceImpl)this.locks.get(conditionKey.getKey());
        return lock != null && lock.hasSignalKey(conditionKey);
    }

    public void registerExpiredAwaitOp(AwaitOperation awaitResponse) {
        Data key = awaitResponse.getKey();
        LockResourceImpl lock = this.getLock(key);
        lock.registerExpiredAwaitOp(awaitResponse);
    }

    public AwaitOperation pollExpiredAwaitOp(Data key) {
        LockResourceImpl lock = (LockResourceImpl)this.locks.get(key);
        if (lock == null) {
            return null;
        }
        return lock.pollExpiredAwaitOp();
    }

    @Override
    public String getOwnerInfo(Data key) {
        LockResource lock = (LockResource)this.locks.get(key);
        if (lock == null) {
            return "<not-locked>";
        }
        return "Owner: " + lock.getOwner() + ", thread ID: " + lock.getThreadId();
    }

    @Override
    public int getFactoryId() {
        return LockDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        ObjectNamespaceSerializationHelper.writeNamespaceCompatibly(this.namespace, out);
        out.writeInt(this.backupCount);
        out.writeInt(this.asyncBackupCount);
        int len = 0;
        for (LockResourceImpl lock : this.locks.values()) {
            if (lock.isLocal()) continue;
            ++len;
        }
        out.writeInt(len);
        if (len > 0) {
            for (LockResourceImpl lock : this.locks.values()) {
                if (lock.isLocal()) continue;
                lock.writeData(out);
            }
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.namespace = ObjectNamespaceSerializationHelper.readNamespaceCompatibly(in);
        this.backupCount = in.readInt();
        this.asyncBackupCount = in.readInt();
        int len = in.readInt();
        if (len > 0) {
            for (int i = 0; i < len; ++i) {
                LockResourceImpl lock = new LockResourceImpl();
                lock.readData(in);
                lock.setLockStore(this);
                this.locks.put(lock.getKey(), lock);
            }
        }
    }

    public String toString() {
        return "LockStoreImpl{namespace=" + this.namespace + ", backupCount=" + this.backupCount + ", asyncBackupCount=" + this.asyncBackupCount + '}';
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock;

import com.hazelcast.concurrent.lock.ConditionKey;
import com.hazelcast.concurrent.lock.LockDataSerializerHook;
import com.hazelcast.concurrent.lock.LockResource;
import com.hazelcast.concurrent.lock.LockStoreImpl;
import com.hazelcast.concurrent.lock.WaitersInfo;
import com.hazelcast.concurrent.lock.operations.AwaitOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Clock;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class LockResourceImpl
implements IdentifiedDataSerializable,
LockResource {
    private Data key;
    private String owner;
    private long threadId;
    private long referenceId;
    private int lockCount;
    private long expirationTime = -1L;
    private long acquireTime = -1L;
    private boolean transactional;
    private boolean blockReads;
    private boolean local;
    private Map<String, WaitersInfo> waiters;
    private Set<ConditionKey> conditionKeys;
    private List<AwaitOperation> expiredAwaitOps;
    private LockStoreImpl lockStore;
    private transient int version;

    public LockResourceImpl() {
    }

    public LockResourceImpl(Data key, LockStoreImpl lockStore) {
        this.key = key;
        this.lockStore = lockStore;
    }

    @Override
    public Data getKey() {
        return this.key;
    }

    @Override
    public boolean isLocked() {
        return this.lockCount > 0;
    }

    @Override
    public boolean isLockedBy(String owner, long threadId) {
        return this.threadId == threadId && owner != null && owner.equals(this.owner);
    }

    boolean lock(String owner, long threadId, long referenceId, long leaseTime, boolean transactional, boolean blockReads, boolean local) {
        if (this.lockCount == 0) {
            this.owner = owner;
            this.threadId = threadId;
            this.referenceId = referenceId;
            this.lockCount = 1;
            this.acquireTime = Clock.currentTimeMillis();
            this.setExpirationTime(leaseTime);
            this.transactional = transactional;
            this.blockReads = blockReads;
            this.local = local;
            return true;
        }
        if (this.isLockedBy(owner, threadId)) {
            if (!transactional && !local && this.referenceId == referenceId) {
                return true;
            }
            this.referenceId = referenceId;
            ++this.lockCount;
            this.setExpirationTime(leaseTime);
            this.transactional = transactional;
            this.blockReads = blockReads;
            this.local = local;
            return true;
        }
        return false;
    }

    boolean extendLeaseTime(String caller, long threadId, long leaseTime) {
        if (!this.isLockedBy(caller, threadId)) {
            return false;
        }
        this.blockReads = true;
        if (this.expirationTime < Long.MAX_VALUE) {
            this.setExpirationTime(this.expirationTime - Clock.currentTimeMillis() + leaseTime);
        }
        return true;
    }

    private void setExpirationTime(long leaseTime) {
        ++this.version;
        if (leaseTime < 0L) {
            this.expirationTime = Long.MAX_VALUE;
            this.lockStore.cancelEviction(this.key);
        } else {
            this.expirationTime = Clock.currentTimeMillis() + leaseTime;
            if (this.expirationTime < 0L) {
                this.expirationTime = Long.MAX_VALUE;
                this.lockStore.cancelEviction(this.key);
            } else {
                this.lockStore.scheduleEviction(this.key, this.version, leaseTime);
            }
        }
    }

    boolean unlock(String owner, long threadId, long referenceId) {
        if (this.lockCount == 0) {
            return false;
        }
        if (!this.isLockedBy(owner, threadId)) {
            return false;
        }
        if (!this.transactional && !this.local && this.referenceId == referenceId) {
            return true;
        }
        this.referenceId = referenceId;
        --this.lockCount;
        if (this.lockCount == 0) {
            this.clear();
        }
        return true;
    }

    boolean canAcquireLock(String caller, long threadId) {
        return this.lockCount == 0 || this.getThreadId() == threadId && this.getOwner().equals(caller);
    }

    void addAwait(String conditionId, String caller, long threadId) {
        WaitersInfo condition;
        if (this.waiters == null) {
            this.waiters = MapUtil.createHashMap(2);
        }
        if ((condition = this.waiters.get(conditionId)) == null) {
            condition = new WaitersInfo(conditionId);
            this.waiters.put(conditionId, condition);
        }
        condition.addWaiter(caller, threadId);
    }

    void removeAwait(String conditionId, String caller, long threadId) {
        if (this.waiters == null) {
            return;
        }
        WaitersInfo condition = this.waiters.get(conditionId);
        if (condition == null) {
            return;
        }
        condition.removeWaiter(caller, threadId);
        if (!condition.hasWaiter()) {
            this.waiters.remove(conditionId);
        }
    }

    public void signal(String conditionId, int maxSignalCount, String objectName) {
        if (this.waiters == null) {
            return;
        }
        WaitersInfo condition = this.waiters.get(conditionId);
        if (condition == null) {
            return;
        }
        Set<WaitersInfo.ConditionWaiter> waiters = condition.getWaiters();
        if (waiters == null) {
            return;
        }
        Iterator<WaitersInfo.ConditionWaiter> iterator = waiters.iterator();
        for (int i = 0; iterator.hasNext() && i < maxSignalCount; ++i) {
            WaitersInfo.ConditionWaiter waiter = iterator.next();
            ConditionKey signalKey = new ConditionKey(objectName, this.key, conditionId, waiter.getCaller(), waiter.getThreadId());
            this.registerSignalKey(signalKey);
            iterator.remove();
        }
        if (!condition.hasWaiter()) {
            this.waiters.remove(conditionId);
        }
    }

    private void registerSignalKey(ConditionKey conditionKey) {
        if (this.conditionKeys == null) {
            this.conditionKeys = new HashSet<ConditionKey>();
        }
        this.conditionKeys.add(conditionKey);
    }

    ConditionKey getSignalKey() {
        Set<ConditionKey> keys = this.conditionKeys;
        if (LockResourceImpl.isNullOrEmpty(keys)) {
            return null;
        }
        return keys.iterator().next();
    }

    void removeSignalKey(ConditionKey conditionKey) {
        if (this.conditionKeys != null) {
            this.conditionKeys.remove(conditionKey);
        }
    }

    boolean hasSignalKey(ConditionKey conditionKey) {
        if (this.conditionKeys == null) {
            return false;
        }
        return this.conditionKeys.contains(conditionKey);
    }

    void registerExpiredAwaitOp(AwaitOperation awaitResponse) {
        if (this.expiredAwaitOps == null) {
            this.expiredAwaitOps = new LinkedList<AwaitOperation>();
        }
        this.expiredAwaitOps.add(awaitResponse);
    }

    AwaitOperation pollExpiredAwaitOp() {
        List<AwaitOperation> ops = this.expiredAwaitOps;
        if (LockResourceImpl.isNullOrEmpty(ops)) {
            return null;
        }
        Iterator<AwaitOperation> iterator = ops.iterator();
        AwaitOperation awaitResponse = iterator.next();
        iterator.remove();
        return awaitResponse;
    }

    void clear() {
        this.threadId = 0L;
        this.lockCount = 0;
        this.owner = null;
        this.referenceId = 0L;
        this.expirationTime = 0L;
        this.acquireTime = -1L;
        this.cancelEviction();
        this.version = 0;
        this.transactional = false;
        this.blockReads = false;
        this.local = false;
    }

    void cancelEviction() {
        this.lockStore.cancelEviction(this.key);
    }

    boolean isRemovable() {
        return !this.isLocked() && LockResourceImpl.isNullOrEmpty(this.waiters) && LockResourceImpl.isNullOrEmpty(this.expiredAwaitOps) && LockResourceImpl.isNullOrEmpty(this.conditionKeys);
    }

    @Override
    public String getOwner() {
        return this.owner;
    }

    @Override
    public boolean isTransactional() {
        return this.transactional;
    }

    @Override
    public boolean isLocal() {
        return this.local;
    }

    @Override
    public boolean shouldBlockReads() {
        return this.blockReads;
    }

    @Override
    public long getThreadId() {
        return this.threadId;
    }

    @Override
    public int getLockCount() {
        return this.lockCount;
    }

    @Override
    public long getAcquireTime() {
        return this.acquireTime;
    }

    @Override
    public long getRemainingLeaseTime() {
        if (!this.isLocked()) {
            return -1L;
        }
        if (this.expirationTime < 0L) {
            return Long.MAX_VALUE;
        }
        long now = Clock.currentTimeMillis();
        if (now >= this.expirationTime) {
            return 0L;
        }
        return this.expirationTime - now;
    }

    @Override
    public long getExpirationTime() {
        return this.expirationTime;
    }

    @Override
    public int getVersion() {
        return this.version;
    }

    void setLockStore(LockStoreImpl lockStore) {
        this.lockStore = lockStore;
    }

    @Override
    public int getFactoryId() {
        return LockDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeData(this.key);
        out.writeUTF(this.owner);
        out.writeLong(this.threadId);
        out.writeLong(this.referenceId);
        out.writeInt(this.lockCount);
        out.writeLong(this.expirationTime);
        out.writeLong(this.acquireTime);
        out.writeBoolean(this.transactional);
        out.writeBoolean(this.blockReads);
        int conditionCount = this.getConditionCount();
        out.writeInt(conditionCount);
        if (conditionCount > 0) {
            for (WaitersInfo waitersInfo : this.waiters.values()) {
                waitersInfo.writeData(out);
            }
        }
        int signalCount = this.getSignalCount();
        out.writeInt(signalCount);
        if (signalCount > 0) {
            for (ConditionKey signalKey : this.conditionKeys) {
                out.writeUTF(signalKey.getObjectName());
                out.writeUTF(signalKey.getConditionId());
                out.writeUTF(signalKey.getUuid());
                out.writeLong(signalKey.getThreadId());
            }
        }
        int n = this.getExpiredAwaitsOpsCount();
        out.writeInt(n);
        if (n > 0) {
            for (AwaitOperation op : this.expiredAwaitOps) {
                op.writeData(out);
            }
        }
    }

    private int getExpiredAwaitsOpsCount() {
        return this.expiredAwaitOps == null ? 0 : this.expiredAwaitOps.size();
    }

    private int getSignalCount() {
        return this.conditionKeys == null ? 0 : this.conditionKeys.size();
    }

    private int getConditionCount() {
        return this.waiters == null ? 0 : this.waiters.size();
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int i;
        this.key = in.readData();
        this.owner = in.readUTF();
        this.threadId = in.readLong();
        this.referenceId = in.readLong();
        this.lockCount = in.readInt();
        this.expirationTime = in.readLong();
        this.acquireTime = in.readLong();
        this.transactional = in.readBoolean();
        this.blockReads = in.readBoolean();
        int len = in.readInt();
        if (len > 0) {
            this.waiters = MapUtil.createHashMap(len);
            for (i = 0; i < len; ++i) {
                WaitersInfo condition = new WaitersInfo();
                condition.readData(in);
                this.waiters.put(condition.getConditionId(), condition);
            }
        }
        if ((len = in.readInt()) > 0) {
            this.conditionKeys = SetUtil.createHashSet(len);
            for (i = 0; i < len; ++i) {
                this.conditionKeys.add(new ConditionKey(in.readUTF(), this.key, in.readUTF(), in.readUTF(), in.readLong()));
            }
        }
        if ((len = in.readInt()) > 0) {
            this.expiredAwaitOps = new ArrayList<AwaitOperation>(len);
            for (i = 0; i < len; ++i) {
                AwaitOperation op = new AwaitOperation();
                op.readData(in);
                this.expiredAwaitOps.add(op);
            }
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LockResourceImpl that = (LockResourceImpl)o;
        if (this.threadId != that.threadId) {
            return false;
        }
        return !(this.owner != null ? !this.owner.equals(that.owner) : that.owner != null);
    }

    public int hashCode() {
        int result = this.owner != null ? this.owner.hashCode() : 0;
        result = 31 * result + (int)(this.threadId ^ this.threadId >>> 32);
        return result;
    }

    public String toString() {
        return "LockResource{owner='" + this.owner + '\'' + ", threadId=" + this.threadId + ", lockCount=" + this.lockCount + ", acquireTime=" + this.acquireTime + ", expirationTime=" + this.expirationTime + '}';
    }

    private static boolean isNullOrEmpty(Collection c) {
        return c == null || c.isEmpty();
    }

    private static boolean isNullOrEmpty(Map m) {
        return m == null || m.isEmpty();
    }

    void cleanWaitersAndSignalsFor(String uuid) {
        if (this.conditionKeys != null) {
            Iterator<ConditionKey> iter = this.conditionKeys.iterator();
            while (iter.hasNext()) {
                ConditionKey conditionKey = iter.next();
                if (!conditionKey.getUuid().equals(uuid)) continue;
                iter.remove();
            }
        }
        if (this.waiters != null) {
            for (WaitersInfo waitersInfo : this.waiters.values()) {
                Iterator<WaitersInfo.ConditionWaiter> iter = waitersInfo.getWaiters().iterator();
                while (iter.hasNext()) {
                    WaitersInfo.ConditionWaiter waiter = iter.next();
                    if (!waiter.getCaller().equals(uuid)) continue;
                    iter.remove();
                }
            }
        }
    }
}


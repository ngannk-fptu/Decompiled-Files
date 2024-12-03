/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.lock;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.datastructures.lock.AcquireResult;
import com.hazelcast.cp.internal.datastructures.lock.LockEndpoint;
import com.hazelcast.cp.internal.datastructures.lock.LockInvocationKey;
import com.hazelcast.cp.internal.datastructures.lock.RaftLockDataSerializerHook;
import com.hazelcast.cp.internal.datastructures.lock.RaftLockOwnershipState;
import com.hazelcast.cp.internal.datastructures.lock.ReleaseResult;
import com.hazelcast.cp.internal.datastructures.spi.blocking.BlockingResource;
import com.hazelcast.cp.internal.datastructures.spi.blocking.WaitKeyContainer;
import com.hazelcast.cp.internal.util.Tuple2;
import com.hazelcast.cp.internal.util.UUIDSerializationUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.UuidUtil;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class RaftLock
extends BlockingResource<LockInvocationKey>
implements IdentifiedDataSerializable {
    private int lockCountLimit;
    private LockInvocationKey owner;
    private int lockCount;
    private Map<Tuple2<LockEndpoint, UUID>, RaftLockOwnershipState> ownerInvocationRefUids = new HashMap<Tuple2<LockEndpoint, UUID>, RaftLockOwnershipState>();

    RaftLock() {
    }

    RaftLock(CPGroupId groupId, String name, int lockCountLimit) {
        super(groupId, name);
        this.lockCountLimit = lockCountLimit > 0 ? lockCountLimit : Integer.MAX_VALUE;
    }

    AcquireResult acquire(LockInvocationKey key, boolean wait) {
        UUID invocationUid;
        LockEndpoint endpoint = key.endpoint();
        RaftLockOwnershipState memorized = this.ownerInvocationRefUids.get(Tuple2.of(endpoint, invocationUid = key.invocationUid()));
        if (memorized != null) {
            AcquireResult.AcquireStatus status = memorized.isLocked() ? AcquireResult.AcquireStatus.SUCCESSFUL : AcquireResult.AcquireStatus.FAILED;
            return new AcquireResult(status, memorized.getFence(), Collections.emptyList());
        }
        if (this.owner == null) {
            this.owner = key;
        }
        if (endpoint.equals(this.owner.endpoint())) {
            if (this.lockCount == this.lockCountLimit) {
                this.ownerInvocationRefUids.put(Tuple2.of(endpoint, invocationUid), RaftLockOwnershipState.NOT_LOCKED);
                return AcquireResult.failed(Collections.emptyList());
            }
            ++this.lockCount;
            this.ownerInvocationRefUids.put(Tuple2.of(endpoint, invocationUid), this.lockOwnershipState());
            return AcquireResult.acquired(this.owner.commitIndex());
        }
        Collection<LockInvocationKey> cancelledWaitKeys = this.cancelWaitKeys(endpoint, invocationUid);
        if (wait) {
            this.addWaitKey(endpoint, key);
            return AcquireResult.waitKeyAdded(cancelledWaitKeys);
        }
        return AcquireResult.failed(cancelledWaitKeys);
    }

    private Collection<LockInvocationKey> cancelWaitKeys(LockEndpoint endpoint, UUID invocationUid) {
        Collection<LockInvocationKey> cancelled = null;
        WaitKeyContainer container = this.getWaitKeyContainer(endpoint);
        if (container != null && ((LockInvocationKey)container.key()).isDifferentInvocationOf(endpoint, invocationUid)) {
            cancelled = container.keyAndRetries();
            this.removeWaitKey(endpoint);
        }
        return cancelled != null ? cancelled : Collections.emptyList();
    }

    ReleaseResult release(LockEndpoint endpoint, UUID invocationUid) {
        return this.doRelease(endpoint, invocationUid, 1);
    }

    private ReleaseResult doRelease(LockEndpoint endpoint, UUID invocationUid, int releaseCount) {
        RaftLockOwnershipState memorized = this.ownerInvocationRefUids.get(Tuple2.of(endpoint, invocationUid));
        if (memorized != null) {
            return ReleaseResult.successful(memorized);
        }
        if (this.owner == null || !this.owner.endpoint().equals(endpoint)) {
            return ReleaseResult.failed(this.cancelWaitKeys(endpoint, invocationUid));
        }
        this.lockCount -= Math.min(this.lockCount, releaseCount);
        if (this.lockCount > 0) {
            RaftLockOwnershipState ownership = this.lockOwnershipState();
            this.ownerInvocationRefUids.put(Tuple2.of(endpoint, invocationUid), ownership);
            return ReleaseResult.successful(ownership);
        }
        this.removeInvocationRefUids(endpoint);
        Collection<LockInvocationKey> newOwnerWaitKeys = this.setNewLockOwner();
        this.ownerInvocationRefUids.put(Tuple2.of(endpoint, invocationUid), this.lockOwnershipState());
        return ReleaseResult.successful(this.lockOwnershipState(), newOwnerWaitKeys);
    }

    private void removeInvocationRefUids(LockEndpoint endpoint) {
        Iterator<Tuple2<LockEndpoint, UUID>> it = this.ownerInvocationRefUids.keySet().iterator();
        while (it.hasNext()) {
            if (!((LockEndpoint)it.next().element1).equals(endpoint)) continue;
            it.remove();
        }
    }

    private Collection<LockInvocationKey> setNewLockOwner() {
        Collection<LockInvocationKey> newOwnerWaitKeys;
        Iterator iter = this.waitKeyContainersIterator();
        if (iter.hasNext()) {
            WaitKeyContainer container = iter.next();
            LockInvocationKey newOwner = (LockInvocationKey)container.key();
            newOwnerWaitKeys = container.keyAndRetries();
            iter.remove();
            this.owner = newOwner;
            this.lockCount = 1;
            this.ownerInvocationRefUids.put(Tuple2.of(this.owner.endpoint(), this.owner.invocationUid()), this.lockOwnershipState());
        } else {
            this.owner = null;
            newOwnerWaitKeys = Collections.emptyList();
        }
        return newOwnerWaitKeys;
    }

    RaftLockOwnershipState lockOwnershipState() {
        if (this.owner == null) {
            return RaftLockOwnershipState.NOT_LOCKED;
        }
        return new RaftLockOwnershipState(this.owner.commitIndex(), this.lockCount, this.owner.sessionId(), this.owner.endpoint().threadId());
    }

    RaftLock cloneForSnapshot() {
        RaftLock clone = new RaftLock();
        this.cloneForSnapshot(clone);
        clone.lockCountLimit = this.lockCountLimit;
        clone.owner = this.owner;
        clone.lockCount = this.lockCount;
        clone.ownerInvocationRefUids.putAll(this.ownerInvocationRefUids);
        return clone;
    }

    @Override
    protected void onSessionClose(long sessionId, Map<Long, Object> responses) {
        this.removeInvocationRefUids(sessionId);
        if (this.owner != null && this.owner.sessionId() == sessionId) {
            ReleaseResult result = this.doRelease(this.owner.endpoint(), UuidUtil.newUnsecureUUID(), this.lockCount);
            for (LockInvocationKey key : result.completedWaitKeys()) {
                responses.put(key.commitIndex(), result.ownership().getFence());
            }
        }
    }

    private void removeInvocationRefUids(long sessionId) {
        Iterator<Tuple2<LockEndpoint, UUID>> it = this.ownerInvocationRefUids.keySet().iterator();
        while (it.hasNext()) {
            if (((LockEndpoint)it.next().element1).sessionId() != sessionId) continue;
            it.remove();
        }
    }

    @Override
    protected Collection<Long> getActivelyAttachedSessions() {
        return this.owner != null ? Collections.singleton(this.owner.sessionId()) : Collections.emptyList();
    }

    @Override
    protected void onWaitKeyExpire(LockInvocationKey key) {
        this.ownerInvocationRefUids.put(Tuple2.of(key.endpoint(), key.invocationUid()), RaftLockOwnershipState.NOT_LOCKED);
    }

    @Override
    public int getFactoryId() {
        return RaftLockDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeInt(this.lockCountLimit);
        boolean hasOwner = this.owner != null;
        out.writeBoolean(hasOwner);
        if (hasOwner) {
            out.writeObject(this.owner);
        }
        out.writeInt(this.lockCount);
        out.writeInt(this.ownerInvocationRefUids.size());
        for (Map.Entry<Tuple2<LockEndpoint, UUID>, RaftLockOwnershipState> e : this.ownerInvocationRefUids.entrySet()) {
            out.writeObject(e.getKey().element1);
            UUIDSerializationUtil.writeUUID(out, (UUID)e.getKey().element2);
            out.writeObject(e.getValue());
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.lockCountLimit = in.readInt();
        boolean hasOwner = in.readBoolean();
        if (hasOwner) {
            this.owner = (LockInvocationKey)in.readObject();
        }
        this.lockCount = in.readInt();
        int ownerInvocationRefUidCount = in.readInt();
        for (int i = 0; i < ownerInvocationRefUidCount; ++i) {
            LockEndpoint endpoint = (LockEndpoint)in.readObject();
            UUID invocationUid = UUIDSerializationUtil.readUUID(in);
            RaftLockOwnershipState ownership = (RaftLockOwnershipState)in.readObject();
            this.ownerInvocationRefUids.put(Tuple2.of(endpoint, invocationUid), ownership);
        }
    }

    public String toString() {
        return "RaftLock{" + this.internalToString() + ", lockCountLimit=" + this.lockCountLimit + ", owner=" + this.owner + ", lockCount=" + this.lockCount + ", ownerInvocationRefUids=" + this.ownerInvocationRefUids + '}';
    }
}


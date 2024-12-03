/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.crdt.pncounter;

import com.hazelcast.cluster.impl.VectorClock;
import com.hazelcast.core.ConsistencyLostException;
import com.hazelcast.crdt.CRDT;
import com.hazelcast.crdt.CRDTDataSerializerHook;
import com.hazelcast.crdt.MutationDisallowedException;
import com.hazelcast.crdt.pncounter.operations.CRDTTimestampedLong;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.MapUtil;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PNCounterImpl
implements CRDT<PNCounterImpl>,
IdentifiedDataSerializable {
    private String localReplicaId;
    private String name;
    private Map<String, long[]> state = new ConcurrentHashMap<String, long[]>();
    private VectorClock stateVectorClock = new VectorClock();
    private volatile boolean migrated;
    private final ReadWriteLock stateReadWriteLock = new ReentrantReadWriteLock();
    private final Lock stateReadLock = this.stateReadWriteLock.readLock();
    private final Lock stateWriteLock = this.stateReadWriteLock.writeLock();

    PNCounterImpl(String localReplicaId, String name) {
        this.localReplicaId = localReplicaId;
        this.stateVectorClock.setReplicaTimestamp(localReplicaId, Long.MIN_VALUE);
        this.name = name;
    }

    public PNCounterImpl() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CRDTTimestampedLong get(VectorClock observedTimestamps) {
        this.checkSessionConsistency(observedTimestamps);
        this.stateReadLock.lock();
        try {
            long value = 0L;
            for (long[] pnValue : this.state.values()) {
                value += pnValue[0];
                value -= pnValue[1];
            }
            CRDTTimestampedLong cRDTTimestampedLong = new CRDTTimestampedLong(value, new VectorClock(this.stateVectorClock));
            return cRDTTimestampedLong;
        }
        finally {
            this.stateReadLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CRDTTimestampedLong getAndAdd(long delta, VectorClock observedTimestamps) {
        this.checkSessionConsistency(observedTimestamps);
        this.stateWriteLock.lock();
        try {
            this.checkNotMigrated();
            if (delta < 0L) {
                CRDTTimestampedLong cRDTTimestampedLong = this.getAndSubtract(-delta, observedTimestamps);
                return cRDTTimestampedLong;
            }
            CRDTTimestampedLong cRDTTimestampedLong = this.getAndUpdate(delta, observedTimestamps, true);
            return cRDTTimestampedLong;
        }
        finally {
            this.stateWriteLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CRDTTimestampedLong addAndGet(long delta, VectorClock observedTimestamps) {
        this.checkSessionConsistency(observedTimestamps);
        this.stateWriteLock.lock();
        try {
            this.checkNotMigrated();
            if (delta < 0L) {
                CRDTTimestampedLong cRDTTimestampedLong = this.subtractAndGet(-delta, observedTimestamps);
                return cRDTTimestampedLong;
            }
            CRDTTimestampedLong cRDTTimestampedLong = this.updateAndGet(delta, observedTimestamps, true);
            return cRDTTimestampedLong;
        }
        finally {
            this.stateWriteLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CRDTTimestampedLong getAndSubtract(long delta, VectorClock observedTimestamps) {
        this.checkSessionConsistency(observedTimestamps);
        this.stateWriteLock.lock();
        try {
            this.checkNotMigrated();
            if (delta < 0L) {
                CRDTTimestampedLong cRDTTimestampedLong = this.getAndAdd(-delta, observedTimestamps);
                return cRDTTimestampedLong;
            }
            CRDTTimestampedLong cRDTTimestampedLong = this.getAndUpdate(delta, observedTimestamps, false);
            return cRDTTimestampedLong;
        }
        finally {
            this.stateWriteLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CRDTTimestampedLong subtractAndGet(long delta, VectorClock observedTimestamps) {
        this.checkSessionConsistency(observedTimestamps);
        this.stateWriteLock.lock();
        try {
            this.checkNotMigrated();
            if (delta < 0L) {
                CRDTTimestampedLong cRDTTimestampedLong = this.addAndGet(-delta, observedTimestamps);
                return cRDTTimestampedLong;
            }
            CRDTTimestampedLong cRDTTimestampedLong = this.updateAndGet(delta, observedTimestamps, false);
            return cRDTTimestampedLong;
        }
        finally {
            this.stateWriteLock.unlock();
        }
    }

    private void checkSessionConsistency(VectorClock lastReadVectorClock) {
        if (lastReadVectorClock != null && lastReadVectorClock.isAfter(this.stateVectorClock)) {
            throw new ConsistencyLostException("This replica cannot provide the session guarantees for the PN counter since it's state is stale");
        }
    }

    private CRDTTimestampedLong updateAndGet(long delta, VectorClock observedTimestamps, boolean isAddition) {
        long[] lArray;
        if (delta < 0L) {
            throw new IllegalArgumentException("Delta must be greater than or equal to 0");
        }
        long nextTimestamp = this.stateVectorClock.getTimestampForReplica(this.localReplicaId) + 1L;
        if (this.state.containsKey(this.localReplicaId)) {
            lArray = this.state.get(this.localReplicaId);
        } else {
            long[] lArray2 = new long[2];
            lArray2[0] = 0L;
            lArray = lArray2;
            lArray2[1] = 0L;
        }
        long[] pnValues = lArray;
        int n = isAddition ? 0 : 1;
        pnValues[n] = pnValues[n] + delta;
        this.state.put(this.localReplicaId, pnValues);
        this.stateVectorClock.setReplicaTimestamp(this.localReplicaId, nextTimestamp);
        return this.get(observedTimestamps);
    }

    private CRDTTimestampedLong getAndUpdate(long delta, VectorClock observedTimestamps, boolean isAddition) {
        long[] lArray;
        if (delta < 0L) {
            throw new IllegalArgumentException("Delta must be greater than or equal to 0");
        }
        long nextTimestamp = this.stateVectorClock.getTimestampForReplica(this.localReplicaId) + 1L;
        if (this.state.containsKey(this.localReplicaId)) {
            lArray = this.state.get(this.localReplicaId);
        } else {
            long[] lArray2 = new long[2];
            lArray2[0] = 0L;
            lArray = lArray2;
            lArray2[1] = 0L;
        }
        long[] pnValues = lArray;
        int n = isAddition ? 0 : 1;
        pnValues[n] = pnValues[n] + delta;
        this.state.put(this.localReplicaId, pnValues);
        this.stateVectorClock.setReplicaTimestamp(this.localReplicaId, nextTimestamp);
        CRDTTimestampedLong current = this.get(observedTimestamps);
        current.setValue(isAddition ? current.getValue() - delta : current.getValue() + delta);
        return current;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void merge(PNCounterImpl other) {
        this.stateWriteLock.lock();
        try {
            this.checkNotMigrated();
            for (Map.Entry<String, long[]> pnCounterEntry : other.state.entrySet()) {
                long[] lArray;
                String replicaId = pnCounterEntry.getKey();
                long[] pnOtherValues = pnCounterEntry.getValue();
                if (this.state.containsKey(replicaId)) {
                    lArray = this.state.get(replicaId);
                } else {
                    long[] lArray2 = new long[2];
                    lArray2[0] = 0L;
                    lArray = lArray2;
                    lArray2[1] = 0L;
                }
                long[] pnValues = lArray;
                pnValues[0] = Math.max(pnValues[0], pnOtherValues[0]);
                pnValues[1] = Math.max(pnValues[1], pnOtherValues[1]);
                this.state.put(replicaId, pnValues);
            }
            this.stateVectorClock.merge(other.stateVectorClock);
        }
        finally {
            this.stateWriteLock.unlock();
        }
    }

    @Override
    public VectorClock getCurrentVectorClock() {
        return new VectorClock(this.stateVectorClock);
    }

    @Override
    public int getFactoryId() {
        return CRDTDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        this.stateReadLock.lock();
        try {
            out.writeObject(this.stateVectorClock);
            out.writeInt(this.state.size());
            for (Map.Entry<String, long[]> replicaState : this.state.entrySet()) {
                String replicaID = replicaState.getKey();
                long[] replicaCounts = replicaState.getValue();
                out.writeUTF(replicaID);
                out.writeLong(replicaCounts[0]);
                out.writeLong(replicaCounts[1]);
            }
        }
        finally {
            this.stateReadLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.stateWriteLock.lock();
        try {
            this.stateVectorClock = (VectorClock)in.readObject();
            int stateSize = in.readInt();
            this.state = MapUtil.createHashMap(stateSize);
            for (int i = 0; i < stateSize; ++i) {
                String replicaID = in.readUTF();
                long[] replicaCounts = new long[]{in.readLong(), in.readLong()};
                this.state.put(replicaID, replicaCounts);
            }
        }
        finally {
            this.stateWriteLock.unlock();
        }
    }

    public boolean markMigrated(VectorClock vectorClock) {
        this.stateWriteLock.lock();
        try {
            if (this.stateVectorClock.equals(vectorClock)) {
                this.migrated = true;
            }
            boolean bl = this.migrated;
            return bl;
        }
        finally {
            this.stateWriteLock.unlock();
        }
    }

    public void markMigrated() {
        this.stateWriteLock.lock();
        try {
            this.migrated = true;
        }
        finally {
            this.stateWriteLock.unlock();
        }
    }

    private void checkNotMigrated() {
        if (this.migrated) {
            throw new MutationDisallowedException("The CRDT state for the " + this.name + " + PN counter has already been migrated and cannot be updated");
        }
    }
}


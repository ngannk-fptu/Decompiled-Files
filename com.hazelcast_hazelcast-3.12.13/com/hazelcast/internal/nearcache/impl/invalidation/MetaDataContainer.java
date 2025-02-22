/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.invalidation;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public final class MetaDataContainer {
    private static final AtomicLongFieldUpdater<MetaDataContainer> SEQUENCE = AtomicLongFieldUpdater.newUpdater(MetaDataContainer.class, "sequence");
    private static final AtomicLongFieldUpdater<MetaDataContainer> STALE_SEQUENCE = AtomicLongFieldUpdater.newUpdater(MetaDataContainer.class, "staleSequence");
    private static final AtomicLongFieldUpdater<MetaDataContainer> MISSED_SEQUENCE_COUNT = AtomicLongFieldUpdater.newUpdater(MetaDataContainer.class, "missedSequenceCount");
    private static final AtomicReferenceFieldUpdater<MetaDataContainer, UUID> UUID = AtomicReferenceFieldUpdater.newUpdater(MetaDataContainer.class, UUID.class, "uuid");
    private volatile long sequence;
    private volatile long staleSequence;
    private volatile long missedSequenceCount;
    private volatile UUID uuid;

    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID uuid) {
        UUID.set(this, uuid);
    }

    public boolean casUuid(UUID prevUuid, UUID newUuid) {
        return UUID.compareAndSet(this, prevUuid, newUuid);
    }

    public long getSequence() {
        return this.sequence;
    }

    public void setSequence(long sequence) {
        SEQUENCE.set(this, sequence);
    }

    public boolean casSequence(long currentSequence, long nextSequence) {
        return SEQUENCE.compareAndSet(this, currentSequence, nextSequence);
    }

    public void resetSequence() {
        SEQUENCE.set(this, 0L);
    }

    public long getStaleSequence() {
        return STALE_SEQUENCE.get(this);
    }

    public boolean casStaleSequence(long lastKnownStaleSequence, long lastReceivedSequence) {
        return STALE_SEQUENCE.compareAndSet(this, lastKnownStaleSequence, lastReceivedSequence);
    }

    public void resetStaleSequence() {
        STALE_SEQUENCE.set(this, 0L);
    }

    public long addAndGetMissedSequenceCount(long missCount) {
        return MISSED_SEQUENCE_COUNT.addAndGet(this, missCount);
    }

    public long getMissedSequenceCount() {
        return this.missedSequenceCount;
    }
}


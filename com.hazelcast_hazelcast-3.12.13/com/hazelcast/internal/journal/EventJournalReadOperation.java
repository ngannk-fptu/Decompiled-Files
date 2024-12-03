/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.journal;

import com.hazelcast.internal.journal.EventJournal;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.ringbuffer.impl.ReadResultSetImpl;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.DistributedObjectNamespace;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.WaitNotifyKey;
import java.io.IOException;

public abstract class EventJournalReadOperation<T, J>
extends Operation
implements IdentifiedDataSerializable,
PartitionAwareOperation,
BlockingOperation,
ReadonlyOperation {
    protected String name;
    protected int minSize;
    protected int maxSize;
    protected long startSequence;
    protected transient ReadResultSetImpl<J, T> resultSet;
    protected transient long sequence;
    protected transient DistributedObjectNamespace namespace;
    private WaitNotifyKey waitNotifyKey;

    public EventJournalReadOperation() {
    }

    public EventJournalReadOperation(String name, long startSequence, int minSize, int maxSize) {
        this.name = name;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.startSequence = startSequence;
    }

    @Override
    public void beforeRun() {
        this.namespace = new DistributedObjectNamespace(this.getServiceName(), this.name);
        EventJournal<J> journal = this.getJournal();
        if (!journal.hasEventJournal(this.namespace)) {
            throw new UnsupportedOperationException("Cannot subscribe to event journal because it is either not configured or disabled for " + this.namespace);
        }
        int partitionId = this.getPartitionId();
        journal.cleanup(this.namespace, partitionId);
        this.startSequence = this.clampToBounds(journal, partitionId, this.startSequence);
        journal.isAvailableOrNextSequence(this.namespace, partitionId, this.startSequence);
        this.waitNotifyKey = journal.getWaitNotifyKey(this.namespace, partitionId);
    }

    @Override
    public boolean shouldWait() {
        if (this.resultSet == null) {
            this.resultSet = this.createResultSet();
            this.sequence = this.startSequence;
        }
        EventJournal<J> journal = this.getJournal();
        int partitionId = this.getPartitionId();
        journal.cleanup(this.namespace, partitionId);
        this.sequence = this.clampToBounds(journal, partitionId, this.sequence);
        if (this.minSize == 0) {
            if (!journal.isNextAvailableSequence(this.namespace, partitionId, this.sequence)) {
                this.readMany(journal, partitionId);
            }
            return false;
        }
        if (this.resultSet.isMinSizeReached()) {
            return false;
        }
        if (journal.isNextAvailableSequence(this.namespace, partitionId, this.sequence)) {
            return true;
        }
        this.readMany(journal, partitionId);
        return !this.resultSet.isMinSizeReached();
    }

    private void readMany(EventJournal<J> journal, int partitionId) {
        this.sequence = journal.readMany(this.namespace, partitionId, this.sequence, this.resultSet);
        this.resultSet.setNextSequenceToReadFrom(this.sequence);
    }

    @Override
    public void run() throws Exception {
    }

    @Override
    public Object getResponse() {
        return this.resultSet;
    }

    @Override
    public WaitNotifyKey getWaitKey() {
        return this.waitNotifyKey;
    }

    @Override
    public void onWaitExpire() {
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.name);
        out.writeInt(this.minSize);
        out.writeInt(this.maxSize);
        out.writeLong(this.startSequence);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.name = in.readUTF();
        this.minSize = in.readInt();
        this.maxSize = in.readInt();
        this.startSequence = in.readLong();
    }

    @Override
    public abstract String getServiceName();

    protected abstract ReadResultSetImpl<J, T> createResultSet();

    protected abstract EventJournal<J> getJournal();

    private long clampToBounds(EventJournal<J> journal, int partitionId, long requestedSequence) {
        long oldestSequence = journal.oldestSequence(this.namespace, partitionId);
        long newestSequence = journal.newestSequence(this.namespace, partitionId);
        if (requestedSequence < oldestSequence && !journal.isPersistenceEnabled(this.namespace, partitionId)) {
            return oldestSequence;
        }
        if (requestedSequence > newestSequence + 1L) {
            return newestSequence + 1L;
        }
        return requestedSequence;
    }
}


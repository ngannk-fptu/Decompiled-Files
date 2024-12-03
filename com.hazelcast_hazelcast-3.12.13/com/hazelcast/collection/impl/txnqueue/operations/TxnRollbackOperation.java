/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.collection.impl.txnqueue.operations;

import com.hazelcast.collection.impl.CollectionTxnUtil;
import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.operations.QueueBackupAwareOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnRollbackBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.WaitNotifyKey;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;

public class TxnRollbackOperation
extends QueueBackupAwareOperation
implements Notifier {
    private long[] itemIds;
    private transient long shouldNotify;

    public TxnRollbackOperation() {
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public TxnRollbackOperation(int partitionId, String name, long[] itemIds) {
        super(name);
        this.setPartitionId(partitionId);
        this.itemIds = itemIds;
    }

    @Override
    public void run() throws Exception {
        QueueContainer queueContainer = this.getContainer();
        for (long itemId : this.itemIds) {
            this.response = CollectionTxnUtil.isRemove(itemId) ? Boolean.valueOf(queueContainer.txnRollbackPoll(itemId, false)) : Boolean.valueOf(queueContainer.txnRollbackOffer(-itemId));
        }
    }

    @Override
    public boolean shouldBackup() {
        return true;
    }

    @Override
    public Operation getBackupOperation() {
        return new TxnRollbackBackupOperation(this.name, this.itemIds);
    }

    @Override
    public boolean shouldNotify() {
        for (long itemId : this.itemIds) {
            this.shouldNotify += CollectionTxnUtil.isRemove(itemId) ? 1L : -1L;
        }
        return this.shouldNotify != 0L;
    }

    @Override
    public WaitNotifyKey getNotifiedKey() {
        QueueContainer queueContainer = this.getContainer();
        if (CollectionTxnUtil.isRemove(this.shouldNotify)) {
            return queueContainer.getPollWaitNotifyKey();
        }
        return queueContainer.getOfferWaitNotifyKey();
    }

    @Override
    public int getId() {
        return 34;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLongArray(this.itemIds);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.itemIds = in.readLongArray();
    }
}


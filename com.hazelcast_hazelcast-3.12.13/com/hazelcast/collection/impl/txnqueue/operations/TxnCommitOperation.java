/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.txnqueue.operations;

import com.hazelcast.collection.impl.CollectionTxnUtil;
import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.operations.QueueBackupAwareOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnCommitBackupOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnPollOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.WaitNotifyKey;
import java.io.IOException;
import java.util.List;

public class TxnCommitOperation
extends QueueBackupAwareOperation
implements Notifier {
    private List<Operation> operationList;
    private transient List<Operation> backupList;
    private transient long shouldNotify;

    public TxnCommitOperation() {
    }

    public TxnCommitOperation(int partitionId, String name, List<Operation> operationList) {
        super(name);
        this.setPartitionId(partitionId);
        this.operationList = operationList;
    }

    @Override
    public void beforeRun() throws Exception {
        super.beforeRun();
        CollectionTxnUtil.before(this.operationList, this);
    }

    @Override
    public void run() throws Exception {
        this.backupList = CollectionTxnUtil.run(this.operationList);
        for (Operation operation : this.operationList) {
            boolean shouldNotify;
            if (!(operation instanceof Notifier) || !(shouldNotify = ((Notifier)((Object)operation)).shouldNotify())) continue;
            this.shouldNotify += operation instanceof TxnPollOperation ? 1L : -1L;
        }
    }

    @Override
    public void afterRun() throws Exception {
        super.beforeRun();
        CollectionTxnUtil.after(this.operationList);
    }

    @Override
    public boolean shouldBackup() {
        return !this.backupList.isEmpty();
    }

    @Override
    public Operation getBackupOperation() {
        return new TxnCommitBackupOperation(this.name, this.backupList);
    }

    @Override
    public boolean shouldNotify() {
        return this.shouldNotify != 0L;
    }

    @Override
    public WaitNotifyKey getNotifiedKey() {
        QueueContainer queueContainer = this.getContainer();
        if (CollectionTxnUtil.isRemove(this.shouldNotify)) {
            return queueContainer.getOfferWaitNotifyKey();
        }
        return queueContainer.getPollWaitNotifyKey();
    }

    @Override
    public int getId() {
        return 42;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        CollectionTxnUtil.write(out, this.operationList);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.operationList = CollectionTxnUtil.read(in);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.txn;

import com.hazelcast.multimap.impl.operations.AbstractBackupAwareMultiMapOperation;
import com.hazelcast.multimap.impl.txn.TxnCommitBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.WaitNotifyKey;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TxnCommitOperation
extends AbstractBackupAwareMultiMapOperation
implements Notifier {
    private List<Operation> opList;
    private transient boolean notify = true;

    public TxnCommitOperation() {
    }

    public TxnCommitOperation(int partitionId, String name, Data dataKey, long threadId, List<Operation> opList) {
        super(name, dataKey, threadId);
        this.setPartitionId(partitionId);
        this.opList = opList;
    }

    @Override
    public void run() throws Exception {
        for (Operation op : this.opList) {
            op.setNodeEngine(this.getNodeEngine()).setServiceName(this.getServiceName()).setPartitionId(this.getPartitionId());
            op.beforeRun();
            op.run();
            op.afterRun();
        }
        this.getOrCreateContainer().unlock(this.dataKey, this.getCallerUuid(), this.threadId, this.getCallId());
    }

    @Override
    public boolean shouldBackup() {
        return this.notify;
    }

    @Override
    public Operation getBackupOperation() {
        ArrayList<Operation> backupOpList = new ArrayList<Operation>();
        for (Operation operation : this.opList) {
            BackupAwareOperation backupAwareOperation;
            if (!(operation instanceof BackupAwareOperation) || !(backupAwareOperation = (BackupAwareOperation)((Object)operation)).shouldBackup()) continue;
            backupOpList.add(backupAwareOperation.getBackupOperation());
        }
        return new TxnCommitBackupOperation(this.name, this.dataKey, backupOpList, this.getCallerUuid(), this.threadId);
    }

    @Override
    public boolean shouldNotify() {
        return this.notify;
    }

    @Override
    public WaitNotifyKey getNotifiedKey() {
        return this.getWaitKey();
    }

    @Override
    public int getId() {
        return 28;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.opList.size());
        for (Operation op : this.opList) {
            out.writeObject(op);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int size = in.readInt();
        this.opList = new ArrayList<Operation>(size);
        for (int i = 0; i < size; ++i) {
            this.opList.add((Operation)in.readObject());
        }
    }
}


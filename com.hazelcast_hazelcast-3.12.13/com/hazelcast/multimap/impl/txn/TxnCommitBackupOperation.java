/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.txn;

import com.hazelcast.multimap.impl.operations.AbstractKeyBasedMultiMapOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.spi.Operation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TxnCommitBackupOperation
extends AbstractKeyBasedMultiMapOperation
implements BackupOperation {
    private List<Operation> opList;
    private String caller;

    public TxnCommitBackupOperation() {
    }

    public TxnCommitBackupOperation(String name, Data dataKey, List<Operation> opList, String caller, long threadId) {
        super(name, dataKey);
        this.opList = opList;
        this.caller = caller;
        this.threadId = threadId;
    }

    @Override
    public void run() throws Exception {
        for (Operation op : this.opList) {
            op.setNodeEngine(this.getNodeEngine()).setServiceName(this.getServiceName()).setPartitionId(this.getPartitionId());
            op.beforeRun();
            op.run();
            op.afterRun();
        }
        this.getOrCreateContainerWithoutAccess().forceUnlock(this.dataKey);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.opList.size());
        for (Operation op : this.opList) {
            out.writeObject(op);
        }
        out.writeUTF(this.caller);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int size = in.readInt();
        this.opList = new ArrayList<Operation>(size);
        for (int i = 0; i < size; ++i) {
            this.opList.add((Operation)in.readObject());
        }
        this.caller = in.readUTF();
    }

    @Override
    public int getId() {
        return 27;
    }
}


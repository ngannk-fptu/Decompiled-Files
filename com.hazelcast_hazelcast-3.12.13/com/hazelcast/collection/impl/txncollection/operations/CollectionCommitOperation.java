/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.txncollection.operations;

import com.hazelcast.collection.impl.CollectionTxnUtil;
import com.hazelcast.collection.impl.collection.operations.CollectionBackupAwareOperation;
import com.hazelcast.collection.impl.txncollection.operations.CollectionCommitBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Operation;
import java.io.IOException;
import java.util.List;

public class CollectionCommitOperation
extends CollectionBackupAwareOperation {
    private List<Operation> operationList;
    private transient List<Operation> backupList;

    public CollectionCommitOperation() {
    }

    public CollectionCommitOperation(int partitionId, String name, String serviceName, List<Operation> operationList) {
        super(name);
        this.setPartitionId(partitionId);
        this.setServiceName(serviceName);
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
    }

    @Override
    public void afterRun() throws Exception {
        super.afterRun();
        CollectionTxnUtil.after(this.operationList);
    }

    @Override
    public boolean shouldBackup() {
        return !this.backupList.isEmpty();
    }

    @Override
    public Operation getBackupOperation() {
        return new CollectionCommitBackupOperation(this.name, this.getServiceName(), this.backupList);
    }

    @Override
    public int getId() {
        return 39;
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


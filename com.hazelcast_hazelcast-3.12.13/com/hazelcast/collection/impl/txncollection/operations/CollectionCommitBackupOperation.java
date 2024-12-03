/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.txncollection.operations;

import com.hazelcast.collection.impl.CollectionTxnUtil;
import com.hazelcast.collection.impl.collection.operations.CollectionOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.spi.Operation;
import java.io.IOException;
import java.util.List;

public class CollectionCommitBackupOperation
extends CollectionOperation
implements BackupOperation {
    private List<Operation> backupList;

    public CollectionCommitBackupOperation() {
    }

    public CollectionCommitBackupOperation(String name, String serviceName, List<Operation> backupList) {
        super(name);
        this.setServiceName(serviceName);
        this.backupList = backupList;
    }

    @Override
    public void beforeRun() throws Exception {
        super.beforeRun();
        CollectionTxnUtil.before(this.backupList, this);
    }

    @Override
    public void run() throws Exception {
        CollectionTxnUtil.run(this.backupList);
    }

    @Override
    public void afterRun() throws Exception {
        super.afterRun();
        CollectionTxnUtil.after(this.backupList);
    }

    @Override
    public int getId() {
        return 40;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        CollectionTxnUtil.write(out, this.backupList);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.backupList = CollectionTxnUtil.read(in);
    }
}


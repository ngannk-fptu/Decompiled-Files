/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl.xa.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.transaction.impl.xa.SerializableXID;
import com.hazelcast.transaction.impl.xa.XAService;
import com.hazelcast.transaction.impl.xa.operations.AbstractXAOperation;
import com.hazelcast.transaction.impl.xa.operations.ClearRemoteTransactionBackupOperation;
import java.io.IOException;

public class ClearRemoteTransactionOperation
extends AbstractXAOperation
implements BackupAwareOperation {
    private Data xidData;
    private transient SerializableXID xid;

    public ClearRemoteTransactionOperation() {
    }

    public ClearRemoteTransactionOperation(Data xidData) {
        this.xidData = xidData;
    }

    @Override
    public void beforeRun() throws Exception {
        this.xid = (SerializableXID)this.getNodeEngine().toObject(this.xidData);
    }

    @Override
    public void run() throws Exception {
        XAService xaService = (XAService)this.getService();
        xaService.removeTransactions(this.xid);
    }

    @Override
    public boolean shouldBackup() {
        return true;
    }

    @Override
    public int getSyncBackupCount() {
        return 0;
    }

    @Override
    public int getAsyncBackupCount() {
        return 1;
    }

    @Override
    public Operation getBackupOperation() {
        return new ClearRemoteTransactionBackupOperation(this.xidData);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeData(this.xidData);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.xidData = in.readData();
    }

    @Override
    public int getId() {
        return 10;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl.xa.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.transaction.impl.xa.SerializableXID;
import com.hazelcast.transaction.impl.xa.XAService;
import com.hazelcast.transaction.impl.xa.operations.AbstractXAOperation;
import java.io.IOException;

public class FinalizeRemoteTransactionBackupOperation
extends AbstractXAOperation
implements BackupOperation {
    private Data xidData;
    private transient SerializableXID xid;

    public FinalizeRemoteTransactionBackupOperation() {
    }

    public FinalizeRemoteTransactionBackupOperation(Data xidData) {
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
    public boolean returnsResponse() {
        return false;
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
        return 13;
    }
}


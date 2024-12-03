/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl.xa.operations;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.transaction.impl.xa.SerializableXID;
import com.hazelcast.transaction.impl.xa.XAService;
import com.hazelcast.transaction.impl.xa.XATransaction;
import com.hazelcast.transaction.impl.xa.operations.AbstractXAOperation;
import com.hazelcast.transaction.impl.xa.operations.FinalizeRemoteTransactionBackupOperation;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FinalizeRemoteTransactionOperation
extends AbstractXAOperation
implements BackupAwareOperation {
    private Data xidData;
    private boolean isCommit;
    private transient boolean returnsResponse = true;
    private transient SerializableXID xid;

    public FinalizeRemoteTransactionOperation() {
    }

    public FinalizeRemoteTransactionOperation(Data xidData, boolean isCommit) {
        this.xidData = xidData;
        this.isCommit = isCommit;
    }

    @Override
    public void beforeRun() throws Exception {
        this.returnsResponse = false;
        this.xid = (SerializableXID)this.getNodeEngine().toObject(this.xidData);
    }

    @Override
    public void run() throws Exception {
        XAService xaService = (XAService)this.getService();
        List<XATransaction> list = xaService.removeTransactions(this.xid);
        if (list == null) {
            this.sendResponse(this.getNodeEngine().toData(-4));
            return;
        }
        final int size = list.size();
        ExecutionCallback callback = new ExecutionCallback(){
            AtomicInteger counter = new AtomicInteger();

            public void onResponse(Object response) {
                this.sendResponseIfComplete();
            }

            @Override
            public void onFailure(Throwable t) {
                this.sendResponseIfComplete();
            }

            void sendResponseIfComplete() {
                if (size == this.counter.incrementAndGet()) {
                    FinalizeRemoteTransactionOperation.this.sendResponse(null);
                }
            }
        };
        for (XATransaction xaTransaction : list) {
            this.finalizeTransaction(xaTransaction, callback);
        }
    }

    private void finalizeTransaction(XATransaction xaTransaction, ExecutionCallback callback) {
        if (this.isCommit) {
            xaTransaction.commitAsync(callback);
        } else {
            xaTransaction.rollbackAsync(callback);
        }
    }

    @Override
    public boolean returnsResponse() {
        return this.returnsResponse;
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
        return new FinalizeRemoteTransactionBackupOperation(this.xidData);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeData(this.xidData);
        out.writeBoolean(this.isCommit);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.xidData = in.readData();
        this.isCommit = in.readBoolean();
    }

    @Override
    public int getId() {
        return 14;
    }
}


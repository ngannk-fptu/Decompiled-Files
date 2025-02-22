/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl.xa.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.transaction.impl.TransactionLogRecord;
import com.hazelcast.transaction.impl.xa.SerializableXID;
import com.hazelcast.transaction.impl.xa.XAService;
import com.hazelcast.transaction.impl.xa.XATransaction;
import com.hazelcast.transaction.impl.xa.operations.AbstractXAOperation;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class PutRemoteTransactionBackupOperation
extends AbstractXAOperation
implements BackupOperation {
    private final List<TransactionLogRecord> records = new LinkedList<TransactionLogRecord>();
    private SerializableXID xid;
    private String txnId;
    private String txOwnerUuid;
    private long timeoutMillis;
    private long startTime;

    public PutRemoteTransactionBackupOperation() {
    }

    public PutRemoteTransactionBackupOperation(List<TransactionLogRecord> logs, String txnId, SerializableXID xid, String txOwnerUuid, long timeoutMillis, long startTime) {
        this.records.addAll(logs);
        this.txnId = txnId;
        this.xid = xid;
        this.txOwnerUuid = txOwnerUuid;
        this.timeoutMillis = timeoutMillis;
        this.startTime = startTime;
    }

    @Override
    public void run() throws Exception {
        XAService xaService = (XAService)this.getService();
        NodeEngine nodeEngine = this.getNodeEngine();
        XATransaction transaction = new XATransaction(nodeEngine, this.records, this.txnId, this.xid, this.txOwnerUuid, this.timeoutMillis, this.startTime);
        xaService.putTransaction(transaction);
    }

    @Override
    public boolean returnsResponse() {
        return false;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.txnId);
        out.writeObject(this.xid);
        out.writeUTF(this.txOwnerUuid);
        out.writeLong(this.timeoutMillis);
        out.writeLong(this.startTime);
        int len = this.records.size();
        out.writeInt(len);
        if (len > 0) {
            for (TransactionLogRecord record : this.records) {
                out.writeObject(record);
            }
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.txnId = in.readUTF();
        this.xid = (SerializableXID)in.readObject();
        this.txOwnerUuid = in.readUTF();
        this.timeoutMillis = in.readLong();
        this.startTime = in.readLong();
        int len = in.readInt();
        if (len > 0) {
            for (int i = 0; i < len; ++i) {
                TransactionLogRecord record = (TransactionLogRecord)in.readObject();
                this.records.add(record);
            }
        }
    }

    @Override
    public int getId() {
        return 15;
    }
}


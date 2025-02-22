/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl.xa;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.transaction.impl.TransactionDataSerializerHook;
import com.hazelcast.transaction.impl.TransactionLogRecord;
import com.hazelcast.transaction.impl.xa.SerializableXID;
import com.hazelcast.transaction.impl.xa.XATransaction;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class XATransactionDTO
implements IdentifiedDataSerializable {
    private String txnId;
    private SerializableXID xid;
    private String ownerUuid;
    private long timeoutMilis;
    private long startTime;
    private Collection<TransactionLogRecord> records;

    public XATransactionDTO() {
    }

    public XATransactionDTO(XATransaction xaTransaction) {
        this.txnId = xaTransaction.getTxnId();
        this.xid = xaTransaction.getXid();
        this.ownerUuid = xaTransaction.getOwnerUuid();
        this.timeoutMilis = xaTransaction.getTimeoutMillis();
        this.startTime = xaTransaction.getStartTime();
        this.records = xaTransaction.getTransactionRecords();
    }

    public XATransactionDTO(String txnId, SerializableXID xid, String ownerUuid, long timeoutMilis, long startTime, List<TransactionLogRecord> records) {
        this.txnId = txnId;
        this.xid = xid;
        this.ownerUuid = ownerUuid;
        this.timeoutMilis = timeoutMilis;
        this.startTime = startTime;
        this.records = records;
    }

    public String getTxnId() {
        return this.txnId;
    }

    public SerializableXID getXid() {
        return this.xid;
    }

    public String getOwnerUuid() {
        return this.ownerUuid;
    }

    public long getTimeoutMilis() {
        return this.timeoutMilis;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public Collection<TransactionLogRecord> getRecords() {
        return this.records;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.txnId);
        out.writeObject(this.xid);
        out.writeUTF(this.ownerUuid);
        out.writeLong(this.timeoutMilis);
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
    public void readData(ObjectDataInput in) throws IOException {
        this.txnId = in.readUTF();
        this.xid = (SerializableXID)in.readObject();
        this.ownerUuid = in.readUTF();
        this.timeoutMilis = in.readLong();
        this.startTime = in.readLong();
        int size = in.readInt();
        this.records = new ArrayList<TransactionLogRecord>(size);
        for (int i = 0; i < size; ++i) {
            TransactionLogRecord record = (TransactionLogRecord)in.readObject();
            this.records.add(record);
        }
    }

    @Override
    public int getFactoryId() {
        return TransactionDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 18;
    }
}


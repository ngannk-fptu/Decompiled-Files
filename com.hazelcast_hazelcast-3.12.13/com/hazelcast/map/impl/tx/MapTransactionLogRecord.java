/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.tx;

import com.hazelcast.internal.nearcache.NearCachingHook;
import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.impl.MapRecordKey;
import com.hazelcast.map.impl.tx.MapTxnOperation;
import com.hazelcast.map.impl.tx.TxnPrepareOperation;
import com.hazelcast.map.impl.tx.TxnRollbackOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import com.hazelcast.transaction.impl.TransactionLogRecord;
import com.hazelcast.util.ThreadUtil;
import java.io.IOException;

public class MapTransactionLogRecord
implements TransactionLogRecord {
    private int partitionId;
    private String name;
    private Data key;
    private long threadId = ThreadUtil.getThreadId();
    private String ownerUuid;
    private Operation op;
    private transient NearCachingHook nearCachingHook = NearCachingHook.EMPTY_HOOK;

    public MapTransactionLogRecord() {
    }

    public MapTransactionLogRecord(String name, Data key, int partitionId, Operation op, String ownerUuid, NearCachingHook nearCachingHook) {
        assert (nearCachingHook != null);
        this.name = name;
        this.key = key;
        if (!(op instanceof MapTxnOperation)) {
            throw new IllegalArgumentException();
        }
        this.op = op;
        this.ownerUuid = ownerUuid;
        this.partitionId = partitionId;
        this.nearCachingHook = nearCachingHook;
    }

    @Override
    public Operation newPrepareOperation() {
        TxnPrepareOperation operation = new TxnPrepareOperation(this.partitionId, this.name, this.key, this.ownerUuid);
        operation.setThreadId(this.threadId);
        return operation;
    }

    @Override
    public Operation newCommitOperation() {
        MapTxnOperation operation = (MapTxnOperation)((Object)this.op);
        operation.setThreadId(this.threadId);
        operation.setOwnerUuid(this.ownerUuid);
        this.op.setPartitionId(this.partitionId);
        return this.op;
    }

    @Override
    public void onCommitSuccess() {
        assert (this.nearCachingHook != null);
        this.nearCachingHook.onRemoteCallSuccess();
    }

    @Override
    public void onCommitFailure() {
        assert (this.nearCachingHook != null);
        this.nearCachingHook.onRemoteCallFailure();
    }

    @Override
    public Operation newRollbackOperation() {
        TxnRollbackOperation operation = new TxnRollbackOperation(this.partitionId, this.name, this.key, this.ownerUuid);
        operation.setThreadId(this.threadId);
        return operation;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeInt(this.partitionId);
        boolean isNullKey = this.key == null;
        out.writeBoolean(isNullKey);
        if (!isNullKey) {
            out.writeData(this.key);
        }
        out.writeLong(this.threadId);
        out.writeUTF(this.ownerUuid);
        out.writeObject(this.op);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.partitionId = in.readInt();
        boolean isNullKey = in.readBoolean();
        if (!isNullKey) {
            this.key = in.readData();
        }
        this.threadId = in.readLong();
        this.ownerUuid = in.readUTF();
        this.op = (Operation)in.readObject();
    }

    @Override
    public Object getKey() {
        return new MapRecordKey(this.name, this.key);
    }

    public String toString() {
        return "MapTransactionRecord{name='" + this.name + '\'' + ", key=" + this.key + ", threadId=" + this.threadId + ", ownerUuid='" + this.ownerUuid + '\'' + ", op=" + this.op + '}';
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 111;
    }
}


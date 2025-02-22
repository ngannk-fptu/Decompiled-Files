/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.txn;

import com.hazelcast.multimap.impl.MultiMapDataSerializerHook;
import com.hazelcast.multimap.impl.txn.TransactionRecordKey;
import com.hazelcast.multimap.impl.txn.TxnCommitOperation;
import com.hazelcast.multimap.impl.txn.TxnPrepareOperation;
import com.hazelcast.multimap.impl.txn.TxnPutOperation;
import com.hazelcast.multimap.impl.txn.TxnRemoveAllOperation;
import com.hazelcast.multimap.impl.txn.TxnRemoveOperation;
import com.hazelcast.multimap.impl.txn.TxnRollbackOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import com.hazelcast.transaction.impl.TransactionLogRecord;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MultiMapTransactionLogRecord
implements TransactionLogRecord {
    private final List<Operation> opList = new LinkedList<Operation>();
    private int partitionId;
    private String name;
    private Data key;
    private long ttl;
    private long threadId;

    public MultiMapTransactionLogRecord() {
    }

    public MultiMapTransactionLogRecord(int partitionId, Data key, String name, long ttl, long threadId) {
        this.key = key;
        this.name = name;
        this.ttl = ttl;
        this.threadId = threadId;
        this.partitionId = partitionId;
    }

    @Override
    public Operation newPrepareOperation() {
        return new TxnPrepareOperation(this.partitionId, this.name, this.key, this.threadId);
    }

    @Override
    public Operation newCommitOperation() {
        return new TxnCommitOperation(this.partitionId, this.name, this.key, this.threadId, this.opList);
    }

    @Override
    public void onCommitSuccess() {
    }

    @Override
    public void onCommitFailure() {
    }

    @Override
    public Operation newRollbackOperation() {
        return new TxnRollbackOperation(this.partitionId, this.name, this.key, this.threadId);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeInt(this.partitionId);
        out.writeInt(this.opList.size());
        for (Operation op : this.opList) {
            out.writeObject(op);
        }
        out.writeData(this.key);
        out.writeLong(this.ttl);
        out.writeLong(this.threadId);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.partitionId = in.readInt();
        int size = in.readInt();
        for (int i = 0; i < size; ++i) {
            this.opList.add((Operation)in.readObject());
        }
        this.key = in.readData();
        this.ttl = in.readLong();
        this.threadId = in.readLong();
    }

    @Override
    public Object getKey() {
        return new TransactionRecordKey(this.name, this.key);
    }

    public void addOperation(Operation op) {
        if (op instanceof TxnRemoveOperation) {
            TxnRemoveOperation removeOperation = (TxnRemoveOperation)op;
            Iterator<Operation> iter = this.opList.iterator();
            while (iter.hasNext()) {
                TxnPutOperation putOperation;
                Operation opp = iter.next();
                if (!(opp instanceof TxnPutOperation) || (putOperation = (TxnPutOperation)opp).getRecordId() != removeOperation.getRecordId()) continue;
                iter.remove();
                return;
            }
        } else if (op instanceof TxnRemoveAllOperation) {
            TxnRemoveAllOperation removeAllOperation = (TxnRemoveAllOperation)op;
            Collection<Long> recordIds = removeAllOperation.getRecordIds();
            Iterator<Operation> iterator = this.opList.iterator();
            while (iterator.hasNext()) {
                TxnPutOperation putOperation;
                Operation opp = iterator.next();
                if (!(opp instanceof TxnPutOperation) || !recordIds.remove((putOperation = (TxnPutOperation)opp).getRecordId())) continue;
                iterator.remove();
            }
            if (recordIds.isEmpty()) {
                return;
            }
        }
        this.opList.add(op);
    }

    public int size() {
        int size = 0;
        for (Operation operation : this.opList) {
            if (operation instanceof TxnRemoveAllOperation) {
                TxnRemoveAllOperation removeAllOperation = (TxnRemoveAllOperation)operation;
                size -= removeAllOperation.getRecordIds().size();
                continue;
            }
            if (operation instanceof TxnRemoveOperation) {
                --size;
                continue;
            }
            ++size;
        }
        return size;
    }

    public String toString() {
        return "MultiMapTransactionRecord{name='" + this.name + '\'' + ", opList=" + this.opList + ", key=" + this.key + ", ttl=" + this.ttl + ", threadId=" + this.threadId + '}';
    }

    @Override
    public int getFactoryId() {
        return MultiMapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 42;
    }
}


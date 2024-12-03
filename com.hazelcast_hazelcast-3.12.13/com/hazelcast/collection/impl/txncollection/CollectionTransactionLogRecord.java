/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.txncollection;

import com.hazelcast.collection.impl.CollectionTxnUtil;
import com.hazelcast.collection.impl.collection.CollectionDataSerializerHook;
import com.hazelcast.collection.impl.txncollection.CollectionTxnOperation;
import com.hazelcast.collection.impl.txncollection.operations.CollectionCommitOperation;
import com.hazelcast.collection.impl.txncollection.operations.CollectionPrepareOperation;
import com.hazelcast.collection.impl.txncollection.operations.CollectionRollbackOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Operation;
import com.hazelcast.transaction.impl.TransactionLogRecord;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CollectionTransactionLogRecord
implements TransactionLogRecord {
    protected String name;
    protected List<Operation> operationList;
    protected int partitionId;
    protected String transactionId;
    protected String serviceName;

    public CollectionTransactionLogRecord() {
    }

    public CollectionTransactionLogRecord(String serviceName, String transactionId, String name, int partitionId) {
        this.serviceName = serviceName;
        this.transactionId = transactionId;
        this.name = name;
        this.partitionId = partitionId;
        this.operationList = new ArrayList<Operation>();
    }

    @Override
    public Operation newPrepareOperation() {
        long[] itemIds = this.createItemIdArray();
        return new CollectionPrepareOperation(this.partitionId, this.name, this.serviceName, itemIds, this.transactionId);
    }

    @Override
    public Operation newCommitOperation() {
        return new CollectionCommitOperation(this.partitionId, this.name, this.serviceName, this.operationList);
    }

    @Override
    public void onCommitSuccess() {
    }

    @Override
    public void onCommitFailure() {
    }

    @Override
    public Operation newRollbackOperation() {
        long[] itemIds = this.createItemIdArray();
        return new CollectionRollbackOperation(this.partitionId, this.name, this.serviceName, itemIds);
    }

    @Override
    public Object getKey() {
        return this.name;
    }

    public void addOperation(CollectionTxnOperation operation) {
        Iterator<Operation> iterator = this.operationList.iterator();
        while (iterator.hasNext()) {
            CollectionTxnOperation op = (CollectionTxnOperation)((Object)iterator.next());
            if (op.getItemId() != operation.getItemId()) continue;
            iterator.remove();
            break;
        }
        this.operationList.add((Operation)((Object)operation));
    }

    public int removeOperation(long itemId) {
        Iterator<Operation> iterator = this.operationList.iterator();
        while (iterator.hasNext()) {
            CollectionTxnOperation op = (CollectionTxnOperation)((Object)iterator.next());
            if (op.getItemId() != itemId) continue;
            iterator.remove();
            break;
        }
        return this.operationList.size();
    }

    protected long[] createItemIdArray() {
        int size = this.operationList.size();
        long[] itemIds = new long[size];
        for (int i = 0; i < size; ++i) {
            CollectionTxnOperation operation = (CollectionTxnOperation)((Object)this.operationList.get(i));
            itemIds[i] = CollectionTxnUtil.getItemId(operation);
        }
        return itemIds;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.serviceName);
        out.writeUTF(this.transactionId);
        out.writeUTF(this.name);
        out.writeInt(this.partitionId);
        CollectionTxnUtil.write(out, this.operationList);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.serviceName = in.readUTF();
        this.transactionId = in.readUTF();
        this.name = in.readUTF();
        this.partitionId = in.readInt();
        this.operationList = CollectionTxnUtil.read(in);
    }

    @Override
    public int getFactoryId() {
        return CollectionDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 43;
    }
}


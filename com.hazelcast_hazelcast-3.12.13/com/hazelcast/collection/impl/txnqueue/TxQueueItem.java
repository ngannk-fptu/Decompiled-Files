/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.txnqueue;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.QueueItem;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import java.io.IOException;

public class TxQueueItem
extends QueueItem {
    private String transactionId;
    private boolean pollOperation;

    public TxQueueItem() {
    }

    public TxQueueItem(QueueItem item) {
        this.itemId = item.getItemId();
        this.container = item.getContainer();
        this.data = item.getData();
    }

    public TxQueueItem(QueueContainer container, long itemId, Data data) {
        super(container, itemId, data);
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public TxQueueItem setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public boolean isPollOperation() {
        return this.pollOperation;
    }

    public TxQueueItem setPollOperation(boolean pollOperation) {
        this.pollOperation = pollOperation;
        return this;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeUTF(this.transactionId);
        out.writeBoolean(this.pollOperation);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.transactionId = in.readUTF();
        this.pollOperation = in.readBoolean();
    }

    @Override
    public int getId() {
        return 37;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TxQueueItem)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        TxQueueItem item = (TxQueueItem)o;
        if (this.pollOperation != item.pollOperation) {
            return false;
        }
        return this.transactionId.equals(item.transactionId);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.transactionId.hashCode();
        result = 31 * result + (this.pollOperation ? 1 : 0);
        return result;
    }
}


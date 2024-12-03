/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.collection;

import com.hazelcast.collection.impl.collection.CollectionItem;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import java.io.IOException;

public class TxCollectionItem
extends CollectionItem {
    private String transactionId;
    private boolean removeOperation;

    public TxCollectionItem() {
    }

    public TxCollectionItem(CollectionItem item) {
        super(item.itemId, item.value);
    }

    public TxCollectionItem(long itemId, Data value, String transactionId, boolean removeOperation) {
        super(itemId, value);
        this.transactionId = transactionId;
        this.removeOperation = removeOperation;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public boolean isRemoveOperation() {
        return this.removeOperation;
    }

    public TxCollectionItem setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public TxCollectionItem setRemoveOperation(boolean removeOperation) {
        this.removeOperation = removeOperation;
        return this;
    }

    @Override
    public int getId() {
        return 34;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeUTF(this.transactionId);
        out.writeBoolean(this.removeOperation);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.transactionId = in.readUTF();
        this.removeOperation = in.readBoolean();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TxCollectionItem)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        TxCollectionItem that = (TxCollectionItem)o;
        if (this.removeOperation != that.removeOperation) {
            return false;
        }
        return this.transactionId.equals(that.transactionId);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.transactionId.hashCode();
        result = 31 * result + (this.removeOperation ? 1 : 0);
        return result;
    }
}


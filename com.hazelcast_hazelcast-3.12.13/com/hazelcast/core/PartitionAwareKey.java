/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.PartitionAware;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.util.Preconditions;
import java.io.IOException;

@BinaryInterface
public final class PartitionAwareKey<K, P>
implements PartitionAware<Object>,
DataSerializable {
    private K key;
    private P partitionKey;

    public PartitionAwareKey(K key, P partitionKey) {
        this.key = Preconditions.isNotNull(key, "key");
        this.partitionKey = Preconditions.isNotNull(partitionKey, "partitionKey");
    }

    private PartitionAwareKey() {
    }

    public K getKey() {
        return this.key;
    }

    @Override
    public P getPartitionKey() {
        return this.partitionKey;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.key);
        out.writeObject(this.partitionKey);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.key = in.readObject();
        this.partitionKey = in.readObject();
    }

    public boolean equals(Object thatObject) {
        if (this == thatObject) {
            return true;
        }
        if (thatObject == null || this.getClass() != thatObject.getClass()) {
            return false;
        }
        PartitionAwareKey that = (PartitionAwareKey)thatObject;
        return this.key.equals(that.key) && this.partitionKey.equals(that.partitionKey);
    }

    public int hashCode() {
        int result = this.key.hashCode();
        result = 31 * result + this.partitionKey.hashCode();
        return result;
    }

    public String toString() {
        return "PartitionAwareKey{key=" + this.key + ", partitionKey=" + this.partitionKey + '}';
    }
}


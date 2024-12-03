/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.predicates.PredicateDataSerializerHook;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.Map;

@BinaryInterface
public class PartitionPredicate<K, V>
implements Predicate<K, V>,
IdentifiedDataSerializable {
    private static final long serialVersionUID = 1L;
    private Object partitionKey;
    private Predicate<K, V> target;

    public PartitionPredicate() {
    }

    public PartitionPredicate(Object partitionKey, Predicate<K, V> target) {
        this.partitionKey = Preconditions.checkNotNull(partitionKey, "partitionKey can't be null");
        this.target = Preconditions.checkNotNull(target, "target predicate can't be null");
    }

    public Object getPartitionKey() {
        return this.partitionKey;
    }

    public Predicate<K, V> getTarget() {
        return this.target;
    }

    @Override
    public boolean apply(Map.Entry<K, V> mapEntry) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getFactoryId() {
        return PredicateDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 16;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.partitionKey);
        out.writeObject(this.target);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.partitionKey = in.readObject();
        this.target = (Predicate)in.readObject();
    }

    public String toString() {
        return "PartitionPredicate{partitionKey=" + this.partitionKey + ", target=" + this.target + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PartitionPredicate that = (PartitionPredicate)o;
        if (this.partitionKey != null ? !this.partitionKey.equals(that.partitionKey) : that.partitionKey != null) {
            return false;
        }
        return this.target != null ? this.target.equals(that.target) : that.target == null;
    }

    public int hashCode() {
        int result = this.partitionKey != null ? this.partitionKey.hashCode() : 0;
        result = 31 * result + (this.target != null ? this.target.hashCode() : 0);
        return result;
    }
}


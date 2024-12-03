/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.merge;

import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.impl.merge.SplitBrainDataSerializerHook;
import com.hazelcast.spi.merge.MergingEntry;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.spi.serialization.SerializationServiceAware;
import java.io.IOException;

public abstract class AbstractMergingEntryImpl<K, V, T extends AbstractMergingEntryImpl<K, V, T>>
implements MergingEntry<K, V>,
SerializationServiceAware,
IdentifiedDataSerializable {
    private K key;
    private V value;
    private transient SerializationService serializationService;

    public AbstractMergingEntryImpl() {
    }

    public AbstractMergingEntryImpl(SerializationService serializationService) {
        this.serializationService = serializationService;
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public Object getDeserializedKey() {
        return this.serializationService.toObject(this.key);
    }

    public T setKey(K key) {
        this.key = key;
        return (T)this;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public Object getDeserializedValue() {
        return this.serializationService.toObject(this.value);
    }

    public T setValue(V value) {
        this.value = value;
        return (T)this;
    }

    @Override
    public void setSerializationService(SerializationService serializationService) {
        this.serializationService = serializationService;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        IOUtil.writeObject(out, this.key);
        IOUtil.writeObject(out, this.value);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.key = IOUtil.readObject(in);
        this.value = IOUtil.readObject(in);
    }

    @Override
    public int getFactoryId() {
        return SplitBrainDataSerializerHook.F_ID;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractMergingEntryImpl)) {
            return false;
        }
        AbstractMergingEntryImpl that = (AbstractMergingEntryImpl)o;
        if (this.key != null ? !this.key.equals(that.key) : that.key != null) {
            return false;
        }
        return this.value != null ? this.value.equals(that.value) : that.value == null;
    }

    public int hashCode() {
        int result = this.key != null ? this.key.hashCode() : 0;
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "MergingEntry{key=" + this.key + ", value=" + this.value + '}';
    }
}


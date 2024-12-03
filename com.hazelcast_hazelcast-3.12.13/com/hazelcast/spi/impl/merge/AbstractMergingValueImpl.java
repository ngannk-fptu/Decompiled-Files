/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.merge;

import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.impl.merge.SplitBrainDataSerializerHook;
import com.hazelcast.spi.merge.MergingValue;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.spi.serialization.SerializationServiceAware;
import java.io.IOException;

public abstract class AbstractMergingValueImpl<V, T extends AbstractMergingValueImpl<V, T>>
implements MergingValue<V>,
SerializationServiceAware,
IdentifiedDataSerializable {
    private V value;
    private transient SerializationService serializationService;

    public AbstractMergingValueImpl() {
    }

    public AbstractMergingValueImpl(SerializationService serializationService) {
        this.serializationService = serializationService;
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
        IOUtil.writeObject(out, this.value);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
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
        if (!(o instanceof AbstractMergingValueImpl)) {
            return false;
        }
        AbstractMergingValueImpl that = (AbstractMergingValueImpl)o;
        return this.value != null ? this.value.equals(that.value) : that.value == null;
    }

    public int hashCode() {
        return this.value != null ? this.value.hashCode() : 0;
    }

    public String toString() {
        return "MergingValue{value=" + this.value + '}';
    }
}


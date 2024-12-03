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
import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractCollectionMergingValueImpl<V extends Collection, T extends AbstractCollectionMergingValueImpl<V, T>>
implements MergingValue<V>,
SerializationServiceAware,
IdentifiedDataSerializable {
    private V value;
    private transient SerializationService serializationService;

    public AbstractCollectionMergingValueImpl() {
    }

    public AbstractCollectionMergingValueImpl(SerializationService serializationService) {
        this.serializationService = serializationService;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public <DV> DV getDeserializedValue() {
        ArrayList deserializedValues = new ArrayList(this.value.size());
        for (Object aValue : this.value) {
            deserializedValues.add(this.serializationService.toObject(aValue));
        }
        return (DV)deserializedValues;
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
        out.writeInt(this.value.size());
        for (Object aValue : this.value) {
            IOUtil.writeObject(out, aValue);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        ArrayList list = new ArrayList(size);
        for (int i = 0; i < size; ++i) {
            list.add(IOUtil.readObject(in));
        }
        this.value = list;
    }

    @Override
    public int getFactoryId() {
        return SplitBrainDataSerializerHook.F_ID;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractCollectionMergingValueImpl)) {
            return false;
        }
        AbstractCollectionMergingValueImpl that = (AbstractCollectionMergingValueImpl)o;
        return this.value != null ? this.value.equals(that.value) : that.value == null;
    }

    public int hashCode() {
        return this.value != null ? this.value.hashCode() : 0;
    }

    public String toString() {
        return "MergingValue{value=" + this.value + '}';
    }
}


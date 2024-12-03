/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.merge;

import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.impl.merge.SplitBrainDataSerializerHook;
import com.hazelcast.spi.merge.RingbufferMergeData;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.spi.serialization.SerializationServiceAware;
import java.io.IOException;

public class RingbufferMergingValueImpl
implements SplitBrainMergeTypes.RingbufferMergeTypes,
SerializationServiceAware,
IdentifiedDataSerializable {
    private RingbufferMergeData value;
    private transient SerializationService serializationService;

    public RingbufferMergingValueImpl() {
    }

    public RingbufferMergingValueImpl(SerializationService serializationService) {
        this.serializationService = serializationService;
    }

    @Override
    public RingbufferMergeData getValue() {
        return this.value;
    }

    @Override
    public <DV> DV getDeserializedValue() {
        RingbufferMergeData deserializedValues = new RingbufferMergeData(this.value.getItems().length);
        deserializedValues.setHeadSequence(this.value.getHeadSequence());
        deserializedValues.setTailSequence(this.value.getTailSequence());
        for (long seq = this.value.getHeadSequence(); seq <= this.value.getTailSequence(); ++seq) {
            deserializedValues.set(seq, this.serializationService.toObject(this.value.read(seq)));
        }
        return (DV)deserializedValues;
    }

    public RingbufferMergingValueImpl setValues(RingbufferMergeData values) {
        this.value = values;
        return this;
    }

    @Override
    public void setSerializationService(SerializationService serializationService) {
        this.serializationService = serializationService;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(this.value.getTailSequence());
        out.writeLong(this.value.getHeadSequence());
        out.writeInt(this.value.getItems().length);
        for (long seq = this.value.getHeadSequence(); seq <= this.value.getTailSequence(); ++seq) {
            IOUtil.writeObject(out, this.value.read(seq));
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        long tailSequence = in.readLong();
        long headSequence = in.readLong();
        int capacity = in.readInt();
        this.value = new RingbufferMergeData(capacity);
        this.value.setTailSequence(tailSequence);
        this.value.setHeadSequence(headSequence);
        for (long seq = headSequence; seq <= tailSequence; ++seq) {
            this.value.set(seq, IOUtil.readObject(in));
        }
    }

    @Override
    public int getFactoryId() {
        return SplitBrainDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 8;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RingbufferMergingValueImpl that = (RingbufferMergingValueImpl)o;
        return this.value != null ? this.value.equals(that.value) : that.value == null;
    }

    public int hashCode() {
        return this.value != null ? this.value.hashCode() : 0;
    }

    public String toString() {
        return "RingbufferMergingValueImpl{value=" + this.value + '}';
    }
}


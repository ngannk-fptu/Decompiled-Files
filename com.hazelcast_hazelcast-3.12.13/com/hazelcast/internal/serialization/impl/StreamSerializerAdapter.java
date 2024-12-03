/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.SerializerAdapter;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.nio.serialization.Serializer;
import com.hazelcast.nio.serialization.StreamSerializer;
import com.hazelcast.nio.serialization.TypedStreamDeserializer;
import java.io.IOException;

class StreamSerializerAdapter
implements SerializerAdapter {
    protected final InternalSerializationService service;
    protected final StreamSerializer serializer;

    public StreamSerializerAdapter(InternalSerializationService service, StreamSerializer serializer) {
        this.service = service;
        this.serializer = serializer;
    }

    @Override
    public void write(ObjectDataOutput out, Object object) throws IOException {
        this.serializer.write(out, object);
    }

    @Override
    public Object read(ObjectDataInput in) throws IOException {
        return this.serializer.read(in);
    }

    @Override
    public Object read(ObjectDataInput in, Class aClass) throws IOException {
        if (!(this.serializer instanceof TypedStreamDeserializer)) {
            throw new HazelcastSerializationException(this.toString() + " is not implementing the " + TypedStreamDeserializer.class + " interface. Please implement this interface to deserialize for class " + aClass);
        }
        TypedStreamDeserializer deserializer = (TypedStreamDeserializer)((Object)this.serializer);
        return deserializer.read(in, aClass);
    }

    @Override
    public int getTypeId() {
        return this.serializer.getTypeId();
    }

    @Override
    public void destroy() {
        this.serializer.destroy();
    }

    @Override
    public Serializer getImpl() {
        return this.serializer;
    }

    public String toString() {
        return "StreamSerializerAdapter{serializer=" + this.serializer + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        StreamSerializerAdapter that = (StreamSerializerAdapter)o;
        return !(this.serializer != null ? !this.serializer.equals(that.serializer) : that.serializer != null);
    }

    public int hashCode() {
        return this.serializer != null ? this.serializer.hashCode() : 0;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.internal.serialization.impl.SerializerAdapter;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.ByteArraySerializer;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.nio.serialization.Serializer;
import com.hazelcast.nio.serialization.TypedByteArrayDeserializer;
import java.io.IOException;

class ByteArraySerializerAdapter
implements SerializerAdapter {
    protected final ByteArraySerializer serializer;

    public ByteArraySerializerAdapter(ByteArraySerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public void write(ObjectDataOutput out, Object object) throws IOException {
        byte[] bytes = this.serializer.write(object);
        out.writeByteArray(bytes);
    }

    @Override
    public Object read(ObjectDataInput in) throws IOException {
        byte[] bytes = in.readByteArray();
        if (bytes == null) {
            return null;
        }
        return this.serializer.read(bytes);
    }

    @Override
    public Object read(ObjectDataInput in, Class aClass) throws IOException {
        byte[] bytes = in.readByteArray();
        if (bytes == null) {
            return null;
        }
        if (!(this.serializer instanceof TypedByteArrayDeserializer)) {
            throw new HazelcastSerializationException(this.serializer + " is not implementing the " + TypedByteArrayDeserializer.class + " interface. Please implement this interface to deserialize for class " + aClass);
        }
        TypedByteArrayDeserializer deserializer = (TypedByteArrayDeserializer)((Object)this.serializer);
        return deserializer.read(bytes, aClass);
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
        return "SerializerAdapter{serializer=" + this.serializer + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ByteArraySerializerAdapter that = (ByteArraySerializerAdapter)o;
        return !(this.serializer != null ? !this.serializer.equals(that.serializer) : that.serializer != null);
    }

    public int hashCode() {
        return this.serializer != null ? this.serializer.hashCode() : 0;
    }
}


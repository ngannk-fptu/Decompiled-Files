/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.internal.serialization.InputOutputFactory;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.UnsafeObjectDataInput;
import com.hazelcast.internal.serialization.impl.UnsafeObjectDataOutput;
import com.hazelcast.nio.BufferObjectDataInput;
import com.hazelcast.nio.BufferObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import java.nio.ByteOrder;

final class UnsafeInputOutputFactory
implements InputOutputFactory {
    UnsafeInputOutputFactory() {
    }

    @Override
    public BufferObjectDataInput createInput(Data data, InternalSerializationService service) {
        return new UnsafeObjectDataInput(data.toByteArray(), 8, service);
    }

    @Override
    public BufferObjectDataInput createInput(byte[] buffer, InternalSerializationService service) {
        return new UnsafeObjectDataInput(buffer, service);
    }

    @Override
    public BufferObjectDataOutput createOutput(int size, InternalSerializationService service) {
        return new UnsafeObjectDataOutput(size, service);
    }

    @Override
    public ByteOrder getByteOrder() {
        return ByteOrder.nativeOrder();
    }
}


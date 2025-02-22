/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.internal.serialization.InputOutputFactory;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.ByteArrayObjectDataInput;
import com.hazelcast.internal.serialization.impl.ByteArrayObjectDataOutput;
import com.hazelcast.nio.BufferObjectDataInput;
import com.hazelcast.nio.BufferObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import java.nio.ByteOrder;

final class ByteArrayInputOutputFactory
implements InputOutputFactory {
    private final ByteOrder byteOrder;

    public ByteArrayInputOutputFactory(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
    }

    @Override
    public BufferObjectDataInput createInput(Data data, InternalSerializationService service) {
        return new ByteArrayObjectDataInput(data.toByteArray(), 8, service, this.byteOrder);
    }

    @Override
    public BufferObjectDataInput createInput(byte[] buffer, InternalSerializationService service) {
        return new ByteArrayObjectDataInput(buffer, service, this.byteOrder);
    }

    @Override
    public BufferObjectDataOutput createOutput(int size, InternalSerializationService service) {
        return new ByteArrayObjectDataOutput(size, service, this.byteOrder);
    }

    @Override
    public ByteOrder getByteOrder() {
        return this.byteOrder;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.nio.BufferObjectDataInput;
import com.hazelcast.nio.BufferObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import java.nio.ByteOrder;

public interface InputOutputFactory {
    public BufferObjectDataInput createInput(Data var1, InternalSerializationService var2);

    public BufferObjectDataInput createInput(byte[] var1, InternalSerializationService var2);

    public BufferObjectDataOutput createOutput(int var1, InternalSerializationService var2);

    public ByteOrder getByteOrder();
}


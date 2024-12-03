/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization;

import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.internal.serialization.PortableContext;
import com.hazelcast.nio.BufferObjectDataInput;
import com.hazelcast.nio.BufferObjectDataOutput;
import com.hazelcast.nio.Disposable;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.DataType;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.spi.serialization.SerializationService;
import java.io.IOException;
import java.nio.ByteOrder;

public interface InternalSerializationService
extends SerializationService,
Disposable {
    public static final byte VERSION_1 = 1;

    public byte[] toBytes(Object var1);

    public byte[] toBytes(Object var1, int var2, boolean var3);

    public <B extends Data> B toData(Object var1, DataType var2);

    public <B extends Data> B toData(Object var1, DataType var2, PartitioningStrategy var3);

    public <B extends Data> B convertData(Data var1, DataType var2);

    public void writeObject(ObjectDataOutput var1, Object var2);

    public <T> T readObject(ObjectDataInput var1);

    public <T> T readObject(ObjectDataInput var1, Class var2);

    public void disposeData(Data var1);

    public BufferObjectDataInput createObjectDataInput(byte[] var1);

    public BufferObjectDataInput createObjectDataInput(Data var1);

    public BufferObjectDataOutput createObjectDataOutput(int var1);

    public BufferObjectDataOutput createObjectDataOutput();

    public PortableReader createPortableReader(Data var1) throws IOException;

    public PortableContext getPortableContext();

    public ClassLoader getClassLoader();

    public ByteOrder getByteOrder();

    public byte getVersion();
}


/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio;

import com.hazelcast.nio.VersionAware;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteOrder;

public interface ObjectDataOutput
extends DataOutput,
VersionAware {
    public void writeByteArray(byte[] var1) throws IOException;

    public void writeBooleanArray(boolean[] var1) throws IOException;

    public void writeCharArray(char[] var1) throws IOException;

    public void writeIntArray(int[] var1) throws IOException;

    public void writeLongArray(long[] var1) throws IOException;

    public void writeDoubleArray(double[] var1) throws IOException;

    public void writeFloatArray(float[] var1) throws IOException;

    public void writeShortArray(short[] var1) throws IOException;

    public void writeUTFArray(String[] var1) throws IOException;

    public void writeObject(Object var1) throws IOException;

    public void writeData(Data var1) throws IOException;

    public byte[] toByteArray();

    public byte[] toByteArray(int var1);

    public ByteOrder getByteOrder();

    public SerializationService getSerializationService();
}


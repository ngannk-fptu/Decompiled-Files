/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.nio.VersionAware;
import com.hazelcast.nio.serialization.Data;
import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteOrder;

public interface ObjectDataInput
extends DataInput,
VersionAware {
    public byte[] readByteArray() throws IOException;

    public boolean[] readBooleanArray() throws IOException;

    public char[] readCharArray() throws IOException;

    public int[] readIntArray() throws IOException;

    public long[] readLongArray() throws IOException;

    public double[] readDoubleArray() throws IOException;

    public float[] readFloatArray() throws IOException;

    public short[] readShortArray() throws IOException;

    public String[] readUTFArray() throws IOException;

    public <T> T readObject() throws IOException;

    public <T> T readDataAsObject() throws IOException;

    public <T> T readObject(Class var1) throws IOException;

    public Data readData() throws IOException;

    public ClassLoader getClassLoader();

    public ByteOrder getByteOrder();

    public InternalSerializationService getSerializationService();
}


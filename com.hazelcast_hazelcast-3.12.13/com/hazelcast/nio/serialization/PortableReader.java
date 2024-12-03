/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.serialization;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.serialization.FieldType;
import com.hazelcast.nio.serialization.Portable;
import java.io.IOException;
import java.util.Set;

public interface PortableReader {
    public int getVersion();

    public boolean hasField(String var1);

    public Set<String> getFieldNames();

    public FieldType getFieldType(String var1);

    public int getFieldClassId(String var1);

    public int readInt(String var1) throws IOException;

    public long readLong(String var1) throws IOException;

    public String readUTF(String var1) throws IOException;

    public boolean readBoolean(String var1) throws IOException;

    public byte readByte(String var1) throws IOException;

    public char readChar(String var1) throws IOException;

    public double readDouble(String var1) throws IOException;

    public float readFloat(String var1) throws IOException;

    public short readShort(String var1) throws IOException;

    public <P extends Portable> P readPortable(String var1) throws IOException;

    public byte[] readByteArray(String var1) throws IOException;

    public boolean[] readBooleanArray(String var1) throws IOException;

    public char[] readCharArray(String var1) throws IOException;

    public int[] readIntArray(String var1) throws IOException;

    public long[] readLongArray(String var1) throws IOException;

    public double[] readDoubleArray(String var1) throws IOException;

    public float[] readFloatArray(String var1) throws IOException;

    public short[] readShortArray(String var1) throws IOException;

    public String[] readUTFArray(String var1) throws IOException;

    public Portable[] readPortableArray(String var1) throws IOException;

    public ObjectDataInput getRawDataInput() throws IOException;
}


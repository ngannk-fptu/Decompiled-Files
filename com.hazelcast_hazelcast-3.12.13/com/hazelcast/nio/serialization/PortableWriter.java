/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.serialization;

import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Portable;
import java.io.IOException;

public interface PortableWriter {
    public void writeInt(String var1, int var2) throws IOException;

    public void writeLong(String var1, long var2) throws IOException;

    public void writeUTF(String var1, String var2) throws IOException;

    public void writeBoolean(String var1, boolean var2) throws IOException;

    public void writeByte(String var1, byte var2) throws IOException;

    public void writeChar(String var1, int var2) throws IOException;

    public void writeDouble(String var1, double var2) throws IOException;

    public void writeFloat(String var1, float var2) throws IOException;

    public void writeShort(String var1, short var2) throws IOException;

    public void writePortable(String var1, Portable var2) throws IOException;

    public void writeNullPortable(String var1, int var2, int var3) throws IOException;

    public void writeByteArray(String var1, byte[] var2) throws IOException;

    public void writeBooleanArray(String var1, boolean[] var2) throws IOException;

    public void writeCharArray(String var1, char[] var2) throws IOException;

    public void writeIntArray(String var1, int[] var2) throws IOException;

    public void writeLongArray(String var1, long[] var2) throws IOException;

    public void writeDoubleArray(String var1, double[] var2) throws IOException;

    public void writeFloatArray(String var1, float[] var2) throws IOException;

    public void writeShortArray(String var1, short[] var2) throws IOException;

    public void writeUTFArray(String var1, String[] var2) throws IOException;

    public void writePortableArray(String var1, Portable[] var2) throws IOException;

    public ObjectDataOutput getRawDataOutput() throws IOException;
}


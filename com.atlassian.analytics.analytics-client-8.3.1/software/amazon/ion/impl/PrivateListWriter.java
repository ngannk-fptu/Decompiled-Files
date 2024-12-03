/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import java.io.IOException;
import software.amazon.ion.IonWriter;

@Deprecated
public interface PrivateListWriter
extends IonWriter {
    public void writeBoolList(boolean[] var1) throws IOException;

    public void writeFloatList(float[] var1) throws IOException;

    public void writeFloatList(double[] var1) throws IOException;

    public void writeIntList(byte[] var1) throws IOException;

    public void writeIntList(short[] var1) throws IOException;

    public void writeIntList(int[] var1) throws IOException;

    public void writeIntList(long[] var1) throws IOException;

    public void writeStringList(String[] var1) throws IOException;
}


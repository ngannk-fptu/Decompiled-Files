/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.fileslice.reader;

import java.io.IOException;

public interface SequentialReader {
    public byte readByte() throws IOException;

    public int readUnsignedByte() throws IOException;

    public short readShort() throws IOException;

    public int readUnsignedShort() throws IOException;

    public int readInt() throws IOException;

    public long readUnsignedInt() throws IOException;

    public long readLong() throws IOException;

    public void skip(int var1) throws IOException;

    public String readString(int var1, boolean var2, boolean var3) throws IOException;

    public String readString(int var1) throws IOException;
}


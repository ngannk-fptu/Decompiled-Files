/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.fileslice.reader;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface RandomAccessReader {
    public int read(long var1, ByteBuffer var3, int var4, int var5) throws IOException;

    public int read(long var1, byte[] var3, int var4, int var5) throws IOException;

    public byte readByte(long var1) throws IOException;

    public int readUnsignedByte(long var1) throws IOException;

    public short readShort(long var1) throws IOException;

    public int readUnsignedShort(long var1) throws IOException;

    public int readInt(long var1) throws IOException;

    public long readUnsignedInt(long var1) throws IOException;

    public long readLong(long var1) throws IOException;

    public String readString(long var1, int var3, boolean var4, boolean var5) throws IOException;

    public String readString(long var1, int var3) throws IOException;
}


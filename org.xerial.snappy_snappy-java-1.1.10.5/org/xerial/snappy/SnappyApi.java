/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface SnappyApi {
    public long rawCompress(long var1, long var3, long var5) throws IOException;

    public long rawUncompress(long var1, long var3, long var5) throws IOException;

    public int rawCompress(ByteBuffer var1, int var2, int var3, ByteBuffer var4, int var5) throws IOException;

    public int rawCompress(Object var1, int var2, int var3, Object var4, int var5) throws IOException;

    public int rawUncompress(ByteBuffer var1, int var2, int var3, ByteBuffer var4, int var5) throws IOException;

    public int rawUncompress(Object var1, int var2, int var3, Object var4, int var5) throws IOException;

    public int maxCompressedLength(int var1);

    public int uncompressedLength(ByteBuffer var1, int var2, int var3) throws IOException;

    public int uncompressedLength(Object var1, int var2, int var3) throws IOException;

    public long uncompressedLength(long var1, long var3) throws IOException;

    public boolean isValidCompressedBuffer(ByteBuffer var1, int var2, int var3) throws IOException;

    public boolean isValidCompressedBuffer(Object var1, int var2, int var3) throws IOException;

    public boolean isValidCompressedBuffer(long var1, long var3, long var5) throws IOException;

    public void arrayCopy(Object var1, int var2, int var3, Object var4, int var5) throws IOException;
}


/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.xerial.snappy.SnappyApi;
import org.xerial.snappy.SnappyErrorCode;

public class SnappyNative
implements SnappyApi {
    public native String nativeLibraryVersion();

    @Override
    public native long rawCompress(long var1, long var3, long var5) throws IOException;

    @Override
    public native long rawUncompress(long var1, long var3, long var5) throws IOException;

    @Override
    public native int rawCompress(ByteBuffer var1, int var2, int var3, ByteBuffer var4, int var5) throws IOException;

    @Override
    public native int rawCompress(Object var1, int var2, int var3, Object var4, int var5) throws IOException;

    @Override
    public native int rawUncompress(ByteBuffer var1, int var2, int var3, ByteBuffer var4, int var5) throws IOException;

    @Override
    public native int rawUncompress(Object var1, int var2, int var3, Object var4, int var5) throws IOException;

    @Override
    public native int maxCompressedLength(int var1);

    @Override
    public native int uncompressedLength(ByteBuffer var1, int var2, int var3) throws IOException;

    @Override
    public native int uncompressedLength(Object var1, int var2, int var3) throws IOException;

    @Override
    public native long uncompressedLength(long var1, long var3) throws IOException;

    @Override
    public native boolean isValidCompressedBuffer(ByteBuffer var1, int var2, int var3) throws IOException;

    @Override
    public native boolean isValidCompressedBuffer(Object var1, int var2, int var3) throws IOException;

    @Override
    public native boolean isValidCompressedBuffer(long var1, long var3, long var5) throws IOException;

    @Override
    public native void arrayCopy(Object var1, int var2, int var3, Object var4, int var5) throws IOException;

    public void throw_error(int n) throws IOException {
        throw new IOException(String.format("%s(%d)", SnappyErrorCode.getErrorMessage(n), n));
    }
}


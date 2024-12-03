/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

import com.github.luben.zstd.AutoCloseBase;
import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdDictDecompress;
import com.github.luben.zstd.ZstdException;
import com.github.luben.zstd.util.Native;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ZstdDecompressCtx
extends AutoCloseBase {
    private long nativePtr = ZstdDecompressCtx.init();
    private ZstdDictDecompress decompression_dict = null;

    private static native long init();

    private static native void free(long var0);

    public ZstdDecompressCtx() {
        if (0L == this.nativePtr) {
            throw new IllegalStateException("ZSTD_createDeCompressCtx failed");
        }
        this.storeFence();
    }

    @Override
    void doClose() {
        if (this.nativePtr != 0L) {
            ZstdDecompressCtx.free(this.nativePtr);
            this.nativePtr = 0L;
        }
    }

    public ZstdDecompressCtx setMagicless(boolean bl) {
        this.ensureOpen();
        this.acquireSharedLock();
        Zstd.setDecompressionMagicless(this.nativePtr, bl);
        this.releaseSharedLock();
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ZstdDecompressCtx loadDict(ZstdDictDecompress zstdDictDecompress) {
        this.ensureOpen();
        this.acquireSharedLock();
        zstdDictDecompress.acquireSharedLock();
        try {
            long l = ZstdDecompressCtx.loadDDictFast0(this.nativePtr, zstdDictDecompress);
            if (Zstd.isError(l)) {
                throw new ZstdException(l);
            }
            this.decompression_dict = zstdDictDecompress;
        }
        finally {
            zstdDictDecompress.releaseSharedLock();
            this.releaseSharedLock();
        }
        return this;
    }

    private static native long loadDDictFast0(long var0, ZstdDictDecompress var2);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ZstdDecompressCtx loadDict(byte[] byArray) {
        this.ensureOpen();
        this.acquireSharedLock();
        try {
            long l = ZstdDecompressCtx.loadDDict0(this.nativePtr, byArray);
            if (Zstd.isError(l)) {
                throw new ZstdException(l);
            }
            this.decompression_dict = null;
        }
        finally {
            this.releaseSharedLock();
        }
        return this;
    }

    private static native long loadDDict0(long var0, byte[] var2);

    public void reset() {
        this.ensureOpen();
        ZstdDecompressCtx.reset0(this.nativePtr);
    }

    private static native void reset0(long var0);

    private void ensureOpen() {
        if (this.nativePtr == 0L) {
            throw new IllegalStateException("Decompression context is closed");
        }
    }

    public boolean decompressDirectByteBufferStream(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        this.ensureOpen();
        long l = ZstdDecompressCtx.decompressDirectByteBufferStream0(this.nativePtr, byteBuffer, byteBuffer.position(), byteBuffer.limit(), byteBuffer2, byteBuffer2.position(), byteBuffer2.limit());
        if ((l & 0x80000000L) != 0L) {
            long l2 = l & 0xFFL;
            throw new ZstdException(l2, Zstd.getErrorName(l2));
        }
        byteBuffer2.position((int)(l & Integer.MAX_VALUE));
        byteBuffer.position((int)(l >>> 32) & Integer.MAX_VALUE);
        return l >>> 63 == 1L;
    }

    private static native long decompressDirectByteBufferStream0(long var0, ByteBuffer var2, int var3, int var4, ByteBuffer var5, int var6, int var7);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int decompressDirectByteBuffer(ByteBuffer byteBuffer, int n, int n2, ByteBuffer byteBuffer2, int n3, int n4) {
        this.ensureOpen();
        if (!byteBuffer2.isDirect()) {
            throw new IllegalArgumentException("srcBuff must be a direct buffer");
        }
        if (!byteBuffer.isDirect()) {
            throw new IllegalArgumentException("dstBuff must be a direct buffer");
        }
        this.acquireSharedLock();
        try {
            long l = ZstdDecompressCtx.decompressDirectByteBuffer0(this.nativePtr, byteBuffer, n, n2, byteBuffer2, n3, n4);
            if (Zstd.isError(l)) {
                throw new ZstdException(l);
            }
            if (l > Integer.MAX_VALUE) {
                throw new ZstdException(Zstd.errGeneric(), "Output size is greater than MAX_INT");
            }
            int n5 = (int)l;
            return n5;
        }
        finally {
            this.releaseSharedLock();
        }
    }

    private static native long decompressDirectByteBuffer0(long var0, ByteBuffer var2, int var3, int var4, ByteBuffer var5, int var6, int var7);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int decompressByteArray(byte[] byArray, int n, int n2, byte[] byArray2, int n3, int n4) {
        this.ensureOpen();
        this.acquireSharedLock();
        try {
            long l = ZstdDecompressCtx.decompressByteArray0(this.nativePtr, byArray, n, n2, byArray2, n3, n4);
            if (Zstd.isError(l)) {
                throw new ZstdException(l);
            }
            if (l > Integer.MAX_VALUE) {
                throw new ZstdException(Zstd.errGeneric(), "Output size is greater than MAX_INT");
            }
            int n5 = (int)l;
            return n5;
        }
        finally {
            this.releaseSharedLock();
        }
    }

    private static native long decompressByteArray0(long var0, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7);

    public int decompress(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws ZstdException {
        int n = this.decompressDirectByteBuffer(byteBuffer, byteBuffer.position(), byteBuffer.limit() - byteBuffer.position(), byteBuffer2, byteBuffer2.position(), byteBuffer2.limit() - byteBuffer2.position());
        byteBuffer2.position(byteBuffer2.limit());
        byteBuffer.position(byteBuffer.position() + n);
        return n;
    }

    public ByteBuffer decompress(ByteBuffer byteBuffer, int n) throws ZstdException {
        ByteBuffer byteBuffer2 = ByteBuffer.allocateDirect(n);
        int n2 = this.decompressDirectByteBuffer(byteBuffer2, 0, n, byteBuffer, byteBuffer.position(), byteBuffer.limit() - byteBuffer.position());
        byteBuffer.position(byteBuffer.limit());
        return byteBuffer2;
    }

    public int decompress(byte[] byArray, byte[] byArray2) {
        return this.decompressByteArray(byArray, 0, byArray.length, byArray2, 0, byArray2.length);
    }

    public byte[] decompress(byte[] byArray, int n) throws ZstdException {
        if (n < 0) {
            throw new ZstdException(Zstd.errGeneric(), "Original size should not be negative");
        }
        byte[] byArray2 = new byte[n];
        int n2 = this.decompress(byArray2, byArray);
        if (n2 != n) {
            return Arrays.copyOfRange(byArray2, 0, n2);
        }
        return byArray2;
    }

    static {
        Native.load();
    }
}


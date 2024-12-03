/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.httpclient.util.EncodingUtil;

public class ChunkedOutputStream
extends OutputStream {
    private static final byte[] CRLF = new byte[]{13, 10};
    private static final byte[] ENDCHUNK = CRLF;
    private static final byte[] ZERO = new byte[]{48};
    private OutputStream stream = null;
    private byte[] cache;
    private int cachePosition = 0;
    private boolean wroteLastChunk = false;

    public ChunkedOutputStream(OutputStream stream, int bufferSize) throws IOException {
        this.cache = new byte[bufferSize];
        this.stream = stream;
    }

    public ChunkedOutputStream(OutputStream stream) throws IOException {
        this(stream, 2048);
    }

    protected void flushCache() throws IOException {
        if (this.cachePosition > 0) {
            byte[] chunkHeader = EncodingUtil.getAsciiBytes(Integer.toHexString(this.cachePosition) + "\r\n");
            this.stream.write(chunkHeader, 0, chunkHeader.length);
            this.stream.write(this.cache, 0, this.cachePosition);
            this.stream.write(ENDCHUNK, 0, ENDCHUNK.length);
            this.cachePosition = 0;
        }
    }

    protected void flushCacheWithAppend(byte[] bufferToAppend, int off, int len) throws IOException {
        byte[] chunkHeader = EncodingUtil.getAsciiBytes(Integer.toHexString(this.cachePosition + len) + "\r\n");
        this.stream.write(chunkHeader, 0, chunkHeader.length);
        this.stream.write(this.cache, 0, this.cachePosition);
        this.stream.write(bufferToAppend, off, len);
        this.stream.write(ENDCHUNK, 0, ENDCHUNK.length);
        this.cachePosition = 0;
    }

    protected void writeClosingChunk() throws IOException {
        this.stream.write(ZERO, 0, ZERO.length);
        this.stream.write(CRLF, 0, CRLF.length);
        this.stream.write(ENDCHUNK, 0, ENDCHUNK.length);
    }

    public void finish() throws IOException {
        if (!this.wroteLastChunk) {
            this.flushCache();
            this.writeClosingChunk();
            this.wroteLastChunk = true;
        }
    }

    @Override
    public void write(int b) throws IOException {
        this.cache[this.cachePosition] = (byte)b;
        ++this.cachePosition;
        if (this.cachePosition == this.cache.length) {
            this.flushCache();
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    @Override
    public void write(byte[] src, int off, int len) throws IOException {
        if (len >= this.cache.length - this.cachePosition) {
            this.flushCacheWithAppend(src, off, len);
        } else {
            System.arraycopy(src, off, this.cache, this.cachePosition, len);
            this.cachePosition += len;
        }
    }

    @Override
    public void flush() throws IOException {
        this.stream.flush();
    }

    @Override
    public void close() throws IOException {
        this.finish();
        super.close();
    }
}


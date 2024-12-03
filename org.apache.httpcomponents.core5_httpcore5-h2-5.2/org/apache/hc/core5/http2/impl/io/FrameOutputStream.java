/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.core5.http2.impl.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import org.apache.hc.core5.util.Args;

abstract class FrameOutputStream
extends OutputStream {
    private final OutputStream outputStream;
    private final byte[] cache;
    private int cachePosition;

    public FrameOutputStream(int minChunkSize, OutputStream outputStream) {
        this.outputStream = (OutputStream)Args.notNull((Object)outputStream, (String)"Output stream");
        this.cache = new byte[minChunkSize];
    }

    protected abstract void write(ByteBuffer var1, boolean var2, OutputStream var3) throws IOException;

    private void flushCache(boolean endStream) throws IOException {
        if (this.cachePosition > 0) {
            this.write(ByteBuffer.wrap(this.cache, 0, this.cachePosition), endStream, this.outputStream);
            this.cachePosition = 0;
        }
    }

    @Override
    public void write(int b) throws IOException {
        this.cache[this.cachePosition] = (byte)b;
        ++this.cachePosition;
        if (this.cachePosition == this.cache.length) {
            this.flushCache(false);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    @Override
    public void write(byte[] src, int off, int len) throws IOException {
        if (len >= this.cache.length - this.cachePosition) {
            this.flushCache(false);
            this.write(ByteBuffer.wrap(src, off, len), false, this.outputStream);
        } else {
            System.arraycopy(src, off, this.cache, this.cachePosition, len);
            this.cachePosition += len;
        }
    }

    @Override
    public void flush() throws IOException {
        this.flushCache(false);
        this.outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        if (this.cachePosition > 0) {
            this.flushCache(true);
        } else {
            this.write(null, true, this.outputStream);
        }
        this.flushCache(true);
        this.outputStream.flush();
    }
}


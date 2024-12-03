/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.stream;

import com.twelvemonkeys.lang.Validate;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.imageio.stream.ImageInputStreamImpl;

public final class BufferedFileImageInputStream
extends ImageInputStreamImpl {
    static final int DEFAULT_BUFFER_SIZE = 8192;
    private byte[] buffer = new byte[8192];
    private int bufferPos;
    private int bufferLimit;
    private final ByteBuffer integralCache = ByteBuffer.allocate(8);
    private final byte[] integralCacheArray = this.integralCache.array();
    private RandomAccessFile raf;

    public BufferedFileImageInputStream(File file) throws FileNotFoundException {
        this(new RandomAccessFile((File)Validate.notNull((Object)file, (String)"file"), "r"));
    }

    public BufferedFileImageInputStream(RandomAccessFile randomAccessFile) {
        this.raf = (RandomAccessFile)Validate.notNull((Object)randomAccessFile, (String)"raf");
    }

    private boolean fillBuffer() throws IOException {
        int n = this.raf.read(this.buffer, 0, this.buffer.length);
        this.bufferPos = 0;
        this.bufferLimit = Math.max(n, 0);
        return this.bufferLimit > 0;
    }

    private boolean bufferEmpty() {
        return this.bufferPos >= this.bufferLimit;
    }

    @Override
    public void setByteOrder(ByteOrder byteOrder) {
        super.setByteOrder(byteOrder);
        this.integralCache.order(byteOrder);
    }

    @Override
    public int read() throws IOException {
        this.checkClosed();
        if (this.bufferEmpty() && !this.fillBuffer()) {
            return -1;
        }
        this.bitOffset = 0;
        ++this.streamPos;
        return this.buffer[this.bufferPos++] & 0xFF;
    }

    @Override
    public int read(byte[] byArray, int n, int n2) throws IOException {
        int n3;
        this.checkClosed();
        this.bitOffset = 0;
        if (this.bufferEmpty()) {
            if (n2 >= this.buffer.length) {
                return this.readDirect(byArray, n, n2);
            }
            if (!this.fillBuffer()) {
                return -1;
            }
        }
        if (n2 > (n3 = this.readBuffered(byArray, n, n2))) {
            return n3 + Math.max(0, this.readDirect(byArray, n + n3, n2 - n3));
        }
        return n3;
    }

    private int readDirect(byte[] byArray, int n, int n2) throws IOException {
        this.bufferLimit = 0;
        int n3 = this.raf.read(byArray, n, n2);
        if (n3 > 0) {
            this.streamPos += (long)n3;
        }
        return n3;
    }

    private int readBuffered(byte[] byArray, int n, int n2) {
        int n3 = Math.min(this.bufferLimit - this.bufferPos, n2);
        if (n3 > 0) {
            System.arraycopy(this.buffer, this.bufferPos, byArray, n, n3);
            this.bufferPos += n3;
            this.streamPos += (long)n3;
        }
        return n3;
    }

    @Override
    public long length() {
        try {
            this.checkClosed();
            return this.raf.length();
        }
        catch (IOException iOException) {
            return -1L;
        }
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.raf.close();
        this.raf = null;
        this.buffer = null;
    }

    @Override
    public short readShort() throws IOException {
        this.readFully(this.integralCacheArray, 0, 2);
        return this.integralCache.getShort(0);
    }

    @Override
    public int readInt() throws IOException {
        this.readFully(this.integralCacheArray, 0, 4);
        return this.integralCache.getInt(0);
    }

    @Override
    public long readLong() throws IOException {
        this.readFully(this.integralCacheArray, 0, 8);
        return this.integralCache.getLong(0);
    }

    @Override
    public void seek(long l) throws IOException {
        this.checkClosed();
        if (l < this.flushedPos) {
            throw new IndexOutOfBoundsException("position < flushedPos!");
        }
        this.bitOffset = 0;
        if (this.streamPos == l) {
            return;
        }
        long l2 = (long)this.bufferPos + l - this.streamPos;
        if (l2 >= 0L && l2 < (long)this.bufferLimit) {
            this.bufferPos = (int)l2;
        } else {
            this.bufferLimit = 0;
            this.raf.seek(l);
        }
        this.streamPos = l;
    }
}


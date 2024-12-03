/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.stream;

import com.twelvemonkeys.lang.Validate;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageInputStreamImpl;

@Deprecated
public final class BufferedImageInputStream
extends ImageInputStreamImpl
implements ImageInputStream {
    static final int DEFAULT_BUFFER_SIZE = 8192;
    private ImageInputStream stream;
    private ByteBuffer buffer;
    private final ByteBuffer integralCache = ByteBuffer.allocate(8);
    private final byte[] integralCacheArray = this.integralCache.array();

    public BufferedImageInputStream(ImageInputStream imageInputStream) throws IOException {
        this(imageInputStream, 8192);
    }

    private BufferedImageInputStream(ImageInputStream imageInputStream, int n) throws IOException {
        this.stream = (ImageInputStream)Validate.notNull((Object)imageInputStream, (String)"stream");
        this.streamPos = imageInputStream.getStreamPosition();
        this.buffer = ByteBuffer.allocate(n);
        this.buffer.limit(0);
    }

    private void fillBuffer() throws IOException {
        this.buffer.clear();
        int n = this.stream.read(this.buffer.array(), 0, this.buffer.capacity());
        if (n >= 0) {
            this.buffer.position(n);
            this.buffer.flip();
        } else {
            this.buffer.limit(0);
        }
    }

    @Override
    public void setByteOrder(ByteOrder byteOrder) {
        super.setByteOrder(byteOrder);
        this.integralCache.order(byteOrder);
    }

    @Override
    public int read() throws IOException {
        this.checkClosed();
        if (!this.buffer.hasRemaining()) {
            this.fillBuffer();
            if (!this.buffer.hasRemaining()) {
                return -1;
            }
        }
        this.bitOffset = 0;
        ++this.streamPos;
        return this.buffer.get() & 0xFF;
    }

    @Override
    public int read(byte[] byArray, int n, int n2) throws IOException {
        this.checkClosed();
        this.bitOffset = 0;
        if (!this.buffer.hasRemaining()) {
            if (n2 >= this.buffer.capacity()) {
                return this.readDirect(byArray, n, n2);
            }
            this.fillBuffer();
        }
        return this.readBuffered(byArray, n, n2);
    }

    private int readDirect(byte[] byArray, int n, int n2) throws IOException {
        this.buffer.limit(0);
        int n3 = this.stream.read(byArray, n, n2);
        if (n3 > 0) {
            this.streamPos += (long)n3;
        }
        return n3;
    }

    private int readBuffered(byte[] byArray, int n, int n2) {
        if (!this.buffer.hasRemaining()) {
            return -1;
        }
        int n3 = Math.min(this.buffer.remaining(), n2);
        if (n3 > 0) {
            int n4 = this.buffer.position();
            System.arraycopy(this.buffer.array(), n4, byArray, n, n3);
            this.buffer.position(n4 + n3);
        }
        this.streamPos += (long)n3;
        return n3;
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
    public int readBit() throws IOException {
        this.checkClosed();
        if (!this.buffer.hasRemaining()) {
            this.fillBuffer();
            if (!this.buffer.hasRemaining()) {
                throw new EOFException();
            }
        }
        int n = this.bitOffset + 1 & 7;
        int n2 = this.buffer.get() & 0xFF;
        if (n != 0) {
            this.buffer.position(this.buffer.position() - 1);
            n2 >>= 8 - n;
        } else {
            ++this.streamPos;
        }
        this.bitOffset = n;
        return n2 & 1;
    }

    @Override
    public long readBits(int n) throws IOException {
        int n2;
        this.checkClosed();
        if (n < 0 || n > 64) {
            throw new IllegalArgumentException();
        }
        if (n == 0) {
            return 0L;
        }
        int n3 = this.bitOffset + n & 7;
        long l = 0L;
        for (n2 = n + this.bitOffset; n2 > 0; n2 -= 8) {
            if (!this.buffer.hasRemaining()) {
                this.fillBuffer();
                if (!this.buffer.hasRemaining()) {
                    throw new EOFException();
                }
            }
            int n4 = this.buffer.get() & 0xFF;
            ++this.streamPos;
            l <<= 8;
            l |= (long)n4;
        }
        if (n3 != 0) {
            this.buffer.position(this.buffer.position() - 1);
            --this.streamPos;
        }
        this.bitOffset = n3;
        l >>>= -n2;
        return l &= -1L >>> 64 - n;
    }

    @Override
    public void seek(long l) throws IOException {
        this.checkClosed();
        this.bitOffset = 0;
        if (this.streamPos == l) {
            return;
        }
        long l2 = (long)this.buffer.position() + l - this.streamPos;
        if (l2 >= 0L && l2 <= (long)this.buffer.limit()) {
            this.buffer.position((int)l2);
        } else {
            this.buffer.limit(0);
            this.stream.seek(l);
        }
        this.streamPos = l;
    }

    @Override
    public void flushBefore(long l) throws IOException {
        this.checkClosed();
        this.stream.flushBefore(l);
    }

    @Override
    public long getFlushedPosition() {
        return this.stream.getFlushedPosition();
    }

    @Override
    public boolean isCached() {
        return this.stream.isCached();
    }

    @Override
    public boolean isCachedMemory() {
        return this.stream.isCachedMemory();
    }

    @Override
    public boolean isCachedFile() {
        return this.stream.isCachedFile();
    }

    @Override
    public void close() throws IOException {
        if (this.stream != null) {
            this.stream = null;
            this.buffer = null;
        }
        super.close();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public long length() {
        try {
            return this.stream.length();
        }
        catch (IOException iOException) {
            return -1L;
        }
    }
}


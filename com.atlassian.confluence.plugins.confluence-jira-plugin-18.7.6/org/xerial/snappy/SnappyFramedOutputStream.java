/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.zip.Checksum;
import org.xerial.snappy.Snappy;
import org.xerial.snappy.SnappyFramed;
import org.xerial.snappy.pool.BufferPool;
import org.xerial.snappy.pool.DefaultPoolFactory;

public final class SnappyFramedOutputStream
extends OutputStream
implements WritableByteChannel {
    public static final int MAX_BLOCK_SIZE = 65536;
    public static final int DEFAULT_BLOCK_SIZE = 65536;
    public static final double DEFAULT_MIN_COMPRESSION_RATIO = 0.85;
    private final Checksum crc32 = SnappyFramed.getCRC32C();
    private final ByteBuffer headerBuffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
    private final BufferPool bufferPool;
    private final int blockSize;
    private final ByteBuffer buffer;
    private final ByteBuffer directInputBuffer;
    private final ByteBuffer outputBuffer;
    private final double minCompressionRatio;
    private final WritableByteChannel out;
    private boolean closed;

    public SnappyFramedOutputStream(OutputStream outputStream) throws IOException {
        this(outputStream, 65536, 0.85, DefaultPoolFactory.getDefaultPool());
    }

    public SnappyFramedOutputStream(OutputStream outputStream, BufferPool bufferPool) throws IOException {
        this(outputStream, 65536, 0.85, bufferPool);
    }

    public SnappyFramedOutputStream(OutputStream outputStream, int n, double d) throws IOException {
        this(Channels.newChannel(outputStream), n, d, DefaultPoolFactory.getDefaultPool());
    }

    public SnappyFramedOutputStream(OutputStream outputStream, int n, double d, BufferPool bufferPool) throws IOException {
        this(Channels.newChannel(outputStream), n, d, bufferPool);
    }

    public SnappyFramedOutputStream(WritableByteChannel writableByteChannel) throws IOException {
        this(writableByteChannel, 65536, 0.85, DefaultPoolFactory.getDefaultPool());
    }

    public SnappyFramedOutputStream(WritableByteChannel writableByteChannel, BufferPool bufferPool) throws IOException {
        this(writableByteChannel, 65536, 0.85, bufferPool);
    }

    public SnappyFramedOutputStream(WritableByteChannel writableByteChannel, int n, double d) throws IOException {
        this(writableByteChannel, n, d, DefaultPoolFactory.getDefaultPool());
    }

    public SnappyFramedOutputStream(WritableByteChannel writableByteChannel, int n, double d, BufferPool bufferPool) throws IOException {
        if (writableByteChannel == null) {
            throw new NullPointerException("out is null");
        }
        if (bufferPool == null) {
            throw new NullPointerException("buffer pool is null");
        }
        if (d <= 0.0 || d > 1.0) {
            throw new IllegalArgumentException("minCompressionRatio " + d + " must be in (0,1.0]");
        }
        if (n <= 0 || n > 65536) {
            throw new IllegalArgumentException("block size " + n + " must be in (0, 65536]");
        }
        this.blockSize = n;
        this.out = writableByteChannel;
        this.minCompressionRatio = d;
        this.bufferPool = bufferPool;
        this.buffer = ByteBuffer.wrap(bufferPool.allocateArray(n), 0, n);
        this.directInputBuffer = bufferPool.allocateDirect(n);
        this.outputBuffer = bufferPool.allocateDirect(Snappy.maxCompressedLength(n));
        this.writeHeader(writableByteChannel);
    }

    private void writeHeader(WritableByteChannel writableByteChannel) throws IOException {
        writableByteChannel.write(ByteBuffer.wrap(SnappyFramed.HEADER_BYTES));
    }

    @Override
    public boolean isOpen() {
        return !this.closed;
    }

    @Override
    public void write(int n) throws IOException {
        if (this.closed) {
            throw new IOException("Stream is closed");
        }
        if (this.buffer.remaining() <= 0) {
            this.flushBuffer();
        }
        this.buffer.put((byte)n);
    }

    @Override
    public void write(byte[] byArray, int n, int n2) throws IOException {
        if (this.closed) {
            throw new IOException("Stream is closed");
        }
        if (byArray == null) {
            throw new NullPointerException();
        }
        if (n < 0 || n > byArray.length || n2 < 0 || n + n2 > byArray.length || n + n2 < 0) {
            throw new IndexOutOfBoundsException();
        }
        while (n2 > 0) {
            if (this.buffer.remaining() <= 0) {
                this.flushBuffer();
            }
            int n3 = Math.min(n2, this.buffer.remaining());
            this.buffer.put(byArray, n, n3);
            n += n3;
            n2 -= n3;
        }
    }

    @Override
    public int write(ByteBuffer byteBuffer) throws IOException {
        if (this.closed) {
            throw new ClosedChannelException();
        }
        if (this.buffer.remaining() <= 0) {
            this.flushBuffer();
        }
        int n = byteBuffer.remaining();
        if (this.buffer.remaining() >= byteBuffer.remaining()) {
            this.buffer.put(byteBuffer);
            return n;
        }
        int n2 = byteBuffer.position() + byteBuffer.remaining();
        while (byteBuffer.position() + this.buffer.remaining() <= n2) {
            byteBuffer.limit(byteBuffer.position() + this.buffer.remaining());
            this.buffer.put(byteBuffer);
            this.flushBuffer();
        }
        byteBuffer.limit(n2);
        this.buffer.put(byteBuffer);
        return n;
    }

    public long transferFrom(InputStream inputStream) throws IOException {
        int n;
        if (this.closed) {
            throw new ClosedChannelException();
        }
        if (inputStream == null) {
            throw new NullPointerException();
        }
        if (this.buffer.remaining() == 0) {
            this.flushBuffer();
        }
        assert (this.buffer.hasArray());
        byte[] byArray = this.buffer.array();
        int n2 = this.buffer.arrayOffset();
        long l = 0L;
        while ((n = inputStream.read(byArray, n2 + this.buffer.position(), this.buffer.remaining())) != -1) {
            this.buffer.position(this.buffer.position() + n);
            if (this.buffer.remaining() == 0) {
                this.flushBuffer();
            }
            l += (long)n;
        }
        return l;
    }

    public long transferFrom(ReadableByteChannel readableByteChannel) throws IOException {
        int n;
        if (this.closed) {
            throw new ClosedChannelException();
        }
        if (readableByteChannel == null) {
            throw new NullPointerException();
        }
        if (this.buffer.remaining() == 0) {
            this.flushBuffer();
        }
        long l = 0L;
        while ((n = readableByteChannel.read(this.buffer)) != -1) {
            if (this.buffer.remaining() == 0) {
                this.flushBuffer();
            }
            l += (long)n;
        }
        return l;
    }

    @Override
    public final void flush() throws IOException {
        if (this.closed) {
            throw new IOException("Stream is closed");
        }
        this.flushBuffer();
    }

    @Override
    public final void close() throws IOException {
        if (this.closed) {
            return;
        }
        try {
            this.flush();
            this.out.close();
        }
        finally {
            this.closed = true;
            this.bufferPool.releaseArray(this.buffer.array());
            this.bufferPool.releaseDirect(this.directInputBuffer);
            this.bufferPool.releaseDirect(this.outputBuffer);
        }
    }

    private void flushBuffer() throws IOException {
        if (this.buffer.position() > 0) {
            this.buffer.flip();
            this.writeCompressed(this.buffer);
            this.buffer.clear();
            this.buffer.limit(this.blockSize);
        }
    }

    private void writeCompressed(ByteBuffer byteBuffer) throws IOException {
        byte[] byArray = byteBuffer.array();
        int n = byteBuffer.remaining();
        int n2 = SnappyFramed.maskedCrc32c(this.crc32, byArray, 0, n);
        this.directInputBuffer.clear();
        this.directInputBuffer.put(byteBuffer);
        this.directInputBuffer.flip();
        this.outputBuffer.clear();
        Snappy.compress(this.directInputBuffer, this.outputBuffer);
        int n3 = this.outputBuffer.remaining();
        if ((double)n3 / (double)n <= this.minCompressionRatio) {
            this.writeBlock(this.out, this.outputBuffer, true, n2);
        } else {
            byteBuffer.flip();
            this.writeBlock(this.out, byteBuffer, false, n2);
        }
    }

    private void writeBlock(WritableByteChannel writableByteChannel, ByteBuffer byteBuffer, boolean bl, int n) throws IOException {
        this.headerBuffer.clear();
        this.headerBuffer.put((byte)(!bl ? 1 : 0));
        int n2 = byteBuffer.remaining() + 4;
        this.headerBuffer.put((byte)n2);
        this.headerBuffer.put((byte)(n2 >>> 8));
        this.headerBuffer.put((byte)(n2 >>> 16));
        this.headerBuffer.putInt(n);
        this.headerBuffer.flip();
        writableByteChannel.write(this.headerBuffer);
        writableByteChannel.write(byteBuffer);
    }
}


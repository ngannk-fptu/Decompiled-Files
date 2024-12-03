/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.zip.Checksum;
import org.xerial.snappy.Snappy;
import org.xerial.snappy.SnappyFramed;
import org.xerial.snappy.pool.BufferPool;
import org.xerial.snappy.pool.DefaultPoolFactory;

public final class SnappyFramedInputStream
extends InputStream
implements ReadableByteChannel {
    private final Checksum crc32 = SnappyFramed.getCRC32C();
    private final ReadableByteChannel rbc;
    private final ByteBuffer frameHeader;
    private final boolean verifyChecksums;
    private final BufferPool bufferPool;
    private ByteBuffer input;
    private ByteBuffer uncompressedDirect;
    private boolean closed;
    private boolean eof;
    private int valid;
    private int position;
    private byte[] buffer;

    public SnappyFramedInputStream(InputStream inputStream) throws IOException {
        this(inputStream, true, DefaultPoolFactory.getDefaultPool());
    }

    public SnappyFramedInputStream(InputStream inputStream, BufferPool bufferPool) throws IOException {
        this(inputStream, true, bufferPool);
    }

    public SnappyFramedInputStream(InputStream inputStream, boolean bl) throws IOException {
        this(inputStream, bl, DefaultPoolFactory.getDefaultPool());
    }

    public SnappyFramedInputStream(InputStream inputStream, boolean bl, BufferPool bufferPool) throws IOException {
        this(Channels.newChannel(inputStream), bl, bufferPool);
    }

    public SnappyFramedInputStream(ReadableByteChannel readableByteChannel, BufferPool bufferPool) throws IOException {
        this(readableByteChannel, true, bufferPool);
    }

    public SnappyFramedInputStream(ReadableByteChannel readableByteChannel) throws IOException {
        this(readableByteChannel, true);
    }

    public SnappyFramedInputStream(ReadableByteChannel readableByteChannel, boolean bl) throws IOException {
        this(readableByteChannel, bl, DefaultPoolFactory.getDefaultPool());
    }

    public SnappyFramedInputStream(ReadableByteChannel readableByteChannel, boolean bl, BufferPool bufferPool) throws IOException {
        if (readableByteChannel == null) {
            throw new NullPointerException("in is null");
        }
        if (bufferPool == null) {
            throw new NullPointerException("bufferPool is null");
        }
        this.bufferPool = bufferPool;
        this.rbc = readableByteChannel;
        this.verifyChecksums = bl;
        this.allocateBuffersBasedOnSize(65541);
        this.frameHeader = ByteBuffer.allocate(4);
        byte[] byArray = SnappyFramed.HEADER_BYTES;
        byte[] byArray2 = new byte[byArray.length];
        ByteBuffer byteBuffer = ByteBuffer.wrap(byArray2);
        int n = SnappyFramed.readBytes(readableByteChannel, byteBuffer);
        if (n < byArray.length) {
            throw new EOFException("encountered EOF while reading stream header");
        }
        if (!Arrays.equals(byArray, byArray2)) {
            throw new IOException("invalid stream header");
        }
    }

    private void allocateBuffersBasedOnSize(int n) {
        if (this.input != null) {
            this.bufferPool.releaseDirect(this.input);
        }
        if (this.uncompressedDirect != null) {
            this.bufferPool.releaseDirect(this.uncompressedDirect);
        }
        if (this.buffer != null) {
            this.bufferPool.releaseArray(this.buffer);
        }
        this.input = this.bufferPool.allocateDirect(n);
        int n2 = Snappy.maxCompressedLength(n);
        this.uncompressedDirect = this.bufferPool.allocateDirect(n2);
        this.buffer = this.bufferPool.allocateArray(n2);
    }

    @Override
    public int read() throws IOException {
        if (this.closed) {
            return -1;
        }
        if (!this.ensureBuffer()) {
            return -1;
        }
        return this.buffer[this.position++] & 0xFF;
    }

    @Override
    public int read(byte[] byArray, int n, int n2) throws IOException {
        if (byArray == null) {
            throw new IllegalArgumentException("output is null");
        }
        if (n < 0 || n2 < 0 || n + n2 > byArray.length) {
            throw new IllegalArgumentException("invalid offset [" + n + "] and length [" + n2 + ']');
        }
        if (this.closed) {
            throw new ClosedChannelException();
        }
        if (n2 == 0) {
            return 0;
        }
        if (!this.ensureBuffer()) {
            return -1;
        }
        int n3 = Math.min(n2, this.available());
        System.arraycopy(this.buffer, this.position, byArray, n, n3);
        this.position += n3;
        return n3;
    }

    @Override
    public int available() throws IOException {
        if (this.closed) {
            return 0;
        }
        return this.valid - this.position;
    }

    @Override
    public boolean isOpen() {
        return !this.closed;
    }

    @Override
    public int read(ByteBuffer byteBuffer) throws IOException {
        if (byteBuffer == null) {
            throw new IllegalArgumentException("dst is null");
        }
        if (this.closed) {
            throw new ClosedChannelException();
        }
        if (byteBuffer.remaining() == 0) {
            return 0;
        }
        if (!this.ensureBuffer()) {
            return -1;
        }
        int n = Math.min(byteBuffer.remaining(), this.available());
        byteBuffer.put(this.buffer, this.position, n);
        this.position += n;
        return n;
    }

    @Override
    public long transferTo(OutputStream outputStream) throws IOException {
        if (outputStream == null) {
            throw new IllegalArgumentException("os is null");
        }
        if (this.closed) {
            throw new ClosedChannelException();
        }
        long l = 0L;
        while (this.ensureBuffer()) {
            int n = this.available();
            outputStream.write(this.buffer, this.position, n);
            this.position += n;
            l += (long)n;
        }
        return l;
    }

    public long transferTo(WritableByteChannel writableByteChannel) throws IOException {
        if (writableByteChannel == null) {
            throw new IllegalArgumentException("wbc is null");
        }
        if (this.closed) {
            throw new ClosedChannelException();
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(this.buffer);
        long l = 0L;
        while (this.ensureBuffer()) {
            byteBuffer.clear();
            byteBuffer.position(this.position);
            byteBuffer.limit(this.position + this.available());
            writableByteChannel.write(byteBuffer);
            int n = byteBuffer.position() - this.position;
            this.position += n;
            l += (long)n;
        }
        return l;
    }

    @Override
    public void close() throws IOException {
        try {
            this.rbc.close();
        }
        finally {
            if (!this.closed) {
                this.closed = true;
                if (this.input != null) {
                    this.bufferPool.releaseDirect(this.input);
                    this.input = null;
                }
                if (this.uncompressedDirect != null) {
                    this.bufferPool.releaseDirect(this.uncompressedDirect);
                    this.uncompressedDirect = null;
                }
                if (this.buffer != null) {
                    this.bufferPool.releaseArray(this.buffer);
                    this.buffer = null;
                }
            }
        }
    }

    private boolean ensureBuffer() throws IOException {
        int n;
        if (this.available() > 0) {
            return true;
        }
        if (this.eof) {
            return false;
        }
        if (!this.readBlockHeader()) {
            this.eof = true;
            return false;
        }
        FrameMetaData frameMetaData = this.getFrameMetaData(this.frameHeader);
        if (FrameAction.SKIP == frameMetaData.frameAction) {
            SnappyFramed.skip(this.rbc, frameMetaData.length, ByteBuffer.wrap(this.buffer));
            return this.ensureBuffer();
        }
        if (frameMetaData.length > this.input.capacity()) {
            this.allocateBuffersBasedOnSize(frameMetaData.length);
        }
        this.input.clear();
        this.input.limit(frameMetaData.length);
        int n2 = SnappyFramed.readBytes(this.rbc, this.input);
        if (n2 != frameMetaData.length) {
            throw new EOFException("unexpectd EOF when reading frame");
        }
        this.input.flip();
        FrameData frameData = this.getFrameData(this.input);
        if (FrameAction.UNCOMPRESS == frameMetaData.frameAction) {
            this.input.position(frameData.offset);
            n = Snappy.uncompressedLength(this.input);
            if (n > this.uncompressedDirect.capacity()) {
                this.bufferPool.releaseDirect(this.uncompressedDirect);
                this.bufferPool.releaseArray(this.buffer);
                this.uncompressedDirect = this.bufferPool.allocateDirect(n);
                this.buffer = this.bufferPool.allocateArray(n);
            }
            this.uncompressedDirect.clear();
            this.valid = Snappy.uncompress(this.input, this.uncompressedDirect);
            this.uncompressedDirect.get(this.buffer, 0, this.valid);
            this.position = 0;
        } else {
            this.input.position(frameData.offset);
            this.position = 0;
            this.valid = this.input.remaining();
            this.input.get(this.buffer, 0, this.input.remaining());
        }
        if (this.verifyChecksums && frameData.checkSum != (n = SnappyFramed.maskedCrc32c(this.crc32, this.buffer, this.position, this.valid - this.position))) {
            throw new IOException("Corrupt input: invalid checksum");
        }
        return true;
    }

    private boolean readBlockHeader() throws IOException {
        this.frameHeader.clear();
        int n = SnappyFramed.readBytes(this.rbc, this.frameHeader);
        if (n == -1) {
            return false;
        }
        if (n < this.frameHeader.capacity()) {
            throw new EOFException("encountered EOF while reading block header");
        }
        this.frameHeader.flip();
        return true;
    }

    private FrameMetaData getFrameMetaData(ByteBuffer byteBuffer) throws IOException {
        FrameAction frameAction;
        assert (byteBuffer.hasArray());
        byte[] byArray = byteBuffer.array();
        int n = byArray[1] & 0xFF;
        n |= (byArray[2] & 0xFF) << 8;
        n |= (byArray[3] & 0xFF) << 16;
        int n2 = 0;
        int n3 = byArray[0] & 0xFF;
        switch (n3) {
            case 0: {
                frameAction = FrameAction.UNCOMPRESS;
                n2 = 5;
                break;
            }
            case 1: {
                frameAction = FrameAction.RAW;
                n2 = 5;
                break;
            }
            case 255: {
                if (n != 6) {
                    throw new IOException("stream identifier chunk with invalid length: " + n);
                }
                frameAction = FrameAction.SKIP;
                n2 = 6;
                break;
            }
            default: {
                if (n3 <= 127) {
                    throw new IOException("unsupported unskippable chunk: " + Integer.toHexString(n3));
                }
                frameAction = FrameAction.SKIP;
                n2 = 0;
            }
        }
        if (n < n2) {
            throw new IOException("invalid length: " + n + " for chunk flag: " + Integer.toHexString(n3));
        }
        return new FrameMetaData(frameAction, n);
    }

    private FrameData getFrameData(ByteBuffer byteBuffer) throws IOException {
        return new FrameData(this.getCrc32c(byteBuffer), 4);
    }

    private int getCrc32c(ByteBuffer byteBuffer) {
        int n = byteBuffer.position();
        return (byteBuffer.get(n + 3) & 0xFF) << 24 | (byteBuffer.get(n + 2) & 0xFF) << 16 | (byteBuffer.get(n + 1) & 0xFF) << 8 | byteBuffer.get(n) & 0xFF;
    }

    public static final class FrameData {
        final int checkSum;
        final int offset;

        public FrameData(int n, int n2) {
            this.checkSum = n;
            this.offset = n2;
        }
    }

    public static final class FrameMetaData {
        final int length;
        final FrameAction frameAction;

        public FrameMetaData(FrameAction frameAction, int n) {
            this.frameAction = frameAction;
            this.length = n;
        }
    }

    static enum FrameAction {
        RAW,
        SKIP,
        UNCOMPRESS;

    }
}


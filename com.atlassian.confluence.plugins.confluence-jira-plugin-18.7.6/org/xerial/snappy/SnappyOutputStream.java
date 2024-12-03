/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy;

import java.io.IOException;
import java.io.OutputStream;
import org.xerial.snappy.Snappy;
import org.xerial.snappy.SnappyCodec;
import org.xerial.snappy.buffer.BufferAllocator;
import org.xerial.snappy.buffer.BufferAllocatorFactory;
import org.xerial.snappy.buffer.CachedBufferAllocator;

public class SnappyOutputStream
extends OutputStream {
    public static final int MAX_BLOCK_SIZE = 0x20000000;
    static final int MIN_BLOCK_SIZE = 1024;
    static final int DEFAULT_BLOCK_SIZE = 32768;
    protected final OutputStream out;
    private final int blockSize;
    private final BufferAllocator inputBufferAllocator;
    private final BufferAllocator outputBufferAllocator;
    protected byte[] inputBuffer;
    protected byte[] outputBuffer;
    private int inputCursor = 0;
    private int outputCursor = 0;
    private boolean headerWritten;
    private boolean closed;

    public SnappyOutputStream(OutputStream outputStream) {
        this(outputStream, 32768);
    }

    public SnappyOutputStream(OutputStream outputStream, int n) {
        this(outputStream, n, CachedBufferAllocator.getBufferAllocatorFactory());
    }

    public SnappyOutputStream(OutputStream outputStream, int n, BufferAllocatorFactory bufferAllocatorFactory) {
        this.out = outputStream;
        this.blockSize = Math.max(1024, n);
        if (this.blockSize > 0x20000000) {
            throw new IllegalArgumentException(String.format("Provided chunk size %,d larger than max %,d", this.blockSize, 0x20000000));
        }
        int n2 = n;
        int n3 = SnappyCodec.HEADER_SIZE + 4 + Snappy.maxCompressedLength(n);
        this.inputBufferAllocator = bufferAllocatorFactory.getBufferAllocator(n2);
        this.outputBufferAllocator = bufferAllocatorFactory.getBufferAllocator(n3);
        this.inputBuffer = this.inputBufferAllocator.allocate(n2);
        this.outputBuffer = this.outputBufferAllocator.allocate(n3);
    }

    @Override
    public void write(byte[] byArray, int n, int n2) throws IOException {
        int n3;
        if (this.closed) {
            throw new IOException("Stream is closed");
        }
        for (int i = 0; i < n2; i += n3) {
            n3 = Math.min(n2 - i, this.blockSize - this.inputCursor);
            if (n3 > 0) {
                System.arraycopy(byArray, n + i, this.inputBuffer, this.inputCursor, n3);
                this.inputCursor += n3;
            }
            if (this.inputCursor < this.blockSize) {
                return;
            }
            this.compressInput();
        }
    }

    public void write(long[] lArray, int n, int n2) throws IOException {
        this.rawWrite(lArray, n * 8, n2 * 8);
    }

    public void write(double[] dArray, int n, int n2) throws IOException {
        this.rawWrite(dArray, n * 8, n2 * 8);
    }

    public void write(float[] fArray, int n, int n2) throws IOException {
        this.rawWrite(fArray, n * 4, n2 * 4);
    }

    public void write(int[] nArray, int n, int n2) throws IOException {
        this.rawWrite(nArray, n * 4, n2 * 4);
    }

    public void write(short[] sArray, int n, int n2) throws IOException {
        this.rawWrite(sArray, n * 2, n2 * 2);
    }

    public void write(long[] lArray) throws IOException {
        this.write(lArray, 0, lArray.length);
    }

    public void write(double[] dArray) throws IOException {
        this.write(dArray, 0, dArray.length);
    }

    public void write(float[] fArray) throws IOException {
        this.write(fArray, 0, fArray.length);
    }

    public void write(int[] nArray) throws IOException {
        this.write(nArray, 0, nArray.length);
    }

    public void write(short[] sArray) throws IOException {
        this.write(sArray, 0, sArray.length);
    }

    private boolean hasSufficientOutputBufferFor(int n) {
        int n2 = Snappy.maxCompressedLength(n);
        return n2 < this.outputBuffer.length - this.outputCursor - 4;
    }

    public void rawWrite(Object object, int n, int n2) throws IOException {
        int n3;
        if (this.closed) {
            throw new IOException("Stream is closed");
        }
        for (int i = 0; i < n2; i += n3) {
            n3 = Math.min(n2 - i, this.blockSize - this.inputCursor);
            if (n3 > 0) {
                Snappy.arrayCopy(object, n + i, n3, this.inputBuffer, this.inputCursor);
                this.inputCursor += n3;
            }
            if (this.inputCursor < this.blockSize) {
                return;
            }
            this.compressInput();
        }
    }

    @Override
    public void write(int n) throws IOException {
        if (this.closed) {
            throw new IOException("Stream is closed");
        }
        if (this.inputCursor >= this.inputBuffer.length) {
            this.compressInput();
        }
        this.inputBuffer[this.inputCursor++] = (byte)n;
    }

    @Override
    public void flush() throws IOException {
        if (this.closed) {
            throw new IOException("Stream is closed");
        }
        this.compressInput();
        this.dumpOutput();
        this.out.flush();
    }

    static void writeInt(byte[] byArray, int n, int n2) {
        byArray[n] = (byte)(n2 >> 24 & 0xFF);
        byArray[n + 1] = (byte)(n2 >> 16 & 0xFF);
        byArray[n + 2] = (byte)(n2 >> 8 & 0xFF);
        byArray[n + 3] = (byte)(n2 >> 0 & 0xFF);
    }

    static int readInt(byte[] byArray, int n) {
        int n2 = (byArray[n] & 0xFF) << 24;
        int n3 = (byArray[n + 1] & 0xFF) << 16;
        int n4 = (byArray[n + 2] & 0xFF) << 8;
        int n5 = byArray[n + 3] & 0xFF;
        return n2 | n3 | n4 | n5;
    }

    protected void dumpOutput() throws IOException {
        if (this.outputCursor > 0) {
            this.out.write(this.outputBuffer, 0, this.outputCursor);
            this.outputCursor = 0;
        }
    }

    protected void compressInput() throws IOException {
        if (!this.headerWritten) {
            this.outputCursor = this.writeHeader();
            this.headerWritten = true;
        }
        if (this.inputCursor <= 0) {
            return;
        }
        if (!this.hasSufficientOutputBufferFor(this.inputCursor)) {
            this.dumpOutput();
        }
        this.writeBlockPreemble();
        int n = Snappy.compress(this.inputBuffer, 0, this.inputCursor, this.outputBuffer, this.outputCursor + 4);
        SnappyOutputStream.writeInt(this.outputBuffer, this.outputCursor, n);
        this.outputCursor += 4 + n;
        this.inputCursor = 0;
    }

    protected int writeHeader() {
        return SnappyCodec.currentHeader.writeHeader(this.outputBuffer, 0);
    }

    protected void writeBlockPreemble() {
    }

    protected void writeCurrentDataSize() {
        SnappyOutputStream.writeInt(this.outputBuffer, this.outputCursor, this.inputCursor);
        this.outputCursor += 4;
    }

    @Override
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        try {
            this.flush();
            this.out.close();
        }
        finally {
            this.closed = true;
            this.inputBufferAllocator.release(this.inputBuffer);
            this.outputBufferAllocator.release(this.outputBuffer);
            this.inputBuffer = null;
            this.outputBuffer = null;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.IOException;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.RAMFile;

public class RAMOutputStream
extends IndexOutput {
    static final int BUFFER_SIZE = 1024;
    private RAMFile file;
    private byte[] currentBuffer;
    private int currentBufferIndex;
    private int bufferPosition;
    private long bufferStart;
    private int bufferLength;

    public RAMOutputStream() {
        this(new RAMFile());
    }

    public RAMOutputStream(RAMFile f) {
        this.file = f;
        this.currentBufferIndex = -1;
        this.currentBuffer = null;
    }

    public void writeTo(IndexOutput out) throws IOException {
        this.flush();
        long end = this.file.length;
        long pos = 0L;
        int buffer = 0;
        while (pos < end) {
            int length = 1024;
            long nextPos = pos + (long)length;
            if (nextPos > end) {
                length = (int)(end - pos);
            }
            out.writeBytes(this.file.getBuffer(buffer++), length);
            pos = nextPos;
        }
    }

    public void writeTo(byte[] bytes, int offset) throws IOException {
        this.flush();
        long end = this.file.length;
        long pos = 0L;
        int buffer = 0;
        int bytesUpto = offset;
        while (pos < end) {
            int length = 1024;
            long nextPos = pos + (long)length;
            if (nextPos > end) {
                length = (int)(end - pos);
            }
            System.arraycopy(this.file.getBuffer(buffer++), 0, bytes, bytesUpto, length);
            bytesUpto += length;
            pos = nextPos;
        }
    }

    public void reset() {
        this.currentBuffer = null;
        this.currentBufferIndex = -1;
        this.bufferPosition = 0;
        this.bufferStart = 0L;
        this.bufferLength = 0;
        this.file.setLength(0L);
    }

    @Override
    public void close() throws IOException {
        this.flush();
    }

    @Override
    public void seek(long pos) throws IOException {
        this.setFileLength();
        if (pos < this.bufferStart || pos >= this.bufferStart + (long)this.bufferLength) {
            this.currentBufferIndex = (int)(pos / 1024L);
            this.switchCurrentBuffer();
        }
        this.bufferPosition = (int)(pos % 1024L);
    }

    @Override
    public long length() {
        return this.file.length;
    }

    @Override
    public void writeByte(byte b) throws IOException {
        if (this.bufferPosition == this.bufferLength) {
            ++this.currentBufferIndex;
            this.switchCurrentBuffer();
        }
        this.currentBuffer[this.bufferPosition++] = b;
    }

    @Override
    public void writeBytes(byte[] b, int offset, int len) throws IOException {
        assert (b != null);
        while (len > 0) {
            int remainInBuffer;
            if (this.bufferPosition == this.bufferLength) {
                ++this.currentBufferIndex;
                this.switchCurrentBuffer();
            }
            int bytesToCopy = len < (remainInBuffer = this.currentBuffer.length - this.bufferPosition) ? len : remainInBuffer;
            System.arraycopy(b, offset, this.currentBuffer, this.bufferPosition, bytesToCopy);
            offset += bytesToCopy;
            len -= bytesToCopy;
            this.bufferPosition += bytesToCopy;
        }
    }

    private final void switchCurrentBuffer() {
        this.currentBuffer = this.currentBufferIndex == this.file.numBuffers() ? this.file.addBuffer(1024) : this.file.getBuffer(this.currentBufferIndex);
        this.bufferPosition = 0;
        this.bufferStart = 1024L * (long)this.currentBufferIndex;
        this.bufferLength = this.currentBuffer.length;
    }

    private void setFileLength() {
        long pointer = this.bufferStart + (long)this.bufferPosition;
        if (pointer > this.file.length) {
            this.file.setLength(pointer);
        }
    }

    @Override
    public void flush() throws IOException {
        this.setFileLength();
    }

    @Override
    public long getFilePointer() {
        return this.currentBufferIndex < 0 ? 0L : this.bufferStart + (long)this.bufferPosition;
    }

    public long sizeInBytes() {
        return (long)this.file.numBuffers() * 1024L;
    }
}


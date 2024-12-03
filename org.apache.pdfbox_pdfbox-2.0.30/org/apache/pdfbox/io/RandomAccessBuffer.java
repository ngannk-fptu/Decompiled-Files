/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.io.RandomAccess;

public class RandomAccessBuffer
implements RandomAccess,
Cloneable {
    private static final int DEFAULT_CHUNK_SIZE = 1024;
    private int chunkSize = 1024;
    private List<byte[]> bufferList = null;
    private byte[] currentBuffer;
    private long pointer;
    private int currentBufferPointer;
    private long size;
    private int bufferListIndex;
    private int bufferListMaxIndex;

    public RandomAccessBuffer() {
        this(1024);
    }

    private RandomAccessBuffer(int definedChunkSize) {
        this.bufferList = new ArrayList<byte[]>();
        this.chunkSize = definedChunkSize;
        this.currentBuffer = new byte[this.chunkSize];
        this.bufferList.add(this.currentBuffer);
        this.pointer = 0L;
        this.currentBufferPointer = 0;
        this.size = 0L;
        this.bufferListIndex = 0;
        this.bufferListMaxIndex = 0;
    }

    public RandomAccessBuffer(byte[] input) {
        this.bufferList = new ArrayList<byte[]>(1);
        this.chunkSize = input.length;
        this.currentBuffer = input;
        this.bufferList.add(this.currentBuffer);
        this.pointer = 0L;
        this.currentBufferPointer = 0;
        this.size = this.chunkSize;
        this.bufferListIndex = 0;
        this.bufferListMaxIndex = 0;
    }

    public RandomAccessBuffer(InputStream input) throws IOException {
        this();
        byte[] byteBuffer = new byte[8192];
        int bytesRead = 0;
        while ((bytesRead = input.read(byteBuffer)) > -1) {
            this.write(byteBuffer, 0, bytesRead);
        }
        this.seek(0L);
    }

    public RandomAccessBuffer clone() {
        RandomAccessBuffer copy = new RandomAccessBuffer(this.chunkSize);
        copy.bufferList = new ArrayList<byte[]>(this.bufferList.size());
        for (byte[] buffer : this.bufferList) {
            byte[] newBuffer = new byte[buffer.length];
            System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
            copy.bufferList.add(newBuffer);
        }
        copy.currentBuffer = (byte[])(this.currentBuffer != null ? copy.bufferList.get(copy.bufferList.size() - 1) : null);
        copy.pointer = this.pointer;
        copy.currentBufferPointer = this.currentBufferPointer;
        copy.size = this.size;
        copy.bufferListIndex = this.bufferListIndex;
        copy.bufferListMaxIndex = this.bufferListMaxIndex;
        return copy;
    }

    @Override
    public void close() throws IOException {
        this.currentBuffer = null;
        this.bufferList.clear();
        this.pointer = 0L;
        this.currentBufferPointer = 0;
        this.size = 0L;
        this.bufferListIndex = 0;
    }

    @Override
    public void clear() {
        this.bufferList.clear();
        this.currentBuffer = new byte[this.chunkSize];
        this.bufferList.add(this.currentBuffer);
        this.pointer = 0L;
        this.currentBufferPointer = 0;
        this.size = 0L;
        this.bufferListIndex = 0;
        this.bufferListMaxIndex = 0;
    }

    @Override
    public void seek(long position) throws IOException {
        this.checkClosed();
        if (position < 0L) {
            throw new IOException("Invalid position " + position);
        }
        this.pointer = position;
        if (this.pointer < this.size) {
            this.bufferListIndex = (int)(this.pointer / (long)this.chunkSize);
            this.currentBufferPointer = (int)(this.pointer % (long)this.chunkSize);
            this.currentBuffer = this.bufferList.get(this.bufferListIndex);
        } else {
            this.bufferListIndex = this.bufferListMaxIndex;
            this.currentBuffer = this.bufferList.get(this.bufferListIndex);
            this.currentBufferPointer = (int)(this.size % (long)this.chunkSize);
        }
    }

    @Override
    public long getPosition() throws IOException {
        this.checkClosed();
        return this.pointer;
    }

    @Override
    public int read() throws IOException {
        this.checkClosed();
        if (this.pointer >= this.size) {
            return -1;
        }
        if (this.currentBufferPointer >= this.chunkSize) {
            if (this.bufferListIndex >= this.bufferListMaxIndex) {
                return -1;
            }
            this.currentBuffer = this.bufferList.get(++this.bufferListIndex);
            this.currentBufferPointer = 0;
        }
        ++this.pointer;
        return this.currentBuffer[this.currentBufferPointer++] & 0xFF;
    }

    @Override
    public int read(byte[] b, int offset, int length) throws IOException {
        int bytesRead;
        this.checkClosed();
        if (this.pointer >= this.size) {
            return -1;
        }
        for (bytesRead = this.readRemainingBytes(b, offset, length); bytesRead < length && this.available() > 0; bytesRead += this.readRemainingBytes(b, offset + bytesRead, length - bytesRead)) {
            if (this.currentBufferPointer != this.chunkSize) continue;
            this.nextBuffer();
        }
        return bytesRead;
    }

    private int readRemainingBytes(byte[] b, int offset, int length) {
        int maxLength = (int)Math.min((long)length, this.size - this.pointer);
        int remainingBytes = this.chunkSize - this.currentBufferPointer;
        if (remainingBytes == 0) {
            return 0;
        }
        if (maxLength >= remainingBytes) {
            System.arraycopy(this.currentBuffer, this.currentBufferPointer, b, offset, remainingBytes);
            this.currentBufferPointer += remainingBytes;
            this.pointer += (long)remainingBytes;
            return remainingBytes;
        }
        System.arraycopy(this.currentBuffer, this.currentBufferPointer, b, offset, maxLength);
        this.currentBufferPointer += maxLength;
        this.pointer += (long)maxLength;
        return maxLength;
    }

    @Override
    public long length() throws IOException {
        this.checkClosed();
        return this.size;
    }

    @Override
    public void write(int b) throws IOException {
        this.checkClosed();
        if (this.currentBufferPointer >= this.chunkSize) {
            if (this.pointer + (long)this.chunkSize >= Integer.MAX_VALUE) {
                throw new IOException("RandomAccessBuffer overflow");
            }
            this.expandBuffer();
        }
        this.currentBuffer[this.currentBufferPointer++] = (byte)b;
        ++this.pointer;
        if (this.pointer > this.size) {
            this.size = this.pointer;
        }
        if (this.currentBufferPointer >= this.chunkSize) {
            if (this.pointer + (long)this.chunkSize >= Integer.MAX_VALUE) {
                throw new IOException("RandomAccessBuffer overflow");
            }
            this.expandBuffer();
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int offset, int length) throws IOException {
        this.checkClosed();
        long newSize = this.pointer + (long)length;
        int remainingBytes = this.chunkSize - this.currentBufferPointer;
        if (length >= remainingBytes) {
            if (newSize > Integer.MAX_VALUE) {
                throw new IOException("RandomAccessBuffer overflow");
            }
            System.arraycopy(b, offset, this.currentBuffer, this.currentBufferPointer, remainingBytes);
            int newOffset = offset + remainingBytes;
            long remainingBytes2Write = length - remainingBytes;
            int numberOfNewArrays = (int)remainingBytes2Write / this.chunkSize;
            for (int i = 0; i < numberOfNewArrays; ++i) {
                this.expandBuffer();
                System.arraycopy(b, newOffset, this.currentBuffer, this.currentBufferPointer, this.chunkSize);
                newOffset += this.chunkSize;
            }
            if ((remainingBytes2Write -= (long)numberOfNewArrays * (long)this.chunkSize) >= 0L) {
                this.expandBuffer();
                if (remainingBytes2Write > 0L) {
                    System.arraycopy(b, newOffset, this.currentBuffer, this.currentBufferPointer, (int)remainingBytes2Write);
                }
                this.currentBufferPointer = (int)remainingBytes2Write;
            }
        } else {
            System.arraycopy(b, offset, this.currentBuffer, this.currentBufferPointer, length);
            this.currentBufferPointer += length;
        }
        this.pointer += (long)length;
        if (this.pointer > this.size) {
            this.size = this.pointer;
        }
    }

    private void expandBuffer() throws IOException {
        if (this.bufferListMaxIndex > this.bufferListIndex) {
            this.nextBuffer();
        } else {
            this.currentBuffer = new byte[this.chunkSize];
            this.bufferList.add(this.currentBuffer);
            this.currentBufferPointer = 0;
            ++this.bufferListMaxIndex;
            ++this.bufferListIndex;
        }
    }

    private void nextBuffer() throws IOException {
        if (this.bufferListIndex == this.bufferListMaxIndex) {
            throw new IOException("No more chunks available, end of buffer reached");
        }
        this.currentBufferPointer = 0;
        this.currentBuffer = this.bufferList.get(++this.bufferListIndex);
    }

    private void checkClosed() throws IOException {
        if (this.currentBuffer == null) {
            throw new IOException("RandomAccessBuffer already closed");
        }
    }

    @Override
    public boolean isClosed() {
        return this.currentBuffer == null;
    }

    @Override
    public boolean isEOF() throws IOException {
        this.checkClosed();
        return this.pointer >= this.size;
    }

    @Override
    public int available() throws IOException {
        return (int)Math.min(this.length() - this.getPosition(), Integer.MAX_VALUE);
    }

    @Override
    public int peek() throws IOException {
        int result = this.read();
        if (result != -1) {
            this.rewind(1);
        }
        return result;
    }

    @Override
    public void rewind(int bytes) throws IOException {
        this.checkClosed();
        this.seek(this.getPosition() - (long)bytes);
    }

    @Override
    public byte[] readFully(int length) throws IOException {
        int count;
        byte[] bytes = new byte[length];
        int bytesRead = 0;
        do {
            if ((count = this.read(bytes, bytesRead, length - bytesRead)) >= 0) continue;
            throw new EOFException();
        } while ((bytesRead += count) < length);
        return bytes;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
}


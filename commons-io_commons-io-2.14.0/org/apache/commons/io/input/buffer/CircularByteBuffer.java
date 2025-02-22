/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io.input.buffer;

import java.util.Objects;
import org.apache.commons.io.IOUtils;

public class CircularByteBuffer {
    private final byte[] buffer;
    private int startOffset;
    private int endOffset;
    private int currentNumberOfBytes;

    public CircularByteBuffer() {
        this(8192);
    }

    public CircularByteBuffer(int size) {
        this.buffer = IOUtils.byteArray(size);
        this.startOffset = 0;
        this.endOffset = 0;
        this.currentNumberOfBytes = 0;
    }

    public void add(byte value) {
        if (this.currentNumberOfBytes >= this.buffer.length) {
            throw new IllegalStateException("No space available");
        }
        this.buffer[this.endOffset] = value;
        ++this.currentNumberOfBytes;
        if (++this.endOffset == this.buffer.length) {
            this.endOffset = 0;
        }
    }

    public void add(byte[] targetBuffer, int offset, int length) {
        Objects.requireNonNull(targetBuffer, "Buffer");
        if (offset < 0 || offset >= targetBuffer.length) {
            throw new IllegalArgumentException("Illegal offset: " + offset);
        }
        if (length < 0) {
            throw new IllegalArgumentException("Illegal length: " + length);
        }
        if (this.currentNumberOfBytes + length > this.buffer.length) {
            throw new IllegalStateException("No space available");
        }
        for (int i = 0; i < length; ++i) {
            this.buffer[this.endOffset] = targetBuffer[offset + i];
            if (++this.endOffset != this.buffer.length) continue;
            this.endOffset = 0;
        }
        this.currentNumberOfBytes += length;
    }

    public void clear() {
        this.startOffset = 0;
        this.endOffset = 0;
        this.currentNumberOfBytes = 0;
    }

    public int getCurrentNumberOfBytes() {
        return this.currentNumberOfBytes;
    }

    public int getSpace() {
        return this.buffer.length - this.currentNumberOfBytes;
    }

    public boolean hasBytes() {
        return this.currentNumberOfBytes > 0;
    }

    public boolean hasSpace() {
        return this.currentNumberOfBytes < this.buffer.length;
    }

    public boolean hasSpace(int count) {
        return this.currentNumberOfBytes + count <= this.buffer.length;
    }

    public boolean peek(byte[] sourceBuffer, int offset, int length) {
        Objects.requireNonNull(sourceBuffer, "Buffer");
        if (offset < 0 || offset >= sourceBuffer.length) {
            throw new IllegalArgumentException("Illegal offset: " + offset);
        }
        if (length < 0 || length > this.buffer.length) {
            throw new IllegalArgumentException("Illegal length: " + length);
        }
        if (length < this.currentNumberOfBytes) {
            return false;
        }
        int localOffset = this.startOffset;
        for (int i = 0; i < length; ++i) {
            if (this.buffer[localOffset] != sourceBuffer[i + offset]) {
                return false;
            }
            if (++localOffset != this.buffer.length) continue;
            localOffset = 0;
        }
        return true;
    }

    public byte read() {
        if (this.currentNumberOfBytes <= 0) {
            throw new IllegalStateException("No bytes available.");
        }
        byte b = this.buffer[this.startOffset];
        --this.currentNumberOfBytes;
        if (++this.startOffset == this.buffer.length) {
            this.startOffset = 0;
        }
        return b;
    }

    public void read(byte[] targetBuffer, int targetOffset, int length) {
        Objects.requireNonNull(targetBuffer, "targetBuffer");
        if (targetOffset < 0 || targetOffset >= targetBuffer.length) {
            throw new IllegalArgumentException("Illegal offset: " + targetOffset);
        }
        if (length < 0 || length > this.buffer.length) {
            throw new IllegalArgumentException("Illegal length: " + length);
        }
        if (targetOffset + length > targetBuffer.length) {
            throw new IllegalArgumentException("The supplied byte array contains only " + targetBuffer.length + " bytes, but offset, and length would require " + (targetOffset + length - 1));
        }
        if (this.currentNumberOfBytes < length) {
            throw new IllegalStateException("Currently, there are only " + this.currentNumberOfBytes + "in the buffer, not " + length);
        }
        int offset = targetOffset;
        for (int i = 0; i < length; ++i) {
            targetBuffer[offset++] = this.buffer[this.startOffset];
            --this.currentNumberOfBytes;
            if (++this.startOffset != this.buffer.length) continue;
            this.startOffset = 0;
        }
    }
}


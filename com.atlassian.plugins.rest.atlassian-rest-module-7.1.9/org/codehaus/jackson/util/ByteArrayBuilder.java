/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.util;

import java.io.OutputStream;
import java.util.LinkedList;
import org.codehaus.jackson.util.BufferRecycler;

public final class ByteArrayBuilder
extends OutputStream {
    private static final byte[] NO_BYTES = new byte[0];
    private static final int INITIAL_BLOCK_SIZE = 500;
    private static final int MAX_BLOCK_SIZE = 262144;
    static final int DEFAULT_BLOCK_ARRAY_SIZE = 40;
    private final BufferRecycler _bufferRecycler;
    private final LinkedList<byte[]> _pastBlocks = new LinkedList();
    private int _pastLen;
    private byte[] _currBlock;
    private int _currBlockPtr;

    public ByteArrayBuilder() {
        this(null);
    }

    public ByteArrayBuilder(BufferRecycler br) {
        this(br, 500);
    }

    public ByteArrayBuilder(int firstBlockSize) {
        this(null, firstBlockSize);
    }

    public ByteArrayBuilder(BufferRecycler br, int firstBlockSize) {
        this._bufferRecycler = br;
        this._currBlock = br == null ? new byte[firstBlockSize] : br.allocByteBuffer(BufferRecycler.ByteBufferType.WRITE_CONCAT_BUFFER);
    }

    public void reset() {
        this._pastLen = 0;
        this._currBlockPtr = 0;
        if (!this._pastBlocks.isEmpty()) {
            this._pastBlocks.clear();
        }
    }

    public void release() {
        this.reset();
        if (this._bufferRecycler != null && this._currBlock != null) {
            this._bufferRecycler.releaseByteBuffer(BufferRecycler.ByteBufferType.WRITE_CONCAT_BUFFER, this._currBlock);
            this._currBlock = null;
        }
    }

    public void append(int i) {
        if (this._currBlockPtr >= this._currBlock.length) {
            this._allocMore();
        }
        this._currBlock[this._currBlockPtr++] = (byte)i;
    }

    public void appendTwoBytes(int b16) {
        if (this._currBlockPtr + 1 < this._currBlock.length) {
            this._currBlock[this._currBlockPtr++] = (byte)(b16 >> 8);
            this._currBlock[this._currBlockPtr++] = (byte)b16;
        } else {
            this.append(b16 >> 8);
            this.append(b16);
        }
    }

    public void appendThreeBytes(int b24) {
        if (this._currBlockPtr + 2 < this._currBlock.length) {
            this._currBlock[this._currBlockPtr++] = (byte)(b24 >> 16);
            this._currBlock[this._currBlockPtr++] = (byte)(b24 >> 8);
            this._currBlock[this._currBlockPtr++] = (byte)b24;
        } else {
            this.append(b24 >> 16);
            this.append(b24 >> 8);
            this.append(b24);
        }
    }

    public byte[] toByteArray() {
        int totalLen = this._pastLen + this._currBlockPtr;
        if (totalLen == 0) {
            return NO_BYTES;
        }
        byte[] result = new byte[totalLen];
        int offset = 0;
        for (byte[] block : this._pastBlocks) {
            int len = block.length;
            System.arraycopy(block, 0, result, offset, len);
            offset += len;
        }
        System.arraycopy(this._currBlock, 0, result, offset, this._currBlockPtr);
        if ((offset += this._currBlockPtr) != totalLen) {
            throw new RuntimeException("Internal error: total len assumed to be " + totalLen + ", copied " + offset + " bytes");
        }
        if (!this._pastBlocks.isEmpty()) {
            this.reset();
        }
        return result;
    }

    public byte[] resetAndGetFirstSegment() {
        this.reset();
        return this._currBlock;
    }

    public byte[] finishCurrentSegment() {
        this._allocMore();
        return this._currBlock;
    }

    public byte[] completeAndCoalesce(int lastBlockLength) {
        this._currBlockPtr = lastBlockLength;
        return this.toByteArray();
    }

    public byte[] getCurrentSegment() {
        return this._currBlock;
    }

    public void setCurrentSegmentLength(int len) {
        this._currBlockPtr = len;
    }

    public int getCurrentSegmentLength() {
        return this._currBlockPtr;
    }

    public void write(byte[] b) {
        this.write(b, 0, b.length);
    }

    public void write(byte[] b, int off, int len) {
        while (true) {
            int max;
            int toCopy;
            if ((toCopy = Math.min(max = this._currBlock.length - this._currBlockPtr, len)) > 0) {
                System.arraycopy(b, off, this._currBlock, this._currBlockPtr, toCopy);
                off += toCopy;
                this._currBlockPtr += toCopy;
                len -= toCopy;
            }
            if (len <= 0) break;
            this._allocMore();
        }
    }

    public void write(int b) {
        this.append(b);
    }

    public void close() {
    }

    public void flush() {
    }

    private void _allocMore() {
        this._pastLen += this._currBlock.length;
        int newSize = Math.max(this._pastLen >> 1, 1000);
        if (newSize > 262144) {
            newSize = 262144;
        }
        this._pastBlocks.add(this._currBlock);
        this._currBlock = new byte[newSize];
        this._currBlockPtr = 0;
    }
}


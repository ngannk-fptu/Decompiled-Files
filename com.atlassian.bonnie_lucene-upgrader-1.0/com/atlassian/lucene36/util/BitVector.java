/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.store.IndexInput;
import com.atlassian.lucene36.store.IndexOutput;
import com.atlassian.lucene36.util.Bits;
import com.atlassian.lucene36.util.CodecUtil;
import java.io.IOException;

public final class BitVector
implements Cloneable,
Bits {
    private byte[] bits;
    private int size;
    private int count;
    private static final byte[] BYTE_COUNTS = new byte[]{0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 4, 5, 5, 6, 5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8};
    private static String CODEC = "BitVector";
    private static final int VERSION_PRE = -1;
    private static final int VERSION_START = 0;
    private static final int VERSION_CURRENT = 0;

    public BitVector(int n) {
        this.size = n;
        this.bits = new byte[this.getNumBytes(this.size)];
        this.count = 0;
    }

    BitVector(byte[] bits, int size) {
        this.bits = bits;
        this.size = size;
        this.count = -1;
    }

    private int getNumBytes(int size) {
        int bytesLength = size >>> 3;
        if ((size & 7) != 0) {
            ++bytesLength;
        }
        return bytesLength;
    }

    public Object clone() {
        byte[] copyBits = new byte[this.bits.length];
        System.arraycopy(this.bits, 0, copyBits, 0, this.bits.length);
        BitVector clone = new BitVector(copyBits, this.size);
        clone.count = this.count;
        return clone;
    }

    public final void set(int bit) {
        if (bit >= this.size) {
            throw new ArrayIndexOutOfBoundsException("bit=" + bit + " size=" + this.size);
        }
        int n = bit >> 3;
        this.bits[n] = (byte)(this.bits[n] | 1 << (bit & 7));
        this.count = -1;
    }

    public final boolean getAndSet(int bit) {
        if (bit >= this.size) {
            throw new ArrayIndexOutOfBoundsException("bit=" + bit + " size=" + this.size);
        }
        int flag = 1 << (bit & 7);
        int pos = bit >> 3;
        byte v = this.bits[pos];
        if ((flag & v) != 0) {
            return true;
        }
        this.bits[pos] = (byte)(v | flag);
        if (this.count != -1) {
            ++this.count;
        }
        return false;
    }

    public final void clear(int bit) {
        if (bit >= this.size) {
            throw new ArrayIndexOutOfBoundsException(bit);
        }
        int n = bit >> 3;
        this.bits[n] = (byte)(this.bits[n] & ~(1 << (bit & 7)));
        this.count = -1;
    }

    public final boolean get(int bit) {
        assert (bit >= 0 && bit < this.size) : "bit " + bit + " is out of bounds 0.." + (this.size - 1);
        return (this.bits[bit >> 3] & 1 << (bit & 7)) != 0;
    }

    public final int size() {
        return this.size;
    }

    public final int length() {
        return this.size;
    }

    public final int count() {
        if (this.count == -1) {
            int c = 0;
            int end = this.bits.length;
            for (int i = 0; i < end; ++i) {
                c += BYTE_COUNTS[this.bits[i] & 0xFF];
            }
            this.count = c;
        }
        return this.count;
    }

    public final int getRecomputedCount() {
        int c = 0;
        int end = this.bits.length;
        for (int i = 0; i < end; ++i) {
            c += BYTE_COUNTS[this.bits[i] & 0xFF];
        }
        return c;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void write(Directory d, String name) throws IOException {
        IndexOutput output = d.createOutput(name);
        try {
            output.writeInt(-2);
            CodecUtil.writeHeader(output, CODEC, 0);
            if (this.isSparse()) {
                this.writeDgaps(output);
            } else {
                this.writeBits(output);
            }
        }
        finally {
            output.close();
        }
    }

    private void writeBits(IndexOutput output) throws IOException {
        output.writeInt(this.size());
        output.writeInt(this.count());
        output.writeBytes(this.bits, this.bits.length);
    }

    private void writeDgaps(IndexOutput output) throws IOException {
        output.writeInt(-1);
        output.writeInt(this.size());
        output.writeInt(this.count());
        int last = 0;
        int n = this.count();
        int m = this.bits.length;
        for (int i = 0; i < m && n > 0; ++i) {
            if (this.bits[i] == 0) continue;
            output.writeVInt(i - last);
            output.writeByte(this.bits[i]);
            last = i;
            n -= BYTE_COUNTS[this.bits[i] & 0xFF];
        }
    }

    private boolean isSparse() {
        int setCount = this.count();
        if (setCount == 0) {
            return true;
        }
        int avgGapLength = this.bits.length / setCount;
        int expectedDGapBytes = avgGapLength <= 128 ? 1 : (avgGapLength <= 16384 ? 2 : (avgGapLength <= 0x200000 ? 3 : (avgGapLength <= 0x10000000 ? 4 : 5)));
        int bytesPerSetBit = expectedDGapBytes + 1;
        long expectedBits = 32 + 8 * bytesPerSetBit * this.count();
        long factor = 10L;
        return 10L * expectedBits < (long)this.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public BitVector(Directory d, String name) throws IOException {
        IndexInput input = d.openInput(name);
        try {
            int firstInt = input.readInt();
            if (firstInt == -2) {
                int version = CodecUtil.checkHeader(input, CODEC, 0, 0);
                this.size = input.readInt();
            } else {
                int version = -1;
                this.size = firstInt;
            }
            if (this.size == -1) {
                this.readDgaps(input);
            } else {
                this.readBits(input);
            }
        }
        finally {
            input.close();
        }
    }

    private void readBits(IndexInput input) throws IOException {
        this.count = input.readInt();
        this.bits = new byte[this.getNumBytes(this.size)];
        input.readBytes(this.bits, 0, this.bits.length);
    }

    private void readDgaps(IndexInput input) throws IOException {
        this.size = input.readInt();
        this.count = input.readInt();
        this.bits = new byte[(this.size >> 3) + 1];
        int last = 0;
        for (int n = this.count(); n > 0; n -= BYTE_COUNTS[this.bits[last] & 0xFF]) {
            this.bits[last += input.readVInt()] = input.readByte();
        }
    }
}


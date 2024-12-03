/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene40;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.store.CompoundFileDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.MutableBits;

final class BitVector
implements Cloneable,
MutableBits {
    private byte[] bits;
    private int size;
    private int count;
    private int version;
    private static final byte[] BYTE_COUNTS = new byte[]{0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 4, 5, 5, 6, 5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8};
    private static String CODEC = "BitVector";
    public static final int VERSION_PRE = -1;
    public static final int VERSION_START = 0;
    public static final int VERSION_DGAPS_CLEARED = 1;
    public static final int VERSION_CURRENT = 1;

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

    public BitVector clone() {
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
            assert (this.count <= this.size);
        }
        return false;
    }

    @Override
    public final void clear(int bit) {
        if (bit >= this.size) {
            throw new ArrayIndexOutOfBoundsException(bit);
        }
        int n = bit >> 3;
        this.bits[n] = (byte)(this.bits[n] & ~(1 << (bit & 7)));
        this.count = -1;
    }

    public final boolean getAndClear(int bit) {
        if (bit >= this.size) {
            throw new ArrayIndexOutOfBoundsException(bit);
        }
        int flag = 1 << (bit & 7);
        int pos = bit >> 3;
        byte v = this.bits[pos];
        if ((flag & v) == 0) {
            return false;
        }
        int n = pos;
        this.bits[n] = (byte)(this.bits[n] & ~flag);
        if (this.count != -1) {
            --this.count;
            assert (this.count >= 0);
        }
        return true;
    }

    @Override
    public final boolean get(int bit) {
        assert (bit >= 0 && bit < this.size) : "bit " + bit + " is out of bounds 0.." + (this.size - 1);
        return (this.bits[bit >> 3] & 1 << (bit & 7)) != 0;
    }

    public final int size() {
        return this.size;
    }

    @Override
    public int length() {
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
        assert (this.count <= this.size) : "count=" + this.count + " size=" + this.size;
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

    public int getVersion() {
        return this.version;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void write(Directory d, String name, IOContext context) throws IOException {
        assert (!(d instanceof CompoundFileDirectory));
        IndexOutput output = d.createOutput(name, context);
        try {
            output.writeInt(-2);
            CodecUtil.writeHeader(output, CODEC, 1);
            if (this.isSparse()) {
                this.writeClearedDgaps(output);
            } else {
                this.writeBits(output);
            }
            assert (this.verifyCount());
        }
        catch (Throwable throwable) {
            IOUtils.close(output);
            throw throwable;
        }
        IOUtils.close(output);
    }

    public void invertAll() {
        if (this.count != -1) {
            this.count = this.size - this.count;
        }
        if (this.bits.length > 0) {
            for (int idx = 0; idx < this.bits.length; ++idx) {
                this.bits[idx] = ~this.bits[idx];
            }
            this.clearUnusedBits();
        }
    }

    private void clearUnusedBits() {
        int lastNBits;
        if (this.bits.length > 0 && (lastNBits = this.size & 7) != 0) {
            int mask = (1 << lastNBits) - 1;
            int n = this.bits.length - 1;
            this.bits[n] = (byte)(this.bits[n] & mask);
        }
    }

    public void setAll() {
        Arrays.fill(this.bits, (byte)-1);
        this.clearUnusedBits();
        this.count = this.size;
    }

    private void writeBits(IndexOutput output) throws IOException {
        output.writeInt(this.size());
        output.writeInt(this.count());
        output.writeBytes(this.bits, this.bits.length);
    }

    private void writeClearedDgaps(IndexOutput output) throws IOException {
        output.writeInt(-1);
        output.writeInt(this.size());
        output.writeInt(this.count());
        int last = 0;
        int numCleared = this.size() - this.count();
        for (int i = 0; i < this.bits.length && numCleared > 0; ++i) {
            if (this.bits[i] == -1) continue;
            output.writeVInt(i - last);
            output.writeByte(this.bits[i]);
            last = i;
            assert ((numCleared -= 8 - BYTE_COUNTS[this.bits[i] & 0xFF]) >= 0 || i == this.bits.length - 1 && numCleared == -(8 - (this.size & 7)));
        }
    }

    private boolean isSparse() {
        int clearedCount = this.size() - this.count();
        if (clearedCount == 0) {
            return true;
        }
        int avgGapLength = this.bits.length / clearedCount;
        int expectedDGapBytes = avgGapLength <= 128 ? 1 : (avgGapLength <= 16384 ? 2 : (avgGapLength <= 0x200000 ? 3 : (avgGapLength <= 0x10000000 ? 4 : 5)));
        int bytesPerSetBit = expectedDGapBytes + 1;
        long expectedBits = 32 + 8 * bytesPerSetBit * clearedCount;
        long factor = 10L;
        return 10L * expectedBits < (long)this.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public BitVector(Directory d, String name, IOContext context) throws IOException {
        try (IndexInput input = d.openInput(name, context);){
            int firstInt = input.readInt();
            if (firstInt == -2) {
                this.version = CodecUtil.checkHeader(input, CODEC, 0, 1);
                this.size = input.readInt();
            } else {
                this.version = -1;
                this.size = firstInt;
            }
            if (this.size == -1) {
                if (this.version >= 1) {
                    this.readClearedDgaps(input);
                } else {
                    this.readSetDgaps(input);
                }
            } else {
                this.readBits(input);
            }
            if (this.version < 1) {
                this.invertAll();
            }
            assert (this.verifyCount());
        }
    }

    private boolean verifyCount() {
        assert (this.count != -1);
        int countSav = this.count;
        this.count = -1;
        assert (countSav == this.count()) : "saved count was " + countSav + " but recomputed count is " + this.count;
        return true;
    }

    private void readBits(IndexInput input) throws IOException {
        this.count = input.readInt();
        this.bits = new byte[this.getNumBytes(this.size)];
        input.readBytes(this.bits, 0, this.bits.length);
    }

    private void readSetDgaps(IndexInput input) throws IOException {
        this.size = input.readInt();
        this.count = input.readInt();
        this.bits = new byte[this.getNumBytes(this.size)];
        int last = 0;
        int n = this.count();
        while (n > 0) {
            this.bits[last += input.readVInt()] = input.readByte();
            assert ((n -= BYTE_COUNTS[this.bits[last] & 0xFF]) >= 0);
        }
    }

    private void readClearedDgaps(IndexInput input) throws IOException {
        this.size = input.readInt();
        this.count = input.readInt();
        this.bits = new byte[this.getNumBytes(this.size)];
        Arrays.fill(this.bits, (byte)-1);
        this.clearUnusedBits();
        int last = 0;
        int numCleared = this.size() - this.count();
        while (numCleared > 0) {
            this.bits[last += input.readVInt()] = input.readByte();
            assert ((numCleared -= 8 - BYTE_COUNTS[this.bits[last] & 0xFF]) >= 0 || last == this.bits.length - 1 && numCleared == -(8 - (this.size & 7)));
        }
    }
}


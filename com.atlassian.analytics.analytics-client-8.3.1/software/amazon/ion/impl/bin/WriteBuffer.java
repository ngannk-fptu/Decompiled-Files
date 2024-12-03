/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.bin;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import software.amazon.ion.impl.bin.Block;
import software.amazon.ion.impl.bin.BlockAllocator;

final class WriteBuffer
implements Closeable {
    private final BlockAllocator allocator;
    private final List<Block> blocks;
    private Block current;
    private int index;
    private static final int OCTET_MASK = 255;
    private static final char HIGH_SURROGATE_FIRST = '\ud800';
    private static final char HIGH_SURROGATE_LAST = '\udbff';
    private static final char LOW_SURROGATE_FIRST = '\udc00';
    private static final char LOW_SURROGATE_LAST = '\udfff';
    private static final int SURROGATE_BASE = 65536;
    private static final int BITS_PER_SURROGATE = 10;
    private static final int UTF8_FOLLOW_MASK = 63;
    private static final int UTF8_FOLLOW_PREFIX_MASK = 128;
    private static final int UTF8_2_OCTET_PREFIX_MASK = 192;
    private static final int UTF8_3_OCTET_PREFIX_MASK = 224;
    private static final int UTF8_4_OCTET_PREFIX_MASK = 240;
    private static final int UTF8_BITS_PER_FOLLOW_OCTET = 6;
    private static final int UTF8_2_OCTET_SHIFT = 6;
    private static final int UTF8_3_OCTET_SHIFT = 12;
    private static final int UTF8_4_OCTET_SHIFT = 18;
    private static final int UTF8_2_OCTET_MIN_VALUE = 128;
    private static final int UTF8_3_OCTET_MIN_VALUE = 2048;
    private static final int UINT_2_OCTET_SHIFT = 8;
    private static final int UINT_3_OCTET_SHIFT = 16;
    private static final int UINT_4_OCTET_SHIFT = 24;
    private static final int UINT_5_OCTET_SHIFT = 32;
    private static final int UINT_6_OCTET_SHIFT = 40;
    private static final int UINT_7_OCTET_SHIFT = 48;
    private static final int UINT_8_OCTET_SHIFT = 56;
    private static final long INT8_SIGN_MASK = 128L;
    private static final long INT16_SIGN_MASK = 32768L;
    private static final long INT24_SIGN_MASK = 0x800000L;
    private static final long INT32_SIGN_MASK = 0x80000000L;
    private static final long INT40_SIGN_MASK = 0x8000000000L;
    private static final long INT48_SIGN_MASK = 0x800000000000L;
    private static final long INT56_SIGN_MASK = 0x80000000000000L;
    private static final long INT64_SIGN_MASK = Long.MIN_VALUE;
    private static final long VAR_INT_BITS_PER_OCTET = 7L;
    private static final long VAR_INT_MASK = 127L;
    private static final long VAR_UINT_9_OCTET_SHIFT = 56L;
    private static final long VAR_UINT_9_OCTET_MIN_VALUE = 0x100000000000000L;
    private static final long VAR_UINT_8_OCTET_SHIFT = 49L;
    private static final long VAR_UINT_8_OCTET_MIN_VALUE = 0x2000000000000L;
    private static final long VAR_UINT_7_OCTET_SHIFT = 42L;
    private static final long VAR_UINT_7_OCTET_MIN_VALUE = 0x40000000000L;
    private static final long VAR_UINT_6_OCTET_SHIFT = 35L;
    private static final long VAR_UINT_6_OCTET_MIN_VALUE = 0x800000000L;
    private static final long VAR_UINT_5_OCTET_SHIFT = 28L;
    private static final long VAR_UINT_5_OCTET_MIN_VALUE = 0x10000000L;
    private static final long VAR_UINT_4_OCTET_SHIFT = 21L;
    private static final long VAR_UINT_4_OCTET_MIN_VALUE = 0x200000L;
    private static final long VAR_UINT_3_OCTET_SHIFT = 14L;
    private static final long VAR_UINT_3_OCTET_MIN_VALUE = 16384L;
    private static final long VAR_UINT_2_OCTET_SHIFT = 7L;
    private static final long VAR_UINT_2_OCTET_MIN_VALUE = 128L;
    private static final long VAR_INT_FINAL_OCTET_SIGNAL_MASK = 128L;
    private static final long VAR_INT_SIGNED_OCTET_MASK = 63L;
    private static final long VAR_INT_SIGNBIT_ON_MASK = 64L;
    private static final long VAR_INT_SIGNBIT_OFF_MASK = 0L;
    private static final long VAR_INT_10_OCTET_SHIFT = 62L;
    private static final long VAR_INT_10_OCTET_MIN_VALUE = 0x4000000000000000L;
    private static final long VAR_INT_9_OCTET_MIN_VALUE = 0x80000000000000L;
    private static final long VAR_INT_8_OCTET_MIN_VALUE = 0x1000000000000L;
    private static final long VAR_INT_7_OCTET_MIN_VALUE = 0x20000000000L;
    private static final long VAR_INT_6_OCTET_MIN_VALUE = 0x400000000L;
    private static final long VAR_INT_5_OCTET_MIN_VALUE = 0x8000000L;
    private static final long VAR_INT_4_OCTET_MIN_VALUE = 0x100000L;
    private static final long VAR_INT_3_OCTET_MIN_VALUE = 8192L;
    private static final long VAR_INT_2_OCTET_MIN_VALUE = 64L;
    private static final long VAR_INT_BITS_PER_SIGNED_OCTET = 6L;
    private static final long VAR_SINT_2_OCTET_SHIFT = 13L;
    private static final long VAR_SINT_3_OCTET_SHIFT = 20L;
    private static final long VAR_SINT_4_OCTET_SHIFT = 27L;
    private static final long VAR_SINT_5_OCTET_SHIFT = 34L;

    public WriteBuffer(BlockAllocator allocator) {
        this.allocator = allocator;
        this.blocks = new ArrayList<Block>();
        this.allocateNewBlock();
        this.index = 0;
        this.current = this.blocks.get(0);
    }

    private void allocateNewBlock() {
        this.blocks.add(this.allocator.allocateBlock());
    }

    private int index(long position) {
        return (int)(position / (long)this.allocator.getBlockSize());
    }

    private int offset(long position) {
        return (int)(position % (long)this.allocator.getBlockSize());
    }

    public void reset() {
        this.close();
        this.allocateNewBlock();
        this.index = 0;
        this.current = this.blocks.get(this.index);
    }

    public void close() {
        for (Block block : this.blocks) {
            block.close();
        }
        this.blocks.clear();
    }

    public void truncate(long position) {
        int index = this.index(position);
        int offset = this.offset(position);
        Block block = this.blocks.get(index);
        this.index = index;
        block.limit = offset;
        this.current = block;
    }

    public int remaining() {
        return this.current.remaining();
    }

    public long position() {
        return (long)this.index * (long)this.allocator.getBlockSize() + (long)this.current.limit;
    }

    public int getUInt8At(long position) {
        int index = this.index(position);
        int offset = this.offset(position);
        Block block = this.blocks.get(index);
        return block.data[offset] & 0xFF;
    }

    public void writeByte(byte octet) {
        if (this.remaining() < 1) {
            if (this.index == this.blocks.size() - 1) {
                this.allocateNewBlock();
            }
            ++this.index;
            this.current = this.blocks.get(this.index);
        }
        Block block = this.current;
        block.data[block.limit] = octet;
        ++block.limit;
    }

    private void writeBytesSlow(byte[] bytes, int off, int len) {
        while (len > 0) {
            Block block = this.current;
            int amount = Math.min(len, block.remaining());
            System.arraycopy(bytes, off, block.data, block.limit, amount);
            block.limit += amount;
            off += amount;
            len -= amount;
            if (block.remaining() != 0) continue;
            if (this.index == this.blocks.size() - 1) {
                this.allocateNewBlock();
            }
            ++this.index;
            this.current = this.blocks.get(this.index);
        }
    }

    public void writeBytes(byte[] bytes, int off, int len) {
        if (len > this.remaining()) {
            this.writeBytesSlow(bytes, off, len);
            return;
        }
        Block block = this.current;
        System.arraycopy(bytes, off, block.data, block.limit, len);
        block.limit += len;
    }

    public void writeBytes(byte[] bytes) {
        this.writeBytes(bytes, 0, bytes.length);
    }

    private int writeUTF8Slow(CharSequence chars, int off, int len) {
        int octets = 0;
        while (len > 0) {
            char ch = chars.charAt(off);
            if (ch >= '\udc00' && ch <= '\udfff') {
                throw new IllegalArgumentException("Unpaired low surrogate: " + ch);
            }
            if (ch >= '\ud800' && ch <= '\udbff') {
                ++off;
                if (--len == 0) {
                    throw new IllegalArgumentException("Unpaired low surrogate at end of character sequence: " + ch);
                }
                char ch2 = chars.charAt(off);
                if (ch2 < '\udc00' || ch2 > '\udfff') {
                    throw new IllegalArgumentException("Low surrogate with unpaired high surrogate: " + ch + " + " + ch2);
                }
                int codepoint = (ch - 55296 << 10 | ch2 - 56320) + 65536;
                this.writeByte((byte)(0xF0 | codepoint >> 18));
                this.writeByte((byte)(0x80 | codepoint >> 12 & 0x3F));
                this.writeByte((byte)(0x80 | codepoint >> 6 & 0x3F));
                this.writeByte((byte)(0x80 | codepoint & 0x3F));
                octets += 4;
            } else if (ch < '\u0080') {
                this.writeByte((byte)ch);
                ++octets;
            } else if (ch < '\u0800') {
                this.writeByte((byte)(0xC0 | ch >> 6));
                this.writeByte((byte)(0x80 | ch & 0x3F));
                octets += 2;
            } else {
                this.writeByte((byte)(0xE0 | ch >> 12));
                this.writeByte((byte)(0x80 | ch >> 6 & 0x3F));
                this.writeByte((byte)(0x80 | ch & 0x3F));
                octets += 3;
            }
            ++off;
            --len;
        }
        return octets;
    }

    private int writeUTF8UpTo3Byte(CharSequence chars, int off, int len) {
        if (len * 3 > this.remaining()) {
            return this.writeUTF8Slow(chars, off, len);
        }
        Block block = this.current;
        int limit = block.limit;
        int octets = 0;
        while (len > 0) {
            char ch = chars.charAt(off);
            if (ch >= '\udc00' && ch <= '\udfff') {
                throw new IllegalArgumentException("Unpaired low surrogate: " + ch);
            }
            if (ch >= '\ud800' && ch <= '\udbff') break;
            if (ch < '\u0080') {
                block.data[limit++] = (byte)ch;
                ++octets;
            } else if (ch < '\u0800') {
                block.data[limit++] = (byte)(0xC0 | ch >> 6);
                block.data[limit++] = (byte)(0x80 | ch & 0x3F);
                octets += 2;
            } else {
                block.data[limit++] = (byte)(0xE0 | ch >> 12);
                block.data[limit++] = (byte)(0x80 | ch >> 6 & 0x3F);
                block.data[limit++] = (byte)(0x80 | ch & 0x3F);
                octets += 3;
            }
            ++off;
            --len;
        }
        block.limit = limit;
        if (len > 0) {
            return octets + this.writeUTF8Slow(chars, off, len);
        }
        return octets;
    }

    private int writeUTF8UpTo2Byte(CharSequence chars, int off, int len) {
        if (len * 2 > this.remaining()) {
            return this.writeUTF8Slow(chars, off, len);
        }
        Block block = this.current;
        int limit = block.limit;
        char ch = '\u0000';
        int octets = 0;
        while (len > 0 && (ch = chars.charAt(off)) < '\u0800') {
            if (ch < '\u0080') {
                block.data[limit++] = (byte)ch;
                ++octets;
            } else {
                block.data[limit++] = (byte)(0xC0 | ch >> 6);
                block.data[limit++] = (byte)(0x80 | ch & 0x3F);
                octets += 2;
            }
            ++off;
            --len;
        }
        block.limit = limit;
        if (len > 0) {
            if (ch >= '\udc00' && ch <= '\udfff') {
                throw new IllegalArgumentException("Unpaired low surrogate: " + ch);
            }
            if (ch >= '\ud800' && ch <= '\udbff') {
                return octets + this.writeUTF8Slow(chars, off, len);
            }
            return octets + this.writeUTF8UpTo3Byte(chars, off, len);
        }
        return octets;
    }

    public int writeUTF8(CharSequence chars, int off, int len) {
        if (len > this.remaining()) {
            return this.writeUTF8Slow(chars, off, len);
        }
        Block block = this.current;
        int limit = block.limit;
        char ch = '\u0000';
        int octets = 0;
        while (len > 0 && (ch = chars.charAt(off)) < '\u0080') {
            block.data[limit++] = (byte)ch;
            ++octets;
            ++off;
            --len;
        }
        block.limit = limit;
        if (len > 0) {
            if (ch < '\u0800') {
                return octets + this.writeUTF8UpTo2Byte(chars, off, len);
            }
            if (ch >= '\udc00' && ch <= '\udfff') {
                throw new IllegalArgumentException("Unpaired low surrogate: " + ch);
            }
            if (ch >= '\ud800' && ch <= '\udbff') {
                return octets + this.writeUTF8Slow(chars, off, len);
            }
            return octets + this.writeUTF8UpTo3Byte(chars, off, len);
        }
        return octets;
    }

    public int writeUTF8(CharSequence chars) {
        return this.writeUTF8(chars, 0, chars.length());
    }

    public void writeUInt8(long value) {
        this.writeByte((byte)value);
    }

    private void writeUInt16Slow(long value) {
        this.writeByte((byte)(value >> 8));
        this.writeByte((byte)value);
    }

    public void writeUInt16(long value) {
        if (this.remaining() < 2) {
            this.writeUInt16Slow(value);
            return;
        }
        Block block = this.current;
        byte[] data = block.data;
        int limit = block.limit;
        data[limit++] = (byte)(value >> 8);
        data[limit++] = (byte)value;
        block.limit = limit;
    }

    private void writeUInt24Slow(long value) {
        this.writeByte((byte)(value >> 16));
        this.writeByte((byte)(value >> 8));
        this.writeByte((byte)value);
    }

    public void writeUInt24(long value) {
        if (this.remaining() < 3) {
            this.writeUInt24Slow(value);
            return;
        }
        Block block = this.current;
        byte[] data = block.data;
        int limit = block.limit;
        data[limit++] = (byte)(value >> 16);
        data[limit++] = (byte)(value >> 8);
        data[limit++] = (byte)value;
        block.limit = limit;
    }

    private void writeUInt32Slow(long value) {
        this.writeByte((byte)(value >> 24));
        this.writeByte((byte)(value >> 16));
        this.writeByte((byte)(value >> 8));
        this.writeByte((byte)value);
    }

    public void writeUInt32(long value) {
        if (this.remaining() < 4) {
            this.writeUInt32Slow(value);
            return;
        }
        Block block = this.current;
        byte[] data = block.data;
        int limit = block.limit;
        data[limit++] = (byte)(value >> 24);
        data[limit++] = (byte)(value >> 16);
        data[limit++] = (byte)(value >> 8);
        data[limit++] = (byte)value;
        block.limit = limit;
    }

    private void writeUInt40Slow(long value) {
        this.writeByte((byte)(value >> 32));
        this.writeByte((byte)(value >> 24));
        this.writeByte((byte)(value >> 16));
        this.writeByte((byte)(value >> 8));
        this.writeByte((byte)value);
    }

    public void writeUInt40(long value) {
        if (this.remaining() < 5) {
            this.writeUInt40Slow(value);
            return;
        }
        Block block = this.current;
        byte[] data = block.data;
        int limit = block.limit;
        data[limit++] = (byte)(value >> 32);
        data[limit++] = (byte)(value >> 24);
        data[limit++] = (byte)(value >> 16);
        data[limit++] = (byte)(value >> 8);
        data[limit++] = (byte)value;
        block.limit = limit;
    }

    private void writeUInt48Slow(long value) {
        this.writeByte((byte)(value >> 40));
        this.writeByte((byte)(value >> 32));
        this.writeByte((byte)(value >> 24));
        this.writeByte((byte)(value >> 16));
        this.writeByte((byte)(value >> 8));
        this.writeByte((byte)value);
    }

    public void writeUInt48(long value) {
        if (this.remaining() < 6) {
            this.writeUInt48Slow(value);
            return;
        }
        Block block = this.current;
        byte[] data = block.data;
        int limit = block.limit;
        data[limit++] = (byte)(value >> 40);
        data[limit++] = (byte)(value >> 32);
        data[limit++] = (byte)(value >> 24);
        data[limit++] = (byte)(value >> 16);
        data[limit++] = (byte)(value >> 8);
        data[limit++] = (byte)value;
        block.limit = limit;
    }

    private void writeUInt56Slow(long value) {
        this.writeByte((byte)(value >> 48));
        this.writeByte((byte)(value >> 40));
        this.writeByte((byte)(value >> 32));
        this.writeByte((byte)(value >> 24));
        this.writeByte((byte)(value >> 16));
        this.writeByte((byte)(value >> 8));
        this.writeByte((byte)value);
    }

    public void writeUInt56(long value) {
        if (this.remaining() < 7) {
            this.writeUInt56Slow(value);
            return;
        }
        Block block = this.current;
        byte[] data = block.data;
        int limit = block.limit;
        data[limit++] = (byte)(value >> 48);
        data[limit++] = (byte)(value >> 40);
        data[limit++] = (byte)(value >> 32);
        data[limit++] = (byte)(value >> 24);
        data[limit++] = (byte)(value >> 16);
        data[limit++] = (byte)(value >> 8);
        data[limit++] = (byte)value;
        block.limit = limit;
    }

    private void writeUInt64Slow(long value) {
        this.writeByte((byte)(value >> 56));
        this.writeByte((byte)(value >> 48));
        this.writeByte((byte)(value >> 40));
        this.writeByte((byte)(value >> 32));
        this.writeByte((byte)(value >> 24));
        this.writeByte((byte)(value >> 16));
        this.writeByte((byte)(value >> 8));
        this.writeByte((byte)value);
    }

    public void writeUInt64(long value) {
        if (this.remaining() < 8) {
            this.writeUInt64Slow(value);
            return;
        }
        Block block = this.current;
        byte[] data = block.data;
        int limit = block.limit;
        data[limit++] = (byte)(value >> 56);
        data[limit++] = (byte)(value >> 48);
        data[limit++] = (byte)(value >> 40);
        data[limit++] = (byte)(value >> 32);
        data[limit++] = (byte)(value >> 24);
        data[limit++] = (byte)(value >> 16);
        data[limit++] = (byte)(value >> 8);
        data[limit++] = (byte)value;
        block.limit = limit;
    }

    public void writeInt8(long value) {
        if (value < 0L) {
            value = -value | 0x80L;
        }
        this.writeUInt8(value);
    }

    public void writeInt16(long value) {
        if (value < 0L) {
            value = -value | 0x8000L;
        }
        this.writeUInt16(value);
    }

    public void writeInt24(long value) {
        if (value < 0L) {
            value = -value | 0x800000L;
        }
        this.writeUInt24(value);
    }

    public void writeInt32(long value) {
        if (value < 0L) {
            value = -value | 0x80000000L;
        }
        this.writeUInt32(value);
    }

    public void writeInt40(long value) {
        if (value < 0L) {
            value = -value | 0x8000000000L;
        }
        this.writeUInt40(value);
    }

    public void writeInt48(long value) {
        if (value < 0L) {
            value = -value | 0x800000000000L;
        }
        this.writeUInt48(value);
    }

    public void writeInt56(long value) {
        if (value < 0L) {
            value = -value | 0x80000000000000L;
        }
        this.writeUInt56(value);
    }

    public void writeInt64(long value) {
        if (value < 0L) {
            value = -value | Long.MIN_VALUE;
        }
        this.writeUInt64(value);
    }

    private int writeVarUIntSlow(long value) {
        int size = 1;
        if (value >= 0x100000000000000L) {
            this.writeUInt8(value >> 56 & 0x7FL);
            ++size;
        }
        if (value >= 0x2000000000000L) {
            this.writeUInt8(value >> 49 & 0x7FL);
            ++size;
        }
        if (value >= 0x40000000000L) {
            this.writeUInt8(value >> 42 & 0x7FL);
            ++size;
        }
        if (value >= 0x800000000L) {
            this.writeUInt8(value >> 35 & 0x7FL);
            ++size;
        }
        if (value >= 0x10000000L) {
            this.writeUInt8(value >> 28 & 0x7FL);
            ++size;
        }
        if (value >= 0x200000L) {
            this.writeUInt8(value >> 21 & 0x7FL);
            ++size;
        }
        if (value >= 16384L) {
            this.writeUInt8(value >> 14 & 0x7FL);
            ++size;
        }
        if (value >= 128L) {
            this.writeUInt8(value >> 7 & 0x7FL);
            ++size;
        }
        this.writeUInt8(value & 0x7FL | 0x80L);
        return size;
    }

    private int writeVarUIntDirect2(long value) {
        Block block = this.current;
        byte[] data = block.data;
        int limit = block.limit;
        data[limit++] = (byte)(value >> 7 & 0x7FL);
        data[limit++] = (byte)(value & 0x7FL | 0x80L);
        block.limit = limit;
        return 2;
    }

    private int writeVarUIntDirect3(long value) {
        Block block = this.current;
        byte[] data = block.data;
        int limit = block.limit;
        data[limit++] = (byte)(value >> 14 & 0x7FL);
        data[limit++] = (byte)(value >> 7 & 0x7FL);
        data[limit++] = (byte)(value & 0x7FL | 0x80L);
        block.limit = limit;
        return 3;
    }

    private int writeVarUIntDirect4(long value) {
        Block block = this.current;
        byte[] data = block.data;
        int limit = block.limit;
        data[limit++] = (byte)(value >> 21 & 0x7FL);
        data[limit++] = (byte)(value >> 14 & 0x7FL);
        data[limit++] = (byte)(value >> 7 & 0x7FL);
        data[limit++] = (byte)(value & 0x7FL | 0x80L);
        block.limit = limit;
        return 4;
    }

    private int writeVarUIntDirect5(long value) {
        Block block = this.current;
        byte[] data = block.data;
        int limit = block.limit;
        data[limit++] = (byte)(value >> 28 & 0x7FL);
        data[limit++] = (byte)(value >> 21 & 0x7FL);
        data[limit++] = (byte)(value >> 14 & 0x7FL);
        data[limit++] = (byte)(value >> 7 & 0x7FL);
        data[limit++] = (byte)(value & 0x7FL | 0x80L);
        block.limit = limit;
        return 5;
    }

    public int writeVarUInt(long value) {
        if (value < 128L) {
            this.writeUInt8(value & 0x7FL | 0x80L);
            return 1;
        }
        if (value < 16384L) {
            if (this.remaining() < 2) {
                return this.writeVarUIntSlow(value);
            }
            return this.writeVarUIntDirect2(value);
        }
        if (value < 0x200000L) {
            if (this.remaining() < 3) {
                return this.writeVarUIntSlow(value);
            }
            return this.writeVarUIntDirect3(value);
        }
        if (value < 0x10000000L) {
            if (this.remaining() < 4) {
                return this.writeVarUIntSlow(value);
            }
            return this.writeVarUIntDirect4(value);
        }
        if (value < 0x800000000L) {
            if (this.remaining() < 5) {
                return this.writeVarUIntSlow(value);
            }
            return this.writeVarUIntDirect5(value);
        }
        return this.writeVarUIntSlow(value);
    }

    private int writeVarIntSlow(long magnitude, long signMask) {
        long bits;
        int size = 1;
        if (magnitude >= 0x4000000000000000L) {
            this.writeUInt8(magnitude >> 62 & 0x3FL | signMask);
            ++size;
        }
        if (magnitude >= 0x80000000000000L) {
            bits = magnitude >> 56;
            this.writeUInt8(size == 1 ? bits & 0x3FL | signMask : bits & 0x7FL);
            ++size;
        }
        if (magnitude >= 0x1000000000000L) {
            bits = magnitude >> 49;
            this.writeUInt8(size == 1 ? bits & 0x3FL | signMask : bits & 0x7FL);
            ++size;
        }
        if (magnitude >= 0x20000000000L) {
            bits = magnitude >> 42;
            this.writeUInt8(size == 1 ? bits & 0x3FL | signMask : bits & 0x7FL);
            ++size;
        }
        if (magnitude >= 0x400000000L) {
            bits = magnitude >> 35;
            this.writeUInt8(size == 1 ? bits & 0x3FL | signMask : bits & 0x7FL);
            ++size;
        }
        if (magnitude >= 0x8000000L) {
            bits = magnitude >> 28;
            this.writeUInt8(size == 1 ? bits & 0x3FL | signMask : bits & 0x7FL);
            ++size;
        }
        if (magnitude >= 0x100000L) {
            bits = magnitude >> 21;
            this.writeUInt8(size == 1 ? bits & 0x3FL | signMask : bits & 0x7FL);
            ++size;
        }
        if (magnitude >= 8192L) {
            bits = magnitude >> 14;
            this.writeUInt8(size == 1 ? bits & 0x3FL | signMask : bits & 0x7FL);
            ++size;
        }
        if (magnitude >= 64L) {
            bits = magnitude >> 7;
            this.writeUInt8(size == 1 ? bits & 0x3FL | signMask : bits & 0x7FL);
            ++size;
        }
        this.writeUInt8((size == 1 ? magnitude & 0x3FL | signMask : magnitude & 0x7FL) | 0x80L);
        return size;
    }

    public int writeVarInt(long value) {
        long magnitude;
        assert (value != Long.MIN_VALUE);
        long signMask = value < 0L ? 64L : 0L;
        long l = magnitude = value < 0L ? -value : value;
        if (magnitude < 64L) {
            this.writeUInt8(magnitude & 0x3FL | 0x80L | signMask);
            return 1;
        }
        long signBit = value < 0L ? 1L : 0L;
        int remaining = this.remaining();
        if (magnitude < 8192L && remaining >= 2) {
            return this.writeVarUIntDirect2(magnitude | signBit << 13);
        }
        if (magnitude < 0x100000L && remaining >= 3) {
            return this.writeVarUIntDirect3(magnitude | signBit << 20);
        }
        if (magnitude < 0x8000000L && remaining >= 4) {
            return this.writeVarUIntDirect4(magnitude | signBit << 27);
        }
        if (magnitude < 0x400000000L && remaining >= 5) {
            return this.writeVarUIntDirect5(magnitude | signBit << 34);
        }
        return this.writeVarIntSlow(magnitude, signMask);
    }

    public void writeVarUIntDirect1At(long position, long value) {
        this.writeUInt8At(position, value & 0x7FL | 0x80L);
    }

    private void writeVarUIntDirect2StraddlingAt(int index, int offset, long value) {
        Block block1 = this.blocks.get(index);
        block1.data[offset] = (byte)(value >> 7 & 0x7FL);
        Block block2 = this.blocks.get(index + 1);
        block2.data[0] = (byte)(value & 0x7FL | 0x80L);
    }

    public void writeVarUIntDirect2At(long position, long value) {
        int index = this.index(position);
        int offset = this.offset(position);
        if (offset + 2 > this.allocator.getBlockSize()) {
            this.writeVarUIntDirect2StraddlingAt(index, offset, value);
            return;
        }
        Block block = this.blocks.get(index);
        block.data[offset] = (byte)(value >> 7 & 0x7FL);
        block.data[offset + 1] = (byte)(value & 0x7FL | 0x80L);
    }

    public void writeUInt8At(long position, long value) {
        int index = this.index(position);
        int offset = this.offset(position);
        Block block = this.blocks.get(index);
        block.data[offset] = (byte)value;
    }

    public void writeTo(OutputStream out) throws IOException {
        for (Block block : this.blocks) {
            out.write(block.data, 0, block.limit);
        }
    }

    public void writeTo(OutputStream out, long position, long length) throws IOException {
        while (length > 0L) {
            int index = this.index(position);
            int offset = this.offset(position);
            Block block = this.blocks.get(index);
            int amount = (int)Math.min((long)(block.data.length - offset), length);
            out.write(block.data, offset, amount);
            position += (long)amount;
            length -= (long)amount;
        }
    }
}


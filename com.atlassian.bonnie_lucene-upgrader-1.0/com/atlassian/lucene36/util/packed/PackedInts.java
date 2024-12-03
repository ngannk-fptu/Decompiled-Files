/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util.packed;

import com.atlassian.lucene36.store.DataInput;
import com.atlassian.lucene36.store.DataOutput;
import com.atlassian.lucene36.util.CodecUtil;
import com.atlassian.lucene36.util.Constants;
import com.atlassian.lucene36.util.packed.Direct16;
import com.atlassian.lucene36.util.packed.Direct32;
import com.atlassian.lucene36.util.packed.Direct64;
import com.atlassian.lucene36.util.packed.Direct8;
import com.atlassian.lucene36.util.packed.Packed32;
import com.atlassian.lucene36.util.packed.Packed64;
import com.atlassian.lucene36.util.packed.PackedWriter;
import java.io.IOException;

public class PackedInts {
    private static final String CODEC_NAME = "PackedInts";
    private static final int VERSION_START = 0;
    private static final int VERSION_CURRENT = 0;

    public static Reader getReader(DataInput in) throws IOException {
        CodecUtil.checkHeader(in, CODEC_NAME, 0, 0);
        int bitsPerValue = in.readVInt();
        assert (bitsPerValue > 0 && bitsPerValue <= 64) : "bitsPerValue=" + bitsPerValue;
        int valueCount = in.readVInt();
        switch (bitsPerValue) {
            case 8: {
                return new Direct8(in, valueCount);
            }
            case 16: {
                return new Direct16(in, valueCount);
            }
            case 32: {
                return new Direct32(in, valueCount);
            }
            case 64: {
                return new Direct64(in, valueCount);
            }
        }
        if (Constants.JRE_IS_64BIT || bitsPerValue >= 32) {
            return new Packed64(in, valueCount, bitsPerValue);
        }
        return new Packed32(in, valueCount, bitsPerValue);
    }

    public static Mutable getMutable(int valueCount, int bitsPerValue) {
        switch (bitsPerValue) {
            case 8: {
                return new Direct8(valueCount);
            }
            case 16: {
                return new Direct16(valueCount);
            }
            case 32: {
                return new Direct32(valueCount);
            }
            case 64: {
                return new Direct64(valueCount);
            }
        }
        if (Constants.JRE_IS_64BIT || bitsPerValue >= 32) {
            return new Packed64(valueCount, bitsPerValue);
        }
        return new Packed32(valueCount, bitsPerValue);
    }

    public static Writer getWriter(DataOutput out, int valueCount, int bitsPerValue) throws IOException {
        return new PackedWriter(out, valueCount, bitsPerValue);
    }

    public static int bitsRequired(long maxValue) {
        if (maxValue > 0x3FFFFFFFFFFFFFFFL) {
            return 63;
        }
        if (maxValue > 0x1FFFFFFFFFFFFFFFL) {
            return 62;
        }
        return Math.max(1, (int)Math.ceil(Math.log(1L + maxValue) / Math.log(2.0)));
    }

    public static long maxValue(int bitsPerValue) {
        return bitsPerValue == 64 ? Long.MAX_VALUE : -1L << bitsPerValue ^ 0xFFFFFFFFFFFFFFFFL;
    }

    public static int getNextFixedSize(int bitsPerValue) {
        if (bitsPerValue <= 8) {
            return 8;
        }
        if (bitsPerValue <= 16) {
            return 16;
        }
        if (bitsPerValue <= 32) {
            return 32;
        }
        return 64;
    }

    public static int getRoundedFixedSize(int bitsPerValue) {
        if (bitsPerValue > 58 || bitsPerValue < 32 && bitsPerValue > 29) {
            return PackedInts.getNextFixedSize(bitsPerValue);
        }
        return bitsPerValue;
    }

    public static abstract class Writer {
        protected final DataOutput out;
        protected final int bitsPerValue;
        protected final int valueCount;

        protected Writer(DataOutput out, int valueCount, int bitsPerValue) throws IOException {
            assert (bitsPerValue <= 64);
            this.out = out;
            this.valueCount = valueCount;
            this.bitsPerValue = bitsPerValue;
            CodecUtil.writeHeader(out, PackedInts.CODEC_NAME, 0);
            out.writeVInt(bitsPerValue);
            out.writeVInt(valueCount);
        }

        public abstract void add(long var1) throws IOException;

        public abstract void finish() throws IOException;
    }

    public static abstract class ReaderImpl
    implements Reader {
        protected final int bitsPerValue;
        protected final int valueCount;

        protected ReaderImpl(int valueCount, int bitsPerValue) {
            this.bitsPerValue = bitsPerValue;
            assert (bitsPerValue > 0 && bitsPerValue <= 64) : "bitsPerValue=" + bitsPerValue;
            this.valueCount = valueCount;
        }

        public int getBitsPerValue() {
            return this.bitsPerValue;
        }

        public int size() {
            return this.valueCount;
        }

        public long getMaxValue() {
            return PackedInts.maxValue(this.bitsPerValue);
        }

        public Object getArray() {
            return null;
        }

        public boolean hasArray() {
            return false;
        }
    }

    public static interface Mutable
    extends Reader {
        public void set(int var1, long var2);

        public void clear();
    }

    public static interface Reader {
        public long get(int var1);

        public int getBitsPerValue();

        public int size();

        public Object getArray();

        public boolean hasArray();
    }
}


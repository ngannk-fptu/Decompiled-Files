/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import java.io.IOException;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.LongsRef;
import org.apache.lucene.util.packed.BulkOperation;
import org.apache.lucene.util.packed.Direct16;
import org.apache.lucene.util.packed.Direct32;
import org.apache.lucene.util.packed.Direct64;
import org.apache.lucene.util.packed.Direct8;
import org.apache.lucene.util.packed.DirectPacked64SingleBlockReader;
import org.apache.lucene.util.packed.DirectPackedReader;
import org.apache.lucene.util.packed.Packed16ThreeBlocks;
import org.apache.lucene.util.packed.Packed64;
import org.apache.lucene.util.packed.Packed64SingleBlock;
import org.apache.lucene.util.packed.Packed8ThreeBlocks;
import org.apache.lucene.util.packed.PackedReaderIterator;
import org.apache.lucene.util.packed.PackedWriter;

public class PackedInts {
    public static final float FASTEST = 7.0f;
    public static final float FAST = 0.5f;
    public static final float DEFAULT = 0.2f;
    public static final float COMPACT = 0.0f;
    public static final int DEFAULT_BUFFER_SIZE = 1024;
    public static final String CODEC_NAME = "PackedInts";
    public static final int VERSION_START = 0;
    public static final int VERSION_BYTE_ALIGNED = 1;
    public static final int VERSION_CURRENT = 1;

    public static void checkVersion(int version) {
        if (version < 0) {
            throw new IllegalArgumentException("Version is too old, should be at least 0 (got " + version + ")");
        }
        if (version > 1) {
            throw new IllegalArgumentException("Version is too new, should be at most 1 (got " + version + ")");
        }
    }

    public static FormatAndBits fastestFormatAndBits(int valueCount, int bitsPerValue, float acceptableOverheadRatio) {
        if (valueCount == -1) {
            valueCount = Integer.MAX_VALUE;
        }
        acceptableOverheadRatio = Math.max(0.0f, acceptableOverheadRatio);
        acceptableOverheadRatio = Math.min(7.0f, acceptableOverheadRatio);
        float acceptableOverheadPerValue = acceptableOverheadRatio * (float)bitsPerValue;
        int maxBitsPerValue = bitsPerValue + (int)acceptableOverheadPerValue;
        int actualBitsPerValue = -1;
        Format format = Format.PACKED;
        if (bitsPerValue <= 8 && maxBitsPerValue >= 8) {
            actualBitsPerValue = 8;
        } else if (bitsPerValue <= 16 && maxBitsPerValue >= 16) {
            actualBitsPerValue = 16;
        } else if (bitsPerValue <= 32 && maxBitsPerValue >= 32) {
            actualBitsPerValue = 32;
        } else if (bitsPerValue <= 64 && maxBitsPerValue >= 64) {
            actualBitsPerValue = 64;
        } else if (valueCount <= 0x2AAAAAAA && bitsPerValue <= 24 && maxBitsPerValue >= 24) {
            actualBitsPerValue = 24;
        } else if (valueCount <= 0x2AAAAAAA && bitsPerValue <= 48 && maxBitsPerValue >= 48) {
            actualBitsPerValue = 48;
        } else {
            for (int bpv = bitsPerValue; bpv <= maxBitsPerValue; ++bpv) {
                float acceptableOverhead;
                float overhead;
                if (!Format.PACKED_SINGLE_BLOCK.isSupported(bpv) || !((overhead = Format.PACKED_SINGLE_BLOCK.overheadPerValue(bpv)) <= (acceptableOverhead = acceptableOverheadPerValue + (float)bitsPerValue - (float)bpv))) continue;
                actualBitsPerValue = bpv;
                format = Format.PACKED_SINGLE_BLOCK;
                break;
            }
            if (actualBitsPerValue < 0) {
                actualBitsPerValue = bitsPerValue;
            }
        }
        return new FormatAndBits(format, actualBitsPerValue);
    }

    public static Decoder getDecoder(Format format, int version, int bitsPerValue) {
        PackedInts.checkVersion(version);
        return BulkOperation.of(format, bitsPerValue);
    }

    public static Encoder getEncoder(Format format, int version, int bitsPerValue) {
        PackedInts.checkVersion(version);
        return BulkOperation.of(format, bitsPerValue);
    }

    public static Reader getReaderNoHeader(DataInput in, Format format, int version, int valueCount, int bitsPerValue) throws IOException {
        PackedInts.checkVersion(version);
        switch (format) {
            case PACKED_SINGLE_BLOCK: {
                return Packed64SingleBlock.create(in, valueCount, bitsPerValue);
            }
            case PACKED: {
                switch (bitsPerValue) {
                    case 8: {
                        return new Direct8(version, in, valueCount);
                    }
                    case 16: {
                        return new Direct16(version, in, valueCount);
                    }
                    case 32: {
                        return new Direct32(version, in, valueCount);
                    }
                    case 64: {
                        return new Direct64(version, in, valueCount);
                    }
                    case 24: {
                        if (valueCount > 0x2AAAAAAA) break;
                        return new Packed8ThreeBlocks(version, in, valueCount);
                    }
                    case 48: {
                        if (valueCount > 0x2AAAAAAA) break;
                        return new Packed16ThreeBlocks(version, in, valueCount);
                    }
                }
                return new Packed64(version, in, valueCount, bitsPerValue);
            }
        }
        throw new AssertionError((Object)("Unknown Writer format: " + (Object)((Object)format)));
    }

    public static Reader getReaderNoHeader(DataInput in, Header header) throws IOException {
        return PackedInts.getReaderNoHeader(in, header.format, header.version, header.valueCount, header.bitsPerValue);
    }

    public static Reader getReader(DataInput in) throws IOException {
        int version = CodecUtil.checkHeader(in, CODEC_NAME, 0, 1);
        int bitsPerValue = in.readVInt();
        assert (bitsPerValue > 0 && bitsPerValue <= 64) : "bitsPerValue=" + bitsPerValue;
        int valueCount = in.readVInt();
        Format format = Format.byId(in.readVInt());
        return PackedInts.getReaderNoHeader(in, format, version, valueCount, bitsPerValue);
    }

    public static ReaderIterator getReaderIteratorNoHeader(DataInput in, Format format, int version, int valueCount, int bitsPerValue, int mem) {
        PackedInts.checkVersion(version);
        return new PackedReaderIterator(format, version, valueCount, bitsPerValue, in, mem);
    }

    public static ReaderIterator getReaderIterator(DataInput in, int mem) throws IOException {
        int version = CodecUtil.checkHeader(in, CODEC_NAME, 0, 1);
        int bitsPerValue = in.readVInt();
        assert (bitsPerValue > 0 && bitsPerValue <= 64) : "bitsPerValue=" + bitsPerValue;
        int valueCount = in.readVInt();
        Format format = Format.byId(in.readVInt());
        return PackedInts.getReaderIteratorNoHeader(in, format, version, valueCount, bitsPerValue, mem);
    }

    public static Reader getDirectReaderNoHeader(final IndexInput in, Format format, int version, int valueCount, int bitsPerValue) {
        PackedInts.checkVersion(version);
        switch (format) {
            case PACKED: {
                long byteCount = format.byteCount(version, valueCount, bitsPerValue);
                if (byteCount != format.byteCount(1, valueCount, bitsPerValue)) {
                    assert (version == 0);
                    final long endPointer = in.getFilePointer() + byteCount;
                    return new DirectPackedReader(bitsPerValue, valueCount, in){

                        @Override
                        public long get(int index) {
                            long result = super.get(index);
                            if (index == this.valueCount - 1) {
                                try {
                                    in.seek(endPointer);
                                }
                                catch (IOException e) {
                                    throw new IllegalStateException("failed", e);
                                }
                            }
                            return result;
                        }
                    };
                }
                return new DirectPackedReader(bitsPerValue, valueCount, in);
            }
            case PACKED_SINGLE_BLOCK: {
                return new DirectPacked64SingleBlockReader(bitsPerValue, valueCount, in);
            }
        }
        throw new AssertionError((Object)("Unknwown format: " + (Object)((Object)format)));
    }

    public static Reader getDirectReaderNoHeader(IndexInput in, Header header) throws IOException {
        return PackedInts.getDirectReaderNoHeader(in, header.format, header.version, header.valueCount, header.bitsPerValue);
    }

    public static Reader getDirectReader(IndexInput in) throws IOException {
        int version = CodecUtil.checkHeader(in, CODEC_NAME, 0, 1);
        int bitsPerValue = in.readVInt();
        assert (bitsPerValue > 0 && bitsPerValue <= 64) : "bitsPerValue=" + bitsPerValue;
        int valueCount = in.readVInt();
        Format format = Format.byId(in.readVInt());
        return PackedInts.getDirectReaderNoHeader(in, format, version, valueCount, bitsPerValue);
    }

    public static Mutable getMutable(int valueCount, int bitsPerValue, float acceptableOverheadRatio) {
        FormatAndBits formatAndBits = PackedInts.fastestFormatAndBits(valueCount, bitsPerValue, acceptableOverheadRatio);
        return PackedInts.getMutable(valueCount, formatAndBits.bitsPerValue, formatAndBits.format);
    }

    public static Mutable getMutable(int valueCount, int bitsPerValue, Format format) {
        assert (valueCount >= 0);
        switch (format) {
            case PACKED_SINGLE_BLOCK: {
                return Packed64SingleBlock.create(valueCount, bitsPerValue);
            }
            case PACKED: {
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
                    case 24: {
                        if (valueCount > 0x2AAAAAAA) break;
                        return new Packed8ThreeBlocks(valueCount);
                    }
                    case 48: {
                        if (valueCount > 0x2AAAAAAA) break;
                        return new Packed16ThreeBlocks(valueCount);
                    }
                }
                return new Packed64(valueCount, bitsPerValue);
            }
        }
        throw new AssertionError();
    }

    public static Writer getWriterNoHeader(DataOutput out, Format format, int valueCount, int bitsPerValue, int mem) {
        return new PackedWriter(format, out, valueCount, bitsPerValue, mem);
    }

    public static Writer getWriter(DataOutput out, int valueCount, int bitsPerValue, float acceptableOverheadRatio) throws IOException {
        assert (valueCount >= 0);
        FormatAndBits formatAndBits = PackedInts.fastestFormatAndBits(valueCount, bitsPerValue, acceptableOverheadRatio);
        Writer writer = PackedInts.getWriterNoHeader(out, formatAndBits.format, valueCount, formatAndBits.bitsPerValue, 1024);
        writer.writeHeader();
        return writer;
    }

    public static int bitsRequired(long maxValue) {
        if (maxValue < 0L) {
            throw new IllegalArgumentException("maxValue must be non-negative (got: " + maxValue + ")");
        }
        return Math.max(1, 64 - Long.numberOfLeadingZeros(maxValue));
    }

    public static long maxValue(int bitsPerValue) {
        return bitsPerValue == 64 ? Long.MAX_VALUE : -1L << bitsPerValue ^ 0xFFFFFFFFFFFFFFFFL;
    }

    public static void copy(Reader src, int srcPos, Mutable dest, int destPos, int len, int mem) {
        assert (srcPos + len <= src.size());
        assert (destPos + len <= dest.size());
        int capacity = mem >>> 3;
        if (capacity == 0) {
            for (int i = 0; i < len; ++i) {
                dest.set(destPos++, src.get(srcPos++));
            }
        } else if (len > 0) {
            long[] buf = new long[Math.min(capacity, len)];
            PackedInts.copy(src, srcPos, dest, destPos, len, buf);
        }
    }

    static void copy(Reader src, int srcPos, Mutable dest, int destPos, int len, long[] buf) {
        assert (buf.length > 0);
        int remaining = 0;
        while (len > 0) {
            int read = src.get(srcPos, buf, remaining, Math.min(len, buf.length - remaining));
            assert (read > 0);
            srcPos += read;
            len -= read;
            int written = dest.set(destPos, buf, 0, remaining += read);
            assert (written > 0);
            destPos += written;
            if (written < remaining) {
                System.arraycopy(buf, written, buf, 0, remaining - written);
            }
            remaining -= written;
        }
        while (remaining > 0) {
            int written = dest.set(destPos, buf, 0, remaining);
            destPos += written;
            System.arraycopy(buf, written, buf, 0, remaining -= written);
        }
    }

    public static Header readHeader(DataInput in) throws IOException {
        int version = CodecUtil.checkHeader(in, CODEC_NAME, 0, 1);
        int bitsPerValue = in.readVInt();
        assert (bitsPerValue > 0 && bitsPerValue <= 64) : "bitsPerValue=" + bitsPerValue;
        int valueCount = in.readVInt();
        Format format = Format.byId(in.readVInt());
        return new Header(format, valueCount, bitsPerValue, version);
    }

    static int checkBlockSize(int blockSize, int minBlockSize, int maxBlockSize) {
        if (blockSize < minBlockSize || blockSize > maxBlockSize) {
            throw new IllegalArgumentException("blockSize must be >= " + minBlockSize + " and <= " + maxBlockSize + ", got " + blockSize);
        }
        if ((blockSize & blockSize - 1) != 0) {
            throw new IllegalArgumentException("blockSize must be a power of two, got " + blockSize);
        }
        return Integer.numberOfTrailingZeros(blockSize);
    }

    static int numBlocks(long size, int blockSize) {
        int numBlocks = (int)(size / (long)blockSize) + (size % (long)blockSize == 0L ? 0 : 1);
        if ((long)numBlocks * (long)blockSize < size) {
            throw new IllegalArgumentException("size is too large for this block size");
        }
        return numBlocks;
    }

    public static class Header {
        private final Format format;
        private final int valueCount;
        private final int bitsPerValue;
        private final int version;

        public Header(Format format, int valueCount, int bitsPerValue, int version) {
            this.format = format;
            this.valueCount = valueCount;
            this.bitsPerValue = bitsPerValue;
            this.version = version;
        }
    }

    public static abstract class Writer {
        protected final DataOutput out;
        protected final int valueCount;
        protected final int bitsPerValue;

        protected Writer(DataOutput out, int valueCount, int bitsPerValue) {
            assert (bitsPerValue <= 64);
            assert (valueCount >= 0 || valueCount == -1);
            this.out = out;
            this.valueCount = valueCount;
            this.bitsPerValue = bitsPerValue;
        }

        void writeHeader() throws IOException {
            assert (this.valueCount != -1);
            CodecUtil.writeHeader(this.out, PackedInts.CODEC_NAME, 1);
            this.out.writeVInt(this.bitsPerValue);
            this.out.writeVInt(this.valueCount);
            this.out.writeVInt(this.getFormat().getId());
        }

        protected abstract Format getFormat();

        public abstract void add(long var1) throws IOException;

        public final int bitsPerValue() {
            return this.bitsPerValue;
        }

        public abstract void finish() throws IOException;

        public abstract int ord();
    }

    public static final class NullReader
    implements Reader {
        private final int valueCount;

        public NullReader(int valueCount) {
            this.valueCount = valueCount;
        }

        @Override
        public long get(int index) {
            return 0L;
        }

        @Override
        public int get(int index, long[] arr, int off, int len) {
            return 0;
        }

        @Override
        public int getBitsPerValue() {
            return 0;
        }

        @Override
        public int size() {
            return this.valueCount;
        }

        @Override
        public long ramBytesUsed() {
            return 0L;
        }

        @Override
        public Object getArray() {
            return null;
        }

        @Override
        public boolean hasArray() {
            return false;
        }
    }

    static abstract class MutableImpl
    extends ReaderImpl
    implements Mutable {
        protected MutableImpl(int valueCount, int bitsPerValue) {
            super(valueCount, bitsPerValue);
        }

        @Override
        public int set(int index, long[] arr, int off, int len) {
            assert (len > 0) : "len must be > 0 (got " + len + ")";
            assert (index >= 0 && index < this.valueCount);
            len = Math.min(len, this.valueCount - index);
            assert (off + len <= arr.length);
            int i = index;
            int o = off;
            int end = index + len;
            while (i < end) {
                this.set(i, arr[o]);
                ++i;
                ++o;
            }
            return len;
        }

        @Override
        public void fill(int fromIndex, int toIndex, long val) {
            assert (val <= PackedInts.maxValue(this.bitsPerValue));
            assert (fromIndex <= toIndex);
            for (int i = fromIndex; i < toIndex; ++i) {
                this.set(i, val);
            }
        }

        protected Format getFormat() {
            return Format.PACKED;
        }

        @Override
        public void save(DataOutput out) throws IOException {
            Writer writer = PackedInts.getWriterNoHeader(out, this.getFormat(), this.valueCount, this.bitsPerValue, 1024);
            writer.writeHeader();
            for (int i = 0; i < this.valueCount; ++i) {
                writer.add(this.get(i));
            }
            writer.finish();
        }
    }

    static abstract class ReaderImpl
    implements Reader {
        protected final int bitsPerValue;
        protected final int valueCount;

        protected ReaderImpl(int valueCount, int bitsPerValue) {
            this.bitsPerValue = bitsPerValue;
            assert (bitsPerValue > 0 && bitsPerValue <= 64) : "bitsPerValue=" + bitsPerValue;
            this.valueCount = valueCount;
        }

        @Override
        public int getBitsPerValue() {
            return this.bitsPerValue;
        }

        @Override
        public int size() {
            return this.valueCount;
        }

        @Override
        public Object getArray() {
            return null;
        }

        @Override
        public boolean hasArray() {
            return false;
        }

        @Override
        public int get(int index, long[] arr, int off, int len) {
            assert (len > 0) : "len must be > 0 (got " + len + ")";
            assert (index >= 0 && index < this.valueCount);
            assert (off + len <= arr.length);
            int gets = Math.min(this.valueCount - index, len);
            int i = index;
            int o = off;
            int end = index + gets;
            while (i < end) {
                arr[o] = this.get(i);
                ++i;
                ++o;
            }
            return gets;
        }
    }

    public static interface Mutable
    extends Reader {
        public void set(int var1, long var2);

        public int set(int var1, long[] var2, int var3, int var4);

        public void fill(int var1, int var2, long var3);

        public void clear();

        public void save(DataOutput var1) throws IOException;
    }

    static abstract class ReaderIteratorImpl
    implements ReaderIterator {
        protected final DataInput in;
        protected final int bitsPerValue;
        protected final int valueCount;

        protected ReaderIteratorImpl(int valueCount, int bitsPerValue, DataInput in) {
            this.in = in;
            this.bitsPerValue = bitsPerValue;
            this.valueCount = valueCount;
        }

        @Override
        public long next() throws IOException {
            LongsRef nextValues = this.next(1);
            assert (nextValues.length > 0);
            long result = nextValues.longs[nextValues.offset];
            ++nextValues.offset;
            --nextValues.length;
            return result;
        }

        @Override
        public int getBitsPerValue() {
            return this.bitsPerValue;
        }

        @Override
        public int size() {
            return this.valueCount;
        }
    }

    public static interface ReaderIterator {
        public long next() throws IOException;

        public LongsRef next(int var1) throws IOException;

        public int getBitsPerValue();

        public int size();

        public int ord();
    }

    public static interface Reader {
        public long get(int var1);

        public int get(int var1, long[] var2, int var3, int var4);

        public int getBitsPerValue();

        public int size();

        public long ramBytesUsed();

        public Object getArray();

        public boolean hasArray();
    }

    public static interface Encoder {
        public int longBlockCount();

        public int longValueCount();

        public int byteBlockCount();

        public int byteValueCount();

        public void encode(long[] var1, int var2, long[] var3, int var4, int var5);

        public void encode(long[] var1, int var2, byte[] var3, int var4, int var5);

        public void encode(int[] var1, int var2, long[] var3, int var4, int var5);

        public void encode(int[] var1, int var2, byte[] var3, int var4, int var5);
    }

    public static interface Decoder {
        public int longBlockCount();

        public int longValueCount();

        public int byteBlockCount();

        public int byteValueCount();

        public void decode(long[] var1, int var2, long[] var3, int var4, int var5);

        public void decode(byte[] var1, int var2, long[] var3, int var4, int var5);

        public void decode(long[] var1, int var2, int[] var3, int var4, int var5);

        public void decode(byte[] var1, int var2, int[] var3, int var4, int var5);
    }

    public static class FormatAndBits {
        public final Format format;
        public final int bitsPerValue;

        public FormatAndBits(Format format, int bitsPerValue) {
            this.format = format;
            this.bitsPerValue = bitsPerValue;
        }

        public String toString() {
            return "FormatAndBits(format=" + (Object)((Object)this.format) + " bitsPerValue=" + this.bitsPerValue + ")";
        }
    }

    public static class Format
    extends Enum<Format> {
        public static final /* enum */ Format PACKED = new Format(0){

            @Override
            public long byteCount(int packedIntsVersion, int valueCount, int bitsPerValue) {
                if (packedIntsVersion < 1) {
                    return 8L * (long)Math.ceil((double)valueCount * (double)bitsPerValue / 64.0);
                }
                return (long)Math.ceil((double)valueCount * (double)bitsPerValue / 8.0);
            }
        };
        public static final /* enum */ Format PACKED_SINGLE_BLOCK = new Format(1){

            @Override
            public int longCount(int packedIntsVersion, int valueCount, int bitsPerValue) {
                int valuesPerBlock = 64 / bitsPerValue;
                return (int)Math.ceil((double)valueCount / (double)valuesPerBlock);
            }

            @Override
            public boolean isSupported(int bitsPerValue) {
                return Packed64SingleBlock.isSupported(bitsPerValue);
            }

            @Override
            public float overheadPerValue(int bitsPerValue) {
                assert (this.isSupported(bitsPerValue));
                int valuesPerBlock = 64 / bitsPerValue;
                int overhead = 64 % bitsPerValue;
                return (float)overhead / (float)valuesPerBlock;
            }
        };
        public int id;
        private static final /* synthetic */ Format[] $VALUES;

        public static Format[] values() {
            return (Format[])$VALUES.clone();
        }

        public static Format valueOf(String name) {
            return Enum.valueOf(Format.class, name);
        }

        public static Format byId(int id) {
            for (Format format : Format.values()) {
                if (format.getId() != id) continue;
                return format;
            }
            throw new IllegalArgumentException("Unknown format id: " + id);
        }

        private Format(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        public long byteCount(int packedIntsVersion, int valueCount, int bitsPerValue) {
            assert (bitsPerValue >= 0 && bitsPerValue <= 64) : bitsPerValue;
            return 8L * (long)this.longCount(packedIntsVersion, valueCount, bitsPerValue);
        }

        public int longCount(int packedIntsVersion, int valueCount, int bitsPerValue) {
            assert (bitsPerValue >= 0 && bitsPerValue <= 64) : bitsPerValue;
            long byteCount = this.byteCount(packedIntsVersion, valueCount, bitsPerValue);
            assert (byteCount < 0x3FFFFFFF8L);
            if (byteCount % 8L == 0L) {
                return (int)(byteCount / 8L);
            }
            return (int)(byteCount / 8L + 1L);
        }

        public boolean isSupported(int bitsPerValue) {
            return bitsPerValue >= 1 && bitsPerValue <= 64;
        }

        public float overheadPerValue(int bitsPerValue) {
            assert (this.isSupported(bitsPerValue));
            return 0.0f;
        }

        public final float overheadRatio(int bitsPerValue) {
            assert (this.isSupported(bitsPerValue));
            return this.overheadPerValue(bitsPerValue) / (float)bitsPerValue;
        }

        static {
            $VALUES = new Format[]{PACKED, PACKED_SINGLE_BLOCK};
        }
    }
}


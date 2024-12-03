/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene41;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.util.packed.PackedInts;

final class ForUtil {
    private static final int ALL_VALUES_EQUAL = 0;
    static final int MAX_ENCODED_SIZE = 512;
    static final int MAX_DATA_SIZE;
    private final int[] encodedSizes;
    private final PackedInts.Encoder[] encoders;
    private final PackedInts.Decoder[] decoders;
    private final int[] iterations;

    private static int computeIterations(PackedInts.Decoder decoder) {
        return (int)Math.ceil(128.0f / (float)decoder.byteValueCount());
    }

    private static int encodedSize(PackedInts.Format format, int packedIntsVersion, int bitsPerValue) {
        long byteCount = format.byteCount(packedIntsVersion, 128, bitsPerValue);
        assert (byteCount >= 0L && byteCount <= Integer.MAX_VALUE) : byteCount;
        return (int)byteCount;
    }

    ForUtil(float acceptableOverheadRatio, DataOutput out) throws IOException {
        out.writeVInt(1);
        this.encodedSizes = new int[33];
        this.encoders = new PackedInts.Encoder[33];
        this.decoders = new PackedInts.Decoder[33];
        this.iterations = new int[33];
        for (int bpv = 1; bpv <= 32; ++bpv) {
            PackedInts.FormatAndBits formatAndBits = PackedInts.fastestFormatAndBits(128, bpv, acceptableOverheadRatio);
            assert (formatAndBits.format.isSupported(formatAndBits.bitsPerValue));
            assert (formatAndBits.bitsPerValue <= 32);
            this.encodedSizes[bpv] = ForUtil.encodedSize(formatAndBits.format, 1, formatAndBits.bitsPerValue);
            this.encoders[bpv] = PackedInts.getEncoder(formatAndBits.format, 1, formatAndBits.bitsPerValue);
            this.decoders[bpv] = PackedInts.getDecoder(formatAndBits.format, 1, formatAndBits.bitsPerValue);
            this.iterations[bpv] = ForUtil.computeIterations(this.decoders[bpv]);
            out.writeVInt(formatAndBits.format.getId() << 5 | formatAndBits.bitsPerValue - 1);
        }
    }

    ForUtil(DataInput in) throws IOException {
        int packedIntsVersion = in.readVInt();
        PackedInts.checkVersion(packedIntsVersion);
        this.encodedSizes = new int[33];
        this.encoders = new PackedInts.Encoder[33];
        this.decoders = new PackedInts.Decoder[33];
        this.iterations = new int[33];
        for (int bpv = 1; bpv <= 32; ++bpv) {
            int code = in.readVInt();
            int formatId = code >>> 5;
            int bitsPerValue = (code & 0x1F) + 1;
            PackedInts.Format format = PackedInts.Format.byId(formatId);
            assert (format.isSupported(bitsPerValue));
            this.encodedSizes[bpv] = ForUtil.encodedSize(format, packedIntsVersion, bitsPerValue);
            this.encoders[bpv] = PackedInts.getEncoder(format, packedIntsVersion, bitsPerValue);
            this.decoders[bpv] = PackedInts.getDecoder(format, packedIntsVersion, bitsPerValue);
            this.iterations[bpv] = ForUtil.computeIterations(this.decoders[bpv]);
        }
    }

    void writeBlock(int[] data, byte[] encoded, IndexOutput out) throws IOException {
        if (ForUtil.isAllEqual(data)) {
            out.writeByte((byte)0);
            out.writeVInt(data[0]);
            return;
        }
        int numBits = ForUtil.bitsRequired(data);
        assert (numBits > 0 && numBits <= 32) : numBits;
        PackedInts.Encoder encoder = this.encoders[numBits];
        int iters = this.iterations[numBits];
        assert (iters * encoder.byteValueCount() >= 128);
        int encodedSize = this.encodedSizes[numBits];
        assert (iters * encoder.byteBlockCount() >= encodedSize);
        out.writeByte((byte)numBits);
        encoder.encode(data, 0, encoded, 0, iters);
        out.writeBytes(encoded, encodedSize);
    }

    void readBlock(IndexInput in, byte[] encoded, int[] decoded) throws IOException {
        byte numBits = in.readByte();
        assert (numBits <= 32) : numBits;
        if (numBits == 0) {
            int value = in.readVInt();
            Arrays.fill(decoded, 0, 128, value);
            return;
        }
        int encodedSize = this.encodedSizes[numBits];
        in.readBytes(encoded, 0, encodedSize);
        PackedInts.Decoder decoder = this.decoders[numBits];
        int iters = this.iterations[numBits];
        assert (iters * decoder.byteValueCount() >= 128);
        decoder.decode(encoded, 0, decoded, 0, iters);
    }

    void skipBlock(IndexInput in) throws IOException {
        byte numBits = in.readByte();
        if (numBits == 0) {
            in.readVInt();
            return;
        }
        assert (numBits > 0 && numBits <= 32) : numBits;
        int encodedSize = this.encodedSizes[numBits];
        in.seek(in.getFilePointer() + (long)encodedSize);
    }

    private static boolean isAllEqual(int[] data) {
        int v = data[0];
        for (int i = 1; i < 128; ++i) {
            if (data[i] == v) continue;
            return false;
        }
        return true;
    }

    private static int bitsRequired(int[] data) {
        long or = 0L;
        for (int i = 0; i < 128; ++i) {
            assert (data[i] >= 0);
            or |= (long)data[i];
        }
        return PackedInts.bitsRequired(or);
    }

    static {
        int maxDataSize = 0;
        for (int version = 0; version <= 1; ++version) {
            for (PackedInts.Format format : PackedInts.Format.values()) {
                for (int bpv = 1; bpv <= 32; ++bpv) {
                    if (!format.isSupported(bpv)) continue;
                    PackedInts.Decoder decoder = PackedInts.getDecoder(format, version, bpv);
                    int iterations = ForUtil.computeIterations(decoder);
                    maxDataSize = Math.max(maxDataSize, iterations * decoder.byteValueCount());
                }
            }
        }
        MAX_DATA_SIZE = maxDataSize;
    }
}


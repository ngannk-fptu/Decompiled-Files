/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.compressing;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.apache.lucene.codecs.compressing.Compressor;
import org.apache.lucene.codecs.compressing.Decompressor;
import org.apache.lucene.codecs.compressing.LZ4;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.BytesRef;

public abstract class CompressionMode {
    public static final CompressionMode FAST = new CompressionMode(){

        @Override
        public Compressor newCompressor() {
            return new LZ4FastCompressor();
        }

        @Override
        public Decompressor newDecompressor() {
            return LZ4_DECOMPRESSOR;
        }

        public String toString() {
            return "FAST";
        }
    };
    public static final CompressionMode HIGH_COMPRESSION = new CompressionMode(){

        @Override
        public Compressor newCompressor() {
            return new DeflateCompressor(9);
        }

        @Override
        public Decompressor newDecompressor() {
            return new DeflateDecompressor();
        }

        public String toString() {
            return "HIGH_COMPRESSION";
        }
    };
    public static final CompressionMode FAST_DECOMPRESSION = new CompressionMode(){

        @Override
        public Compressor newCompressor() {
            return new LZ4HighCompressor();
        }

        @Override
        public Decompressor newDecompressor() {
            return LZ4_DECOMPRESSOR;
        }

        public String toString() {
            return "FAST_DECOMPRESSION";
        }
    };
    private static final Decompressor LZ4_DECOMPRESSOR = new Decompressor(){

        @Override
        public void decompress(DataInput in, int originalLength, int offset, int length, BytesRef bytes) throws IOException {
            int decompressedLength;
            assert (offset + length <= originalLength);
            if (bytes.bytes.length < originalLength + 7) {
                bytes.bytes = new byte[ArrayUtil.oversize(originalLength + 7, 1)];
            }
            if ((decompressedLength = LZ4.decompress(in, offset + length, bytes.bytes, 0)) > originalLength) {
                throw new CorruptIndexException("Corrupted: lengths mismatch: " + decompressedLength + " > " + originalLength + " (resource=" + in + ")");
            }
            bytes.offset = offset;
            bytes.length = length;
        }

        @Override
        public Decompressor clone() {
            return this;
        }
    };

    protected CompressionMode() {
    }

    public abstract Compressor newCompressor();

    public abstract Decompressor newDecompressor();

    private static class DeflateCompressor
    extends Compressor {
        final Deflater compressor;
        byte[] compressed;

        DeflateCompressor(int level) {
            this.compressor = new Deflater(level);
            this.compressed = new byte[64];
        }

        @Override
        public void compress(byte[] bytes, int off, int len, DataOutput out) throws IOException {
            this.compressor.reset();
            this.compressor.setInput(bytes, off, len);
            this.compressor.finish();
            if (this.compressor.needsInput()) {
                assert (len == 0) : len;
                out.writeVInt(0);
                return;
            }
            int totalCount = 0;
            while (true) {
                int count = this.compressor.deflate(this.compressed, totalCount, this.compressed.length - totalCount);
                assert ((totalCount += count) <= this.compressed.length);
                if (this.compressor.finished()) break;
                this.compressed = ArrayUtil.grow(this.compressed);
            }
            out.writeVInt(totalCount);
            out.writeBytes(this.compressed, totalCount);
        }
    }

    private static final class DeflateDecompressor
    extends Decompressor {
        final Inflater decompressor = new Inflater();
        byte[] compressed = new byte[0];

        DeflateDecompressor() {
        }

        @Override
        public void decompress(DataInput in, int originalLength, int offset, int length, BytesRef bytes) throws IOException {
            assert (offset + length <= originalLength);
            if (length == 0) {
                bytes.length = 0;
                return;
            }
            int compressedLength = in.readVInt();
            if (compressedLength > this.compressed.length) {
                this.compressed = new byte[ArrayUtil.oversize(compressedLength, 1)];
            }
            in.readBytes(this.compressed, 0, compressedLength);
            this.decompressor.reset();
            this.decompressor.setInput(this.compressed, 0, compressedLength);
            bytes.length = 0;
            bytes.offset = 0;
            while (true) {
                int count;
                try {
                    int remaining = bytes.bytes.length - bytes.length;
                    count = this.decompressor.inflate(bytes.bytes, bytes.length, remaining);
                }
                catch (DataFormatException e) {
                    throw new IOException(e);
                }
                bytes.length += count;
                if (this.decompressor.finished()) break;
                bytes.bytes = ArrayUtil.grow(bytes.bytes);
            }
            if (bytes.length != originalLength) {
                throw new CorruptIndexException("Lengths mismatch: " + bytes.length + " != " + originalLength + " (resource=" + in + ")");
            }
            bytes.offset = offset;
            bytes.length = length;
        }

        @Override
        public Decompressor clone() {
            return new DeflateDecompressor();
        }
    }

    private static final class LZ4HighCompressor
    extends Compressor {
        private final LZ4.HCHashTable ht = new LZ4.HCHashTable();

        LZ4HighCompressor() {
        }

        @Override
        public void compress(byte[] bytes, int off, int len, DataOutput out) throws IOException {
            LZ4.compressHC(bytes, off, len, out, this.ht);
        }
    }

    private static final class LZ4FastCompressor
    extends Compressor {
        private final LZ4.HashTable ht = new LZ4.HashTable();

        LZ4FastCompressor() {
        }

        @Override
        public void compress(byte[] bytes, int off, int len, DataOutput out) throws IOException {
            LZ4.compress(bytes, off, len, out, this.ht);
        }
    }
}


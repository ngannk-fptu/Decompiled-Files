/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.compressing;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.packed.PackedInts;

public final class CompressingStoredFieldsIndexReader
implements Cloneable {
    final int maxDoc;
    final int[] docBases;
    final long[] startPointers;
    final int[] avgChunkDocs;
    final long[] avgChunkSizes;
    final PackedInts.Reader[] docBasesDeltas;
    final PackedInts.Reader[] startPointersDeltas;

    static long moveLowOrderBitToSign(long n) {
        return n >>> 1 ^ -(n & 1L);
    }

    CompressingStoredFieldsIndexReader(IndexInput fieldsIndexIn, SegmentInfo si) throws IOException {
        int numChunks;
        this.maxDoc = si.getDocCount();
        int[] docBases = new int[16];
        long[] startPointers = new long[16];
        int[] avgChunkDocs = new int[16];
        long[] avgChunkSizes = new long[16];
        PackedInts.Reader[] docBasesDeltas = new PackedInts.Reader[16];
        PackedInts.Reader[] startPointersDeltas = new PackedInts.Reader[16];
        int packedIntsVersion = fieldsIndexIn.readVInt();
        int blockCount = 0;
        while ((numChunks = fieldsIndexIn.readVInt()) != 0) {
            if (blockCount == docBases.length) {
                int newSize = ArrayUtil.oversize(blockCount + 1, 8);
                docBases = Arrays.copyOf(docBases, newSize);
                startPointers = Arrays.copyOf(startPointers, newSize);
                avgChunkDocs = Arrays.copyOf(avgChunkDocs, newSize);
                avgChunkSizes = Arrays.copyOf(avgChunkSizes, newSize);
                docBasesDeltas = Arrays.copyOf(docBasesDeltas, newSize);
                startPointersDeltas = Arrays.copyOf(startPointersDeltas, newSize);
            }
            docBases[blockCount] = fieldsIndexIn.readVInt();
            avgChunkDocs[blockCount] = fieldsIndexIn.readVInt();
            int bitsPerDocBase = fieldsIndexIn.readVInt();
            if (bitsPerDocBase > 32) {
                throw new CorruptIndexException("Corrupted bitsPerDocBase (resource=" + fieldsIndexIn + ")");
            }
            docBasesDeltas[blockCount] = PackedInts.getReaderNoHeader(fieldsIndexIn, PackedInts.Format.PACKED, packedIntsVersion, numChunks, bitsPerDocBase);
            startPointers[blockCount] = fieldsIndexIn.readVLong();
            avgChunkSizes[blockCount] = fieldsIndexIn.readVLong();
            int bitsPerStartPointer = fieldsIndexIn.readVInt();
            if (bitsPerStartPointer > 64) {
                throw new CorruptIndexException("Corrupted bitsPerStartPointer (resource=" + fieldsIndexIn + ")");
            }
            startPointersDeltas[blockCount] = PackedInts.getReaderNoHeader(fieldsIndexIn, PackedInts.Format.PACKED, packedIntsVersion, numChunks, bitsPerStartPointer);
            ++blockCount;
        }
        this.docBases = Arrays.copyOf(docBases, blockCount);
        this.startPointers = Arrays.copyOf(startPointers, blockCount);
        this.avgChunkDocs = Arrays.copyOf(avgChunkDocs, blockCount);
        this.avgChunkSizes = Arrays.copyOf(avgChunkSizes, blockCount);
        this.docBasesDeltas = Arrays.copyOf(docBasesDeltas, blockCount);
        this.startPointersDeltas = Arrays.copyOf(startPointersDeltas, blockCount);
    }

    private int block(int docID) {
        int lo = 0;
        int hi = this.docBases.length - 1;
        while (lo <= hi) {
            int mid = lo + hi >>> 1;
            int midValue = this.docBases[mid];
            if (midValue == docID) {
                return mid;
            }
            if (midValue < docID) {
                lo = mid + 1;
                continue;
            }
            hi = mid - 1;
        }
        return hi;
    }

    private int relativeDocBase(int block, int relativeChunk) {
        int expected = this.avgChunkDocs[block] * relativeChunk;
        long delta = CompressingStoredFieldsIndexReader.moveLowOrderBitToSign(this.docBasesDeltas[block].get(relativeChunk));
        return expected + (int)delta;
    }

    private long relativeStartPointer(int block, int relativeChunk) {
        long expected = this.avgChunkSizes[block] * (long)relativeChunk;
        long delta = CompressingStoredFieldsIndexReader.moveLowOrderBitToSign(this.startPointersDeltas[block].get(relativeChunk));
        return expected + delta;
    }

    private int relativeChunk(int block, int relativeDoc) {
        int lo = 0;
        int hi = this.docBasesDeltas[block].size() - 1;
        while (lo <= hi) {
            int mid = lo + hi >>> 1;
            int midValue = this.relativeDocBase(block, mid);
            if (midValue == relativeDoc) {
                return mid;
            }
            if (midValue < relativeDoc) {
                lo = mid + 1;
                continue;
            }
            hi = mid - 1;
        }
        return hi;
    }

    long getStartPointer(int docID) {
        if (docID < 0 || docID >= this.maxDoc) {
            throw new IllegalArgumentException("docID out of range [0-" + this.maxDoc + "]: " + docID);
        }
        int block = this.block(docID);
        int relativeChunk = this.relativeChunk(block, docID - this.docBases[block]);
        return this.startPointers[block] + this.relativeStartPointer(block, relativeChunk);
    }

    public CompressingStoredFieldsIndexReader clone() {
        return this;
    }
}


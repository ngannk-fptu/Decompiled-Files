/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.compressing;

import java.io.Closeable;
import java.io.IOException;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.util.packed.PackedInts;

public final class CompressingStoredFieldsIndexWriter
implements Closeable {
    static final int BLOCK_SIZE = 1024;
    final IndexOutput fieldsIndexOut;
    int totalDocs;
    int blockDocs;
    int blockChunks;
    long firstStartPointer;
    long maxStartPointer;
    final int[] docBaseDeltas;
    final long[] startPointerDeltas;

    static long moveSignToLowOrderBit(long n) {
        return n >> 63 ^ n << 1;
    }

    CompressingStoredFieldsIndexWriter(IndexOutput indexOutput) throws IOException {
        this.fieldsIndexOut = indexOutput;
        this.reset();
        this.totalDocs = 0;
        this.docBaseDeltas = new int[1024];
        this.startPointerDeltas = new long[1024];
        this.fieldsIndexOut.writeVInt(1);
    }

    private void reset() {
        this.blockChunks = 0;
        this.blockDocs = 0;
        this.firstStartPointer = -1L;
    }

    private void writeBlock() throws IOException {
        assert (this.blockChunks > 0);
        this.fieldsIndexOut.writeVInt(this.blockChunks);
        int avgChunkDocs = this.blockChunks == 1 ? 0 : Math.round((float)(this.blockDocs - this.docBaseDeltas[this.blockChunks - 1]) / (float)(this.blockChunks - 1));
        this.fieldsIndexOut.writeVInt(this.totalDocs - this.blockDocs);
        this.fieldsIndexOut.writeVInt(avgChunkDocs);
        int docBase = 0;
        long maxDelta = 0L;
        for (int i = 0; i < this.blockChunks; ++i) {
            int delta = docBase - avgChunkDocs * i;
            maxDelta |= CompressingStoredFieldsIndexWriter.moveSignToLowOrderBit(delta);
            docBase += this.docBaseDeltas[i];
        }
        int bitsPerDocBase = PackedInts.bitsRequired(maxDelta);
        this.fieldsIndexOut.writeVInt(bitsPerDocBase);
        PackedInts.Writer writer = PackedInts.getWriterNoHeader(this.fieldsIndexOut, PackedInts.Format.PACKED, this.blockChunks, bitsPerDocBase, 1);
        docBase = 0;
        for (int i = 0; i < this.blockChunks; ++i) {
            long delta = docBase - avgChunkDocs * i;
            assert (PackedInts.bitsRequired(CompressingStoredFieldsIndexWriter.moveSignToLowOrderBit(delta)) <= writer.bitsPerValue());
            writer.add(CompressingStoredFieldsIndexWriter.moveSignToLowOrderBit(delta));
            docBase += this.docBaseDeltas[i];
        }
        writer.finish();
        this.fieldsIndexOut.writeVLong(this.firstStartPointer);
        long avgChunkSize = this.blockChunks == 1 ? 0L : (this.maxStartPointer - this.firstStartPointer) / (long)(this.blockChunks - 1);
        this.fieldsIndexOut.writeVLong(avgChunkSize);
        long startPointer = 0L;
        maxDelta = 0L;
        for (int i = 0; i < this.blockChunks; ++i) {
            long delta = (startPointer += this.startPointerDeltas[i]) - avgChunkSize * (long)i;
            maxDelta |= CompressingStoredFieldsIndexWriter.moveSignToLowOrderBit(delta);
        }
        int bitsPerStartPointer = PackedInts.bitsRequired(maxDelta);
        this.fieldsIndexOut.writeVInt(bitsPerStartPointer);
        writer = PackedInts.getWriterNoHeader(this.fieldsIndexOut, PackedInts.Format.PACKED, this.blockChunks, bitsPerStartPointer, 1);
        startPointer = 0L;
        for (int i = 0; i < this.blockChunks; ++i) {
            long delta = (startPointer += this.startPointerDeltas[i]) - avgChunkSize * (long)i;
            assert (PackedInts.bitsRequired(CompressingStoredFieldsIndexWriter.moveSignToLowOrderBit(delta)) <= writer.bitsPerValue());
            writer.add(CompressingStoredFieldsIndexWriter.moveSignToLowOrderBit(delta));
        }
        writer.finish();
    }

    void writeIndex(int numDocs, long startPointer) throws IOException {
        if (this.blockChunks == 1024) {
            this.writeBlock();
            this.reset();
        }
        if (this.firstStartPointer == -1L) {
            this.firstStartPointer = this.maxStartPointer = startPointer;
        }
        assert (this.firstStartPointer > 0L && startPointer >= this.firstStartPointer);
        this.docBaseDeltas[this.blockChunks] = numDocs;
        this.startPointerDeltas[this.blockChunks] = startPointer - this.maxStartPointer;
        ++this.blockChunks;
        this.blockDocs += numDocs;
        this.totalDocs += numDocs;
        this.maxStartPointer = startPointer;
    }

    void finish(int numDocs) throws IOException {
        if (numDocs != this.totalDocs) {
            throw new IllegalStateException("Expected " + numDocs + " docs, but got " + this.totalDocs);
        }
        if (this.blockChunks > 0) {
            this.writeBlock();
        }
        this.fieldsIndexOut.writeVInt(0);
    }

    @Override
    public void close() throws IOException {
        this.fieldsIndexOut.close();
    }
}


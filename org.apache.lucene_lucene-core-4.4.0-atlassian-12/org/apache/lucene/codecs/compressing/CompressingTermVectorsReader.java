/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.compressing;

import java.io.Closeable;
import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.codecs.TermVectorsReader;
import org.apache.lucene.codecs.compressing.CompressingStoredFieldsIndexReader;
import org.apache.lucene.codecs.compressing.CompressingTermVectorsWriter;
import org.apache.lucene.codecs.compressing.CompressionMode;
import org.apache.lucene.codecs.compressing.Decompressor;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.ByteArrayDataInput;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.LongsRef;
import org.apache.lucene.util.packed.BlockPackedReaderIterator;
import org.apache.lucene.util.packed.PackedInts;

public final class CompressingTermVectorsReader
extends TermVectorsReader
implements Closeable {
    private final FieldInfos fieldInfos;
    final CompressingStoredFieldsIndexReader indexReader;
    final IndexInput vectorsStream;
    private final int packedIntsVersion;
    private final CompressionMode compressionMode;
    private final Decompressor decompressor;
    private final int chunkSize;
    private final int numDocs;
    private boolean closed;
    private final BlockPackedReaderIterator reader;

    private CompressingTermVectorsReader(CompressingTermVectorsReader reader) {
        this.fieldInfos = reader.fieldInfos;
        this.vectorsStream = reader.vectorsStream.clone();
        this.indexReader = reader.indexReader.clone();
        this.packedIntsVersion = reader.packedIntsVersion;
        this.compressionMode = reader.compressionMode;
        this.decompressor = reader.decompressor.clone();
        this.chunkSize = reader.chunkSize;
        this.numDocs = reader.numDocs;
        this.reader = new BlockPackedReaderIterator(this.vectorsStream, this.packedIntsVersion, 64, 0L);
        this.closed = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public CompressingTermVectorsReader(Directory d, SegmentInfo si, String segmentSuffix, FieldInfos fn, IOContext context, String formatName, CompressionMode compressionMode) throws IOException {
        this.compressionMode = compressionMode;
        String segment = si.name;
        boolean success = false;
        this.fieldInfos = fn;
        this.numDocs = si.getDocCount();
        IndexInput indexStream = null;
        try {
            String indexStreamFN = IndexFileNames.segmentFileName(segment, segmentSuffix, "tvx");
            indexStream = d.openInput(indexStreamFN, context);
            String codecNameIdx = formatName + "Index";
            CodecUtil.checkHeader(indexStream, codecNameIdx, 0, 0);
            assert ((long)CodecUtil.headerLength(codecNameIdx) == indexStream.getFilePointer());
            this.indexReader = new CompressingStoredFieldsIndexReader(indexStream, si);
            indexStream.close();
            indexStream = null;
            String vectorsStreamFN = IndexFileNames.segmentFileName(segment, segmentSuffix, "tvd");
            this.vectorsStream = d.openInput(vectorsStreamFN, context);
            String codecNameDat = formatName + "Data";
            CodecUtil.checkHeader(this.vectorsStream, codecNameDat, 0, 0);
            assert ((long)CodecUtil.headerLength(codecNameDat) == this.vectorsStream.getFilePointer());
            this.packedIntsVersion = this.vectorsStream.readVInt();
            this.chunkSize = this.vectorsStream.readVInt();
            this.decompressor = compressionMode.newDecompressor();
            this.reader = new BlockPackedReaderIterator(this.vectorsStream, this.packedIntsVersion, 64, 0L);
            success = true;
            if (success) return;
        }
        catch (Throwable throwable) {
            if (success) throw throwable;
            IOUtils.closeWhileHandlingException(this, indexStream);
            throw throwable;
        }
        IOUtils.closeWhileHandlingException(this, indexStream);
    }

    CompressionMode getCompressionMode() {
        return this.compressionMode;
    }

    int getChunkSize() {
        return this.chunkSize;
    }

    int getPackedIntsVersion() {
        return this.packedIntsVersion;
    }

    CompressingStoredFieldsIndexReader getIndex() {
        return this.indexReader;
    }

    IndexInput getVectorsStream() {
        return this.vectorsStream;
    }

    private void ensureOpen() throws AlreadyClosedException {
        if (this.closed) {
            throw new AlreadyClosedException("this FieldsReader is closed");
        }
    }

    @Override
    public void close() throws IOException {
        if (!this.closed) {
            IOUtils.close(this.vectorsStream);
            this.closed = true;
        }
    }

    @Override
    public TermVectorsReader clone() {
        return new CompressingTermVectorsReader(this);
    }

    @Override
    public Fields get(int doc) throws IOException {
        int i;
        int k;
        Object lengths;
        Object startOffsets;
        int j;
        int k2;
        LongsRef next;
        int j2;
        int termCount;
        int i2;
        PackedInts.Reader flags;
        int i3;
        int numFields;
        int totalFields;
        int skip;
        this.ensureOpen();
        long startPointer = this.indexReader.getStartPointer(doc);
        this.vectorsStream.seek(startPointer);
        int docBase = this.vectorsStream.readVInt();
        int chunkDocs = this.vectorsStream.readVInt();
        if (doc < docBase || doc >= docBase + chunkDocs || docBase + chunkDocs > this.numDocs) {
            throw new CorruptIndexException("docBase=" + docBase + ",chunkDocs=" + chunkDocs + ",doc=" + doc + " (resource=" + this.vectorsStream + ")");
        }
        if (chunkDocs == 1) {
            skip = 0;
            numFields = totalFields = this.vectorsStream.readVInt();
        } else {
            int i4;
            this.reader.reset(this.vectorsStream, chunkDocs);
            int sum = 0;
            for (i4 = docBase; i4 < doc; ++i4) {
                sum = (int)((long)sum + this.reader.next());
            }
            skip = sum;
            numFields = (int)this.reader.next();
            sum += numFields;
            for (i4 = doc + 1; i4 < docBase + chunkDocs; ++i4) {
                sum = (int)((long)sum + this.reader.next());
            }
            totalFields = sum;
        }
        if (numFields == 0) {
            return null;
        }
        int token = this.vectorsStream.readByte() & 0xFF;
        assert (token != 0);
        int bitsPerFieldNum = token & 0x1F;
        int totalDistinctFields = token >>> 5;
        if (totalDistinctFields == 7) {
            totalDistinctFields += this.vectorsStream.readVInt();
        }
        PackedInts.ReaderIterator it = PackedInts.getReaderIteratorNoHeader(this.vectorsStream, PackedInts.Format.PACKED, this.packedIntsVersion, ++totalDistinctFields, bitsPerFieldNum, 1);
        int[] fieldNums = new int[totalDistinctFields];
        for (int i5 = 0; i5 < totalDistinctFields; ++i5) {
            fieldNums[i5] = (int)it.next();
        }
        int[] fieldNumOffs = new int[numFields];
        int bitsPerOff = PackedInts.bitsRequired(fieldNums.length - 1);
        PackedInts.Reader allFieldNumOffs = PackedInts.getReaderNoHeader(this.vectorsStream, PackedInts.Format.PACKED, this.packedIntsVersion, totalFields, bitsPerOff);
        switch (this.vectorsStream.readVInt()) {
            case 0: {
                PackedInts.Reader fieldFlags = PackedInts.getReaderNoHeader(this.vectorsStream, PackedInts.Format.PACKED, this.packedIntsVersion, fieldNums.length, CompressingTermVectorsWriter.FLAGS_BITS);
                PackedInts.Mutable f = PackedInts.getMutable(totalFields, CompressingTermVectorsWriter.FLAGS_BITS, 0.0f);
                for (i3 = 0; i3 < totalFields; ++i3) {
                    int fieldNumOff = (int)allFieldNumOffs.get(i3);
                    assert (fieldNumOff >= 0 && fieldNumOff < fieldNums.length);
                    int fgs = (int)fieldFlags.get(fieldNumOff);
                    f.set(i3, fgs);
                }
                flags = f;
                break;
            }
            case 1: {
                flags = PackedInts.getReaderNoHeader(this.vectorsStream, PackedInts.Format.PACKED, this.packedIntsVersion, totalFields, CompressingTermVectorsWriter.FLAGS_BITS);
                break;
            }
            default: {
                throw new AssertionError();
            }
        }
        for (int i6 = 0; i6 < numFields; ++i6) {
            fieldNumOffs[i6] = (int)allFieldNumOffs.get(skip + i6);
        }
        int bitsRequired = this.vectorsStream.readVInt();
        PackedInts.Reader numTerms = PackedInts.getReaderNoHeader(this.vectorsStream, PackedInts.Format.PACKED, this.packedIntsVersion, totalFields, bitsRequired);
        int sum = 0;
        for (i3 = 0; i3 < totalFields; ++i3) {
            sum = (int)((long)sum + numTerms.get(i3));
        }
        int totalTerms = sum;
        int docOff = 0;
        int docLen = 0;
        int[] fieldLengths = new int[numFields];
        int[][] prefixLengths = new int[numFields][];
        int[][] suffixLengths = new int[numFields][];
        this.reader.reset(this.vectorsStream, totalTerms);
        int toSkip = 0;
        for (i2 = 0; i2 < skip; ++i2) {
            toSkip = (int)((long)toSkip + numTerms.get(i2));
        }
        this.reader.skip(toSkip);
        for (i2 = 0; i2 < numFields; ++i2) {
            termCount = (int)numTerms.get(skip + i2);
            int[] fieldPrefixLengths = new int[termCount];
            prefixLengths[i2] = fieldPrefixLengths;
            j2 = 0;
            while (j2 < termCount) {
                next = this.reader.next(termCount - j2);
                for (k2 = 0; k2 < next.length; ++k2) {
                    fieldPrefixLengths[j2++] = (int)next.longs[next.offset + k2];
                }
            }
        }
        this.reader.skip((long)totalTerms - this.reader.ord());
        this.reader.reset(this.vectorsStream, totalTerms);
        toSkip = 0;
        for (i2 = 0; i2 < skip; ++i2) {
            j = 0;
            while ((long)j < numTerms.get(i2)) {
                docOff = (int)((long)docOff + this.reader.next());
                ++j;
            }
        }
        for (i2 = 0; i2 < numFields; ++i2) {
            termCount = (int)numTerms.get(skip + i2);
            int[] fieldSuffixLengths = new int[termCount];
            suffixLengths[i2] = fieldSuffixLengths;
            j2 = 0;
            while (j2 < termCount) {
                next = this.reader.next(termCount - j2);
                for (k2 = 0; k2 < next.length; ++k2) {
                    fieldSuffixLengths[j2++] = (int)next.longs[next.offset + k2];
                }
            }
            fieldLengths[i2] = CompressingTermVectorsReader.sum(suffixLengths[i2]);
            docLen += fieldLengths[i2];
        }
        int totalLen = docOff + docLen;
        for (i2 = skip + numFields; i2 < totalFields; ++i2) {
            j = 0;
            while ((long)j < numTerms.get(i2)) {
                totalLen = (int)((long)totalLen + this.reader.next());
                ++j;
            }
        }
        int[] termFreqs = new int[totalTerms];
        this.reader.reset(this.vectorsStream, totalTerms);
        i2 = 0;
        while (i2 < totalTerms) {
            LongsRef next2 = this.reader.next(totalTerms - i2);
            for (int k3 = 0; k3 < next2.length; ++k3) {
                termFreqs[i2++] = 1 + (int)next2.longs[next2.offset + k3];
            }
        }
        int totalPositions = 0;
        int totalOffsets = 0;
        int totalPayloads = 0;
        int termIndex = 0;
        for (int i7 = 0; i7 < totalFields; ++i7) {
            int f = (int)flags.get(i7);
            int termCount2 = (int)numTerms.get(i7);
            for (int j3 = 0; j3 < termCount2; ++j3) {
                int freq = termFreqs[termIndex++];
                if ((f & 1) != 0) {
                    totalPositions += freq;
                }
                if ((f & 2) != 0) {
                    totalOffsets += freq;
                }
                if ((f & 4) == 0) continue;
                totalPayloads += freq;
            }
            assert (i7 != totalFields - 1 || termIndex == totalTerms) : termIndex + " " + totalTerms;
        }
        int[][] positionIndex = this.positionIndex(skip, numFields, numTerms, termFreqs);
        Object positions = totalPositions > 0 ? (Object)this.readPositions(skip, numFields, flags, numTerms, termFreqs, 1, totalPositions, positionIndex) : new int[numFields][];
        if (totalOffsets > 0) {
            int i8;
            float[] charsPerTerm = new float[fieldNums.length];
            for (i8 = 0; i8 < charsPerTerm.length; ++i8) {
                charsPerTerm[i8] = Float.intBitsToFloat(this.vectorsStream.readInt());
            }
            startOffsets = this.readPositions(skip, numFields, flags, numTerms, termFreqs, 2, totalOffsets, positionIndex);
            lengths = this.readPositions(skip, numFields, flags, numTerms, termFreqs, 2, totalOffsets, positionIndex);
            for (i8 = 0; i8 < numFields; ++i8) {
                int[] fStartOffsets = startOffsets[i8];
                int[] fPositions = positions[i8];
                if (fStartOffsets != null && fPositions != null) {
                    float fieldCharsPerTerm = charsPerTerm[fieldNumOffs[i8]];
                    for (int j4 = 0; j4 < startOffsets[i8].length; ++j4) {
                        int n = j4;
                        fStartOffsets[n] = fStartOffsets[n] + (int)(fieldCharsPerTerm * (float)fPositions[j4]);
                    }
                }
                if (fStartOffsets == null) continue;
                int[] fPrefixLengths = prefixLengths[i8];
                int[] fSuffixLengths = suffixLengths[i8];
                int[] fLengths = lengths[i8];
                int end = (int)numTerms.get(skip + i8);
                for (int j5 = 0; j5 < end; ++j5) {
                    int termLength = fPrefixLengths[j5] + fSuffixLengths[j5];
                    int[] nArray = lengths[i8];
                    int n = positionIndex[i8][j5];
                    nArray[n] = nArray[n] + termLength;
                    k = positionIndex[i8][j5] + 1;
                    while (k < positionIndex[i8][j5 + 1]) {
                        int n2 = k;
                        fStartOffsets[n2] = fStartOffsets[n2] + fStartOffsets[k - 1];
                        int n3 = k++;
                        fLengths[n3] = fLengths[n3] + termLength;
                    }
                }
            }
        } else {
            startOffsets = lengths = new int[numFields][];
        }
        if (totalPositions > 0) {
            for (int i9 = 0; i9 < numFields; ++i9) {
                int[] fPositions = positions[i9];
                int[] fpositionIndex = positionIndex[i9];
                if (fPositions == null) continue;
                int end = (int)numTerms.get(skip + i9);
                for (int j6 = 0; j6 < end; ++j6) {
                    for (int k4 = fpositionIndex[j6] + 1; k4 < fpositionIndex[j6 + 1]; ++k4) {
                        int n = k4;
                        fPositions[n] = fPositions[n] + fPositions[k4 - 1];
                    }
                }
            }
        }
        int[][] payloadIndex = new int[numFields][];
        int totalPayloadLength = 0;
        int payloadOff = 0;
        int payloadLen = 0;
        if (totalPayloads > 0) {
            int freq;
            int j7;
            int termCount3;
            int i10;
            this.reader.reset(this.vectorsStream, totalPayloads);
            int termIndex2 = 0;
            for (i10 = 0; i10 < skip; ++i10) {
                int f = (int)flags.get(i10);
                termCount3 = (int)numTerms.get(i10);
                if ((f & 4) != 0) {
                    for (j7 = 0; j7 < termCount3; ++j7) {
                        freq = termFreqs[termIndex2 + j7];
                        for (k = 0; k < freq; ++k) {
                            int l = (int)this.reader.next();
                            payloadOff += l;
                        }
                    }
                }
                termIndex2 += termCount3;
            }
            totalPayloadLength = payloadOff;
            for (i10 = 0; i10 < numFields; ++i10) {
                int f = (int)flags.get(skip + i10);
                termCount3 = (int)numTerms.get(skip + i10);
                if ((f & 4) != 0) {
                    int totalFreq = positionIndex[i10][termCount3];
                    payloadIndex[i10] = new int[totalFreq + 1];
                    int posIdx = 0;
                    payloadIndex[i10][posIdx] = payloadLen;
                    for (int j8 = 0; j8 < termCount3; ++j8) {
                        int freq2 = termFreqs[termIndex2 + j8];
                        for (int k5 = 0; k5 < freq2; ++k5) {
                            int payloadLength = (int)this.reader.next();
                            payloadIndex[i10][posIdx + 1] = payloadLen += payloadLength;
                            ++posIdx;
                        }
                    }
                    assert (posIdx == totalFreq);
                }
                termIndex2 += termCount3;
            }
            totalPayloadLength += payloadLen;
            for (i10 = skip + numFields; i10 < totalFields; ++i10) {
                int f = (int)flags.get(i10);
                termCount3 = (int)numTerms.get(i10);
                if ((f & 4) != 0) {
                    for (j7 = 0; j7 < termCount3; ++j7) {
                        freq = termFreqs[termIndex2 + j7];
                        for (k = 0; k < freq; ++k) {
                            totalPayloadLength = (int)((long)totalPayloadLength + this.reader.next());
                        }
                    }
                }
                termIndex2 += termCount3;
            }
            assert (termIndex2 == totalTerms) : termIndex2 + " " + totalTerms;
        }
        BytesRef suffixBytes = new BytesRef();
        this.decompressor.decompress(this.vectorsStream, totalLen + totalPayloadLength, docOff + payloadOff, docLen + payloadLen, suffixBytes);
        suffixBytes.length = docLen;
        BytesRef payloadBytes = new BytesRef(suffixBytes.bytes, suffixBytes.offset + docLen, payloadLen);
        int[] fieldFlags = new int[numFields];
        for (int i11 = 0; i11 < numFields; ++i11) {
            fieldFlags[i11] = (int)flags.get(skip + i11);
        }
        int[] fieldNumTerms = new int[numFields];
        for (int i12 = 0; i12 < numFields; ++i12) {
            fieldNumTerms[i12] = (int)numTerms.get(skip + i12);
        }
        int[][] fieldTermFreqs = new int[numFields][];
        int termIdx = 0;
        for (i = 0; i < skip; ++i) {
            termIdx = (int)((long)termIdx + numTerms.get(i));
        }
        for (i = 0; i < numFields; ++i) {
            int termCount4 = (int)numTerms.get(skip + i);
            fieldTermFreqs[i] = new int[termCount4];
            for (int j9 = 0; j9 < termCount4; ++j9) {
                fieldTermFreqs[i][j9] = termFreqs[termIdx++];
            }
        }
        assert (CompressingTermVectorsReader.sum(fieldLengths) == docLen) : CompressingTermVectorsReader.sum(fieldLengths) + " != " + docLen;
        return new TVFields(fieldNums, fieldFlags, fieldNumOffs, fieldNumTerms, fieldLengths, prefixLengths, suffixLengths, fieldTermFreqs, positionIndex, (int[][])positions, (int[][])startOffsets, (int[][])lengths, payloadBytes, payloadIndex, suffixBytes);
    }

    private int[][] positionIndex(int skip, int numFields, PackedInts.Reader numTerms, int[] termFreqs) {
        int termCount;
        int i;
        int[][] positionIndex = new int[numFields][];
        int termIndex = 0;
        for (i = 0; i < skip; ++i) {
            termCount = (int)numTerms.get(i);
            termIndex += termCount;
        }
        for (i = 0; i < numFields; ++i) {
            termCount = (int)numTerms.get(skip + i);
            positionIndex[i] = new int[termCount + 1];
            for (int j = 0; j < termCount; ++j) {
                int freq = termFreqs[termIndex + j];
                positionIndex[i][j + 1] = positionIndex[i][j] + freq;
            }
            termIndex += termCount;
        }
        return positionIndex;
    }

    private int[][] readPositions(int skip, int numFields, PackedInts.Reader flags, PackedInts.Reader numTerms, int[] termFreqs, int flag, int totalPositions, int[][] positionIndex) throws IOException {
        int termCount;
        int f;
        int i;
        int[][] positions = new int[numFields][];
        this.reader.reset(this.vectorsStream, totalPositions);
        int toSkip = 0;
        int termIndex = 0;
        for (i = 0; i < skip; ++i) {
            f = (int)flags.get(i);
            termCount = (int)numTerms.get(i);
            if ((f & flag) != 0) {
                for (int j = 0; j < termCount; ++j) {
                    int freq = termFreqs[termIndex + j];
                    toSkip += freq;
                }
            }
            termIndex += termCount;
        }
        this.reader.skip(toSkip);
        for (i = 0; i < numFields; ++i) {
            f = (int)flags.get(skip + i);
            termCount = (int)numTerms.get(skip + i);
            if ((f & flag) != 0) {
                int totalFreq = positionIndex[i][termCount];
                int[] fieldPositions = new int[totalFreq];
                positions[i] = fieldPositions;
                int j = 0;
                while (j < totalFreq) {
                    LongsRef nextPositions = this.reader.next(totalFreq - j);
                    for (int k = 0; k < nextPositions.length; ++k) {
                        fieldPositions[j++] = (int)nextPositions.longs[nextPositions.offset + k];
                    }
                }
            }
            termIndex += termCount;
        }
        this.reader.skip((long)totalPositions - this.reader.ord());
        return positions;
    }

    private static int sum(int[] arr) {
        int sum = 0;
        for (int el : arr) {
            sum += el;
        }
        return sum;
    }

    private static class TVDocsEnum
    extends DocsAndPositionsEnum {
        private Bits liveDocs;
        private int doc = -1;
        private int termFreq;
        private int positionIndex;
        private int[] positions;
        private int[] startOffsets;
        private int[] lengths;
        private final BytesRef payload = new BytesRef();
        private int[] payloadIndex;
        private int basePayloadOffset;
        private int i;

        TVDocsEnum() {
        }

        public void reset(Bits liveDocs, int freq, int positionIndex, int[] positions, int[] startOffsets, int[] lengths, BytesRef payloads, int[] payloadIndex) {
            this.liveDocs = liveDocs;
            this.termFreq = freq;
            this.positionIndex = positionIndex;
            this.positions = positions;
            this.startOffsets = startOffsets;
            this.lengths = lengths;
            this.basePayloadOffset = payloads.offset;
            this.payload.bytes = payloads.bytes;
            this.payload.length = 0;
            this.payload.offset = 0;
            this.payloadIndex = payloadIndex;
            this.i = -1;
            this.doc = -1;
        }

        private void checkDoc() {
            if (this.doc == Integer.MAX_VALUE) {
                throw new IllegalStateException("DocsEnum exhausted");
            }
            if (this.doc == -1) {
                throw new IllegalStateException("DocsEnum not started");
            }
        }

        private void checkPosition() {
            this.checkDoc();
            if (this.i < 0) {
                throw new IllegalStateException("Position enum not started");
            }
            if (this.i >= this.termFreq) {
                throw new IllegalStateException("Read past last position");
            }
        }

        @Override
        public int nextPosition() throws IOException {
            if (this.doc != 0) {
                throw new IllegalStateException();
            }
            if (this.i >= this.termFreq - 1) {
                throw new IllegalStateException("Read past last position");
            }
            ++this.i;
            if (this.payloadIndex != null) {
                this.payload.offset = this.basePayloadOffset + this.payloadIndex[this.positionIndex + this.i];
                this.payload.length = this.payloadIndex[this.positionIndex + this.i + 1] - this.payloadIndex[this.positionIndex + this.i];
            }
            if (this.positions == null) {
                return -1;
            }
            return this.positions[this.positionIndex + this.i];
        }

        @Override
        public int startOffset() throws IOException {
            this.checkPosition();
            if (this.startOffsets == null) {
                return -1;
            }
            return this.startOffsets[this.positionIndex + this.i];
        }

        @Override
        public int endOffset() throws IOException {
            this.checkPosition();
            if (this.startOffsets == null) {
                return -1;
            }
            return this.startOffsets[this.positionIndex + this.i] + this.lengths[this.positionIndex + this.i];
        }

        @Override
        public BytesRef getPayload() throws IOException {
            this.checkPosition();
            if (this.payloadIndex == null || this.payload.length == 0) {
                return null;
            }
            return this.payload;
        }

        @Override
        public int freq() throws IOException {
            this.checkDoc();
            return this.termFreq;
        }

        @Override
        public int docID() {
            return this.doc;
        }

        @Override
        public int nextDoc() throws IOException {
            if (this.doc == -1 && (this.liveDocs == null || this.liveDocs.get(0))) {
                this.doc = 0;
                return 0;
            }
            this.doc = Integer.MAX_VALUE;
            return Integer.MAX_VALUE;
        }

        @Override
        public int advance(int target) throws IOException {
            return this.slowAdvance(target);
        }

        @Override
        public long cost() {
            return 1L;
        }
    }

    private static class TVTermsEnum
    extends TermsEnum {
        private int numTerms;
        private int startPos;
        private int ord;
        private int[] prefixLengths;
        private int[] suffixLengths;
        private int[] termFreqs;
        private int[] positionIndex;
        private int[] positions;
        private int[] startOffsets;
        private int[] lengths;
        private int[] payloadIndex;
        private ByteArrayDataInput in;
        private BytesRef payloads;
        private final BytesRef term = new BytesRef(16);

        private TVTermsEnum() {
        }

        void reset(int numTerms, int flags, int[] prefixLengths, int[] suffixLengths, int[] termFreqs, int[] positionIndex, int[] positions, int[] startOffsets, int[] lengths, int[] payloadIndex, BytesRef payloads, ByteArrayDataInput in) {
            this.numTerms = numTerms;
            this.prefixLengths = prefixLengths;
            this.suffixLengths = suffixLengths;
            this.termFreqs = termFreqs;
            this.positionIndex = positionIndex;
            this.positions = positions;
            this.startOffsets = startOffsets;
            this.lengths = lengths;
            this.payloadIndex = payloadIndex;
            this.payloads = payloads;
            this.in = in;
            this.startPos = in.getPosition();
            this.reset();
        }

        void reset() {
            this.term.length = 0;
            this.in.setPosition(this.startPos);
            this.ord = -1;
        }

        @Override
        public BytesRef next() throws IOException {
            if (this.ord == this.numTerms - 1) {
                return null;
            }
            assert (this.ord < this.numTerms);
            ++this.ord;
            this.term.offset = 0;
            this.term.length = this.prefixLengths[this.ord] + this.suffixLengths[this.ord];
            if (this.term.length > this.term.bytes.length) {
                this.term.bytes = ArrayUtil.grow(this.term.bytes, this.term.length);
            }
            this.in.readBytes(this.term.bytes, this.prefixLengths[this.ord], this.suffixLengths[this.ord]);
            return this.term;
        }

        @Override
        public Comparator<BytesRef> getComparator() {
            return BytesRef.getUTF8SortedAsUnicodeComparator();
        }

        @Override
        public TermsEnum.SeekStatus seekCeil(BytesRef text, boolean useCache) throws IOException {
            int cmp;
            if (this.ord < this.numTerms && this.ord >= 0) {
                int cmp2 = this.term().compareTo(text);
                if (cmp2 == 0) {
                    return TermsEnum.SeekStatus.FOUND;
                }
                if (cmp2 > 0) {
                    this.reset();
                }
            }
            do {
                BytesRef term;
                if ((term = this.next()) == null) {
                    return TermsEnum.SeekStatus.END;
                }
                cmp = term.compareTo(text);
                if (cmp <= 0) continue;
                return TermsEnum.SeekStatus.NOT_FOUND;
            } while (cmp != 0);
            return TermsEnum.SeekStatus.FOUND;
        }

        @Override
        public void seekExact(long ord) throws IOException {
            if (ord < -1L || ord >= (long)this.numTerms) {
                throw new IOException("ord is out of range: ord=" + ord + ", numTerms=" + this.numTerms);
            }
            if (ord < (long)this.ord) {
                this.reset();
            }
            int i = this.ord;
            while ((long)i < ord) {
                this.next();
                ++i;
            }
            assert (ord == this.ord());
        }

        @Override
        public BytesRef term() throws IOException {
            return this.term;
        }

        @Override
        public long ord() throws IOException {
            return this.ord;
        }

        @Override
        public int docFreq() throws IOException {
            return 1;
        }

        @Override
        public long totalTermFreq() throws IOException {
            return this.termFreqs[this.ord];
        }

        @Override
        public final DocsEnum docs(Bits liveDocs, DocsEnum reuse, int flags) throws IOException {
            TVDocsEnum docsEnum = reuse != null && reuse instanceof TVDocsEnum ? (TVDocsEnum)reuse : new TVDocsEnum();
            docsEnum.reset(liveDocs, this.termFreqs[this.ord], this.positionIndex[this.ord], this.positions, this.startOffsets, this.lengths, this.payloads, this.payloadIndex);
            return docsEnum;
        }

        @Override
        public DocsAndPositionsEnum docsAndPositions(Bits liveDocs, DocsAndPositionsEnum reuse, int flags) throws IOException {
            if (this.positions == null && this.startOffsets == null) {
                return null;
            }
            return (DocsAndPositionsEnum)this.docs(liveDocs, reuse, flags);
        }
    }

    private class TVTerms
    extends Terms {
        private final int numTerms;
        private final int flags;
        private final int[] prefixLengths;
        private final int[] suffixLengths;
        private final int[] termFreqs;
        private final int[] positionIndex;
        private final int[] positions;
        private final int[] startOffsets;
        private final int[] lengths;
        private final int[] payloadIndex;
        private final BytesRef termBytes;
        private final BytesRef payloadBytes;

        TVTerms(int numTerms, int flags, int[] prefixLengths, int[] suffixLengths, int[] termFreqs, int[] positionIndex, int[] positions, int[] startOffsets, int[] lengths, int[] payloadIndex, BytesRef payloadBytes, BytesRef termBytes) {
            this.numTerms = numTerms;
            this.flags = flags;
            this.prefixLengths = prefixLengths;
            this.suffixLengths = suffixLengths;
            this.termFreqs = termFreqs;
            this.positionIndex = positionIndex;
            this.positions = positions;
            this.startOffsets = startOffsets;
            this.lengths = lengths;
            this.payloadIndex = payloadIndex;
            this.payloadBytes = payloadBytes;
            this.termBytes = termBytes;
        }

        @Override
        public TermsEnum iterator(TermsEnum reuse) throws IOException {
            TVTermsEnum termsEnum = reuse != null && reuse instanceof TVTermsEnum ? (TVTermsEnum)reuse : new TVTermsEnum();
            termsEnum.reset(this.numTerms, this.flags, this.prefixLengths, this.suffixLengths, this.termFreqs, this.positionIndex, this.positions, this.startOffsets, this.lengths, this.payloadIndex, this.payloadBytes, new ByteArrayDataInput(this.termBytes.bytes, this.termBytes.offset, this.termBytes.length));
            return termsEnum;
        }

        @Override
        public Comparator<BytesRef> getComparator() {
            return BytesRef.getUTF8SortedAsUnicodeComparator();
        }

        @Override
        public long size() throws IOException {
            return this.numTerms;
        }

        @Override
        public long getSumTotalTermFreq() throws IOException {
            return -1L;
        }

        @Override
        public long getSumDocFreq() throws IOException {
            return this.numTerms;
        }

        @Override
        public int getDocCount() throws IOException {
            return 1;
        }

        @Override
        public boolean hasOffsets() {
            return (this.flags & 2) != 0;
        }

        @Override
        public boolean hasPositions() {
            return (this.flags & 1) != 0;
        }

        @Override
        public boolean hasPayloads() {
            return (this.flags & 4) != 0;
        }
    }

    private class TVFields
    extends Fields {
        private final int[] fieldNums;
        private final int[] fieldFlags;
        private final int[] fieldNumOffs;
        private final int[] numTerms;
        private final int[] fieldLengths;
        private final int[][] prefixLengths;
        private final int[][] suffixLengths;
        private final int[][] termFreqs;
        private final int[][] positionIndex;
        private final int[][] positions;
        private final int[][] startOffsets;
        private final int[][] lengths;
        private final int[][] payloadIndex;
        private final BytesRef suffixBytes;
        private final BytesRef payloadBytes;

        public TVFields(int[] fieldNums, int[] fieldFlags, int[] fieldNumOffs, int[] numTerms, int[] fieldLengths, int[][] prefixLengths, int[][] suffixLengths, int[][] termFreqs, int[][] positionIndex, int[][] positions, int[][] startOffsets, int[][] lengths, BytesRef payloadBytes, int[][] payloadIndex, BytesRef suffixBytes) {
            this.fieldNums = fieldNums;
            this.fieldFlags = fieldFlags;
            this.fieldNumOffs = fieldNumOffs;
            this.numTerms = numTerms;
            this.fieldLengths = fieldLengths;
            this.prefixLengths = prefixLengths;
            this.suffixLengths = suffixLengths;
            this.termFreqs = termFreqs;
            this.positionIndex = positionIndex;
            this.positions = positions;
            this.startOffsets = startOffsets;
            this.lengths = lengths;
            this.payloadBytes = payloadBytes;
            this.payloadIndex = payloadIndex;
            this.suffixBytes = suffixBytes;
        }

        @Override
        public Iterator<String> iterator() {
            return new Iterator<String>(){
                int i = 0;

                @Override
                public boolean hasNext() {
                    return this.i < TVFields.this.fieldNumOffs.length;
                }

                @Override
                public String next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    int fieldNum = TVFields.this.fieldNums[TVFields.this.fieldNumOffs[this.i++]];
                    return ((CompressingTermVectorsReader)CompressingTermVectorsReader.this).fieldInfos.fieldInfo((int)fieldNum).name;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        @Override
        public Terms terms(String field) throws IOException {
            FieldInfo fieldInfo = CompressingTermVectorsReader.this.fieldInfos.fieldInfo(field);
            if (fieldInfo == null) {
                return null;
            }
            int idx = -1;
            for (int i = 0; i < this.fieldNumOffs.length; ++i) {
                if (this.fieldNums[this.fieldNumOffs[i]] != fieldInfo.number) continue;
                idx = i;
                break;
            }
            if (idx == -1 || this.numTerms[idx] == 0) {
                return null;
            }
            int fieldOff = 0;
            int fieldLen = -1;
            for (int i = 0; i < this.fieldNumOffs.length; ++i) {
                if (i < idx) {
                    fieldOff += this.fieldLengths[i];
                    continue;
                }
                fieldLen = this.fieldLengths[i];
                break;
            }
            assert (fieldLen >= 0);
            return new TVTerms(this.numTerms[idx], this.fieldFlags[idx], this.prefixLengths[idx], this.suffixLengths[idx], this.termFreqs[idx], this.positionIndex[idx], this.positions[idx], this.startOffsets[idx], this.lengths[idx], this.payloadIndex[idx], this.payloadBytes, new BytesRef(this.suffixBytes.bytes, this.suffixBytes.offset + fieldOff, fieldLen));
        }

        @Override
        public int size() {
            return this.fieldNumOffs.length;
        }
    }
}


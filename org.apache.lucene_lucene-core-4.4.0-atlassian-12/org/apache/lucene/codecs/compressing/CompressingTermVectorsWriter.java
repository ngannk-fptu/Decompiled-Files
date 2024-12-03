/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.compressing;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.TreeSet;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.codecs.TermVectorsReader;
import org.apache.lucene.codecs.TermVectorsWriter;
import org.apache.lucene.codecs.compressing.CompressingStoredFieldsIndexReader;
import org.apache.lucene.codecs.compressing.CompressingStoredFieldsIndexWriter;
import org.apache.lucene.codecs.compressing.CompressingTermVectorsReader;
import org.apache.lucene.codecs.compressing.CompressionMode;
import org.apache.lucene.codecs.compressing.Compressor;
import org.apache.lucene.codecs.compressing.GrowableByteArrayDataOutput;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.MergeState;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.index.SegmentReader;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.StringHelper;
import org.apache.lucene.util.packed.BlockPackedWriter;
import org.apache.lucene.util.packed.PackedInts;

public final class CompressingTermVectorsWriter
extends TermVectorsWriter {
    static final int MAX_DOCUMENTS_PER_CHUNK = 128;
    static final String VECTORS_EXTENSION = "tvd";
    static final String VECTORS_INDEX_EXTENSION = "tvx";
    static final String CODEC_SFX_IDX = "Index";
    static final String CODEC_SFX_DAT = "Data";
    static final int VERSION_START = 0;
    static final int VERSION_CURRENT = 0;
    static final int BLOCK_SIZE = 64;
    static final int POSITIONS = 1;
    static final int OFFSETS = 2;
    static final int PAYLOADS = 4;
    static final int FLAGS_BITS = PackedInts.bitsRequired(7L);
    private final Directory directory;
    private final String segment;
    private final String segmentSuffix;
    private CompressingStoredFieldsIndexWriter indexWriter;
    private IndexOutput vectorsStream;
    private final CompressionMode compressionMode;
    private final Compressor compressor;
    private final int chunkSize;
    private int numDocs;
    private final Deque<DocData> pendingDocs;
    private DocData curDoc;
    private FieldData curField;
    private final BytesRef lastTerm;
    private int[] positionsBuf;
    private int[] startOffsetsBuf;
    private int[] lengthsBuf;
    private int[] payloadLengthsBuf;
    private final GrowableByteArrayDataOutput termSuffixes;
    private final GrowableByteArrayDataOutput payloadBytes;
    private final BlockPackedWriter writer;

    private DocData addDocData(int numVectorFields) {
        DocData doc;
        FieldData last = null;
        Iterator<DocData> it = this.pendingDocs.descendingIterator();
        while (it.hasNext()) {
            DocData doc2 = it.next();
            if (doc2.fields.isEmpty()) continue;
            last = doc2.fields.getLast();
            break;
        }
        if (last == null) {
            doc = new DocData(numVectorFields, 0, 0, 0);
        } else {
            int posStart = last.posStart + (last.hasPositions ? last.totalPositions : 0);
            int offStart = last.offStart + (last.hasOffsets ? last.totalPositions : 0);
            int payStart = last.payStart + (last.hasPayloads ? last.totalPositions : 0);
            doc = new DocData(numVectorFields, posStart, offStart, payStart);
        }
        this.pendingDocs.add(doc);
        return doc;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public CompressingTermVectorsWriter(Directory directory, SegmentInfo si, String segmentSuffix, IOContext context, String formatName, CompressionMode compressionMode, int chunkSize) throws IOException {
        assert (directory != null);
        this.directory = directory;
        this.segment = si.name;
        this.segmentSuffix = segmentSuffix;
        this.compressionMode = compressionMode;
        this.compressor = compressionMode.newCompressor();
        this.chunkSize = chunkSize;
        this.numDocs = 0;
        this.pendingDocs = new ArrayDeque<DocData>();
        this.termSuffixes = new GrowableByteArrayDataOutput(ArrayUtil.oversize(chunkSize, 1));
        this.payloadBytes = new GrowableByteArrayDataOutput(ArrayUtil.oversize(1, 1));
        this.lastTerm = new BytesRef(ArrayUtil.oversize(30, 1));
        boolean success = false;
        IndexOutput indexStream = directory.createOutput(IndexFileNames.segmentFileName(this.segment, segmentSuffix, VECTORS_INDEX_EXTENSION), context);
        try {
            this.vectorsStream = directory.createOutput(IndexFileNames.segmentFileName(this.segment, segmentSuffix, VECTORS_EXTENSION), context);
            String codecNameIdx = formatName + CODEC_SFX_IDX;
            String codecNameDat = formatName + CODEC_SFX_DAT;
            CodecUtil.writeHeader(indexStream, codecNameIdx, 0);
            CodecUtil.writeHeader(this.vectorsStream, codecNameDat, 0);
            assert ((long)CodecUtil.headerLength(codecNameDat) == this.vectorsStream.getFilePointer());
            assert ((long)CodecUtil.headerLength(codecNameIdx) == indexStream.getFilePointer());
            this.indexWriter = new CompressingStoredFieldsIndexWriter(indexStream);
            indexStream = null;
            this.vectorsStream.writeVInt(1);
            this.vectorsStream.writeVInt(chunkSize);
            this.writer = new BlockPackedWriter(this.vectorsStream, 64);
            this.positionsBuf = new int[1024];
            this.startOffsetsBuf = new int[1024];
            this.lengthsBuf = new int[1024];
            this.payloadLengthsBuf = new int[1024];
            return;
        }
        catch (Throwable throwable) {
            if (success) throw throwable;
            IOUtils.closeWhileHandlingException(indexStream);
            this.abort();
            throw throwable;
        }
    }

    @Override
    public void close() throws IOException {
        try {
            IOUtils.close(this.vectorsStream, this.indexWriter);
        }
        finally {
            this.vectorsStream = null;
            this.indexWriter = null;
        }
    }

    @Override
    public void abort() {
        IOUtils.closeWhileHandlingException(this);
        IOUtils.deleteFilesIgnoringExceptions(this.directory, IndexFileNames.segmentFileName(this.segment, this.segmentSuffix, VECTORS_EXTENSION), IndexFileNames.segmentFileName(this.segment, this.segmentSuffix, VECTORS_INDEX_EXTENSION));
    }

    @Override
    public void startDocument(int numVectorFields) throws IOException {
        this.curDoc = this.addDocData(numVectorFields);
    }

    @Override
    public void finishDocument() throws IOException {
        this.termSuffixes.writeBytes(this.payloadBytes.bytes, this.payloadBytes.length);
        this.payloadBytes.length = 0;
        ++this.numDocs;
        if (this.triggerFlush()) {
            this.flush();
        }
        this.curDoc = null;
    }

    @Override
    public void startField(FieldInfo info, int numTerms, boolean positions, boolean offsets, boolean payloads) throws IOException {
        this.curField = this.curDoc.addField(info.number, numTerms, positions, offsets, payloads);
        this.lastTerm.length = 0;
    }

    @Override
    public void finishField() throws IOException {
        this.curField = null;
    }

    @Override
    public void startTerm(BytesRef term, int freq) throws IOException {
        assert (freq >= 1);
        int prefix = StringHelper.bytesDifference(this.lastTerm, term);
        this.curField.addTerm(freq, prefix, term.length - prefix);
        this.termSuffixes.writeBytes(term.bytes, term.offset + prefix, term.length - prefix);
        if (this.lastTerm.bytes.length < term.length) {
            this.lastTerm.bytes = new byte[ArrayUtil.oversize(term.length, 1)];
        }
        this.lastTerm.offset = 0;
        this.lastTerm.length = term.length;
        System.arraycopy(term.bytes, term.offset, this.lastTerm.bytes, 0, term.length);
    }

    @Override
    public void addPosition(int position, int startOffset, int endOffset, BytesRef payload) throws IOException {
        assert (this.curField.flags != 0);
        this.curField.addPosition(position, startOffset, endOffset - startOffset, payload == null ? 0 : payload.length);
        if (this.curField.hasPayloads && payload != null) {
            this.payloadBytes.writeBytes(payload.bytes, payload.offset, payload.length);
        }
    }

    private boolean triggerFlush() {
        return this.termSuffixes.length >= this.chunkSize || this.pendingDocs.size() >= 128;
    }

    private void flush() throws IOException {
        int chunkDocs = this.pendingDocs.size();
        assert (chunkDocs > 0) : chunkDocs;
        this.indexWriter.writeIndex(chunkDocs, this.vectorsStream.getFilePointer());
        int docBase = this.numDocs - chunkDocs;
        this.vectorsStream.writeVInt(docBase);
        this.vectorsStream.writeVInt(chunkDocs);
        int totalFields = this.flushNumFields(chunkDocs);
        if (totalFields > 0) {
            int[] fieldNums = this.flushFieldNums();
            this.flushFields(totalFields, fieldNums);
            this.flushFlags(totalFields, fieldNums);
            this.flushNumTerms(totalFields);
            this.flushTermLengths();
            this.flushTermFreqs();
            this.flushPositions();
            this.flushOffsets(fieldNums);
            this.flushPayloadLengths();
            this.compressor.compress(this.termSuffixes.bytes, 0, this.termSuffixes.length, this.vectorsStream);
        }
        this.pendingDocs.clear();
        this.curDoc = null;
        this.curField = null;
        this.termSuffixes.length = 0;
    }

    private int flushNumFields(int chunkDocs) throws IOException {
        if (chunkDocs == 1) {
            int numFields = this.pendingDocs.getFirst().numFields;
            this.vectorsStream.writeVInt(numFields);
            return numFields;
        }
        this.writer.reset(this.vectorsStream);
        int totalFields = 0;
        for (DocData dd : this.pendingDocs) {
            this.writer.add(dd.numFields);
            totalFields += dd.numFields;
        }
        this.writer.finish();
        return totalFields;
    }

    private int[] flushFieldNums() throws IOException {
        TreeSet<Integer> fieldNums = new TreeSet<Integer>();
        for (DocData dd : this.pendingDocs) {
            for (FieldData fd : dd.fields) {
                fieldNums.add(fd.fieldNum);
            }
        }
        int numDistinctFields = fieldNums.size();
        assert (numDistinctFields > 0);
        int bitsRequired = PackedInts.bitsRequired(((Integer)fieldNums.last()).intValue());
        int token = Math.min(numDistinctFields - 1, 7) << 5 | bitsRequired;
        this.vectorsStream.writeByte((byte)token);
        if (numDistinctFields - 1 >= 7) {
            this.vectorsStream.writeVInt(numDistinctFields - 1 - 7);
        }
        PackedInts.Writer writer = PackedInts.getWriterNoHeader(this.vectorsStream, PackedInts.Format.PACKED, fieldNums.size(), bitsRequired, 1);
        for (Integer fieldNum : fieldNums) {
            writer.add(fieldNum.intValue());
        }
        writer.finish();
        int[] fns = new int[fieldNums.size()];
        int i = 0;
        for (Integer key : fieldNums) {
            fns[i++] = key;
        }
        return fns;
    }

    private void flushFields(int totalFields, int[] fieldNums) throws IOException {
        PackedInts.Writer writer = PackedInts.getWriterNoHeader(this.vectorsStream, PackedInts.Format.PACKED, totalFields, PackedInts.bitsRequired(fieldNums.length - 1), 1);
        for (DocData dd : this.pendingDocs) {
            for (FieldData fd : dd.fields) {
                int fieldNumIndex = Arrays.binarySearch(fieldNums, fd.fieldNum);
                assert (fieldNumIndex >= 0);
                writer.add(fieldNumIndex);
            }
        }
        writer.finish();
    }

    private void flushFlags(int totalFields, int[] fieldNums) throws IOException {
        PackedInts.Writer writer;
        boolean nonChangingFlags = true;
        int[] fieldFlags = new int[fieldNums.length];
        Arrays.fill(fieldFlags, -1);
        block0: for (DocData dd : this.pendingDocs) {
            for (FieldData fd : dd.fields) {
                int fieldNumOff = Arrays.binarySearch(fieldNums, fd.fieldNum);
                assert (fieldNumOff >= 0);
                if (fieldFlags[fieldNumOff] == -1) {
                    fieldFlags[fieldNumOff] = fd.flags;
                    continue;
                }
                if (fieldFlags[fieldNumOff] == fd.flags) continue;
                nonChangingFlags = false;
                break block0;
            }
        }
        if (nonChangingFlags) {
            this.vectorsStream.writeVInt(0);
            writer = PackedInts.getWriterNoHeader(this.vectorsStream, PackedInts.Format.PACKED, fieldFlags.length, FLAGS_BITS, 1);
            for (int flags : fieldFlags) {
                assert (flags >= 0);
                writer.add(flags);
            }
            assert (writer.ord() == fieldFlags.length - 1);
            writer.finish();
        } else {
            this.vectorsStream.writeVInt(1);
            writer = PackedInts.getWriterNoHeader(this.vectorsStream, PackedInts.Format.PACKED, totalFields, FLAGS_BITS, 1);
            Object object = this.pendingDocs.iterator();
            while (object.hasNext()) {
                DocData dd = (DocData)object.next();
                for (FieldData fd : dd.fields) {
                    writer.add(fd.flags);
                }
            }
            assert (writer.ord() == totalFields - 1);
            writer.finish();
        }
    }

    private void flushNumTerms(int totalFields) throws IOException {
        int maxNumTerms = 0;
        for (DocData dd : this.pendingDocs) {
            for (FieldData fd : dd.fields) {
                maxNumTerms |= fd.numTerms;
            }
        }
        int bitsRequired = PackedInts.bitsRequired(maxNumTerms);
        this.vectorsStream.writeVInt(bitsRequired);
        PackedInts.Writer writer = PackedInts.getWriterNoHeader(this.vectorsStream, PackedInts.Format.PACKED, totalFields, bitsRequired, 1);
        for (DocData dd : this.pendingDocs) {
            for (FieldData fd : dd.fields) {
                writer.add(fd.numTerms);
            }
        }
        assert (writer.ord() == totalFields - 1);
        writer.finish();
    }

    private void flushTermLengths() throws IOException {
        int i;
        this.writer.reset(this.vectorsStream);
        for (DocData dd : this.pendingDocs) {
            for (FieldData fd : dd.fields) {
                for (i = 0; i < fd.numTerms; ++i) {
                    this.writer.add(fd.prefixLengths[i]);
                }
            }
        }
        this.writer.finish();
        this.writer.reset(this.vectorsStream);
        for (DocData dd : this.pendingDocs) {
            for (FieldData fd : dd.fields) {
                for (i = 0; i < fd.numTerms; ++i) {
                    this.writer.add(fd.suffixLengths[i]);
                }
            }
        }
        this.writer.finish();
    }

    private void flushTermFreqs() throws IOException {
        this.writer.reset(this.vectorsStream);
        for (DocData dd : this.pendingDocs) {
            for (FieldData fd : dd.fields) {
                for (int i = 0; i < fd.numTerms; ++i) {
                    this.writer.add(fd.freqs[i] - 1);
                }
            }
        }
        this.writer.finish();
    }

    private void flushPositions() throws IOException {
        this.writer.reset(this.vectorsStream);
        for (DocData dd : this.pendingDocs) {
            for (FieldData fd : dd.fields) {
                if (!fd.hasPositions) continue;
                int pos = 0;
                for (int i = 0; i < fd.numTerms; ++i) {
                    int previousPosition = 0;
                    for (int j = 0; j < fd.freqs[i]; ++j) {
                        int position = this.positionsBuf[fd.posStart + pos++];
                        this.writer.add(position - previousPosition);
                        previousPosition = position;
                    }
                }
                assert (pos == fd.totalPositions);
            }
        }
        this.writer.finish();
    }

    private void flushOffsets(int[] fieldNums) throws IOException {
        int i;
        int pos;
        boolean hasOffsets = false;
        long[] sumPos = new long[fieldNums.length];
        long[] sumOffsets = new long[fieldNums.length];
        for (DocData dd : this.pendingDocs) {
            for (FieldData fd : dd.fields) {
                hasOffsets |= fd.hasOffsets;
                if (!fd.hasOffsets || !fd.hasPositions) continue;
                int fieldNumOff = Arrays.binarySearch(fieldNums, fd.fieldNum);
                pos = 0;
                for (int i2 = 0; i2 < fd.numTerms; ++i2) {
                    int previousPos = 0;
                    int previousOff = 0;
                    for (int j = 0; j < fd.freqs[i2]; ++j) {
                        int position = this.positionsBuf[fd.posStart + pos];
                        int startOffset = this.startOffsetsBuf[fd.offStart + pos];
                        int n = fieldNumOff;
                        sumPos[n] = sumPos[n] + (long)(position - previousPos);
                        int n2 = fieldNumOff;
                        sumOffsets[n2] = sumOffsets[n2] + (long)(startOffset - previousOff);
                        previousPos = position;
                        previousOff = startOffset;
                        ++pos;
                    }
                }
                assert (pos == fd.totalPositions);
            }
        }
        if (!hasOffsets) {
            return;
        }
        float[] charsPerTerm = new float[fieldNums.length];
        for (i = 0; i < fieldNums.length; ++i) {
            charsPerTerm[i] = sumPos[i] <= 0L || sumOffsets[i] <= 0L ? 0.0f : (float)((double)sumOffsets[i] / (double)sumPos[i]);
        }
        for (i = 0; i < fieldNums.length; ++i) {
            this.vectorsStream.writeInt(Float.floatToRawIntBits(charsPerTerm[i]));
        }
        this.writer.reset(this.vectorsStream);
        for (DocData dd : this.pendingDocs) {
            for (FieldData fd : dd.fields) {
                if ((fd.flags & 2) == 0) continue;
                int fieldNumOff = Arrays.binarySearch(fieldNums, fd.fieldNum);
                float cpt = charsPerTerm[fieldNumOff];
                int pos2 = 0;
                for (int i3 = 0; i3 < fd.numTerms; ++i3) {
                    int previousPos = 0;
                    int previousOff = 0;
                    for (int j = 0; j < fd.freqs[i3]; ++j) {
                        int position = fd.hasPositions ? this.positionsBuf[fd.posStart + pos2] : 0;
                        int startOffset = this.startOffsetsBuf[fd.offStart + pos2];
                        this.writer.add(startOffset - previousOff - (int)(cpt * (float)(position - previousPos)));
                        previousPos = position;
                        previousOff = startOffset;
                        ++pos2;
                    }
                }
            }
        }
        this.writer.finish();
        this.writer.reset(this.vectorsStream);
        for (DocData dd : this.pendingDocs) {
            for (FieldData fd : dd.fields) {
                if ((fd.flags & 2) == 0) continue;
                pos = 0;
                for (int i4 = 0; i4 < fd.numTerms; ++i4) {
                    for (int j = 0; j < fd.freqs[i4]; ++j) {
                        this.writer.add(this.lengthsBuf[fd.offStart + pos++] - fd.prefixLengths[i4] - fd.suffixLengths[i4]);
                    }
                }
                assert (pos == fd.totalPositions);
            }
        }
        this.writer.finish();
    }

    private void flushPayloadLengths() throws IOException {
        this.writer.reset(this.vectorsStream);
        for (DocData dd : this.pendingDocs) {
            for (FieldData fd : dd.fields) {
                if (!fd.hasPayloads) continue;
                for (int i = 0; i < fd.totalPositions; ++i) {
                    this.writer.add(this.payloadLengthsBuf[fd.payStart + i]);
                }
            }
        }
        this.writer.finish();
    }

    @Override
    public void finish(FieldInfos fis, int numDocs) throws IOException {
        if (!this.pendingDocs.isEmpty()) {
            this.flush();
        }
        if (numDocs != this.numDocs) {
            throw new RuntimeException("Wrote " + this.numDocs + " docs, finish called with numDocs=" + numDocs);
        }
        this.indexWriter.finish(numDocs);
    }

    @Override
    public Comparator<BytesRef> getComparator() {
        return BytesRef.getUTF8SortedAsUnicodeComparator();
    }

    @Override
    public void addProx(int numProx, DataInput positions, DataInput offsets) throws IOException {
        assert (this.curField.hasPositions == (positions != null));
        assert (this.curField.hasOffsets == (offsets != null));
        if (this.curField.hasPositions) {
            int posStart = this.curField.posStart + this.curField.totalPositions;
            if (posStart + numProx > this.positionsBuf.length) {
                this.positionsBuf = ArrayUtil.grow(this.positionsBuf, posStart + numProx);
            }
            int position = 0;
            if (this.curField.hasPayloads) {
                int payStart = this.curField.payStart + this.curField.totalPositions;
                if (payStart + numProx > this.payloadLengthsBuf.length) {
                    this.payloadLengthsBuf = ArrayUtil.grow(this.payloadLengthsBuf, payStart + numProx);
                }
                for (int i = 0; i < numProx; ++i) {
                    int code = positions.readVInt();
                    if ((code & 1) != 0) {
                        int payloadLength;
                        this.payloadLengthsBuf[payStart + i] = payloadLength = positions.readVInt();
                        this.payloadBytes.copyBytes(positions, payloadLength);
                    } else {
                        this.payloadLengthsBuf[payStart + i] = 0;
                    }
                    this.positionsBuf[posStart + i] = position += code >>> 1;
                }
            } else {
                for (int i = 0; i < numProx; ++i) {
                    this.positionsBuf[posStart + i] = position += positions.readVInt() >>> 1;
                }
            }
        }
        if (this.curField.hasOffsets) {
            int offStart = this.curField.offStart + this.curField.totalPositions;
            if (offStart + numProx > this.startOffsetsBuf.length) {
                int newLength = ArrayUtil.oversize(offStart + numProx, 4);
                this.startOffsetsBuf = Arrays.copyOf(this.startOffsetsBuf, newLength);
                this.lengthsBuf = Arrays.copyOf(this.lengthsBuf, newLength);
            }
            int lastOffset = 0;
            for (int i = 0; i < numProx; ++i) {
                int endOffset;
                int startOffset = lastOffset + offsets.readVInt();
                lastOffset = endOffset = startOffset + offsets.readVInt();
                this.startOffsetsBuf[offStart + i] = startOffset;
                this.lengthsBuf[offStart + i] = endOffset - startOffset;
            }
        }
        this.curField.totalPositions += numProx;
    }

    @Override
    public int merge(MergeState mergeState) throws IOException {
        int docCount = 0;
        int idx = 0;
        for (AtomicReader reader : mergeState.readers) {
            TermVectorsReader vectorsReader;
            SegmentReader matchingSegmentReader = mergeState.matchingSegmentReaders[idx++];
            CompressingTermVectorsReader matchingVectorsReader = null;
            if (matchingSegmentReader != null && (vectorsReader = matchingSegmentReader.getTermVectorsReader()) != null && vectorsReader instanceof CompressingTermVectorsReader) {
                matchingVectorsReader = (CompressingTermVectorsReader)vectorsReader;
            }
            int maxDoc = reader.maxDoc();
            Bits liveDocs = reader.getLiveDocs();
            if (matchingVectorsReader == null || matchingVectorsReader.getCompressionMode() != this.compressionMode || matchingVectorsReader.getChunkSize() != this.chunkSize || matchingVectorsReader.getPackedIntsVersion() != 1) {
                int i = CompressingTermVectorsWriter.nextLiveDoc(0, liveDocs, maxDoc);
                while (i < maxDoc) {
                    Fields vectors = reader.getTermVectors(i);
                    this.addAllDocVectors(vectors, mergeState);
                    ++docCount;
                    mergeState.checkAbort.work(300.0);
                    i = CompressingTermVectorsWriter.nextLiveDoc(i + 1, liveDocs, maxDoc);
                }
                continue;
            }
            CompressingStoredFieldsIndexReader index = matchingVectorsReader.getIndex();
            IndexInput vectorsStream = matchingVectorsReader.getVectorsStream();
            int i = CompressingTermVectorsWriter.nextLiveDoc(0, liveDocs, maxDoc);
            while (i < maxDoc) {
                if (this.pendingDocs.isEmpty() && (i == 0 || index.getStartPointer(i - 1) < index.getStartPointer(i))) {
                    long startPointer = index.getStartPointer(i);
                    vectorsStream.seek(startPointer);
                    int docBase = vectorsStream.readVInt();
                    int chunkDocs = vectorsStream.readVInt();
                    assert (docBase + chunkDocs <= matchingSegmentReader.maxDoc());
                    if (docBase + chunkDocs < matchingSegmentReader.maxDoc() && CompressingTermVectorsWriter.nextDeletedDoc(docBase, liveDocs, docBase + chunkDocs) == docBase + chunkDocs) {
                        long chunkEnd = index.getStartPointer(docBase + chunkDocs);
                        long chunkLength = chunkEnd - vectorsStream.getFilePointer();
                        this.indexWriter.writeIndex(chunkDocs, this.vectorsStream.getFilePointer());
                        this.vectorsStream.writeVInt(docCount);
                        this.vectorsStream.writeVInt(chunkDocs);
                        this.vectorsStream.copyBytes(vectorsStream, chunkLength);
                        docCount += chunkDocs;
                        this.numDocs += chunkDocs;
                        mergeState.checkAbort.work(300 * chunkDocs);
                        i = CompressingTermVectorsWriter.nextLiveDoc(docBase + chunkDocs, liveDocs, maxDoc);
                        continue;
                    }
                    while (i < docBase + chunkDocs) {
                        Fields vectors = reader.getTermVectors(i);
                        this.addAllDocVectors(vectors, mergeState);
                        ++docCount;
                        mergeState.checkAbort.work(300.0);
                        i = CompressingTermVectorsWriter.nextLiveDoc(i + 1, liveDocs, maxDoc);
                    }
                    continue;
                }
                Fields vectors = reader.getTermVectors(i);
                this.addAllDocVectors(vectors, mergeState);
                ++docCount;
                mergeState.checkAbort.work(300.0);
                i = CompressingTermVectorsWriter.nextLiveDoc(i + 1, liveDocs, maxDoc);
            }
        }
        this.finish(mergeState.fieldInfos, docCount);
        return docCount;
    }

    private static int nextLiveDoc(int doc, Bits liveDocs, int maxDoc) {
        if (liveDocs == null) {
            return doc;
        }
        while (doc < maxDoc && !liveDocs.get(doc)) {
            ++doc;
        }
        return doc;
    }

    private static int nextDeletedDoc(int doc, Bits liveDocs, int maxDoc) {
        if (liveDocs == null) {
            return maxDoc;
        }
        while (doc < maxDoc && liveDocs.get(doc)) {
            ++doc;
        }
        return doc;
    }

    static /* synthetic */ int[] access$002(CompressingTermVectorsWriter x0, int[] x1) {
        x0.positionsBuf = x1;
        return x1;
    }

    static /* synthetic */ int[] access$102(CompressingTermVectorsWriter x0, int[] x1) {
        x0.startOffsetsBuf = x1;
        return x1;
    }

    static /* synthetic */ int[] access$202(CompressingTermVectorsWriter x0, int[] x1) {
        x0.lengthsBuf = x1;
        return x1;
    }

    static /* synthetic */ int[] access$302(CompressingTermVectorsWriter x0, int[] x1) {
        x0.payloadLengthsBuf = x1;
        return x1;
    }

    private class FieldData {
        final boolean hasPositions;
        final boolean hasOffsets;
        final boolean hasPayloads;
        final int fieldNum;
        final int flags;
        final int numTerms;
        final int[] freqs;
        final int[] prefixLengths;
        final int[] suffixLengths;
        final int posStart;
        final int offStart;
        final int payStart;
        int totalPositions;
        int ord;

        FieldData(int fieldNum, int numTerms, boolean positions, boolean offsets, boolean payloads, int posStart, int offStart, int payStart) {
            this.fieldNum = fieldNum;
            this.numTerms = numTerms;
            this.hasPositions = positions;
            this.hasOffsets = offsets;
            this.hasPayloads = payloads;
            this.flags = (positions ? 1 : 0) | (offsets ? 2 : 0) | (payloads ? 4 : 0);
            this.freqs = new int[numTerms];
            this.prefixLengths = new int[numTerms];
            this.suffixLengths = new int[numTerms];
            this.posStart = posStart;
            this.offStart = offStart;
            this.payStart = payStart;
            this.totalPositions = 0;
            this.ord = 0;
        }

        void addTerm(int freq, int prefixLength, int suffixLength) {
            this.freqs[this.ord] = freq;
            this.prefixLengths[this.ord] = prefixLength;
            this.suffixLengths[this.ord] = suffixLength;
            ++this.ord;
        }

        void addPosition(int position, int startOffset, int length, int payloadLength) {
            if (this.hasPositions) {
                if (this.posStart + this.totalPositions == CompressingTermVectorsWriter.this.positionsBuf.length) {
                    CompressingTermVectorsWriter.access$002(CompressingTermVectorsWriter.this, ArrayUtil.grow(CompressingTermVectorsWriter.this.positionsBuf));
                }
                ((CompressingTermVectorsWriter)CompressingTermVectorsWriter.this).positionsBuf[this.posStart + this.totalPositions] = position;
            }
            if (this.hasOffsets) {
                if (this.offStart + this.totalPositions == CompressingTermVectorsWriter.this.startOffsetsBuf.length) {
                    int newLength = ArrayUtil.oversize(this.offStart + this.totalPositions, 4);
                    CompressingTermVectorsWriter.access$102(CompressingTermVectorsWriter.this, Arrays.copyOf(CompressingTermVectorsWriter.this.startOffsetsBuf, newLength));
                    CompressingTermVectorsWriter.access$202(CompressingTermVectorsWriter.this, Arrays.copyOf(CompressingTermVectorsWriter.this.lengthsBuf, newLength));
                }
                ((CompressingTermVectorsWriter)CompressingTermVectorsWriter.this).startOffsetsBuf[this.offStart + this.totalPositions] = startOffset;
                ((CompressingTermVectorsWriter)CompressingTermVectorsWriter.this).lengthsBuf[this.offStart + this.totalPositions] = length;
            }
            if (this.hasPayloads) {
                if (this.payStart + this.totalPositions == CompressingTermVectorsWriter.this.payloadLengthsBuf.length) {
                    CompressingTermVectorsWriter.access$302(CompressingTermVectorsWriter.this, ArrayUtil.grow(CompressingTermVectorsWriter.this.payloadLengthsBuf));
                }
                ((CompressingTermVectorsWriter)CompressingTermVectorsWriter.this).payloadLengthsBuf[this.payStart + this.totalPositions] = payloadLength;
            }
            ++this.totalPositions;
        }
    }

    private class DocData {
        final int numFields;
        final Deque<FieldData> fields;
        final int posStart;
        final int offStart;
        final int payStart;

        DocData(int numFields, int posStart, int offStart, int payStart) {
            this.numFields = numFields;
            this.fields = new ArrayDeque<FieldData>(numFields);
            this.posStart = posStart;
            this.offStart = offStart;
            this.payStart = payStart;
        }

        FieldData addField(int fieldNum, int numTerms, boolean positions, boolean offsets, boolean payloads) {
            FieldData field;
            if (this.fields.isEmpty()) {
                field = new FieldData(fieldNum, numTerms, positions, offsets, payloads, this.posStart, this.offStart, this.payStart);
            } else {
                FieldData last = this.fields.getLast();
                int posStart = last.posStart + (last.hasPositions ? last.totalPositions : 0);
                int offStart = last.offStart + (last.hasOffsets ? last.totalPositions : 0);
                int payStart = last.payStart + (last.hasPayloads ? last.totalPositions : 0);
                field = new FieldData(fieldNum, numTerms, positions, offsets, payloads, posStart, offStart, payStart);
            }
            this.fields.add(field);
            return field;
        }
    }
}


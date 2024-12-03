/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene40;

import java.io.IOException;
import java.util.Comparator;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.codecs.TermVectorsReader;
import org.apache.lucene.codecs.TermVectorsWriter;
import org.apache.lucene.codecs.lucene40.Lucene40TermVectorsReader;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.MergeState;
import org.apache.lucene.index.SegmentReader;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.StringHelper;

public final class Lucene40TermVectorsWriter
extends TermVectorsWriter {
    private final Directory directory;
    private final String segment;
    private IndexOutput tvx = null;
    private IndexOutput tvd = null;
    private IndexOutput tvf = null;
    private long[] fps = new long[10];
    private int fieldCount = 0;
    private int numVectorFields = 0;
    private String lastFieldName;
    private final BytesRef lastTerm = new BytesRef(10);
    private int[] offsetStartBuffer = new int[10];
    private int[] offsetEndBuffer = new int[10];
    private BytesRef payloadData = new BytesRef(10);
    private int bufferedIndex = 0;
    private int bufferedFreq = 0;
    private boolean positions = false;
    private boolean offsets = false;
    private boolean payloads = false;
    int lastPosition = 0;
    int lastOffset = 0;
    int lastPayloadLength = -1;
    BytesRef scratch = new BytesRef();
    private static final int MAX_RAW_MERGE_DOCS = 4192;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Lucene40TermVectorsWriter(Directory directory, String segment, IOContext context) throws IOException {
        this.directory = directory;
        this.segment = segment;
        boolean success = false;
        try {
            this.tvx = directory.createOutput(IndexFileNames.segmentFileName(segment, "", "tvx"), context);
            CodecUtil.writeHeader(this.tvx, "Lucene40TermVectorsIndex", 1);
            this.tvd = directory.createOutput(IndexFileNames.segmentFileName(segment, "", "tvd"), context);
            CodecUtil.writeHeader(this.tvd, "Lucene40TermVectorsDocs", 1);
            this.tvf = directory.createOutput(IndexFileNames.segmentFileName(segment, "", "tvf"), context);
            CodecUtil.writeHeader(this.tvf, "Lucene40TermVectorsFields", 1);
            assert (Lucene40TermVectorsReader.HEADER_LENGTH_INDEX == this.tvx.getFilePointer());
            assert (Lucene40TermVectorsReader.HEADER_LENGTH_DOCS == this.tvd.getFilePointer());
            assert (Lucene40TermVectorsReader.HEADER_LENGTH_FIELDS == this.tvf.getFilePointer());
            success = true;
        }
        finally {
            if (!success) {
                this.abort();
            }
        }
    }

    @Override
    public void startDocument(int numVectorFields) throws IOException {
        this.lastFieldName = null;
        this.numVectorFields = numVectorFields;
        this.tvx.writeLong(this.tvd.getFilePointer());
        this.tvx.writeLong(this.tvf.getFilePointer());
        this.tvd.writeVInt(numVectorFields);
        this.fieldCount = 0;
        this.fps = ArrayUtil.grow(this.fps, numVectorFields);
    }

    @Override
    public void startField(FieldInfo info, int numTerms, boolean positions, boolean offsets, boolean payloads) throws IOException {
        assert (this.lastFieldName == null || info.name.compareTo(this.lastFieldName) > 0) : "fieldName=" + info.name + " lastFieldName=" + this.lastFieldName;
        this.lastFieldName = info.name;
        this.positions = positions;
        this.offsets = offsets;
        this.payloads = payloads;
        this.lastTerm.length = 0;
        this.lastPayloadLength = -1;
        this.fps[this.fieldCount++] = this.tvf.getFilePointer();
        this.tvd.writeVInt(info.number);
        this.tvf.writeVInt(numTerms);
        byte bits = 0;
        if (positions) {
            bits = (byte)(bits | 1);
        }
        if (offsets) {
            bits = (byte)(bits | 2);
        }
        if (payloads) {
            bits = (byte)(bits | 4);
        }
        this.tvf.writeByte(bits);
    }

    @Override
    public void finishDocument() throws IOException {
        assert (this.fieldCount == this.numVectorFields);
        for (int i = 1; i < this.fieldCount; ++i) {
            this.tvd.writeVLong(this.fps[i] - this.fps[i - 1]);
        }
    }

    @Override
    public void startTerm(BytesRef term, int freq) throws IOException {
        int prefix = StringHelper.bytesDifference(this.lastTerm, term);
        int suffix = term.length - prefix;
        this.tvf.writeVInt(prefix);
        this.tvf.writeVInt(suffix);
        this.tvf.writeBytes(term.bytes, term.offset + prefix, suffix);
        this.tvf.writeVInt(freq);
        this.lastTerm.copyBytes(term);
        this.lastOffset = 0;
        this.lastPosition = 0;
        if (this.offsets && this.positions) {
            this.offsetStartBuffer = ArrayUtil.grow(this.offsetStartBuffer, freq);
            this.offsetEndBuffer = ArrayUtil.grow(this.offsetEndBuffer, freq);
        }
        this.bufferedIndex = 0;
        this.bufferedFreq = freq;
        this.payloadData.length = 0;
    }

    @Override
    public void addProx(int numProx, DataInput positions, DataInput offsets) throws IOException {
        int i;
        if (this.payloads) {
            for (i = 0; i < numProx; ++i) {
                int code = positions.readVInt();
                if ((code & 1) == 1) {
                    int length = positions.readVInt();
                    this.scratch.grow(length);
                    this.scratch.length = length;
                    positions.readBytes(this.scratch.bytes, this.scratch.offset, this.scratch.length);
                    this.writePosition(code >>> 1, this.scratch);
                    continue;
                }
                this.writePosition(code >>> 1, null);
            }
            this.tvf.writeBytes(this.payloadData.bytes, this.payloadData.offset, this.payloadData.length);
        } else if (positions != null) {
            for (i = 0; i < numProx; ++i) {
                this.tvf.writeVInt(positions.readVInt() >>> 1);
            }
        }
        if (offsets != null) {
            for (i = 0; i < numProx; ++i) {
                this.tvf.writeVInt(offsets.readVInt());
                this.tvf.writeVInt(offsets.readVInt());
            }
        }
    }

    @Override
    public void addPosition(int position, int startOffset, int endOffset, BytesRef payload) throws IOException {
        if (this.positions && (this.offsets || this.payloads)) {
            this.writePosition(position - this.lastPosition, payload);
            this.lastPosition = position;
            if (this.offsets) {
                this.offsetStartBuffer[this.bufferedIndex] = startOffset;
                this.offsetEndBuffer[this.bufferedIndex] = endOffset;
            }
            ++this.bufferedIndex;
        } else if (this.positions) {
            this.writePosition(position - this.lastPosition, payload);
            this.lastPosition = position;
        } else if (this.offsets) {
            this.tvf.writeVInt(startOffset - this.lastOffset);
            this.tvf.writeVInt(endOffset - startOffset);
            this.lastOffset = endOffset;
        }
    }

    @Override
    public void finishTerm() throws IOException {
        if (this.bufferedIndex > 0) {
            assert (this.positions && (this.offsets || this.payloads));
            assert (this.bufferedIndex == this.bufferedFreq);
            if (this.payloads) {
                this.tvf.writeBytes(this.payloadData.bytes, this.payloadData.offset, this.payloadData.length);
            }
            if (this.offsets) {
                for (int i = 0; i < this.bufferedIndex; ++i) {
                    this.tvf.writeVInt(this.offsetStartBuffer[i] - this.lastOffset);
                    this.tvf.writeVInt(this.offsetEndBuffer[i] - this.offsetStartBuffer[i]);
                    this.lastOffset = this.offsetEndBuffer[i];
                }
            }
        }
    }

    private void writePosition(int delta, BytesRef payload) throws IOException {
        if (this.payloads) {
            int payloadLength;
            int n = payloadLength = payload == null ? 0 : payload.length;
            if (payloadLength != this.lastPayloadLength) {
                this.lastPayloadLength = payloadLength;
                this.tvf.writeVInt(delta << 1 | 1);
                this.tvf.writeVInt(payloadLength);
            } else {
                this.tvf.writeVInt(delta << 1);
            }
            if (payloadLength > 0) {
                if (payloadLength + this.payloadData.length < 0) {
                    throw new UnsupportedOperationException("A term cannot have more than Integer.MAX_VALUE bytes of payload data in a single document");
                }
                this.payloadData.append(payload);
            }
        } else {
            this.tvf.writeVInt(delta);
        }
    }

    @Override
    public void abort() {
        try {
            this.close();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        IOUtils.deleteFilesIgnoringExceptions(this.directory, IndexFileNames.segmentFileName(this.segment, "", "tvx"), IndexFileNames.segmentFileName(this.segment, "", "tvd"), IndexFileNames.segmentFileName(this.segment, "", "tvf"));
    }

    private void addRawDocuments(Lucene40TermVectorsReader reader, int[] tvdLengths, int[] tvfLengths, int numDocs) throws IOException {
        long tvdPosition = this.tvd.getFilePointer();
        long tvfPosition = this.tvf.getFilePointer();
        long tvdStart = tvdPosition;
        long tvfStart = tvfPosition;
        for (int i = 0; i < numDocs; ++i) {
            this.tvx.writeLong(tvdPosition);
            tvdPosition += (long)tvdLengths[i];
            this.tvx.writeLong(tvfPosition);
            tvfPosition += (long)tvfLengths[i];
        }
        this.tvd.copyBytes(reader.getTvdStream(), tvdPosition - tvdStart);
        this.tvf.copyBytes(reader.getTvfStream(), tvfPosition - tvfStart);
        assert (this.tvd.getFilePointer() == tvdPosition);
        assert (this.tvf.getFilePointer() == tvfPosition);
    }

    @Override
    public final int merge(MergeState mergeState) throws IOException {
        int[] rawDocLengths = new int[4192];
        int[] rawDocLengths2 = new int[4192];
        int idx = 0;
        int numDocs = 0;
        for (int i = 0; i < mergeState.readers.size(); ++i) {
            TermVectorsReader vectorsReader;
            AtomicReader reader = mergeState.readers.get(i);
            SegmentReader matchingSegmentReader = mergeState.matchingSegmentReaders[idx++];
            Lucene40TermVectorsReader matchingVectorsReader = null;
            if (matchingSegmentReader != null && (vectorsReader = matchingSegmentReader.getTermVectorsReader()) != null && vectorsReader instanceof Lucene40TermVectorsReader) {
                matchingVectorsReader = (Lucene40TermVectorsReader)vectorsReader;
            }
            if (reader.getLiveDocs() != null) {
                numDocs += this.copyVectorsWithDeletions(mergeState, matchingVectorsReader, reader, rawDocLengths, rawDocLengths2);
                continue;
            }
            numDocs += this.copyVectorsNoDeletions(mergeState, matchingVectorsReader, reader, rawDocLengths, rawDocLengths2);
        }
        this.finish(mergeState.fieldInfos, numDocs);
        return numDocs;
    }

    private int copyVectorsWithDeletions(MergeState mergeState, Lucene40TermVectorsReader matchingVectorsReader, AtomicReader reader, int[] rawDocLengths, int[] rawDocLengths2) throws IOException {
        int maxDoc = reader.maxDoc();
        Bits liveDocs = reader.getLiveDocs();
        int totalNumDocs = 0;
        if (matchingVectorsReader != null) {
            int docNum = 0;
            while (docNum < maxDoc) {
                if (!liveDocs.get(docNum)) {
                    ++docNum;
                    continue;
                }
                int start = docNum;
                int numDocs = 0;
                do {
                    ++numDocs;
                    if (++docNum >= maxDoc) break;
                    if (liveDocs.get(docNum)) continue;
                    ++docNum;
                    break;
                } while (numDocs < 4192);
                matchingVectorsReader.rawDocs(rawDocLengths, rawDocLengths2, start, numDocs);
                this.addRawDocuments(matchingVectorsReader, rawDocLengths, rawDocLengths2, numDocs);
                totalNumDocs += numDocs;
                mergeState.checkAbort.work(300 * numDocs);
            }
        } else {
            for (int docNum = 0; docNum < maxDoc; ++docNum) {
                if (!liveDocs.get(docNum)) continue;
                Fields vectors = reader.getTermVectors(docNum);
                this.addAllDocVectors(vectors, mergeState);
                ++totalNumDocs;
                mergeState.checkAbort.work(300.0);
            }
        }
        return totalNumDocs;
    }

    private int copyVectorsNoDeletions(MergeState mergeState, Lucene40TermVectorsReader matchingVectorsReader, AtomicReader reader, int[] rawDocLengths, int[] rawDocLengths2) throws IOException {
        int maxDoc = reader.maxDoc();
        if (matchingVectorsReader != null) {
            int len;
            for (int docCount = 0; docCount < maxDoc; docCount += len) {
                len = Math.min(4192, maxDoc - docCount);
                matchingVectorsReader.rawDocs(rawDocLengths, rawDocLengths2, docCount, len);
                this.addRawDocuments(matchingVectorsReader, rawDocLengths, rawDocLengths2, len);
                mergeState.checkAbort.work(300 * len);
            }
        } else {
            for (int docNum = 0; docNum < maxDoc; ++docNum) {
                Fields vectors = reader.getTermVectors(docNum);
                this.addAllDocVectors(vectors, mergeState);
                mergeState.checkAbort.work(300.0);
            }
        }
        return maxDoc;
    }

    @Override
    public void finish(FieldInfos fis, int numDocs) {
        if (Lucene40TermVectorsReader.HEADER_LENGTH_INDEX + (long)numDocs * 16L != this.tvx.getFilePointer()) {
            throw new RuntimeException("tvx size mismatch: mergedDocs is " + numDocs + " but tvx size is " + this.tvx.getFilePointer() + " file=" + this.tvx.toString() + "; now aborting this merge to prevent index corruption");
        }
    }

    @Override
    public void close() throws IOException {
        IOUtils.close(this.tvx, this.tvd, this.tvf);
        this.tvf = null;
        this.tvd = null;
        this.tvx = null;
    }

    @Override
    public Comparator<BytesRef> getComparator() {
        return BytesRef.getUTF8SortedAsUnicodeComparator();
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.IndexFileNames;
import com.atlassian.lucene36.index.IndexFormatTooNewException;
import com.atlassian.lucene36.index.ParallelArrayTermVectorMapper;
import com.atlassian.lucene36.index.SegmentTermVector;
import com.atlassian.lucene36.index.TermFreqVector;
import com.atlassian.lucene36.index.TermVectorMapper;
import com.atlassian.lucene36.index.TermVectorOffsetInfo;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.store.IndexInput;
import com.atlassian.lucene36.util.ArrayUtil;
import com.atlassian.lucene36.util.IOUtils;
import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;

class TermVectorsReader
implements Cloneable,
Closeable {
    static final int FORMAT_VERSION = 2;
    static final int FORMAT_VERSION2 = 3;
    static final int FORMAT_UTF8_LENGTH_IN_BYTES = 4;
    static final int FORMAT_CURRENT = 4;
    static final int FORMAT_SIZE = 4;
    static final byte STORE_POSITIONS_WITH_TERMVECTOR = 1;
    static final byte STORE_OFFSET_WITH_TERMVECTOR = 2;
    private FieldInfos fieldInfos;
    private IndexInput tvx;
    private IndexInput tvd;
    private IndexInput tvf;
    private int size;
    private int numTotalDocs;
    private int docStoreOffset;
    private final int format;

    TermVectorsReader(Directory d, String segment, FieldInfos fieldInfos) throws CorruptIndexException, IOException {
        this(d, segment, fieldInfos, 1024);
    }

    TermVectorsReader(Directory d, String segment, FieldInfos fieldInfos, int readBufferSize) throws CorruptIndexException, IOException {
        this(d, segment, fieldInfos, readBufferSize, -1, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    TermVectorsReader(Directory d, String segment, FieldInfos fieldInfos, int readBufferSize, int docStoreOffset, int size) throws CorruptIndexException, IOException {
        boolean success = false;
        try {
            String idxName = IndexFileNames.segmentFileName(segment, "tvx");
            this.tvx = d.openInput(idxName, readBufferSize);
            this.format = this.checkValidFormat(idxName, this.tvx);
            String fn = IndexFileNames.segmentFileName(segment, "tvd");
            this.tvd = d.openInput(fn, readBufferSize);
            int tvdFormat = this.checkValidFormat(fn, this.tvd);
            fn = IndexFileNames.segmentFileName(segment, "tvf");
            this.tvf = d.openInput(fn, readBufferSize);
            int tvfFormat = this.checkValidFormat(fn, this.tvf);
            assert (this.format == tvdFormat);
            assert (this.format == tvfFormat);
            if (this.format >= 3) {
                this.numTotalDocs = (int)(this.tvx.length() >> 4);
            } else {
                assert ((this.tvx.length() - 4L) % 8L == 0L);
                this.numTotalDocs = (int)(this.tvx.length() >> 3);
            }
            if (-1 == docStoreOffset) {
                this.docStoreOffset = 0;
                this.size = this.numTotalDocs;
                assert (size == 0 || this.numTotalDocs == size);
            } else {
                this.docStoreOffset = docStoreOffset;
                this.size = size;
                assert (this.numTotalDocs >= size + docStoreOffset) : "numTotalDocs=" + this.numTotalDocs + " size=" + size + " docStoreOffset=" + docStoreOffset;
            }
            this.fieldInfos = fieldInfos;
            success = true;
        }
        finally {
            if (!success) {
                this.close();
            }
        }
    }

    IndexInput getTvdStream() {
        return this.tvd;
    }

    IndexInput getTvfStream() {
        return this.tvf;
    }

    private final void seekTvx(int docNum) throws IOException {
        if (this.format < 3) {
            this.tvx.seek((long)(docNum + this.docStoreOffset) * 8L + 4L);
        } else {
            this.tvx.seek((long)(docNum + this.docStoreOffset) * 16L + 4L);
        }
    }

    boolean canReadRawDocs() {
        return this.format >= 4;
    }

    final void rawDocs(int[] tvdLengths, int[] tvfLengths, int startDocID, int numDocs) throws IOException {
        if (this.tvx == null) {
            Arrays.fill(tvdLengths, 0);
            Arrays.fill(tvfLengths, 0);
            return;
        }
        if (this.format < 3) {
            throw new IllegalStateException("cannot read raw docs with older term vector formats");
        }
        this.seekTvx(startDocID);
        long tvdPosition = this.tvx.readLong();
        this.tvd.seek(tvdPosition);
        long tvfPosition = this.tvx.readLong();
        this.tvf.seek(tvfPosition);
        long lastTvdPosition = tvdPosition;
        long lastTvfPosition = tvfPosition;
        for (int count = 0; count < numDocs; ++count) {
            int docID = this.docStoreOffset + startDocID + count + 1;
            assert (docID <= this.numTotalDocs);
            if (docID < this.numTotalDocs) {
                tvdPosition = this.tvx.readLong();
                tvfPosition = this.tvx.readLong();
            } else {
                tvdPosition = this.tvd.length();
                tvfPosition = this.tvf.length();
                assert (count == numDocs - 1);
            }
            tvdLengths[count] = (int)(tvdPosition - lastTvdPosition);
            tvfLengths[count] = (int)(tvfPosition - lastTvfPosition);
            lastTvdPosition = tvdPosition;
            lastTvfPosition = tvfPosition;
        }
    }

    private int checkValidFormat(String fn, IndexInput in) throws CorruptIndexException, IOException {
        int format = in.readInt();
        if (format > 4) {
            throw new IndexFormatTooNewException(in, format, 1, 4);
        }
        return format;
    }

    public void close() throws IOException {
        IOUtils.close(this.tvx, this.tvd, this.tvf);
    }

    int size() {
        return this.size;
    }

    public void get(int docNum, String field, TermVectorMapper mapper) throws IOException {
        if (this.tvx != null) {
            int fieldNumber = this.fieldInfos.fieldNumber(field);
            this.seekTvx(docNum);
            long tvdPosition = this.tvx.readLong();
            this.tvd.seek(tvdPosition);
            int fieldCount = this.tvd.readVInt();
            int number = 0;
            int found = -1;
            for (int i = 0; i < fieldCount; ++i) {
                number = this.format >= 2 ? this.tvd.readVInt() : (number += this.tvd.readVInt());
                if (number != fieldNumber) continue;
                found = i;
            }
            if (found != -1) {
                long position = this.format >= 3 ? this.tvx.readLong() : this.tvd.readVLong();
                for (int i = 1; i <= found; ++i) {
                    position += this.tvd.readVLong();
                }
                mapper.setDocumentNumber(docNum);
                this.readTermVector(field, position, mapper);
            }
        }
    }

    TermFreqVector get(int docNum, String field) throws IOException {
        ParallelArrayTermVectorMapper mapper = new ParallelArrayTermVectorMapper();
        this.get(docNum, field, mapper);
        return mapper.materializeVector();
    }

    private final String[] readFields(int fieldCount) throws IOException {
        int number = 0;
        String[] fields = new String[fieldCount];
        for (int i = 0; i < fieldCount; ++i) {
            number = this.format >= 2 ? this.tvd.readVInt() : (number += this.tvd.readVInt());
            fields[i] = this.fieldInfos.fieldName(number);
        }
        return fields;
    }

    private final long[] readTvfPointers(int fieldCount) throws IOException {
        long position = this.format >= 3 ? this.tvx.readLong() : this.tvd.readVLong();
        long[] tvfPointers = new long[fieldCount];
        tvfPointers[0] = position;
        for (int i = 1; i < fieldCount; ++i) {
            tvfPointers[i] = position += this.tvd.readVLong();
        }
        return tvfPointers;
    }

    TermFreqVector[] get(int docNum) throws IOException {
        SegmentTermVector[] result = null;
        if (this.tvx != null) {
            this.seekTvx(docNum);
            long tvdPosition = this.tvx.readLong();
            this.tvd.seek(tvdPosition);
            int fieldCount = this.tvd.readVInt();
            if (fieldCount != 0) {
                String[] fields = this.readFields(fieldCount);
                long[] tvfPointers = this.readTvfPointers(fieldCount);
                result = this.readTermVectors(docNum, fields, tvfPointers);
            }
        }
        return result;
    }

    public void get(int docNumber, TermVectorMapper mapper) throws IOException {
        if (this.tvx != null) {
            this.seekTvx(docNumber);
            long tvdPosition = this.tvx.readLong();
            this.tvd.seek(tvdPosition);
            int fieldCount = this.tvd.readVInt();
            if (fieldCount != 0) {
                String[] fields = this.readFields(fieldCount);
                long[] tvfPointers = this.readTvfPointers(fieldCount);
                mapper.setDocumentNumber(docNumber);
                this.readTermVectors(fields, tvfPointers, mapper);
            }
        }
    }

    private SegmentTermVector[] readTermVectors(int docNum, String[] fields, long[] tvfPointers) throws IOException {
        SegmentTermVector[] res = new SegmentTermVector[fields.length];
        for (int i = 0; i < fields.length; ++i) {
            ParallelArrayTermVectorMapper mapper = new ParallelArrayTermVectorMapper();
            mapper.setDocumentNumber(docNum);
            this.readTermVector(fields[i], tvfPointers[i], mapper);
            res[i] = (SegmentTermVector)mapper.materializeVector();
        }
        return res;
    }

    private void readTermVectors(String[] fields, long[] tvfPointers, TermVectorMapper mapper) throws IOException {
        for (int i = 0; i < fields.length; ++i) {
            this.readTermVector(fields[i], tvfPointers[i], mapper);
        }
    }

    private void readTermVector(String field, long tvfPointer, TermVectorMapper mapper) throws IOException {
        byte[] byteBuffer;
        char[] charBuffer;
        boolean preUTF8;
        boolean storeOffsets;
        boolean storePositions;
        this.tvf.seek(tvfPointer);
        int numTerms = this.tvf.readVInt();
        if (numTerms == 0) {
            return;
        }
        if (this.format >= 2) {
            byte bits = this.tvf.readByte();
            storePositions = (bits & 1) != 0;
            storeOffsets = (bits & 2) != 0;
        } else {
            this.tvf.readVInt();
            storePositions = false;
            storeOffsets = false;
        }
        mapper.setExpectations(field, numTerms, storeOffsets, storePositions);
        int start = 0;
        int deltaLength = 0;
        int totalLength = 0;
        boolean bl = preUTF8 = this.format < 4;
        if (preUTF8) {
            charBuffer = new char[10];
            byteBuffer = null;
        } else {
            charBuffer = null;
            byteBuffer = new byte[20];
        }
        for (int i = 0; i < numTerms; ++i) {
            int j;
            String term;
            start = this.tvf.readVInt();
            deltaLength = this.tvf.readVInt();
            totalLength = start + deltaLength;
            if (preUTF8) {
                if (charBuffer.length < totalLength) {
                    charBuffer = ArrayUtil.grow(charBuffer, totalLength);
                }
                this.tvf.readChars(charBuffer, start, deltaLength);
                term = new String(charBuffer, 0, totalLength);
            } else {
                if (byteBuffer.length < totalLength) {
                    byteBuffer = ArrayUtil.grow(byteBuffer, totalLength);
                }
                this.tvf.readBytes(byteBuffer, start, deltaLength);
                term = new String(byteBuffer, 0, totalLength, "UTF-8");
            }
            int freq = this.tvf.readVInt();
            int[] positions = null;
            if (storePositions) {
                if (!mapper.isIgnoringPositions()) {
                    positions = new int[freq];
                    int prevPosition = 0;
                    for (j = 0; j < freq; ++j) {
                        positions[j] = prevPosition + this.tvf.readVInt();
                        prevPosition = positions[j];
                    }
                } else {
                    for (int j2 = 0; j2 < freq; ++j2) {
                        this.tvf.readVInt();
                    }
                }
            }
            TermVectorOffsetInfo[] offsets = null;
            if (storeOffsets) {
                if (!mapper.isIgnoringOffsets()) {
                    offsets = new TermVectorOffsetInfo[freq];
                    int prevOffset = 0;
                    for (int j3 = 0; j3 < freq; ++j3) {
                        int startOffset = prevOffset + this.tvf.readVInt();
                        int endOffset = startOffset + this.tvf.readVInt();
                        offsets[j3] = new TermVectorOffsetInfo(startOffset, endOffset);
                        prevOffset = endOffset;
                    }
                } else {
                    for (j = 0; j < freq; ++j) {
                        this.tvf.readVInt();
                        this.tvf.readVInt();
                    }
                }
            }
            mapper.map(term, freq, offsets, positions);
        }
    }

    protected Object clone() throws CloneNotSupportedException {
        TermVectorsReader clone = (TermVectorsReader)super.clone();
        if (this.tvx != null && this.tvd != null && this.tvf != null) {
            clone.tvx = (IndexInput)this.tvx.clone();
            clone.tvd = (IndexInput)this.tvd.clone();
            clone.tvf = (IndexInput)this.tvf.clone();
        }
        return clone;
    }
}


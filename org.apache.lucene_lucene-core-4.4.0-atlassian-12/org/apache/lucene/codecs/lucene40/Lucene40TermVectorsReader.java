/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene40;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.codecs.TermVectorsReader;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;

public class Lucene40TermVectorsReader
extends TermVectorsReader
implements Closeable {
    static final byte STORE_POSITIONS_WITH_TERMVECTOR = 1;
    static final byte STORE_OFFSET_WITH_TERMVECTOR = 2;
    static final byte STORE_PAYLOAD_WITH_TERMVECTOR = 4;
    static final String VECTORS_FIELDS_EXTENSION = "tvf";
    static final String VECTORS_DOCUMENTS_EXTENSION = "tvd";
    static final String VECTORS_INDEX_EXTENSION = "tvx";
    static final String CODEC_NAME_FIELDS = "Lucene40TermVectorsFields";
    static final String CODEC_NAME_DOCS = "Lucene40TermVectorsDocs";
    static final String CODEC_NAME_INDEX = "Lucene40TermVectorsIndex";
    static final int VERSION_NO_PAYLOADS = 0;
    static final int VERSION_PAYLOADS = 1;
    static final int VERSION_START = 0;
    static final int VERSION_CURRENT = 1;
    static final long HEADER_LENGTH_FIELDS = CodecUtil.headerLength("Lucene40TermVectorsFields");
    static final long HEADER_LENGTH_DOCS = CodecUtil.headerLength("Lucene40TermVectorsDocs");
    static final long HEADER_LENGTH_INDEX = CodecUtil.headerLength("Lucene40TermVectorsIndex");
    private FieldInfos fieldInfos;
    private IndexInput tvx;
    private IndexInput tvd;
    private IndexInput tvf;
    private int size;
    private int numTotalDocs;

    Lucene40TermVectorsReader(FieldInfos fieldInfos, IndexInput tvx, IndexInput tvd, IndexInput tvf, int size, int numTotalDocs) {
        this.fieldInfos = fieldInfos;
        this.tvx = tvx;
        this.tvd = tvd;
        this.tvf = tvf;
        this.size = size;
        this.numTotalDocs = numTotalDocs;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Lucene40TermVectorsReader(Directory d, SegmentInfo si, FieldInfos fieldInfos, IOContext context) throws IOException {
        String segment = si.name;
        int size = si.getDocCount();
        boolean success = false;
        try {
            String idxName = IndexFileNames.segmentFileName(segment, "", VECTORS_INDEX_EXTENSION);
            this.tvx = d.openInput(idxName, context);
            int tvxVersion = CodecUtil.checkHeader(this.tvx, CODEC_NAME_INDEX, 0, 1);
            String fn = IndexFileNames.segmentFileName(segment, "", VECTORS_DOCUMENTS_EXTENSION);
            this.tvd = d.openInput(fn, context);
            int tvdVersion = CodecUtil.checkHeader(this.tvd, CODEC_NAME_DOCS, 0, 1);
            fn = IndexFileNames.segmentFileName(segment, "", VECTORS_FIELDS_EXTENSION);
            this.tvf = d.openInput(fn, context);
            int tvfVersion = CodecUtil.checkHeader(this.tvf, CODEC_NAME_FIELDS, 0, 1);
            assert (HEADER_LENGTH_INDEX == this.tvx.getFilePointer());
            assert (HEADER_LENGTH_DOCS == this.tvd.getFilePointer());
            assert (HEADER_LENGTH_FIELDS == this.tvf.getFilePointer());
            assert (tvxVersion == tvdVersion);
            assert (tvxVersion == tvfVersion);
            this.size = this.numTotalDocs = (int)(this.tvx.length() - HEADER_LENGTH_INDEX >> 4);
            assert (size == 0 || this.numTotalDocs == size);
            this.fieldInfos = fieldInfos;
            success = true;
        }
        finally {
            if (!success) {
                try {
                    this.close();
                }
                catch (Throwable throwable) {}
            }
        }
    }

    IndexInput getTvdStream() {
        return this.tvd;
    }

    IndexInput getTvfStream() {
        return this.tvf;
    }

    void seekTvx(int docNum) throws IOException {
        this.tvx.seek((long)docNum * 16L + HEADER_LENGTH_INDEX);
    }

    final void rawDocs(int[] tvdLengths, int[] tvfLengths, int startDocID, int numDocs) throws IOException {
        if (this.tvx == null) {
            Arrays.fill(tvdLengths, 0);
            Arrays.fill(tvfLengths, 0);
            return;
        }
        this.seekTvx(startDocID);
        long tvdPosition = this.tvx.readLong();
        this.tvd.seek(tvdPosition);
        long tvfPosition = this.tvx.readLong();
        this.tvf.seek(tvfPosition);
        long lastTvdPosition = tvdPosition;
        long lastTvfPosition = tvfPosition;
        for (int count = 0; count < numDocs; ++count) {
            int docID = startDocID + count + 1;
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

    @Override
    public void close() throws IOException {
        IOUtils.close(this.tvx, this.tvd, this.tvf);
    }

    int size() {
        return this.size;
    }

    @Override
    public Fields get(int docID) throws IOException {
        if (this.tvx != null) {
            TVFields fields = new TVFields(docID);
            if (((Fields)fields).size() == 0) {
                return null;
            }
            return fields;
        }
        return null;
    }

    @Override
    public TermVectorsReader clone() {
        IndexInput cloneTvx = null;
        IndexInput cloneTvd = null;
        IndexInput cloneTvf = null;
        if (this.tvx != null && this.tvd != null && this.tvf != null) {
            cloneTvx = this.tvx.clone();
            cloneTvd = this.tvd.clone();
            cloneTvf = this.tvf.clone();
        }
        return new Lucene40TermVectorsReader(this.fieldInfos, cloneTvx, cloneTvd, cloneTvf, this.size, this.numTotalDocs);
    }

    private static class TVDocsAndPositionsEnum
    extends DocsAndPositionsEnum {
        private boolean didNext;
        private int doc = -1;
        private int nextPos;
        private Bits liveDocs;
        private int[] positions;
        private int[] startOffsets;
        private int[] endOffsets;
        private int[] payloadOffsets;
        private BytesRef payload = new BytesRef();
        private byte[] payloadBytes;

        private TVDocsAndPositionsEnum() {
        }

        @Override
        public int freq() throws IOException {
            if (this.positions != null) {
                return this.positions.length;
            }
            assert (this.startOffsets != null);
            return this.startOffsets.length;
        }

        @Override
        public int docID() {
            return this.doc;
        }

        @Override
        public int nextDoc() {
            if (!this.didNext && (this.liveDocs == null || this.liveDocs.get(0))) {
                this.didNext = true;
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

        public void reset(Bits liveDocs, int[] positions, int[] startOffsets, int[] endOffsets, int[] payloadLengths, byte[] payloadBytes) {
            this.liveDocs = liveDocs;
            this.positions = positions;
            this.startOffsets = startOffsets;
            this.endOffsets = endOffsets;
            this.payloadOffsets = payloadLengths;
            this.payloadBytes = payloadBytes;
            this.doc = -1;
            this.didNext = false;
            this.nextPos = 0;
        }

        @Override
        public BytesRef getPayload() {
            int end;
            if (this.payloadOffsets == null) {
                return null;
            }
            int off = this.payloadOffsets[this.nextPos - 1];
            int n = end = this.nextPos == this.payloadOffsets.length ? this.payloadBytes.length : this.payloadOffsets[this.nextPos];
            if (end - off == 0) {
                return null;
            }
            this.payload.bytes = this.payloadBytes;
            this.payload.offset = off;
            this.payload.length = end - off;
            return this.payload;
        }

        @Override
        public int nextPosition() {
            assert (this.positions != null && this.nextPos < this.positions.length || this.startOffsets != null && this.nextPos < this.startOffsets.length);
            if (this.positions != null) {
                return this.positions[this.nextPos++];
            }
            ++this.nextPos;
            return -1;
        }

        @Override
        public int startOffset() {
            if (this.startOffsets == null) {
                return -1;
            }
            return this.startOffsets[this.nextPos - 1];
        }

        @Override
        public int endOffset() {
            if (this.endOffsets == null) {
                return -1;
            }
            return this.endOffsets[this.nextPos - 1];
        }

        @Override
        public long cost() {
            return 1L;
        }
    }

    private static class TVDocsEnum
    extends DocsEnum {
        private boolean didNext;
        private int doc = -1;
        private int freq;
        private Bits liveDocs;

        private TVDocsEnum() {
        }

        @Override
        public int freq() throws IOException {
            return this.freq;
        }

        @Override
        public int docID() {
            return this.doc;
        }

        @Override
        public int nextDoc() {
            if (!this.didNext && (this.liveDocs == null || this.liveDocs.get(0))) {
                this.didNext = true;
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

        public void reset(Bits liveDocs, int freq) {
            this.liveDocs = liveDocs;
            this.freq = freq;
            this.doc = -1;
            this.didNext = false;
        }

        @Override
        public long cost() {
            return 1L;
        }
    }

    private class TVTermsEnum
    extends TermsEnum {
        private final IndexInput origTVF;
        private final IndexInput tvf;
        private int numTerms;
        private int nextTerm;
        private int freq;
        private BytesRef lastTerm = new BytesRef();
        private BytesRef term = new BytesRef();
        private boolean storePositions;
        private boolean storeOffsets;
        private boolean storePayloads;
        private long tvfFP;
        private int[] positions;
        private int[] startOffsets;
        private int[] endOffsets;
        private int[] payloadOffsets;
        private int lastPayloadLength;
        private byte[] payloadData;

        public TVTermsEnum() {
            this.origTVF = Lucene40TermVectorsReader.this.tvf;
            this.tvf = this.origTVF.clone();
        }

        public boolean canReuse(IndexInput tvf) {
            return tvf == this.origTVF;
        }

        public void reset(int numTerms, long tvfFPStart, boolean storePositions, boolean storeOffsets, boolean storePayloads) throws IOException {
            this.numTerms = numTerms;
            this.storePositions = storePositions;
            this.storeOffsets = storeOffsets;
            this.storePayloads = storePayloads;
            this.nextTerm = 0;
            this.tvf.seek(tvfFPStart);
            this.tvfFP = tvfFPStart;
            this.positions = null;
            this.startOffsets = null;
            this.endOffsets = null;
            this.payloadOffsets = null;
            this.payloadData = null;
            this.lastPayloadLength = -1;
        }

        @Override
        public TermsEnum.SeekStatus seekCeil(BytesRef text, boolean useCache) throws IOException {
            int cmp;
            if (this.nextTerm != 0) {
                cmp = text.compareTo(this.term);
                if (cmp < 0) {
                    this.nextTerm = 0;
                    this.tvf.seek(this.tvfFP);
                } else if (cmp == 0) {
                    return TermsEnum.SeekStatus.FOUND;
                }
            }
            while (this.next() != null) {
                cmp = text.compareTo(this.term);
                if (cmp < 0) {
                    return TermsEnum.SeekStatus.NOT_FOUND;
                }
                if (cmp != 0) continue;
                return TermsEnum.SeekStatus.FOUND;
            }
            return TermsEnum.SeekStatus.END;
        }

        @Override
        public void seekExact(long ord) {
            throw new UnsupportedOperationException();
        }

        @Override
        public BytesRef next() throws IOException {
            int posUpto;
            if (this.nextTerm >= this.numTerms) {
                return null;
            }
            this.term.copyBytes(this.lastTerm);
            int start = this.tvf.readVInt();
            int deltaLen = this.tvf.readVInt();
            this.term.length = start + deltaLen;
            this.term.grow(this.term.length);
            this.tvf.readBytes(this.term.bytes, start, deltaLen);
            this.freq = this.tvf.readVInt();
            if (this.storePayloads) {
                this.positions = new int[this.freq];
                this.payloadOffsets = new int[this.freq];
                int totalPayloadLength = 0;
                int pos = 0;
                for (int posUpto2 = 0; posUpto2 < this.freq; ++posUpto2) {
                    int code = this.tvf.readVInt();
                    this.positions[posUpto2] = pos += code >>> 1;
                    if ((code & 1) != 0) {
                        this.lastPayloadLength = this.tvf.readVInt();
                    }
                    this.payloadOffsets[posUpto2] = totalPayloadLength;
                    assert ((totalPayloadLength += this.lastPayloadLength) >= 0);
                }
                this.payloadData = new byte[totalPayloadLength];
                this.tvf.readBytes(this.payloadData, 0, this.payloadData.length);
            } else if (this.storePositions) {
                this.positions = new int[this.freq];
                int pos = 0;
                for (posUpto = 0; posUpto < this.freq; ++posUpto) {
                    this.positions[posUpto] = pos += this.tvf.readVInt();
                }
            }
            if (this.storeOffsets) {
                this.startOffsets = new int[this.freq];
                this.endOffsets = new int[this.freq];
                int offset = 0;
                for (posUpto = 0; posUpto < this.freq; ++posUpto) {
                    this.startOffsets[posUpto] = offset + this.tvf.readVInt();
                    offset = this.endOffsets[posUpto] = this.startOffsets[posUpto] + this.tvf.readVInt();
                }
            }
            this.lastTerm.copyBytes(this.term);
            ++this.nextTerm;
            return this.term;
        }

        @Override
        public BytesRef term() {
            return this.term;
        }

        @Override
        public long ord() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int docFreq() {
            return 1;
        }

        @Override
        public long totalTermFreq() {
            return this.freq;
        }

        @Override
        public DocsEnum docs(Bits liveDocs, DocsEnum reuse, int flags) throws IOException {
            TVDocsEnum docsEnum = reuse != null && reuse instanceof TVDocsEnum ? (TVDocsEnum)reuse : new TVDocsEnum();
            docsEnum.reset(liveDocs, this.freq);
            return docsEnum;
        }

        @Override
        public DocsAndPositionsEnum docsAndPositions(Bits liveDocs, DocsAndPositionsEnum reuse, int flags) throws IOException {
            if (!this.storePositions && !this.storeOffsets) {
                return null;
            }
            TVDocsAndPositionsEnum docsAndPositionsEnum = reuse != null && reuse instanceof TVDocsAndPositionsEnum ? (TVDocsAndPositionsEnum)reuse : new TVDocsAndPositionsEnum();
            docsAndPositionsEnum.reset(liveDocs, this.positions, this.startOffsets, this.endOffsets, this.payloadOffsets, this.payloadData);
            return docsAndPositionsEnum;
        }

        @Override
        public Comparator<BytesRef> getComparator() {
            return BytesRef.getUTF8SortedAsUnicodeComparator();
        }
    }

    private class TVTerms
    extends Terms {
        private final int numTerms;
        private final long tvfFPStart;
        private final boolean storePositions;
        private final boolean storeOffsets;
        private final boolean storePayloads;

        public TVTerms(long tvfFP) throws IOException {
            Lucene40TermVectorsReader.this.tvf.seek(tvfFP);
            this.numTerms = Lucene40TermVectorsReader.this.tvf.readVInt();
            byte bits = Lucene40TermVectorsReader.this.tvf.readByte();
            this.storePositions = (bits & 1) != 0;
            this.storeOffsets = (bits & 2) != 0;
            this.storePayloads = (bits & 4) != 0;
            this.tvfFPStart = Lucene40TermVectorsReader.this.tvf.getFilePointer();
        }

        @Override
        public TermsEnum iterator(TermsEnum reuse) throws IOException {
            TVTermsEnum termsEnum;
            if (reuse instanceof TVTermsEnum) {
                termsEnum = (TVTermsEnum)reuse;
                if (!termsEnum.canReuse(Lucene40TermVectorsReader.this.tvf)) {
                    termsEnum = new TVTermsEnum();
                }
            } else {
                termsEnum = new TVTermsEnum();
            }
            termsEnum.reset(this.numTerms, this.tvfFPStart, this.storePositions, this.storeOffsets, this.storePayloads);
            return termsEnum;
        }

        @Override
        public long size() {
            return this.numTerms;
        }

        @Override
        public long getSumTotalTermFreq() {
            return -1L;
        }

        @Override
        public long getSumDocFreq() {
            return this.numTerms;
        }

        @Override
        public int getDocCount() {
            return 1;
        }

        @Override
        public Comparator<BytesRef> getComparator() {
            return BytesRef.getUTF8SortedAsUnicodeComparator();
        }

        @Override
        public boolean hasOffsets() {
            return this.storeOffsets;
        }

        @Override
        public boolean hasPositions() {
            return this.storePositions;
        }

        @Override
        public boolean hasPayloads() {
            return this.storePayloads;
        }
    }

    private class TVFields
    extends Fields {
        private final int[] fieldNumbers;
        private final long[] fieldFPs;
        private final Map<Integer, Integer> fieldNumberToIndex = new HashMap<Integer, Integer>();

        public TVFields(int docID) throws IOException {
            Lucene40TermVectorsReader.this.seekTvx(docID);
            Lucene40TermVectorsReader.this.tvd.seek(Lucene40TermVectorsReader.this.tvx.readLong());
            int fieldCount = Lucene40TermVectorsReader.this.tvd.readVInt();
            assert (fieldCount >= 0);
            if (fieldCount != 0) {
                long position;
                this.fieldNumbers = new int[fieldCount];
                this.fieldFPs = new long[fieldCount];
                for (int fieldUpto = 0; fieldUpto < fieldCount; ++fieldUpto) {
                    int fieldNumber;
                    this.fieldNumbers[fieldUpto] = fieldNumber = Lucene40TermVectorsReader.this.tvd.readVInt();
                    this.fieldNumberToIndex.put(fieldNumber, fieldUpto);
                }
                this.fieldFPs[0] = position = Lucene40TermVectorsReader.this.tvx.readLong();
                for (int fieldUpto = 1; fieldUpto < fieldCount; ++fieldUpto) {
                    this.fieldFPs[fieldUpto] = position += Lucene40TermVectorsReader.this.tvd.readVLong();
                }
            } else {
                this.fieldNumbers = null;
                this.fieldFPs = null;
            }
        }

        @Override
        public Iterator<String> iterator() {
            return new Iterator<String>(){
                private int fieldUpto;

                @Override
                public String next() {
                    if (TVFields.this.fieldNumbers != null && this.fieldUpto < TVFields.this.fieldNumbers.length) {
                        return ((Lucene40TermVectorsReader)Lucene40TermVectorsReader.this).fieldInfos.fieldInfo((int)((TVFields)TVFields.this).fieldNumbers[this.fieldUpto++]).name;
                    }
                    throw new NoSuchElementException();
                }

                @Override
                public boolean hasNext() {
                    return TVFields.this.fieldNumbers != null && this.fieldUpto < TVFields.this.fieldNumbers.length;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        @Override
        public Terms terms(String field) throws IOException {
            FieldInfo fieldInfo = Lucene40TermVectorsReader.this.fieldInfos.fieldInfo(field);
            if (fieldInfo == null) {
                return null;
            }
            Integer fieldIndex = this.fieldNumberToIndex.get(fieldInfo.number);
            if (fieldIndex == null) {
                return null;
            }
            return new TVTerms(this.fieldFPs[fieldIndex]);
        }

        @Override
        public int size() {
            if (this.fieldNumbers == null) {
                return 0;
            }
            return this.fieldNumbers.length;
        }
    }
}


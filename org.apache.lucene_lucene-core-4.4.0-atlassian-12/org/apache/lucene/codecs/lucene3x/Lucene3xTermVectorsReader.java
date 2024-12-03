/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene3x;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.lucene.codecs.TermVectorsReader;
import org.apache.lucene.codecs.lucene3x.Lucene3xSegmentInfoFormat;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.IndexFormatTooNewException;
import org.apache.lucene.index.IndexFormatTooOldException;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.CompoundFileDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;

@Deprecated
class Lucene3xTermVectorsReader
extends TermVectorsReader {
    static final int FORMAT_UTF8_LENGTH_IN_BYTES = 4;
    public static final int FORMAT_CURRENT = 4;
    public static final int FORMAT_MINIMUM = 4;
    static final int FORMAT_SIZE = 4;
    public static final byte STORE_POSITIONS_WITH_TERMVECTOR = 1;
    public static final byte STORE_OFFSET_WITH_TERMVECTOR = 2;
    public static final String VECTORS_FIELDS_EXTENSION = "tvf";
    public static final String VECTORS_DOCUMENTS_EXTENSION = "tvd";
    public static final String VECTORS_INDEX_EXTENSION = "tvx";
    private FieldInfos fieldInfos;
    private IndexInput tvx;
    private IndexInput tvd;
    private IndexInput tvf;
    private int size;
    private int numTotalDocs;
    private int docStoreOffset;
    private final CompoundFileDirectory storeCFSReader;
    private final int format;

    Lucene3xTermVectorsReader(FieldInfos fieldInfos, IndexInput tvx, IndexInput tvd, IndexInput tvf, int size, int numTotalDocs, int docStoreOffset, int format) {
        this.fieldInfos = fieldInfos;
        this.tvx = tvx;
        this.tvd = tvd;
        this.tvf = tvf;
        this.size = size;
        this.numTotalDocs = numTotalDocs;
        this.docStoreOffset = docStoreOffset;
        this.format = format;
        this.storeCFSReader = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Lucene3xTermVectorsReader(Directory d, SegmentInfo si, FieldInfos fieldInfos, IOContext context) throws CorruptIndexException, IOException {
        String segment = Lucene3xSegmentInfoFormat.getDocStoreSegment(si);
        int docStoreOffset = Lucene3xSegmentInfoFormat.getDocStoreOffset(si);
        int size = si.getDocCount();
        boolean success = false;
        try {
            if (docStoreOffset != -1 && Lucene3xSegmentInfoFormat.getDocStoreIsCompoundFile(si)) {
                this.storeCFSReader = new CompoundFileDirectory(si.dir, IndexFileNames.segmentFileName(segment, "", "cfx"), context, false);
                d = this.storeCFSReader;
            } else {
                this.storeCFSReader = null;
            }
            String idxName = IndexFileNames.segmentFileName(segment, "", VECTORS_INDEX_EXTENSION);
            this.tvx = d.openInput(idxName, context);
            this.format = this.checkValidFormat(this.tvx);
            String fn = IndexFileNames.segmentFileName(segment, "", VECTORS_DOCUMENTS_EXTENSION);
            this.tvd = d.openInput(fn, context);
            int tvdFormat = this.checkValidFormat(this.tvd);
            fn = IndexFileNames.segmentFileName(segment, "", VECTORS_FIELDS_EXTENSION);
            this.tvf = d.openInput(fn, context);
            int tvfFormat = this.checkValidFormat(this.tvf);
            assert (this.format == tvdFormat);
            assert (this.format == tvfFormat);
            this.numTotalDocs = (int)(this.tvx.length() >> 4);
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
                try {
                    this.close();
                }
                catch (Throwable throwable) {}
            }
        }
    }

    void seekTvx(int docNum) throws IOException {
        this.tvx.seek((long)(docNum + this.docStoreOffset) * 16L + 4L);
    }

    private int checkValidFormat(IndexInput in) throws CorruptIndexException, IOException {
        int format = in.readInt();
        if (format < 4) {
            throw new IndexFormatTooOldException(in, format, 4, 4);
        }
        if (format > 4) {
            throw new IndexFormatTooNewException(in, format, 4, 4);
        }
        return format;
    }

    @Override
    public void close() throws IOException {
        IOUtils.close(this.tvx, this.tvd, this.tvf, this.storeCFSReader);
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
        return new Lucene3xTermVectorsReader(this.fieldInfos, cloneTvx, cloneTvd, cloneTvf, this.size, this.numTotalDocs, this.docStoreOffset, this.format);
    }

    protected boolean sortTermsByUnicode() {
        return true;
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
        public int advance(int target) {
            if (!this.didNext && target == 0) {
                return this.nextDoc();
            }
            this.doc = Integer.MAX_VALUE;
            return Integer.MAX_VALUE;
        }

        public void reset(Bits liveDocs, TermAndPostings termAndPostings) {
            this.liveDocs = liveDocs;
            this.positions = termAndPostings.positions;
            this.startOffsets = termAndPostings.startOffsets;
            this.endOffsets = termAndPostings.endOffsets;
            this.doc = -1;
            this.didNext = false;
            this.nextPos = 0;
        }

        @Override
        public BytesRef getPayload() {
            return null;
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
            if (this.startOffsets != null) {
                return this.startOffsets[this.nextPos - 1];
            }
            return -1;
        }

        @Override
        public int endOffset() {
            if (this.endOffsets != null) {
                return this.endOffsets[this.nextPos - 1];
            }
            return -1;
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
        public int advance(int target) {
            if (!this.didNext && target == 0) {
                return this.nextDoc();
            }
            this.doc = Integer.MAX_VALUE;
            return Integer.MAX_VALUE;
        }

        public void reset(Bits liveDocs, TermAndPostings termAndPostings) {
            this.liveDocs = liveDocs;
            this.freq = termAndPostings.freq;
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
        private boolean unicodeSortOrder;
        private final IndexInput origTVF;
        private final IndexInput tvf;
        private int numTerms;
        private int currentTerm;
        private boolean storePositions;
        private boolean storeOffsets;
        private TermAndPostings[] termAndPostings;

        public TVTermsEnum() throws IOException {
            this.origTVF = Lucene3xTermVectorsReader.this.tvf;
            this.tvf = this.origTVF.clone();
        }

        public boolean canReuse(IndexInput tvf) {
            return tvf == this.origTVF;
        }

        public void reset(int numTerms, long tvfFPStart, boolean storePositions, boolean storeOffsets, boolean unicodeSortOrder) throws IOException {
            this.numTerms = numTerms;
            this.storePositions = storePositions;
            this.storeOffsets = storeOffsets;
            this.currentTerm = -1;
            this.tvf.seek(tvfFPStart);
            this.unicodeSortOrder = unicodeSortOrder;
            this.readVectors();
            if (unicodeSortOrder) {
                Arrays.sort(this.termAndPostings, new Comparator<TermAndPostings>(){

                    @Override
                    public int compare(TermAndPostings left, TermAndPostings right) {
                        return left.term.compareTo(right.term);
                    }
                });
            }
        }

        private void readVectors() throws IOException {
            this.termAndPostings = new TermAndPostings[this.numTerms];
            BytesRef lastTerm = new BytesRef();
            for (int i = 0; i < this.numTerms; ++i) {
                int freq;
                TermAndPostings t = new TermAndPostings();
                BytesRef term = new BytesRef();
                term.copyBytes(lastTerm);
                int start = this.tvf.readVInt();
                int deltaLen = this.tvf.readVInt();
                term.length = start + deltaLen;
                term.grow(term.length);
                this.tvf.readBytes(term.bytes, start, deltaLen);
                t.term = term;
                t.freq = freq = this.tvf.readVInt();
                if (this.storePositions) {
                    int[] positions = new int[freq];
                    int pos = 0;
                    for (int posUpto = 0; posUpto < freq; ++posUpto) {
                        int delta = this.tvf.readVInt();
                        if (delta == -1) {
                            delta = 0;
                        }
                        positions[posUpto] = pos += delta;
                    }
                    t.positions = positions;
                }
                if (this.storeOffsets) {
                    int[] startOffsets = new int[freq];
                    int[] endOffsets = new int[freq];
                    int offset = 0;
                    for (int posUpto = 0; posUpto < freq; ++posUpto) {
                        startOffsets[posUpto] = offset + this.tvf.readVInt();
                        offset = endOffsets[posUpto] = startOffsets[posUpto] + this.tvf.readVInt();
                    }
                    t.startOffsets = startOffsets;
                    t.endOffsets = endOffsets;
                }
                lastTerm.copyBytes(term);
                this.termAndPostings[i] = t;
            }
        }

        @Override
        public TermsEnum.SeekStatus seekCeil(BytesRef text, boolean useCache) throws IOException {
            Comparator<BytesRef> comparator = this.getComparator();
            for (int i = 0; i < this.numTerms; ++i) {
                int cmp = comparator.compare(text, this.termAndPostings[i].term);
                if (cmp < 0) {
                    this.currentTerm = i;
                    return TermsEnum.SeekStatus.NOT_FOUND;
                }
                if (cmp != 0) continue;
                this.currentTerm = i;
                return TermsEnum.SeekStatus.FOUND;
            }
            this.currentTerm = this.termAndPostings.length;
            return TermsEnum.SeekStatus.END;
        }

        @Override
        public void seekExact(long ord) {
            throw new UnsupportedOperationException();
        }

        @Override
        public BytesRef next() throws IOException {
            if (++this.currentTerm >= this.numTerms) {
                return null;
            }
            return this.term();
        }

        @Override
        public BytesRef term() {
            return this.termAndPostings[this.currentTerm].term;
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
            return this.termAndPostings[this.currentTerm].freq;
        }

        @Override
        public DocsEnum docs(Bits liveDocs, DocsEnum reuse, int flags) throws IOException {
            TVDocsEnum docsEnum = reuse != null && reuse instanceof TVDocsEnum ? (TVDocsEnum)reuse : new TVDocsEnum();
            docsEnum.reset(liveDocs, this.termAndPostings[this.currentTerm]);
            return docsEnum;
        }

        @Override
        public DocsAndPositionsEnum docsAndPositions(Bits liveDocs, DocsAndPositionsEnum reuse, int flags) throws IOException {
            if (!this.storePositions && !this.storeOffsets) {
                return null;
            }
            TVDocsAndPositionsEnum docsAndPositionsEnum = reuse != null && reuse instanceof TVDocsAndPositionsEnum ? (TVDocsAndPositionsEnum)reuse : new TVDocsAndPositionsEnum();
            docsAndPositionsEnum.reset(liveDocs, this.termAndPostings[this.currentTerm]);
            return docsAndPositionsEnum;
        }

        @Override
        public Comparator<BytesRef> getComparator() {
            if (this.unicodeSortOrder) {
                return BytesRef.getUTF8SortedAsUnicodeComparator();
            }
            return BytesRef.getUTF8SortedAsUTF16Comparator();
        }
    }

    static class TermAndPostings {
        BytesRef term;
        int freq;
        int[] positions;
        int[] startOffsets;
        int[] endOffsets;

        TermAndPostings() {
        }
    }

    private class TVTerms
    extends Terms {
        private final int numTerms;
        private final long tvfFPStart;
        private final boolean storePositions;
        private final boolean storeOffsets;
        private final boolean unicodeSortOrder;

        public TVTerms(long tvfFP) throws IOException {
            Lucene3xTermVectorsReader.this.tvf.seek(tvfFP);
            this.numTerms = Lucene3xTermVectorsReader.this.tvf.readVInt();
            byte bits = Lucene3xTermVectorsReader.this.tvf.readByte();
            this.storePositions = (bits & 1) != 0;
            this.storeOffsets = (bits & 2) != 0;
            this.tvfFPStart = Lucene3xTermVectorsReader.this.tvf.getFilePointer();
            this.unicodeSortOrder = Lucene3xTermVectorsReader.this.sortTermsByUnicode();
        }

        @Override
        public TermsEnum iterator(TermsEnum reuse) throws IOException {
            TVTermsEnum termsEnum;
            if (reuse instanceof TVTermsEnum) {
                termsEnum = (TVTermsEnum)reuse;
                if (!termsEnum.canReuse(Lucene3xTermVectorsReader.this.tvf)) {
                    termsEnum = new TVTermsEnum();
                }
            } else {
                termsEnum = new TVTermsEnum();
            }
            termsEnum.reset(this.numTerms, this.tvfFPStart, this.storePositions, this.storeOffsets, this.unicodeSortOrder);
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
            if (this.unicodeSortOrder) {
                return BytesRef.getUTF8SortedAsUnicodeComparator();
            }
            return BytesRef.getUTF8SortedAsUTF16Comparator();
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
            return false;
        }
    }

    private class TVFields
    extends Fields {
        private final int[] fieldNumbers;
        private final long[] fieldFPs;
        private final Map<Integer, Integer> fieldNumberToIndex = new HashMap<Integer, Integer>();

        public TVFields(int docID) throws IOException {
            Lucene3xTermVectorsReader.this.seekTvx(docID);
            Lucene3xTermVectorsReader.this.tvd.seek(Lucene3xTermVectorsReader.this.tvx.readLong());
            int fieldCount = Lucene3xTermVectorsReader.this.tvd.readVInt();
            assert (fieldCount >= 0);
            if (fieldCount != 0) {
                long position;
                this.fieldNumbers = new int[fieldCount];
                this.fieldFPs = new long[fieldCount];
                for (int fieldUpto = 0; fieldUpto < fieldCount; ++fieldUpto) {
                    int fieldNumber;
                    this.fieldNumbers[fieldUpto] = fieldNumber = Lucene3xTermVectorsReader.this.tvd.readVInt();
                    this.fieldNumberToIndex.put(fieldNumber, fieldUpto);
                }
                this.fieldFPs[0] = position = Lucene3xTermVectorsReader.this.tvx.readLong();
                for (int fieldUpto = 1; fieldUpto < fieldCount; ++fieldUpto) {
                    this.fieldFPs[fieldUpto] = position += Lucene3xTermVectorsReader.this.tvd.readVLong();
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
                        return ((Lucene3xTermVectorsReader)Lucene3xTermVectorsReader.this).fieldInfos.fieldInfo((int)((TVFields)TVFields.this).fieldNumbers[this.fieldUpto++]).name;
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
            FieldInfo fieldInfo = Lucene3xTermVectorsReader.this.fieldInfos.fieldInfo(field);
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


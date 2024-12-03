/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene3x;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.apache.lucene.codecs.FieldsProducer;
import org.apache.lucene.codecs.lucene3x.SegmentTermDocs;
import org.apache.lucene.codecs.lucene3x.SegmentTermEnum;
import org.apache.lucene.codecs.lucene3x.SegmentTermPositions;
import org.apache.lucene.codecs.lucene3x.TermInfosReader;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.UnicodeUtil;

@Deprecated
class Lucene3xFields
extends FieldsProducer {
    private static final boolean DEBUG_SURROGATES = false;
    public TermInfosReader tis;
    public final TermInfosReader tisNoIndex;
    public final IndexInput freqStream;
    public final IndexInput proxStream;
    private final FieldInfos fieldInfos;
    private final SegmentInfo si;
    final TreeMap<String, FieldInfo> fields = new TreeMap();
    final Map<String, Terms> preTerms = new HashMap<String, Terms>();
    private final Directory dir;
    private final IOContext context;
    private Directory cfsReader;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Lucene3xFields(Directory dir, FieldInfos fieldInfos, SegmentInfo info, IOContext context, int indexDivisor) throws IOException {
        this.si = info;
        if (indexDivisor < 0) {
            indexDivisor = -indexDivisor;
        }
        boolean success = false;
        try {
            TermInfosReader r = new TermInfosReader(dir, info.name, fieldInfos, context, indexDivisor);
            if (indexDivisor == -1) {
                this.tisNoIndex = r;
            } else {
                this.tisNoIndex = null;
                this.tis = r;
            }
            this.context = context;
            this.fieldInfos = fieldInfos;
            this.freqStream = dir.openInput(IndexFileNames.segmentFileName(info.name, "", "frq"), context);
            boolean anyProx = false;
            for (FieldInfo fi : fieldInfos) {
                if (!fi.isIndexed()) continue;
                this.fields.put(fi.name, fi);
                this.preTerms.put(fi.name, new PreTerms(fi));
                if (fi.getIndexOptions() != FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) continue;
                anyProx = true;
            }
            this.proxStream = anyProx ? dir.openInput(IndexFileNames.segmentFileName(info.name, "", "prx"), context) : null;
            success = true;
        }
        finally {
            if (!success) {
                this.close();
            }
        }
        this.dir = dir;
    }

    protected boolean sortTermsByUnicode() {
        return true;
    }

    @Override
    public Iterator<String> iterator() {
        return Collections.unmodifiableSet(this.fields.keySet()).iterator();
    }

    @Override
    public Terms terms(String field) {
        return this.preTerms.get(field);
    }

    @Override
    public int size() {
        assert (this.preTerms.size() == this.fields.size());
        return this.fields.size();
    }

    @Override
    public long getUniqueTermCount() throws IOException {
        return this.getTermsDict().size();
    }

    private synchronized TermInfosReader getTermsDict() {
        if (this.tis != null) {
            return this.tis;
        }
        return this.tisNoIndex;
    }

    @Override
    public void close() throws IOException {
        IOUtils.close(this.tis, this.tisNoIndex, this.cfsReader, this.freqStream, this.proxStream);
    }

    private final class PreDocsAndPositionsEnum
    extends DocsAndPositionsEnum {
        private final SegmentTermPositions pos;
        private int docID = -1;

        PreDocsAndPositionsEnum() throws IOException {
            this.pos = new SegmentTermPositions(Lucene3xFields.this.freqStream, Lucene3xFields.this.proxStream, Lucene3xFields.this.getTermsDict(), Lucene3xFields.this.fieldInfos);
        }

        IndexInput getFreqStream() {
            return Lucene3xFields.this.freqStream;
        }

        public DocsAndPositionsEnum reset(SegmentTermEnum termEnum, Bits liveDocs) throws IOException {
            this.pos.setLiveDocs(liveDocs);
            this.pos.seek(termEnum);
            this.docID = -1;
            return this;
        }

        @Override
        public int nextDoc() throws IOException {
            if (this.pos.next()) {
                this.docID = this.pos.doc();
                return this.docID;
            }
            this.docID = Integer.MAX_VALUE;
            return Integer.MAX_VALUE;
        }

        @Override
        public int advance(int target) throws IOException {
            if (this.pos.skipTo(target)) {
                this.docID = this.pos.doc();
                return this.docID;
            }
            this.docID = Integer.MAX_VALUE;
            return Integer.MAX_VALUE;
        }

        @Override
        public int freq() throws IOException {
            return this.pos.freq();
        }

        @Override
        public int docID() {
            return this.docID;
        }

        @Override
        public int nextPosition() throws IOException {
            assert (this.docID != Integer.MAX_VALUE);
            return this.pos.nextPosition();
        }

        @Override
        public int startOffset() throws IOException {
            return -1;
        }

        @Override
        public int endOffset() throws IOException {
            return -1;
        }

        @Override
        public BytesRef getPayload() throws IOException {
            return this.pos.getPayload();
        }

        @Override
        public long cost() {
            return this.pos.df;
        }
    }

    private final class PreDocsEnum
    extends DocsEnum {
        private final SegmentTermDocs docs;
        private int docID = -1;

        PreDocsEnum() throws IOException {
            this.docs = new SegmentTermDocs(Lucene3xFields.this.freqStream, Lucene3xFields.this.getTermsDict(), Lucene3xFields.this.fieldInfos);
        }

        IndexInput getFreqStream() {
            return Lucene3xFields.this.freqStream;
        }

        public PreDocsEnum reset(SegmentTermEnum termEnum, Bits liveDocs) throws IOException {
            this.docs.setLiveDocs(liveDocs);
            this.docs.seek(termEnum);
            this.docs.freq = 1;
            this.docID = -1;
            return this;
        }

        @Override
        public int nextDoc() throws IOException {
            if (this.docs.next()) {
                this.docID = this.docs.doc();
                return this.docID;
            }
            this.docID = Integer.MAX_VALUE;
            return Integer.MAX_VALUE;
        }

        @Override
        public int advance(int target) throws IOException {
            if (this.docs.skipTo(target)) {
                this.docID = this.docs.doc();
                return this.docID;
            }
            this.docID = Integer.MAX_VALUE;
            return Integer.MAX_VALUE;
        }

        @Override
        public int freq() throws IOException {
            return this.docs.freq();
        }

        @Override
        public int docID() {
            return this.docID;
        }

        @Override
        public long cost() {
            return this.docs.df;
        }
    }

    private class PreTermsEnum
    extends TermsEnum {
        private SegmentTermEnum termEnum;
        private FieldInfo fieldInfo;
        private String internedFieldName;
        private boolean skipNext;
        private BytesRef current;
        private SegmentTermEnum seekTermEnum;
        private static final byte UTF8_NON_BMP_LEAD = -16;
        private static final byte UTF8_HIGH_BMP_LEAD = -18;
        private final byte[] scratch = new byte[4];
        private final BytesRef prevTerm = new BytesRef();
        private final BytesRef scratchTerm = new BytesRef();
        private int newSuffixStart;
        private boolean unicodeSortOrder;

        private PreTermsEnum() {
        }

        private final boolean isHighBMPChar(byte[] b, int idx) {
            return (b[idx] & 0xFFFFFFEE) == -18;
        }

        private final boolean isNonBMPChar(byte[] b, int idx) {
            return (b[idx] & 0xFFFFFFF0) == -16;
        }

        private boolean seekToNonBMP(SegmentTermEnum te, BytesRef term, int pos) throws IOException {
            boolean matches;
            int savLength = term.length;
            assert (term.offset == 0);
            assert (this.isHighBMPChar(term.bytes, pos));
            if (term.bytes.length < 4 + pos) {
                term.grow(4 + pos);
            }
            this.scratch[0] = term.bytes[pos];
            this.scratch[1] = term.bytes[pos + 1];
            this.scratch[2] = term.bytes[pos + 2];
            term.bytes[pos] = -16;
            term.bytes[pos + 1] = -112;
            term.bytes[pos + 2] = -128;
            term.bytes[pos + 3] = -128;
            term.length = 4 + pos;
            Lucene3xFields.this.getTermsDict().seekEnum(te, new Term(this.fieldInfo.name, term), true);
            Term t2 = te.term();
            if (t2 == null || t2.field() != this.internedFieldName) {
                return false;
            }
            BytesRef b2 = t2.bytes();
            assert (b2.offset == 0);
            if (b2.length >= term.length && this.isNonBMPChar(b2.bytes, pos)) {
                matches = true;
                for (int i = 0; i < pos; ++i) {
                    if (term.bytes[i] == b2.bytes[i]) continue;
                    matches = false;
                    break;
                }
            } else {
                matches = false;
            }
            term.length = savLength;
            term.bytes[pos] = this.scratch[0];
            term.bytes[pos + 1] = this.scratch[1];
            term.bytes[pos + 2] = this.scratch[2];
            return matches;
        }

        private boolean doContinue() throws IOException {
            boolean didSeek = false;
            int limit = Math.min(this.newSuffixStart, this.scratchTerm.length - 1);
            for (int downTo = this.prevTerm.length - 1; downTo > limit; --downTo) {
                if (this.isHighBMPChar(this.prevTerm.bytes, downTo) && this.seekToNonBMP(this.seekTermEnum, this.prevTerm, downTo)) {
                    Lucene3xFields.this.getTermsDict().seekEnum(this.termEnum, this.seekTermEnum.term(), true);
                    this.newSuffixStart = downTo;
                    this.scratchTerm.copyBytes(this.termEnum.term().bytes());
                    didSeek = true;
                    break;
                }
                if ((this.prevTerm.bytes[downTo] & 0xC0) != 192 && (this.prevTerm.bytes[downTo] & 0x80) != 0) continue;
                this.prevTerm.length = downTo;
            }
            return didSeek;
        }

        private boolean doPop() throws IOException {
            assert (this.newSuffixStart <= this.prevTerm.length);
            assert (this.newSuffixStart < this.scratchTerm.length || this.newSuffixStart == 0);
            if (this.prevTerm.length > this.newSuffixStart && this.isNonBMPChar(this.prevTerm.bytes, this.newSuffixStart) && this.isHighBMPChar(this.scratchTerm.bytes, this.newSuffixStart)) {
                this.scratchTerm.bytes[this.newSuffixStart] = -1;
                this.scratchTerm.length = this.newSuffixStart + 1;
                Lucene3xFields.this.getTermsDict().seekEnum(this.termEnum, new Term(this.fieldInfo.name, this.scratchTerm), true);
                Term t2 = this.termEnum.term();
                if (t2 != null && t2.field() == this.internedFieldName) {
                    BytesRef b2 = t2.bytes();
                    assert (b2.offset == 0);
                    this.scratchTerm.copyBytes(b2);
                    this.setNewSuffixStart(this.prevTerm, this.scratchTerm);
                    return true;
                }
                if (this.newSuffixStart != 0 || this.scratchTerm.length != 0) {
                    this.newSuffixStart = 0;
                    this.scratchTerm.length = 0;
                    return true;
                }
            }
            return false;
        }

        private void surrogateDance() throws IOException {
            if (!this.unicodeSortOrder) {
                return;
            }
            if (this.termEnum.term() == null || this.termEnum.term().field() != this.internedFieldName) {
                this.scratchTerm.length = 0;
            } else {
                this.scratchTerm.copyBytes(this.termEnum.term().bytes());
            }
            assert (this.prevTerm.offset == 0);
            assert (this.scratchTerm.offset == 0);
            while (!this.doContinue() && this.doPop()) {
            }
            this.doPushes();
        }

        private void doPushes() throws IOException {
            int upTo = this.newSuffixStart;
            while (upTo < this.scratchTerm.length) {
                if (this.isNonBMPChar(this.scratchTerm.bytes, upTo) && (upTo > this.newSuffixStart || upTo >= this.prevTerm.length || !this.isNonBMPChar(this.prevTerm.bytes, upTo) && !this.isHighBMPChar(this.prevTerm.bytes, upTo))) {
                    boolean matches;
                    assert (this.scratchTerm.length >= upTo + 4);
                    int savLength = this.scratchTerm.length;
                    this.scratch[0] = this.scratchTerm.bytes[upTo];
                    this.scratch[1] = this.scratchTerm.bytes[upTo + 1];
                    this.scratch[2] = this.scratchTerm.bytes[upTo + 2];
                    this.scratchTerm.bytes[upTo] = -18;
                    this.scratchTerm.bytes[upTo + 1] = -128;
                    this.scratchTerm.bytes[upTo + 2] = -128;
                    this.scratchTerm.length = upTo + 3;
                    Lucene3xFields.this.getTermsDict().seekEnum(this.seekTermEnum, new Term(this.fieldInfo.name, this.scratchTerm), true);
                    this.scratchTerm.bytes[upTo] = this.scratch[0];
                    this.scratchTerm.bytes[upTo + 1] = this.scratch[1];
                    this.scratchTerm.bytes[upTo + 2] = this.scratch[2];
                    this.scratchTerm.length = savLength;
                    Term t2 = this.seekTermEnum.term();
                    if (t2 != null && t2.field() == this.internedFieldName) {
                        BytesRef b2 = t2.bytes();
                        assert (b2.offset == 0);
                        if (b2.length >= upTo + 3 && this.isHighBMPChar(b2.bytes, upTo)) {
                            matches = true;
                            for (int i = 0; i < upTo; ++i) {
                                if (this.scratchTerm.bytes[i] == b2.bytes[i]) continue;
                                matches = false;
                                break;
                            }
                        } else {
                            matches = false;
                        }
                    } else {
                        matches = false;
                    }
                    if (matches) {
                        Lucene3xFields.this.getTermsDict().seekEnum(this.termEnum, this.seekTermEnum.term(), true);
                        this.scratchTerm.copyBytes(this.seekTermEnum.term().bytes());
                        upTo += 3;
                        continue;
                    }
                    ++upTo;
                    continue;
                }
                ++upTo;
            }
        }

        void reset(FieldInfo fieldInfo) throws IOException {
            this.fieldInfo = fieldInfo;
            this.internedFieldName = fieldInfo.name.intern();
            Term term = new Term(this.internedFieldName);
            if (this.termEnum == null) {
                this.termEnum = Lucene3xFields.this.getTermsDict().terms(term);
                this.seekTermEnum = Lucene3xFields.this.getTermsDict().terms(term);
            } else {
                Lucene3xFields.this.getTermsDict().seekEnum(this.termEnum, term, true);
            }
            this.skipNext = true;
            this.unicodeSortOrder = Lucene3xFields.this.sortTermsByUnicode();
            Term t = this.termEnum.term();
            if (t != null && t.field() == this.internedFieldName) {
                this.newSuffixStart = 0;
                this.prevTerm.length = 0;
                this.surrogateDance();
            }
        }

        @Override
        public Comparator<BytesRef> getComparator() {
            if (this.unicodeSortOrder) {
                return BytesRef.getUTF8SortedAsUnicodeComparator();
            }
            return BytesRef.getUTF8SortedAsUTF16Comparator();
        }

        @Override
        public void seekExact(long ord) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public long ord() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public TermsEnum.SeekStatus seekCeil(BytesRef term, boolean useCache) throws IOException {
            this.skipNext = false;
            TermInfosReader tis = Lucene3xFields.this.getTermsDict();
            Term t0 = new Term(this.fieldInfo.name, term);
            assert (this.termEnum != null);
            tis.seekEnum(this.termEnum, t0, useCache);
            Term t = this.termEnum.term();
            if (t != null && t.field() == this.internedFieldName && term.bytesEquals(t.bytes())) {
                this.current = t.bytes();
                return TermsEnum.SeekStatus.FOUND;
            }
            if (t == null || t.field() != this.internedFieldName) {
                this.scratchTerm.copyBytes(term);
                assert (this.scratchTerm.offset == 0);
                for (int i = this.scratchTerm.length - 1; i >= 0; --i) {
                    if (!this.isHighBMPChar(this.scratchTerm.bytes, i) || !this.seekToNonBMP(this.seekTermEnum, this.scratchTerm, i)) continue;
                    this.scratchTerm.copyBytes(this.seekTermEnum.term().bytes());
                    Lucene3xFields.this.getTermsDict().seekEnum(this.termEnum, this.seekTermEnum.term(), useCache);
                    this.newSuffixStart = 1 + i;
                    this.doPushes();
                    this.current = this.termEnum.term().bytes();
                    return TermsEnum.SeekStatus.NOT_FOUND;
                }
                this.current = null;
                return TermsEnum.SeekStatus.END;
            }
            this.prevTerm.copyBytes(term);
            BytesRef br = t.bytes();
            assert (br.offset == 0);
            this.setNewSuffixStart(term, br);
            this.surrogateDance();
            Term t2 = this.termEnum.term();
            if (t2 == null || t2.field() != this.internedFieldName) {
                assert (t2 == null || !t2.field().equals(this.internedFieldName));
                this.current = null;
                return TermsEnum.SeekStatus.END;
            }
            this.current = t2.bytes();
            assert (!this.unicodeSortOrder || term.compareTo(this.current) < 0) : "term=" + UnicodeUtil.toHexString(term.utf8ToString()) + " vs current=" + UnicodeUtil.toHexString(this.current.utf8ToString());
            return TermsEnum.SeekStatus.NOT_FOUND;
        }

        private void setNewSuffixStart(BytesRef br1, BytesRef br2) {
            int limit = Math.min(br1.length, br2.length);
            int lastStart = 0;
            for (int i = 0; i < limit; ++i) {
                if ((br1.bytes[br1.offset + i] & 0xC0) == 192 || (br1.bytes[br1.offset + i] & 0x80) == 0) {
                    lastStart = i;
                }
                if (br1.bytes[br1.offset + i] == br2.bytes[br2.offset + i]) continue;
                this.newSuffixStart = lastStart;
                return;
            }
            this.newSuffixStart = limit;
        }

        @Override
        public BytesRef next() throws IOException {
            if (this.skipNext) {
                this.skipNext = false;
                if (this.termEnum.term() == null) {
                    return null;
                }
                if (this.termEnum.term().field() != this.internedFieldName) {
                    return null;
                }
                this.current = this.termEnum.term().bytes();
                return this.current;
            }
            this.prevTerm.copyBytes(this.termEnum.term().bytes());
            if (this.termEnum.next() && this.termEnum.term().field() == this.internedFieldName) {
                this.newSuffixStart = this.termEnum.newSuffixStart;
                this.surrogateDance();
                Term t = this.termEnum.term();
                if (t == null || t.field() != this.internedFieldName) {
                    assert (t == null || !t.field().equals(this.internedFieldName));
                    this.current = null;
                } else {
                    this.current = t.bytes();
                }
                return this.current;
            }
            this.newSuffixStart = 0;
            this.surrogateDance();
            Term t = this.termEnum.term();
            if (t == null || t.field() != this.internedFieldName) {
                assert (t == null || !t.field().equals(this.internedFieldName));
                return null;
            }
            this.current = t.bytes();
            return this.current;
        }

        @Override
        public BytesRef term() {
            return this.current;
        }

        @Override
        public int docFreq() {
            return this.termEnum.docFreq();
        }

        @Override
        public long totalTermFreq() {
            return -1L;
        }

        @Override
        public DocsEnum docs(Bits liveDocs, DocsEnum reuse, int flags) throws IOException {
            PreDocsEnum docsEnum;
            if (reuse == null || !(reuse instanceof PreDocsEnum)) {
                docsEnum = new PreDocsEnum();
            } else {
                docsEnum = (PreDocsEnum)reuse;
                if (docsEnum.getFreqStream() != Lucene3xFields.this.freqStream) {
                    docsEnum = new PreDocsEnum();
                }
            }
            return docsEnum.reset(this.termEnum, liveDocs);
        }

        @Override
        public DocsAndPositionsEnum docsAndPositions(Bits liveDocs, DocsAndPositionsEnum reuse, int flags) throws IOException {
            PreDocsAndPositionsEnum docsPosEnum;
            if (this.fieldInfo.getIndexOptions() != FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) {
                return null;
            }
            if (reuse == null || !(reuse instanceof PreDocsAndPositionsEnum)) {
                docsPosEnum = new PreDocsAndPositionsEnum();
            } else {
                docsPosEnum = (PreDocsAndPositionsEnum)reuse;
                if (docsPosEnum.getFreqStream() != Lucene3xFields.this.freqStream) {
                    docsPosEnum = new PreDocsAndPositionsEnum();
                }
            }
            return docsPosEnum.reset(this.termEnum, liveDocs);
        }
    }

    private class PreTerms
    extends Terms {
        final FieldInfo fieldInfo;

        PreTerms(FieldInfo fieldInfo) {
            this.fieldInfo = fieldInfo;
        }

        @Override
        public TermsEnum iterator(TermsEnum reuse) throws IOException {
            PreTermsEnum termsEnum = new PreTermsEnum();
            termsEnum.reset(this.fieldInfo);
            return termsEnum;
        }

        @Override
        public Comparator<BytesRef> getComparator() {
            if (Lucene3xFields.this.sortTermsByUnicode()) {
                return BytesRef.getUTF8SortedAsUnicodeComparator();
            }
            return BytesRef.getUTF8SortedAsUTF16Comparator();
        }

        @Override
        public long size() throws IOException {
            return -1L;
        }

        @Override
        public long getSumTotalTermFreq() {
            return -1L;
        }

        @Override
        public long getSumDocFreq() throws IOException {
            return -1L;
        }

        @Override
        public int getDocCount() throws IOException {
            return -1;
        }

        @Override
        public boolean hasOffsets() {
            assert (this.fieldInfo.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) < 0);
            return false;
        }

        @Override
        public boolean hasPositions() {
            return this.fieldInfo.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0;
        }

        @Override
        public boolean hasPayloads() {
            return this.fieldInfo.hasPayloads();
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import org.apache.lucene.index.BitsSlice;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.MultiBits;
import org.apache.lucene.index.MultiDocsAndPositionsEnum;
import org.apache.lucene.index.MultiDocsEnum;
import org.apache.lucene.index.ReaderSlice;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.PriorityQueue;

public final class MultiTermsEnum
extends TermsEnum {
    private final TermMergeQueue queue;
    private final TermsEnumWithSlice[] subs;
    private final TermsEnumWithSlice[] currentSubs;
    private final TermsEnumWithSlice[] top;
    private final MultiDocsEnum.EnumWithSlice[] subDocs;
    private final MultiDocsAndPositionsEnum.EnumWithSlice[] subDocsAndPositions;
    private BytesRef lastSeek;
    private boolean lastSeekExact;
    private final BytesRef lastSeekScratch = new BytesRef();
    private int numTop;
    private int numSubs;
    private BytesRef current;
    private Comparator<BytesRef> termComp;

    public int getMatchCount() {
        return this.numTop;
    }

    public TermsEnumWithSlice[] getMatchArray() {
        return this.top;
    }

    public MultiTermsEnum(ReaderSlice[] slices) {
        this.queue = new TermMergeQueue(slices.length);
        this.top = new TermsEnumWithSlice[slices.length];
        this.subs = new TermsEnumWithSlice[slices.length];
        this.subDocs = new MultiDocsEnum.EnumWithSlice[slices.length];
        this.subDocsAndPositions = new MultiDocsAndPositionsEnum.EnumWithSlice[slices.length];
        for (int i = 0; i < slices.length; ++i) {
            this.subs[i] = new TermsEnumWithSlice(i, slices[i]);
            this.subDocs[i] = new MultiDocsEnum.EnumWithSlice();
            this.subDocs[i].slice = slices[i];
            this.subDocsAndPositions[i] = new MultiDocsAndPositionsEnum.EnumWithSlice();
            this.subDocsAndPositions[i].slice = slices[i];
        }
        this.currentSubs = new TermsEnumWithSlice[slices.length];
    }

    @Override
    public BytesRef term() {
        return this.current;
    }

    @Override
    public Comparator<BytesRef> getComparator() {
        return this.termComp;
    }

    public TermsEnum reset(TermsEnumIndex[] termsEnumsIndex) throws IOException {
        assert (termsEnumsIndex.length <= this.top.length);
        this.numSubs = 0;
        this.numTop = 0;
        this.termComp = null;
        this.queue.clear();
        for (int i = 0; i < termsEnumsIndex.length; ++i) {
            TermsEnumIndex termsEnumIndex = termsEnumsIndex[i];
            assert (termsEnumIndex != null);
            if (this.termComp == null) {
                this.termComp = termsEnumIndex.termsEnum.getComparator();
                this.queue.termComp = this.termComp;
            } else {
                Comparator<BytesRef> subTermComp = termsEnumIndex.termsEnum.getComparator();
                if (subTermComp != null && !subTermComp.equals(this.termComp)) {
                    throw new IllegalStateException("sub-readers have different BytesRef.Comparators: " + subTermComp + " vs " + this.termComp + "; cannot merge");
                }
            }
            BytesRef term = termsEnumIndex.termsEnum.next();
            if (term == null) continue;
            TermsEnumWithSlice entry = this.subs[termsEnumIndex.subIndex];
            entry.reset(termsEnumIndex.termsEnum, term);
            this.queue.add(entry);
            this.currentSubs[this.numSubs++] = entry;
        }
        if (this.queue.size() == 0) {
            return TermsEnum.EMPTY;
        }
        return this;
    }

    @Override
    public boolean seekExact(BytesRef term, boolean useCache) throws IOException {
        this.queue.clear();
        this.numTop = 0;
        boolean seekOpt = false;
        if (this.lastSeek != null && this.termComp.compare(this.lastSeek, term) <= 0) {
            seekOpt = true;
        }
        this.lastSeek = null;
        this.lastSeekExact = true;
        for (int i = 0; i < this.numSubs; ++i) {
            int cmp;
            BytesRef curTerm;
            boolean status = seekOpt ? ((curTerm = this.currentSubs[i].current) != null ? ((cmp = this.termComp.compare(term, curTerm)) == 0 ? true : (cmp < 0 ? false : this.currentSubs[i].terms.seekExact(term, useCache))) : false) : this.currentSubs[i].terms.seekExact(term, useCache);
            if (!status) continue;
            this.top[this.numTop++] = this.currentSubs[i];
            this.current = this.currentSubs[i].current = this.currentSubs[i].terms.term();
            assert (term.equals(this.currentSubs[i].current));
        }
        return this.numTop > 0;
    }

    @Override
    public TermsEnum.SeekStatus seekCeil(BytesRef term, boolean useCache) throws IOException {
        this.queue.clear();
        this.numTop = 0;
        this.lastSeekExact = false;
        boolean seekOpt = false;
        if (this.lastSeek != null && this.termComp.compare(this.lastSeek, term) <= 0) {
            seekOpt = true;
        }
        this.lastSeekScratch.copyBytes(term);
        this.lastSeek = this.lastSeekScratch;
        for (int i = 0; i < this.numSubs; ++i) {
            int cmp;
            BytesRef curTerm;
            TermsEnum.SeekStatus status = seekOpt ? ((curTerm = this.currentSubs[i].current) != null ? ((cmp = this.termComp.compare(term, curTerm)) == 0 ? TermsEnum.SeekStatus.FOUND : (cmp < 0 ? TermsEnum.SeekStatus.NOT_FOUND : this.currentSubs[i].terms.seekCeil(term, useCache))) : TermsEnum.SeekStatus.END) : this.currentSubs[i].terms.seekCeil(term, useCache);
            if (status == TermsEnum.SeekStatus.FOUND) {
                this.top[this.numTop++] = this.currentSubs[i];
                this.current = this.currentSubs[i].current = this.currentSubs[i].terms.term();
                continue;
            }
            if (status == TermsEnum.SeekStatus.NOT_FOUND) {
                this.currentSubs[i].current = this.currentSubs[i].terms.term();
                assert (this.currentSubs[i].current != null);
                this.queue.add(this.currentSubs[i]);
                continue;
            }
            this.currentSubs[i].current = null;
        }
        if (this.numTop > 0) {
            return TermsEnum.SeekStatus.FOUND;
        }
        if (this.queue.size() > 0) {
            this.pullTop();
            return TermsEnum.SeekStatus.NOT_FOUND;
        }
        return TermsEnum.SeekStatus.END;
    }

    @Override
    public void seekExact(long ord) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long ord() {
        throw new UnsupportedOperationException();
    }

    private void pullTop() {
        assert (this.numTop == 0);
        do {
            this.top[this.numTop++] = (TermsEnumWithSlice)this.queue.pop();
        } while (this.queue.size() != 0 && ((TermsEnumWithSlice)this.queue.top()).current.bytesEquals(this.top[0].current));
        this.current = this.top[0].current;
    }

    private void pushTop() throws IOException {
        for (int i = 0; i < this.numTop; ++i) {
            this.top[i].current = this.top[i].terms.next();
            if (this.top[i].current == null) continue;
            this.queue.add(this.top[i]);
        }
        this.numTop = 0;
    }

    @Override
    public BytesRef next() throws IOException {
        if (this.lastSeekExact) {
            TermsEnum.SeekStatus status = this.seekCeil(this.current);
            assert (status == TermsEnum.SeekStatus.FOUND);
            this.lastSeekExact = false;
        }
        this.lastSeek = null;
        this.pushTop();
        if (this.queue.size() > 0) {
            this.pullTop();
        } else {
            this.current = null;
        }
        return this.current;
    }

    @Override
    public int docFreq() throws IOException {
        int sum = 0;
        for (int i = 0; i < this.numTop; ++i) {
            sum += this.top[i].terms.docFreq();
        }
        return sum;
    }

    @Override
    public long totalTermFreq() throws IOException {
        long sum = 0L;
        for (int i = 0; i < this.numTop; ++i) {
            long v = this.top[i].terms.totalTermFreq();
            if (v == -1L) {
                return v;
            }
            sum += v;
        }
        return sum;
    }

    @Override
    public DocsEnum docs(Bits liveDocs, DocsEnum reuse, int flags) throws IOException {
        MultiDocsEnum docsEnum;
        if (reuse != null && reuse instanceof MultiDocsEnum) {
            docsEnum = (MultiDocsEnum)reuse;
            if (!docsEnum.canReuse(this)) {
                docsEnum = new MultiDocsEnum(this, this.subs.length);
            }
        } else {
            docsEnum = new MultiDocsEnum(this, this.subs.length);
        }
        MultiBits multiLiveDocs = liveDocs instanceof MultiBits ? (MultiBits)liveDocs : null;
        int upto = 0;
        for (int i = 0; i < this.numTop; ++i) {
            Bits b;
            TermsEnumWithSlice entry = this.top[i];
            if (multiLiveDocs != null) {
                MultiBits.SubResult sub = multiLiveDocs.getMatchingSub(entry.subSlice);
                b = sub.matches ? sub.result : new BitsSlice(liveDocs, entry.subSlice);
            } else {
                b = liveDocs != null ? new BitsSlice(liveDocs, entry.subSlice) : null;
            }
            assert (entry.index < docsEnum.subDocsEnum.length) : entry.index + " vs " + docsEnum.subDocsEnum.length + "; " + this.subs.length;
            DocsEnum subDocsEnum = entry.terms.docs(b, docsEnum.subDocsEnum[entry.index], flags);
            if (subDocsEnum != null) {
                docsEnum.subDocsEnum[entry.index] = subDocsEnum;
                this.subDocs[upto].docsEnum = subDocsEnum;
                this.subDocs[upto].slice = entry.subSlice;
                ++upto;
                continue;
            }
            assert (false) : "One of our subs cannot provide a docsenum";
        }
        if (upto == 0) {
            return null;
        }
        return docsEnum.reset(this.subDocs, upto);
    }

    @Override
    public DocsAndPositionsEnum docsAndPositions(Bits liveDocs, DocsAndPositionsEnum reuse, int flags) throws IOException {
        MultiDocsAndPositionsEnum docsAndPositionsEnum;
        if (reuse != null && reuse instanceof MultiDocsAndPositionsEnum) {
            docsAndPositionsEnum = (MultiDocsAndPositionsEnum)reuse;
            if (!docsAndPositionsEnum.canReuse(this)) {
                docsAndPositionsEnum = new MultiDocsAndPositionsEnum(this, this.subs.length);
            }
        } else {
            docsAndPositionsEnum = new MultiDocsAndPositionsEnum(this, this.subs.length);
        }
        MultiBits multiLiveDocs = liveDocs instanceof MultiBits ? (MultiBits)liveDocs : null;
        int upto = 0;
        for (int i = 0; i < this.numTop; ++i) {
            Bits b;
            TermsEnumWithSlice entry = this.top[i];
            if (multiLiveDocs != null) {
                MultiBits.SubResult sub = multiLiveDocs.getMatchingSub(this.top[i].subSlice);
                b = sub.matches ? sub.result : new BitsSlice(liveDocs, this.top[i].subSlice);
            } else {
                b = liveDocs != null ? new BitsSlice(liveDocs, this.top[i].subSlice) : null;
            }
            assert (entry.index < docsAndPositionsEnum.subDocsAndPositionsEnum.length) : entry.index + " vs " + docsAndPositionsEnum.subDocsAndPositionsEnum.length + "; " + this.subs.length;
            DocsAndPositionsEnum subPostings = entry.terms.docsAndPositions(b, docsAndPositionsEnum.subDocsAndPositionsEnum[entry.index], flags);
            if (subPostings != null) {
                docsAndPositionsEnum.subDocsAndPositionsEnum[entry.index] = subPostings;
                this.subDocsAndPositions[upto].docsAndPositionsEnum = subPostings;
                this.subDocsAndPositions[upto].slice = entry.subSlice;
                ++upto;
                continue;
            }
            if (entry.terms.docs(b, null, 0) == null) continue;
            return null;
        }
        if (upto == 0) {
            return null;
        }
        return docsAndPositionsEnum.reset(this.subDocsAndPositions, upto);
    }

    public String toString() {
        return "MultiTermsEnum(" + Arrays.toString(this.subs) + ")";
    }

    private static final class TermMergeQueue
    extends PriorityQueue<TermsEnumWithSlice> {
        Comparator<BytesRef> termComp;

        TermMergeQueue(int size) {
            super(size);
        }

        @Override
        protected boolean lessThan(TermsEnumWithSlice termsA, TermsEnumWithSlice termsB) {
            int cmp = this.termComp.compare(termsA.current, termsB.current);
            if (cmp != 0) {
                return cmp < 0;
            }
            return ((TermsEnumWithSlice)termsA).subSlice.start < ((TermsEnumWithSlice)termsB).subSlice.start;
        }
    }

    static final class TermsEnumWithSlice {
        private final ReaderSlice subSlice;
        TermsEnum terms;
        public BytesRef current;
        final int index;

        public TermsEnumWithSlice(int index, ReaderSlice subSlice) {
            this.subSlice = subSlice;
            this.index = index;
            assert (subSlice.length >= 0) : "length=" + subSlice.length;
        }

        public void reset(TermsEnum terms, BytesRef term) {
            this.terms = terms;
            this.current = term;
        }

        public String toString() {
            return this.subSlice.toString() + ":" + this.terms;
        }
    }

    static class TermsEnumIndex {
        public static final TermsEnumIndex[] EMPTY_ARRAY = new TermsEnumIndex[0];
        final int subIndex;
        final TermsEnum termsEnum;

        public TermsEnumIndex(TermsEnum termsEnum, int subIndex) {
            this.termsEnum = termsEnum;
            this.subIndex = subIndex;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import org.apache.lucene.index.MultiTermsEnum;
import org.apache.lucene.index.ReaderSlice;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.automaton.CompiledAutomaton;

public final class MultiTerms
extends Terms {
    private final Terms[] subs;
    private final ReaderSlice[] subSlices;
    private final Comparator<BytesRef> termComp;
    private final boolean hasOffsets;
    private final boolean hasPositions;
    private final boolean hasPayloads;

    public MultiTerms(Terms[] subs, ReaderSlice[] subSlices) throws IOException {
        this.subs = subs;
        this.subSlices = subSlices;
        Comparator<BytesRef> _termComp = null;
        assert (subs.length > 0) : "inefficient: don't use MultiTerms over one sub";
        boolean _hasOffsets = true;
        boolean _hasPositions = true;
        boolean _hasPayloads = false;
        for (int i = 0; i < subs.length; ++i) {
            if (_termComp == null) {
                _termComp = subs[i].getComparator();
            } else {
                Comparator<BytesRef> subTermComp = subs[i].getComparator();
                if (subTermComp != null && !subTermComp.equals(_termComp)) {
                    throw new IllegalStateException("sub-readers have different BytesRef.Comparators; cannot merge");
                }
            }
            _hasOffsets &= subs[i].hasOffsets();
            _hasPositions &= subs[i].hasPositions();
            _hasPayloads |= subs[i].hasPayloads();
        }
        this.termComp = _termComp;
        this.hasOffsets = _hasOffsets;
        this.hasPositions = _hasPositions;
        this.hasPayloads = this.hasPositions && _hasPayloads;
    }

    @Override
    public TermsEnum intersect(CompiledAutomaton compiled, BytesRef startTerm) throws IOException {
        ArrayList<MultiTermsEnum.TermsEnumIndex> termsEnums = new ArrayList<MultiTermsEnum.TermsEnumIndex>();
        for (int i = 0; i < this.subs.length; ++i) {
            TermsEnum termsEnum = this.subs[i].intersect(compiled, startTerm);
            if (termsEnum == null) continue;
            termsEnums.add(new MultiTermsEnum.TermsEnumIndex(termsEnum, i));
        }
        if (termsEnums.size() > 0) {
            return new MultiTermsEnum(this.subSlices).reset(termsEnums.toArray(MultiTermsEnum.TermsEnumIndex.EMPTY_ARRAY));
        }
        return TermsEnum.EMPTY;
    }

    @Override
    public TermsEnum iterator(TermsEnum reuse) throws IOException {
        ArrayList<MultiTermsEnum.TermsEnumIndex> termsEnums = new ArrayList<MultiTermsEnum.TermsEnumIndex>();
        for (int i = 0; i < this.subs.length; ++i) {
            TermsEnum termsEnum = this.subs[i].iterator(null);
            if (termsEnum == null) continue;
            termsEnums.add(new MultiTermsEnum.TermsEnumIndex(termsEnum, i));
        }
        if (termsEnums.size() > 0) {
            return new MultiTermsEnum(this.subSlices).reset(termsEnums.toArray(MultiTermsEnum.TermsEnumIndex.EMPTY_ARRAY));
        }
        return TermsEnum.EMPTY;
    }

    @Override
    public long size() {
        return -1L;
    }

    @Override
    public long getSumTotalTermFreq() throws IOException {
        long sum = 0L;
        for (Terms terms : this.subs) {
            long v = terms.getSumTotalTermFreq();
            if (v == -1L) {
                return -1L;
            }
            sum += v;
        }
        return sum;
    }

    @Override
    public long getSumDocFreq() throws IOException {
        long sum = 0L;
        for (Terms terms : this.subs) {
            long v = terms.getSumDocFreq();
            if (v == -1L) {
                return -1L;
            }
            sum += v;
        }
        return sum;
    }

    @Override
    public int getDocCount() throws IOException {
        int sum = 0;
        for (Terms terms : this.subs) {
            int v = terms.getDocCount();
            if (v == -1) {
                return -1;
            }
            sum += v;
        }
        return sum;
    }

    @Override
    public Comparator<BytesRef> getComparator() {
        return this.termComp;
    }

    @Override
    public boolean hasOffsets() {
        return this.hasOffsets;
    }

    @Override
    public boolean hasPositions() {
        return this.hasPositions;
    }

    @Override
    public boolean hasPayloads() {
        return this.hasPayloads;
    }
}


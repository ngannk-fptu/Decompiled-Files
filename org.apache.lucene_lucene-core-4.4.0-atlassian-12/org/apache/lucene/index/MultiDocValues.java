/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.List;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.MultiTermsEnum;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.ReaderSlice;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.packed.AppendingLongBuffer;
import org.apache.lucene.util.packed.MonotonicAppendingLongBuffer;

public class MultiDocValues {
    private MultiDocValues() {
    }

    public static NumericDocValues getNormValues(IndexReader r, String field) throws IOException {
        List<AtomicReaderContext> leaves = r.leaves();
        int size = leaves.size();
        if (size == 0) {
            return null;
        }
        if (size == 1) {
            return leaves.get(0).reader().getNormValues(field);
        }
        FieldInfo fi = MultiFields.getMergedFieldInfos(r).fieldInfo(field);
        if (fi == null || !fi.hasNorms()) {
            return null;
        }
        boolean anyReal = false;
        final NumericDocValues[] values = new NumericDocValues[size];
        final int[] starts = new int[size + 1];
        for (int i = 0; i < size; ++i) {
            AtomicReaderContext context = leaves.get(i);
            NumericDocValues v = context.reader().getNormValues(field);
            if (v == null) {
                v = NumericDocValues.EMPTY;
            } else {
                anyReal = true;
            }
            values[i] = v;
            starts[i] = context.docBase;
        }
        starts[size] = r.maxDoc();
        assert (anyReal);
        return new NumericDocValues(){

            @Override
            public long get(int docID) {
                int subIndex = ReaderUtil.subIndex(docID, starts);
                return values[subIndex].get(docID - starts[subIndex]);
            }
        };
    }

    public static NumericDocValues getNumericValues(IndexReader r, String field) throws IOException {
        List<AtomicReaderContext> leaves = r.leaves();
        int size = leaves.size();
        if (size == 0) {
            return null;
        }
        if (size == 1) {
            return leaves.get(0).reader().getNumericDocValues(field);
        }
        boolean anyReal = false;
        final NumericDocValues[] values = new NumericDocValues[size];
        final int[] starts = new int[size + 1];
        for (int i = 0; i < size; ++i) {
            AtomicReaderContext context = leaves.get(i);
            NumericDocValues v = context.reader().getNumericDocValues(field);
            if (v == null) {
                v = NumericDocValues.EMPTY;
            } else {
                anyReal = true;
            }
            values[i] = v;
            starts[i] = context.docBase;
        }
        starts[size] = r.maxDoc();
        if (!anyReal) {
            return null;
        }
        return new NumericDocValues(){

            @Override
            public long get(int docID) {
                int subIndex = ReaderUtil.subIndex(docID, starts);
                return values[subIndex].get(docID - starts[subIndex]);
            }
        };
    }

    public static BinaryDocValues getBinaryValues(IndexReader r, String field) throws IOException {
        List<AtomicReaderContext> leaves = r.leaves();
        int size = leaves.size();
        if (size == 0) {
            return null;
        }
        if (size == 1) {
            return leaves.get(0).reader().getBinaryDocValues(field);
        }
        boolean anyReal = false;
        final BinaryDocValues[] values = new BinaryDocValues[size];
        final int[] starts = new int[size + 1];
        for (int i = 0; i < size; ++i) {
            AtomicReaderContext context = leaves.get(i);
            BinaryDocValues v = context.reader().getBinaryDocValues(field);
            if (v == null) {
                v = BinaryDocValues.EMPTY;
            } else {
                anyReal = true;
            }
            values[i] = v;
            starts[i] = context.docBase;
        }
        starts[size] = r.maxDoc();
        if (!anyReal) {
            return null;
        }
        return new BinaryDocValues(){

            @Override
            public void get(int docID, BytesRef result) {
                int subIndex = ReaderUtil.subIndex(docID, starts);
                values[subIndex].get(docID - starts[subIndex], result);
            }
        };
    }

    public static SortedDocValues getSortedValues(IndexReader r, String field) throws IOException {
        List<AtomicReaderContext> leaves = r.leaves();
        int size = leaves.size();
        if (size == 0) {
            return null;
        }
        if (size == 1) {
            return leaves.get(0).reader().getSortedDocValues(field);
        }
        boolean anyReal = false;
        SortedDocValues[] values = new SortedDocValues[size];
        int[] starts = new int[size + 1];
        for (int i = 0; i < size; ++i) {
            AtomicReaderContext context = leaves.get(i);
            SortedDocValues v = context.reader().getSortedDocValues(field);
            if (v == null) {
                v = SortedDocValues.EMPTY;
            } else {
                anyReal = true;
            }
            values[i] = v;
            starts[i] = context.docBase;
        }
        starts[size] = r.maxDoc();
        if (!anyReal) {
            return null;
        }
        TermsEnum[] enums = new TermsEnum[values.length];
        for (int i = 0; i < values.length; ++i) {
            enums[i] = values[i].termsEnum();
        }
        OrdinalMap mapping = new OrdinalMap(r.getCoreCacheKey(), enums);
        return new MultiSortedDocValues(values, starts, mapping);
    }

    public static SortedSetDocValues getSortedSetValues(IndexReader r, String field) throws IOException {
        List<AtomicReaderContext> leaves = r.leaves();
        int size = leaves.size();
        if (size == 0) {
            return null;
        }
        if (size == 1) {
            return leaves.get(0).reader().getSortedSetDocValues(field);
        }
        boolean anyReal = false;
        SortedSetDocValues[] values = new SortedSetDocValues[size];
        int[] starts = new int[size + 1];
        for (int i = 0; i < size; ++i) {
            AtomicReaderContext context = leaves.get(i);
            SortedSetDocValues v = context.reader().getSortedSetDocValues(field);
            if (v == null) {
                v = SortedSetDocValues.EMPTY;
            } else {
                anyReal = true;
            }
            values[i] = v;
            starts[i] = context.docBase;
        }
        starts[size] = r.maxDoc();
        if (!anyReal) {
            return null;
        }
        TermsEnum[] enums = new TermsEnum[values.length];
        for (int i = 0; i < values.length; ++i) {
            enums[i] = values[i].termsEnum();
        }
        OrdinalMap mapping = new OrdinalMap(r.getCoreCacheKey(), enums);
        return new MultiSortedSetDocValues(values, starts, mapping);
    }

    public static class MultiSortedSetDocValues
    extends SortedSetDocValues {
        public final int[] docStarts;
        public final SortedSetDocValues[] values;
        public final OrdinalMap mapping;
        int currentSubIndex;

        MultiSortedSetDocValues(SortedSetDocValues[] values, int[] docStarts, OrdinalMap mapping) throws IOException {
            assert (values.length == mapping.ordDeltas.length);
            assert (docStarts.length == values.length + 1);
            this.values = values;
            this.docStarts = docStarts;
            this.mapping = mapping;
        }

        @Override
        public long nextOrd() {
            long segmentOrd = this.values[this.currentSubIndex].nextOrd();
            if (segmentOrd == -1L) {
                return segmentOrd;
            }
            return this.mapping.getGlobalOrd(this.currentSubIndex, segmentOrd);
        }

        @Override
        public void setDocument(int docID) {
            this.currentSubIndex = ReaderUtil.subIndex(docID, this.docStarts);
            this.values[this.currentSubIndex].setDocument(docID - this.docStarts[this.currentSubIndex]);
        }

        @Override
        public void lookupOrd(long ord, BytesRef result) {
            int subIndex = this.mapping.getSegmentNumber(ord);
            long segmentOrd = this.mapping.getSegmentOrd(subIndex, ord);
            this.values[subIndex].lookupOrd(segmentOrd, result);
        }

        @Override
        public long getValueCount() {
            return this.mapping.getValueCount();
        }
    }

    public static class MultiSortedDocValues
    extends SortedDocValues {
        public final int[] docStarts;
        public final SortedDocValues[] values;
        public final OrdinalMap mapping;

        MultiSortedDocValues(SortedDocValues[] values, int[] docStarts, OrdinalMap mapping) throws IOException {
            assert (values.length == mapping.ordDeltas.length);
            assert (docStarts.length == values.length + 1);
            this.values = values;
            this.docStarts = docStarts;
            this.mapping = mapping;
        }

        @Override
        public int getOrd(int docID) {
            int subIndex = ReaderUtil.subIndex(docID, this.docStarts);
            int segmentOrd = this.values[subIndex].getOrd(docID - this.docStarts[subIndex]);
            return (int)this.mapping.getGlobalOrd(subIndex, segmentOrd);
        }

        @Override
        public void lookupOrd(int ord, BytesRef result) {
            int subIndex = this.mapping.getSegmentNumber(ord);
            int segmentOrd = (int)this.mapping.getSegmentOrd(subIndex, ord);
            this.values[subIndex].lookupOrd(segmentOrd, result);
        }

        @Override
        public int getValueCount() {
            return (int)this.mapping.getValueCount();
        }
    }

    public static class OrdinalMap {
        final Object owner;
        final MonotonicAppendingLongBuffer globalOrdDeltas;
        final AppendingLongBuffer subIndexes;
        final MonotonicAppendingLongBuffer[] ordDeltas;

        public OrdinalMap(Object owner, TermsEnum[] subs) throws IOException {
            this.owner = owner;
            this.globalOrdDeltas = new MonotonicAppendingLongBuffer();
            this.subIndexes = new AppendingLongBuffer();
            this.ordDeltas = new MonotonicAppendingLongBuffer[subs.length];
            for (int i = 0; i < this.ordDeltas.length; ++i) {
                this.ordDeltas[i] = new MonotonicAppendingLongBuffer();
            }
            long[] segmentOrds = new long[subs.length];
            ReaderSlice[] slices = new ReaderSlice[subs.length];
            MultiTermsEnum.TermsEnumIndex[] indexes = new MultiTermsEnum.TermsEnumIndex[slices.length];
            for (int i = 0; i < slices.length; ++i) {
                slices[i] = new ReaderSlice(0, 0, i);
                indexes[i] = new MultiTermsEnum.TermsEnumIndex(subs[i], i);
            }
            MultiTermsEnum mte = new MultiTermsEnum(slices);
            mte.reset(indexes);
            long globalOrd = 0L;
            while (mte.next() != null) {
                MultiTermsEnum.TermsEnumWithSlice[] matches = mte.getMatchArray();
                for (int i = 0; i < mte.getMatchCount(); ++i) {
                    int subIndex = matches[i].index;
                    long segmentOrd = matches[i].terms.ord();
                    long delta = globalOrd - segmentOrd;
                    if (i == 0) {
                        this.subIndexes.add(subIndex);
                        this.globalOrdDeltas.add(delta);
                    }
                    while (segmentOrds[subIndex] <= segmentOrd) {
                        this.ordDeltas[subIndex].add(delta);
                        int n = subIndex;
                        segmentOrds[n] = segmentOrds[n] + 1L;
                    }
                }
                ++globalOrd;
            }
        }

        public long getGlobalOrd(int subIndex, long segmentOrd) {
            return segmentOrd + this.ordDeltas[subIndex].get(segmentOrd);
        }

        public long getSegmentOrd(int subIndex, long globalOrd) {
            return globalOrd - this.globalOrdDeltas.get(globalOrd);
        }

        public int getSegmentNumber(long globalOrd) {
            return (int)this.subIndexes.get(globalOrd);
        }

        public long getValueCount() {
            return this.globalOrdDeltas.size();
        }
    }
}


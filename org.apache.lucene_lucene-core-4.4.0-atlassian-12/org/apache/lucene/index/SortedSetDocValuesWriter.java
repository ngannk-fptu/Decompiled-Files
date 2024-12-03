/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.index.DocValuesWriter;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.ByteBlockPool;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefHash;
import org.apache.lucene.util.Counter;
import org.apache.lucene.util.packed.AppendingLongBuffer;

class SortedSetDocValuesWriter
extends DocValuesWriter {
    final BytesRefHash hash;
    private AppendingLongBuffer pending;
    private AppendingLongBuffer pendingCounts;
    private final Counter iwBytesUsed;
    private long bytesUsed;
    private final FieldInfo fieldInfo;
    private int currentDoc;
    private int[] currentValues = new int[8];
    private int currentUpto = 0;
    private int maxCount = 0;

    public SortedSetDocValuesWriter(FieldInfo fieldInfo, Counter iwBytesUsed) {
        this.fieldInfo = fieldInfo;
        this.iwBytesUsed = iwBytesUsed;
        this.hash = new BytesRefHash(new ByteBlockPool(new ByteBlockPool.DirectTrackingAllocator(iwBytesUsed)), 16, new BytesRefHash.DirectBytesStartArray(16, iwBytesUsed));
        this.pending = new AppendingLongBuffer();
        this.pendingCounts = new AppendingLongBuffer();
        this.bytesUsed = this.pending.ramBytesUsed() + this.pendingCounts.ramBytesUsed();
        iwBytesUsed.addAndGet(this.bytesUsed);
    }

    public void addValue(int docID, BytesRef value) {
        if (value == null) {
            throw new IllegalArgumentException("field \"" + this.fieldInfo.name + "\": null value not allowed");
        }
        if (value.length > 32766) {
            throw new IllegalArgumentException("DocValuesField \"" + this.fieldInfo.name + "\" is too large, must be <= " + 32766);
        }
        if (docID != this.currentDoc) {
            this.finishCurrentDoc();
        }
        while (this.currentDoc < docID) {
            this.pendingCounts.add(0L);
            ++this.currentDoc;
        }
        this.addOneValue(value);
        this.updateBytesUsed();
    }

    private void finishCurrentDoc() {
        Arrays.sort(this.currentValues, 0, this.currentUpto);
        int lastValue = -1;
        int count = 0;
        for (int i = 0; i < this.currentUpto; ++i) {
            int termID = this.currentValues[i];
            if (termID != lastValue) {
                this.pending.add(termID);
                ++count;
            }
            lastValue = termID;
        }
        this.pendingCounts.add(count);
        this.maxCount = Math.max(this.maxCount, count);
        this.currentUpto = 0;
        ++this.currentDoc;
    }

    @Override
    public void finish(int maxDoc) {
        this.finishCurrentDoc();
        for (int i = this.currentDoc; i < maxDoc; ++i) {
            this.pendingCounts.add(0L);
        }
    }

    private void addOneValue(BytesRef value) {
        int termID = this.hash.add(value);
        if (termID < 0) {
            termID = -termID - 1;
        } else {
            this.iwBytesUsed.addAndGet(8L);
        }
        if (this.currentUpto == this.currentValues.length) {
            this.currentValues = ArrayUtil.grow(this.currentValues, this.currentValues.length + 1);
            this.iwBytesUsed.addAndGet((this.currentValues.length - this.currentUpto) * 2 * 4);
        }
        this.currentValues[this.currentUpto] = termID;
        ++this.currentUpto;
    }

    private void updateBytesUsed() {
        long newBytesUsed = this.pending.ramBytesUsed() + this.pendingCounts.ramBytesUsed();
        this.iwBytesUsed.addAndGet(newBytesUsed - this.bytesUsed);
        this.bytesUsed = newBytesUsed;
    }

    @Override
    public void flush(SegmentWriteState state, DocValuesConsumer dvConsumer) throws IOException {
        final int maxDoc = state.segmentInfo.getDocCount();
        final int maxCountPerDoc = this.maxCount;
        assert (this.pendingCounts.size() == (long)maxDoc);
        final int valueCount = this.hash.size();
        final int[] sortedValues = this.hash.sort(BytesRef.getUTF8SortedAsUnicodeComparator());
        final int[] ordMap = new int[valueCount];
        for (int ord = 0; ord < valueCount; ++ord) {
            ordMap[sortedValues[ord]] = ord;
        }
        dvConsumer.addSortedSetField(this.fieldInfo, new Iterable<BytesRef>(){

            @Override
            public Iterator<BytesRef> iterator() {
                return new ValuesIterator(sortedValues, valueCount);
            }
        }, new Iterable<Number>(){

            @Override
            public Iterator<Number> iterator() {
                return new OrdCountIterator(maxDoc);
            }
        }, new Iterable<Number>(){

            @Override
            public Iterator<Number> iterator() {
                return new OrdsIterator(ordMap, maxCountPerDoc);
            }
        });
    }

    @Override
    public void abort() {
    }

    private class OrdCountIterator
    implements Iterator<Number> {
        final AppendingLongBuffer.Iterator iter;
        final int maxDoc;
        int docUpto;

        OrdCountIterator(int maxDoc) {
            this.iter = SortedSetDocValuesWriter.this.pendingCounts.iterator();
            this.maxDoc = maxDoc;
            assert (SortedSetDocValuesWriter.this.pendingCounts.size() == (long)maxDoc);
        }

        @Override
        public boolean hasNext() {
            return this.docUpto < this.maxDoc;
        }

        @Override
        public Number next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            ++this.docUpto;
            return this.iter.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class OrdsIterator
    implements Iterator<Number> {
        final AppendingLongBuffer.Iterator iter;
        final AppendingLongBuffer.Iterator counts;
        final int[] ordMap;
        final long numOrds;
        long ordUpto;
        final int[] currentDoc;
        int currentUpto;
        int currentLength;

        OrdsIterator(int[] ordMap, int maxCount) {
            this.iter = SortedSetDocValuesWriter.this.pending.iterator();
            this.counts = SortedSetDocValuesWriter.this.pendingCounts.iterator();
            this.currentDoc = new int[maxCount];
            this.ordMap = ordMap;
            this.numOrds = SortedSetDocValuesWriter.this.pending.size();
        }

        @Override
        public boolean hasNext() {
            return this.ordUpto < this.numOrds;
        }

        @Override
        public Number next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            while (this.currentUpto == this.currentLength) {
                this.currentUpto = 0;
                this.currentLength = (int)this.counts.next();
                for (int i = 0; i < this.currentLength; ++i) {
                    this.currentDoc[i] = this.ordMap[(int)this.iter.next()];
                }
                Arrays.sort(this.currentDoc, 0, this.currentLength);
            }
            int ord = this.currentDoc[this.currentUpto];
            ++this.currentUpto;
            ++this.ordUpto;
            return ord;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class ValuesIterator
    implements Iterator<BytesRef> {
        final int[] sortedValues;
        final BytesRef scratch = new BytesRef();
        final int valueCount;
        int ordUpto;

        ValuesIterator(int[] sortedValues, int valueCount) {
            this.sortedValues = sortedValues;
            this.valueCount = valueCount;
        }

        @Override
        public boolean hasNext() {
            return this.ordUpto < this.valueCount;
        }

        @Override
        public BytesRef next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            SortedSetDocValuesWriter.this.hash.get(this.sortedValues[this.ordUpto], this.scratch);
            ++this.ordUpto;
            return this.scratch;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}


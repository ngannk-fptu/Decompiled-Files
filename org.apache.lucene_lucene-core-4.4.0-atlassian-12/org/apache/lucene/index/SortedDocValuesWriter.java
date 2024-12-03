/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.index.DocValuesWriter;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.util.ByteBlockPool;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefHash;
import org.apache.lucene.util.Counter;
import org.apache.lucene.util.packed.AppendingLongBuffer;

class SortedDocValuesWriter
extends DocValuesWriter {
    final BytesRefHash hash;
    private AppendingLongBuffer pending;
    private final Counter iwBytesUsed;
    private long bytesUsed;
    private final FieldInfo fieldInfo;
    private static final BytesRef EMPTY = new BytesRef(BytesRef.EMPTY_BYTES);

    public SortedDocValuesWriter(FieldInfo fieldInfo, Counter iwBytesUsed) {
        this.fieldInfo = fieldInfo;
        this.iwBytesUsed = iwBytesUsed;
        this.hash = new BytesRefHash(new ByteBlockPool(new ByteBlockPool.DirectTrackingAllocator(iwBytesUsed)), 16, new BytesRefHash.DirectBytesStartArray(16, iwBytesUsed));
        this.pending = new AppendingLongBuffer();
        this.bytesUsed = this.pending.ramBytesUsed();
        iwBytesUsed.addAndGet(this.bytesUsed);
    }

    public void addValue(int docID, BytesRef value) {
        if ((long)docID < this.pending.size()) {
            throw new IllegalArgumentException("DocValuesField \"" + this.fieldInfo.name + "\" appears more than once in this document (only one value is allowed per field)");
        }
        if (value == null) {
            throw new IllegalArgumentException("field \"" + this.fieldInfo.name + "\": null value not allowed");
        }
        if (value.length > 32766) {
            throw new IllegalArgumentException("DocValuesField \"" + this.fieldInfo.name + "\" is too large, must be <= " + 32766);
        }
        while (this.pending.size() < (long)docID) {
            this.addOneValue(EMPTY);
        }
        this.addOneValue(value);
    }

    @Override
    public void finish(int maxDoc) {
        while (this.pending.size() < (long)maxDoc) {
            this.addOneValue(EMPTY);
        }
    }

    private void addOneValue(BytesRef value) {
        int termID = this.hash.add(value);
        if (termID < 0) {
            termID = -termID - 1;
        } else {
            this.iwBytesUsed.addAndGet(8L);
        }
        this.pending.add(termID);
        this.updateBytesUsed();
    }

    private void updateBytesUsed() {
        long newBytesUsed = this.pending.ramBytesUsed();
        this.iwBytesUsed.addAndGet(newBytesUsed - this.bytesUsed);
        this.bytesUsed = newBytesUsed;
    }

    @Override
    public void flush(SegmentWriteState state, DocValuesConsumer dvConsumer) throws IOException {
        final int maxDoc = state.segmentInfo.getDocCount();
        assert (this.pending.size() == (long)maxDoc);
        final int valueCount = this.hash.size();
        final int[] sortedValues = this.hash.sort(BytesRef.getUTF8SortedAsUnicodeComparator());
        final int[] ordMap = new int[valueCount];
        for (int ord = 0; ord < valueCount; ++ord) {
            ordMap[sortedValues[ord]] = ord;
        }
        dvConsumer.addSortedField(this.fieldInfo, new Iterable<BytesRef>(){

            @Override
            public Iterator<BytesRef> iterator() {
                return new ValuesIterator(sortedValues, valueCount);
            }
        }, new Iterable<Number>(){

            @Override
            public Iterator<Number> iterator() {
                return new OrdsIterator(ordMap, maxDoc);
            }
        });
    }

    @Override
    public void abort() {
    }

    private class OrdsIterator
    implements Iterator<Number> {
        final AppendingLongBuffer.Iterator iter;
        final int[] ordMap;
        final int maxDoc;
        int docUpto;

        OrdsIterator(int[] ordMap, int maxDoc) {
            this.iter = SortedDocValuesWriter.this.pending.iterator();
            this.ordMap = ordMap;
            this.maxDoc = maxDoc;
            assert (SortedDocValuesWriter.this.pending.size() == (long)maxDoc);
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
            int ord = (int)this.iter.next();
            ++this.docUpto;
            return this.ordMap[ord];
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
            SortedDocValuesWriter.this.hash.get(this.sortedValues[this.ordUpto], this.scratch);
            ++this.ordUpto;
            return this.scratch;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}


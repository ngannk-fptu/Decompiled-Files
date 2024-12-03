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
import org.apache.lucene.util.Counter;
import org.apache.lucene.util.packed.AppendingLongBuffer;

class NumericDocValuesWriter
extends DocValuesWriter {
    private static final long MISSING = 0L;
    private AppendingLongBuffer pending = new AppendingLongBuffer();
    private final Counter iwBytesUsed;
    private long bytesUsed = this.pending.ramBytesUsed();
    private final FieldInfo fieldInfo;

    public NumericDocValuesWriter(FieldInfo fieldInfo, Counter iwBytesUsed) {
        this.fieldInfo = fieldInfo;
        this.iwBytesUsed = iwBytesUsed;
        iwBytesUsed.addAndGet(this.bytesUsed);
    }

    public void addValue(int docID, long value) {
        if ((long)docID < this.pending.size()) {
            throw new IllegalArgumentException("DocValuesField \"" + this.fieldInfo.name + "\" appears more than once in this document (only one value is allowed per field)");
        }
        for (int i = (int)this.pending.size(); i < docID; ++i) {
            this.pending.add(0L);
        }
        this.pending.add(value);
        this.updateBytesUsed();
    }

    private void updateBytesUsed() {
        long newBytesUsed = this.pending.ramBytesUsed();
        this.iwBytesUsed.addAndGet(newBytesUsed - this.bytesUsed);
        this.bytesUsed = newBytesUsed;
    }

    @Override
    public void finish(int maxDoc) {
    }

    @Override
    public void flush(SegmentWriteState state, DocValuesConsumer dvConsumer) throws IOException {
        final int maxDoc = state.segmentInfo.getDocCount();
        dvConsumer.addNumericField(this.fieldInfo, new Iterable<Number>(){

            @Override
            public Iterator<Number> iterator() {
                return new NumericIterator(maxDoc);
            }
        });
    }

    @Override
    public void abort() {
    }

    private class NumericIterator
    implements Iterator<Number> {
        final AppendingLongBuffer.Iterator iter;
        final int size;
        final int maxDoc;
        int upto;

        NumericIterator(int maxDoc) {
            this.iter = NumericDocValuesWriter.this.pending.iterator();
            this.size = (int)NumericDocValuesWriter.this.pending.size();
            this.maxDoc = maxDoc;
        }

        @Override
        public boolean hasNext() {
            return this.upto < this.maxDoc;
        }

        @Override
        public Number next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            long value = this.upto < this.size ? this.iter.next() : 0L;
            ++this.upto;
            return value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}


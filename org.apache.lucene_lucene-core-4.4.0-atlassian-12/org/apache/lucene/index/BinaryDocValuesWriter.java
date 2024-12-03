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
import org.apache.lucene.util.Counter;
import org.apache.lucene.util.packed.AppendingLongBuffer;

class BinaryDocValuesWriter
extends DocValuesWriter {
    private final ByteBlockPool pool;
    private final AppendingLongBuffer lengths;
    private final FieldInfo fieldInfo;
    private int addedValues = 0;

    public BinaryDocValuesWriter(FieldInfo fieldInfo, Counter iwBytesUsed) {
        this.fieldInfo = fieldInfo;
        this.pool = new ByteBlockPool(new ByteBlockPool.DirectTrackingAllocator(iwBytesUsed));
        this.lengths = new AppendingLongBuffer();
    }

    public void addValue(int docID, BytesRef value) {
        if (docID < this.addedValues) {
            throw new IllegalArgumentException("DocValuesField \"" + this.fieldInfo.name + "\" appears more than once in this document (only one value is allowed per field)");
        }
        if (value == null) {
            throw new IllegalArgumentException("field=\"" + this.fieldInfo.name + "\": null value not allowed");
        }
        if (value.length > 32766) {
            throw new IllegalArgumentException("DocValuesField \"" + this.fieldInfo.name + "\" is too large, must be <= " + 32766);
        }
        while (this.addedValues < docID) {
            ++this.addedValues;
            this.lengths.add(0L);
        }
        ++this.addedValues;
        this.lengths.add(value.length);
        this.pool.append(value);
    }

    @Override
    public void finish(int maxDoc) {
    }

    @Override
    public void flush(SegmentWriteState state, DocValuesConsumer dvConsumer) throws IOException {
        final int maxDoc = state.segmentInfo.getDocCount();
        dvConsumer.addBinaryField(this.fieldInfo, new Iterable<BytesRef>(){

            @Override
            public Iterator<BytesRef> iterator() {
                return new BytesIterator(maxDoc);
            }
        });
    }

    @Override
    public void abort() {
    }

    static /* synthetic */ AppendingLongBuffer access$000(BinaryDocValuesWriter x0) {
        return x0.lengths;
    }

    private class BytesIterator
    implements Iterator<BytesRef> {
        final BytesRef value = new BytesRef();
        final AppendingLongBuffer.Iterator lengthsIterator = BinaryDocValuesWriter.access$000(BinaryDocValuesWriter.this).iterator();
        final int size = (int)BinaryDocValuesWriter.access$000(BinaryDocValuesWriter.this).size();
        final int maxDoc;
        int upto;
        long byteOffset;

        BytesIterator(int maxDoc) {
            this.maxDoc = maxDoc;
        }

        @Override
        public boolean hasNext() {
            return this.upto < this.maxDoc;
        }

        @Override
        public BytesRef next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            if (this.upto < this.size) {
                int length = (int)this.lengthsIterator.next();
                this.value.grow(length);
                this.value.length = length;
                BinaryDocValuesWriter.this.pool.readBytes(this.byteOffset, this.value.bytes, this.value.offset, this.value.length);
                this.byteOffset += (long)length;
            } else {
                this.value.length = 0;
            }
            ++this.upto;
            return this.value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}


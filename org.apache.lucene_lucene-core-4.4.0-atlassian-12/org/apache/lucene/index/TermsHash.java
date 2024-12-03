/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.index.DocInverterPerField;
import org.apache.lucene.index.DocumentsWriterPerThread;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.InvertedDocConsumer;
import org.apache.lucene.index.InvertedDocConsumerPerField;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.index.TermsHashConsumer;
import org.apache.lucene.index.TermsHashConsumerPerField;
import org.apache.lucene.index.TermsHashPerField;
import org.apache.lucene.util.ByteBlockPool;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Counter;
import org.apache.lucene.util.IntBlockPool;

final class TermsHash
extends InvertedDocConsumer {
    final TermsHashConsumer consumer;
    final TermsHash nextTermsHash;
    final IntBlockPool intPool;
    final ByteBlockPool bytePool;
    ByteBlockPool termBytePool;
    final Counter bytesUsed;
    final boolean primary;
    final DocumentsWriterPerThread.DocState docState;
    final BytesRef tr1 = new BytesRef();
    final BytesRef tr2 = new BytesRef();
    final BytesRef termBytesRef = new BytesRef(10);
    final boolean trackAllocations;

    public TermsHash(DocumentsWriterPerThread docWriter, TermsHashConsumer consumer, boolean trackAllocations, TermsHash nextTermsHash) {
        this.docState = docWriter.docState;
        this.consumer = consumer;
        this.trackAllocations = trackAllocations;
        this.nextTermsHash = nextTermsHash;
        this.bytesUsed = trackAllocations ? docWriter.bytesUsed : Counter.newCounter();
        this.intPool = new IntBlockPool(docWriter.intBlockAllocator);
        this.bytePool = new ByteBlockPool(docWriter.byteBlockAllocator);
        if (nextTermsHash != null) {
            this.primary = true;
            this.termBytePool = this.bytePool;
            nextTermsHash.termBytePool = this.bytePool;
        } else {
            this.primary = false;
        }
    }

    @Override
    public void abort() {
        this.reset();
        try {
            this.consumer.abort();
        }
        finally {
            if (this.nextTermsHash != null) {
                this.nextTermsHash.abort();
            }
        }
    }

    void reset() {
        this.intPool.reset(false, false);
        this.bytePool.reset(false, false);
    }

    @Override
    void flush(Map<String, InvertedDocConsumerPerField> fieldsToFlush, SegmentWriteState state) throws IOException {
        HashMap<String, TermsHashConsumerPerField> childFields = new HashMap<String, TermsHashConsumerPerField>();
        HashMap<String, InvertedDocConsumerPerField> nextChildFields = this.nextTermsHash != null ? new HashMap<String, InvertedDocConsumerPerField>() : null;
        for (Map.Entry<String, InvertedDocConsumerPerField> entry : fieldsToFlush.entrySet()) {
            TermsHashPerField perField = (TermsHashPerField)entry.getValue();
            childFields.put(entry.getKey(), perField.consumer);
            if (this.nextTermsHash == null) continue;
            nextChildFields.put(entry.getKey(), perField.nextPerField);
        }
        this.consumer.flush(childFields, state);
        if (this.nextTermsHash != null) {
            this.nextTermsHash.flush(nextChildFields, state);
        }
    }

    @Override
    InvertedDocConsumerPerField addField(DocInverterPerField docInverterPerField, FieldInfo fieldInfo) {
        return new TermsHashPerField(docInverterPerField, this, this.nextTermsHash, fieldInfo);
    }

    @Override
    void finishDocument() throws IOException {
        this.consumer.finishDocument(this);
        if (this.nextTermsHash != null) {
            this.nextTermsHash.consumer.finishDocument(this.nextTermsHash);
        }
    }

    @Override
    void startDocument() throws IOException {
        this.consumer.startDocument();
        if (this.nextTermsHash != null) {
            this.nextTermsHash.consumer.startDocument();
        }
    }
}


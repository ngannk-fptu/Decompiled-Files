/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.index.DocFieldConsumer;
import org.apache.lucene.index.DocFieldConsumerPerField;
import org.apache.lucene.index.DocInverterPerField;
import org.apache.lucene.index.DocumentsWriterPerThread;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.InvertedDocConsumer;
import org.apache.lucene.index.InvertedDocConsumerPerField;
import org.apache.lucene.index.InvertedDocEndConsumer;
import org.apache.lucene.index.InvertedDocEndConsumerPerField;
import org.apache.lucene.index.SegmentWriteState;

final class DocInverter
extends DocFieldConsumer {
    final InvertedDocConsumer consumer;
    final InvertedDocEndConsumer endConsumer;
    final DocumentsWriterPerThread.DocState docState;

    public DocInverter(DocumentsWriterPerThread.DocState docState, InvertedDocConsumer consumer, InvertedDocEndConsumer endConsumer) {
        this.docState = docState;
        this.consumer = consumer;
        this.endConsumer = endConsumer;
    }

    @Override
    void flush(Map<String, DocFieldConsumerPerField> fieldsToFlush, SegmentWriteState state) throws IOException {
        HashMap<String, InvertedDocConsumerPerField> childFieldsToFlush = new HashMap<String, InvertedDocConsumerPerField>();
        HashMap<String, InvertedDocEndConsumerPerField> endChildFieldsToFlush = new HashMap<String, InvertedDocEndConsumerPerField>();
        for (Map.Entry<String, DocFieldConsumerPerField> fieldToFlush : fieldsToFlush.entrySet()) {
            DocInverterPerField perField = (DocInverterPerField)fieldToFlush.getValue();
            childFieldsToFlush.put(fieldToFlush.getKey(), perField.consumer);
            endChildFieldsToFlush.put(fieldToFlush.getKey(), perField.endConsumer);
        }
        this.consumer.flush(childFieldsToFlush, state);
        this.endConsumer.flush(endChildFieldsToFlush, state);
    }

    @Override
    public void startDocument() throws IOException {
        this.consumer.startDocument();
        this.endConsumer.startDocument();
    }

    @Override
    public void finishDocument() throws IOException {
        this.endConsumer.finishDocument();
        this.consumer.finishDocument();
    }

    @Override
    void abort() {
        try {
            this.consumer.abort();
        }
        finally {
            this.endConsumer.abort();
        }
    }

    @Override
    public DocFieldConsumerPerField addField(FieldInfo fi) {
        return new DocInverterPerField(this, fi);
    }
}


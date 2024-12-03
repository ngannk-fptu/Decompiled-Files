/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.index.StoredFieldsConsumer;

class TwoStoredFieldsConsumers
extends StoredFieldsConsumer {
    private final StoredFieldsConsumer first;
    private final StoredFieldsConsumer second;

    public TwoStoredFieldsConsumers(StoredFieldsConsumer first, StoredFieldsConsumer second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public void addField(int docID, IndexableField field, FieldInfo fieldInfo) throws IOException {
        this.first.addField(docID, field, fieldInfo);
        this.second.addField(docID, field, fieldInfo);
    }

    @Override
    void flush(SegmentWriteState state) throws IOException {
        this.first.flush(state);
        this.second.flush(state);
    }

    @Override
    void abort() {
        try {
            this.first.abort();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            this.second.abort();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    @Override
    void startDocument() throws IOException {
        this.first.startDocument();
        this.second.startDocument();
    }

    @Override
    void finishDocument() throws IOException {
        this.first.finishDocument();
        this.second.finishDocument();
    }
}


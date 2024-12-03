/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.DocInverterPerField;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.InvertedDocConsumerPerField;
import org.apache.lucene.index.SegmentWriteState;

abstract class InvertedDocConsumer {
    InvertedDocConsumer() {
    }

    abstract void abort();

    abstract void flush(Map<String, InvertedDocConsumerPerField> var1, SegmentWriteState var2) throws IOException;

    abstract InvertedDocConsumerPerField addField(DocInverterPerField var1, FieldInfo var2);

    abstract void startDocument() throws IOException;

    abstract void finishDocument() throws IOException;
}


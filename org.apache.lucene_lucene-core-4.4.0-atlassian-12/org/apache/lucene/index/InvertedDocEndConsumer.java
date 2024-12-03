/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.DocInverterPerField;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.InvertedDocEndConsumerPerField;
import org.apache.lucene.index.SegmentWriteState;

abstract class InvertedDocEndConsumer {
    InvertedDocEndConsumer() {
    }

    abstract void flush(Map<String, InvertedDocEndConsumerPerField> var1, SegmentWriteState var2) throws IOException;

    abstract void abort();

    abstract InvertedDocEndConsumerPerField addField(DocInverterPerField var1, FieldInfo var2);

    abstract void startDocument() throws IOException;

    abstract void finishDocument() throws IOException;
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.DocFieldConsumerPerField;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.SegmentWriteState;

abstract class DocFieldConsumer {
    DocFieldConsumer() {
    }

    abstract void flush(Map<String, DocFieldConsumerPerField> var1, SegmentWriteState var2) throws IOException;

    abstract void abort();

    abstract void startDocument() throws IOException;

    abstract DocFieldConsumerPerField addField(FieldInfo var1);

    abstract void finishDocument() throws IOException;
}


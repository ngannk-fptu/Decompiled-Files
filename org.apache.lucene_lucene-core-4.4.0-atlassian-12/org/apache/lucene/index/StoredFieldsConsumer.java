/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.SegmentWriteState;

abstract class StoredFieldsConsumer {
    StoredFieldsConsumer() {
    }

    abstract void addField(int var1, IndexableField var2, FieldInfo var3) throws IOException;

    abstract void flush(SegmentWriteState var1) throws IOException;

    abstract void abort() throws IOException;

    abstract void startDocument() throws IOException;

    abstract void finishDocument() throws IOException;
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.DocFieldConsumerPerField;
import com.atlassian.lucene36.index.DocumentsWriter;
import com.atlassian.lucene36.index.FieldInfo;
import java.io.IOException;

abstract class DocFieldConsumerPerThread {
    DocFieldConsumerPerThread() {
    }

    abstract void startDocument() throws IOException;

    abstract DocumentsWriter.DocWriter finishDocument() throws IOException;

    abstract DocFieldConsumerPerField addField(FieldInfo var1);

    abstract void abort();
}


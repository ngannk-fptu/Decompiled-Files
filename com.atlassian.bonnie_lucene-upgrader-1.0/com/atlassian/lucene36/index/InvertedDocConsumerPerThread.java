/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.DocInverterPerField;
import com.atlassian.lucene36.index.DocumentsWriter;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.InvertedDocConsumerPerField;
import java.io.IOException;

abstract class InvertedDocConsumerPerThread {
    InvertedDocConsumerPerThread() {
    }

    abstract void startDocument() throws IOException;

    abstract InvertedDocConsumerPerField addField(DocInverterPerField var1, FieldInfo var2);

    abstract DocumentsWriter.DocWriter finishDocument() throws IOException;

    abstract void abort();
}


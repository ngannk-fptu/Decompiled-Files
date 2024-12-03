/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.DocumentsWriter;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.TermsHashConsumerPerField;
import com.atlassian.lucene36.index.TermsHashPerField;
import java.io.IOException;

abstract class TermsHashConsumerPerThread {
    TermsHashConsumerPerThread() {
    }

    abstract void startDocument() throws IOException;

    abstract DocumentsWriter.DocWriter finishDocument() throws IOException;

    public abstract TermsHashConsumerPerField addField(TermsHashPerField var1, FieldInfo var2);

    public abstract void abort();
}


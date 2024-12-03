/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.DocumentsWriter;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.FreqProxTermsWriterPerField;
import com.atlassian.lucene36.index.TermsHashConsumerPerField;
import com.atlassian.lucene36.index.TermsHashConsumerPerThread;
import com.atlassian.lucene36.index.TermsHashPerField;
import com.atlassian.lucene36.index.TermsHashPerThread;

final class FreqProxTermsWriterPerThread
extends TermsHashConsumerPerThread {
    final TermsHashPerThread termsHashPerThread;
    final DocumentsWriter.DocState docState;

    public FreqProxTermsWriterPerThread(TermsHashPerThread perThread) {
        this.docState = perThread.docState;
        this.termsHashPerThread = perThread;
    }

    public TermsHashConsumerPerField addField(TermsHashPerField termsHashPerField, FieldInfo fieldInfo) {
        return new FreqProxTermsWriterPerField(termsHashPerField, this, fieldInfo);
    }

    void startDocument() {
    }

    DocumentsWriter.DocWriter finishDocument() {
        return null;
    }

    public void abort() {
    }
}


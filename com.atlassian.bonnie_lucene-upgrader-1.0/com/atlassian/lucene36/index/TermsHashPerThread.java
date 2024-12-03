/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.ByteBlockPool;
import com.atlassian.lucene36.index.CharBlockPool;
import com.atlassian.lucene36.index.DocInverterPerField;
import com.atlassian.lucene36.index.DocInverterPerThread;
import com.atlassian.lucene36.index.DocumentsWriter;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.IntBlockPool;
import com.atlassian.lucene36.index.InvertedDocConsumerPerField;
import com.atlassian.lucene36.index.InvertedDocConsumerPerThread;
import com.atlassian.lucene36.index.TermsHash;
import com.atlassian.lucene36.index.TermsHashConsumerPerThread;
import com.atlassian.lucene36.index.TermsHashPerField;
import java.io.IOException;

final class TermsHashPerThread
extends InvertedDocConsumerPerThread {
    final TermsHash termsHash;
    final TermsHashConsumerPerThread consumer;
    final TermsHashPerThread nextPerThread;
    final CharBlockPool charPool;
    final IntBlockPool intPool;
    final ByteBlockPool bytePool;
    final boolean primary;
    final DocumentsWriter.DocState docState;

    public TermsHashPerThread(DocInverterPerThread docInverterPerThread, TermsHash termsHash, TermsHash nextTermsHash, TermsHashPerThread primaryPerThread) {
        this.docState = docInverterPerThread.docState;
        this.termsHash = termsHash;
        this.consumer = termsHash.consumer.addThread(this);
        if (nextTermsHash != null) {
            this.charPool = new CharBlockPool(termsHash.docWriter);
            this.primary = true;
        } else {
            this.charPool = primaryPerThread.charPool;
            this.primary = false;
        }
        this.intPool = new IntBlockPool(termsHash.docWriter);
        this.bytePool = new ByteBlockPool(termsHash.docWriter.byteBlockAllocator);
        this.nextPerThread = nextTermsHash != null ? nextTermsHash.addThread(docInverterPerThread, this) : null;
    }

    InvertedDocConsumerPerField addField(DocInverterPerField docInverterPerField, FieldInfo fieldInfo) {
        return new TermsHashPerField(docInverterPerField, this, this.nextPerThread, fieldInfo);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void abort() {
        this.reset(true);
        try {
            this.consumer.abort();
            Object var2_1 = null;
            if (this.nextPerThread != null) {
                this.nextPerThread.abort();
            }
        }
        catch (Throwable throwable) {
            Object var2_2 = null;
            if (this.nextPerThread != null) {
                this.nextPerThread.abort();
            }
            throw throwable;
        }
    }

    public void startDocument() throws IOException {
        this.consumer.startDocument();
        if (this.nextPerThread != null) {
            this.nextPerThread.consumer.startDocument();
        }
    }

    public DocumentsWriter.DocWriter finishDocument() throws IOException {
        DocumentsWriter.DocWriter doc = this.consumer.finishDocument();
        DocumentsWriter.DocWriter doc2 = this.nextPerThread != null ? this.nextPerThread.consumer.finishDocument() : null;
        if (doc == null) {
            return doc2;
        }
        doc.setNext(doc2);
        return doc;
    }

    void reset(boolean recyclePostings) {
        this.intPool.reset();
        this.bytePool.reset();
        if (this.primary) {
            this.charPool.reset();
        }
    }
}


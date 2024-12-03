/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.DocInverterPerField;
import com.atlassian.lucene36.index.DocInverterPerThread;
import com.atlassian.lucene36.index.DocumentsWriter;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.InvertedDocEndConsumerPerField;
import com.atlassian.lucene36.index.InvertedDocEndConsumerPerThread;
import com.atlassian.lucene36.index.NormsWriter;
import com.atlassian.lucene36.index.NormsWriterPerField;

final class NormsWriterPerThread
extends InvertedDocEndConsumerPerThread {
    final NormsWriter normsWriter;
    final DocumentsWriter.DocState docState;

    public NormsWriterPerThread(DocInverterPerThread docInverterPerThread, NormsWriter normsWriter) {
        this.normsWriter = normsWriter;
        this.docState = docInverterPerThread.docState;
    }

    InvertedDocEndConsumerPerField addField(DocInverterPerField docInverterPerField, FieldInfo fieldInfo) {
        return new NormsWriterPerField(docInverterPerField, this, fieldInfo);
    }

    void abort() {
    }

    void startDocument() {
    }

    void finishDocument() {
    }

    boolean freeRAM() {
        return false;
    }
}


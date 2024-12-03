/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.DocInverterPerThread;
import com.atlassian.lucene36.index.DocumentsWriter;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.InvertedDocConsumer;
import com.atlassian.lucene36.index.InvertedDocConsumerPerField;
import com.atlassian.lucene36.index.InvertedDocConsumerPerThread;
import com.atlassian.lucene36.index.SegmentWriteState;
import com.atlassian.lucene36.index.TermsHashConsumer;
import com.atlassian.lucene36.index.TermsHashConsumerPerField;
import com.atlassian.lucene36.index.TermsHashConsumerPerThread;
import com.atlassian.lucene36.index.TermsHashPerField;
import com.atlassian.lucene36.index.TermsHashPerThread;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class TermsHash
extends InvertedDocConsumer {
    final TermsHashConsumer consumer;
    final TermsHash nextTermsHash;
    final DocumentsWriter docWriter;
    boolean trackAllocations;

    public TermsHash(DocumentsWriter docWriter, boolean trackAllocations, TermsHashConsumer consumer, TermsHash nextTermsHash) {
        this.docWriter = docWriter;
        this.consumer = consumer;
        this.nextTermsHash = nextTermsHash;
        this.trackAllocations = trackAllocations;
    }

    @Override
    InvertedDocConsumerPerThread addThread(DocInverterPerThread docInverterPerThread) {
        return new TermsHashPerThread(docInverterPerThread, this, this.nextTermsHash, null);
    }

    TermsHashPerThread addThread(DocInverterPerThread docInverterPerThread, TermsHashPerThread primaryPerThread) {
        return new TermsHashPerThread(docInverterPerThread, this, this.nextTermsHash, primaryPerThread);
    }

    @Override
    void setFieldInfos(FieldInfos fieldInfos) {
        this.fieldInfos = fieldInfos;
        this.consumer.setFieldInfos(fieldInfos);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void abort() {
        try {
            this.consumer.abort();
            Object var2_1 = null;
            if (this.nextTermsHash != null) {
                this.nextTermsHash.abort();
            }
        }
        catch (Throwable throwable) {
            Object var2_2 = null;
            if (this.nextTermsHash != null) {
                this.nextTermsHash.abort();
            }
            throw throwable;
        }
    }

    @Override
    synchronized void flush(Map<InvertedDocConsumerPerThread, Collection<InvertedDocConsumerPerField>> threadsAndFields, SegmentWriteState state) throws IOException {
        HashMap<TermsHashConsumerPerThread, Collection<TermsHashConsumerPerField>> childThreadsAndFields = new HashMap<TermsHashConsumerPerThread, Collection<TermsHashConsumerPerField>>();
        HashMap<InvertedDocConsumerPerThread, Collection<InvertedDocConsumerPerField>> nextThreadsAndFields = this.nextTermsHash != null ? new HashMap<InvertedDocConsumerPerThread, Collection<InvertedDocConsumerPerField>>() : null;
        for (Map.Entry<InvertedDocConsumerPerThread, Collection<InvertedDocConsumerPerField>> entry : threadsAndFields.entrySet()) {
            TermsHashPerThread perThread = (TermsHashPerThread)entry.getKey();
            Collection<InvertedDocConsumerPerField> fields = entry.getValue();
            Iterator<InvertedDocConsumerPerField> fieldsIt = fields.iterator();
            HashSet<TermsHashConsumerPerField> childFields = new HashSet<TermsHashConsumerPerField>();
            HashSet<TermsHashPerField> nextChildFields = this.nextTermsHash != null ? new HashSet<TermsHashPerField>() : null;
            while (fieldsIt.hasNext()) {
                TermsHashPerField perField = (TermsHashPerField)fieldsIt.next();
                childFields.add(perField.consumer);
                if (this.nextTermsHash == null) continue;
                nextChildFields.add(perField.nextPerField);
            }
            childThreadsAndFields.put(perThread.consumer, childFields);
            if (this.nextTermsHash == null) continue;
            nextThreadsAndFields.put(perThread.nextPerThread, nextChildFields);
        }
        this.consumer.flush(childThreadsAndFields, state);
        if (this.nextTermsHash != null) {
            this.nextTermsHash.flush(nextThreadsAndFields, state);
        }
    }

    @Override
    public synchronized boolean freeRAM() {
        return false;
    }
}


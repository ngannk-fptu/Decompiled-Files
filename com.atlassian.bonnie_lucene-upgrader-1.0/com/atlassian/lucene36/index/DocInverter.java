/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.DocFieldConsumer;
import com.atlassian.lucene36.index.DocFieldConsumerPerField;
import com.atlassian.lucene36.index.DocFieldConsumerPerThread;
import com.atlassian.lucene36.index.DocFieldProcessorPerThread;
import com.atlassian.lucene36.index.DocInverterPerField;
import com.atlassian.lucene36.index.DocInverterPerThread;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.InvertedDocConsumer;
import com.atlassian.lucene36.index.InvertedDocConsumerPerField;
import com.atlassian.lucene36.index.InvertedDocConsumerPerThread;
import com.atlassian.lucene36.index.InvertedDocEndConsumer;
import com.atlassian.lucene36.index.InvertedDocEndConsumerPerField;
import com.atlassian.lucene36.index.InvertedDocEndConsumerPerThread;
import com.atlassian.lucene36.index.SegmentWriteState;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class DocInverter
extends DocFieldConsumer {
    final InvertedDocConsumer consumer;
    final InvertedDocEndConsumer endConsumer;

    public DocInverter(InvertedDocConsumer consumer, InvertedDocEndConsumer endConsumer) {
        this.consumer = consumer;
        this.endConsumer = endConsumer;
    }

    @Override
    void setFieldInfos(FieldInfos fieldInfos) {
        super.setFieldInfos(fieldInfos);
        this.consumer.setFieldInfos(fieldInfos);
        this.endConsumer.setFieldInfos(fieldInfos);
    }

    @Override
    void flush(Map<DocFieldConsumerPerThread, Collection<DocFieldConsumerPerField>> threadsAndFields, SegmentWriteState state) throws IOException {
        HashMap<InvertedDocConsumerPerThread, Collection<InvertedDocConsumerPerField>> childThreadsAndFields = new HashMap<InvertedDocConsumerPerThread, Collection<InvertedDocConsumerPerField>>();
        HashMap<InvertedDocEndConsumerPerThread, Collection<InvertedDocEndConsumerPerField>> endChildThreadsAndFields = new HashMap<InvertedDocEndConsumerPerThread, Collection<InvertedDocEndConsumerPerField>>();
        for (Map.Entry<DocFieldConsumerPerThread, Collection<DocFieldConsumerPerField>> entry : threadsAndFields.entrySet()) {
            DocInverterPerThread perThread = (DocInverterPerThread)entry.getKey();
            HashSet<InvertedDocConsumerPerField> childFields = new HashSet<InvertedDocConsumerPerField>();
            HashSet<InvertedDocEndConsumerPerField> endChildFields = new HashSet<InvertedDocEndConsumerPerField>();
            for (DocFieldConsumerPerField field : entry.getValue()) {
                DocInverterPerField perField = (DocInverterPerField)field;
                childFields.add(perField.consumer);
                endChildFields.add(perField.endConsumer);
            }
            childThreadsAndFields.put(perThread.consumer, childFields);
            endChildThreadsAndFields.put(perThread.endConsumer, endChildFields);
        }
        this.consumer.flush(childThreadsAndFields, state);
        this.endConsumer.flush(endChildThreadsAndFields, state);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void abort() {
        try {
            this.consumer.abort();
            Object var2_1 = null;
            this.endConsumer.abort();
        }
        catch (Throwable throwable) {
            Object var2_2 = null;
            this.endConsumer.abort();
            throw throwable;
        }
    }

    @Override
    public boolean freeRAM() {
        return this.consumer.freeRAM();
    }

    @Override
    public DocFieldConsumerPerThread addThread(DocFieldProcessorPerThread docFieldProcessorPerThread) {
        return new DocInverterPerThread(docFieldProcessorPerThread, this);
    }
}


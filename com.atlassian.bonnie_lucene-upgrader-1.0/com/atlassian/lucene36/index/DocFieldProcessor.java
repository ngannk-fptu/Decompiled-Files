/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.DocConsumer;
import com.atlassian.lucene36.index.DocConsumerPerThread;
import com.atlassian.lucene36.index.DocFieldConsumer;
import com.atlassian.lucene36.index.DocFieldConsumerPerField;
import com.atlassian.lucene36.index.DocFieldConsumerPerThread;
import com.atlassian.lucene36.index.DocFieldProcessorPerThread;
import com.atlassian.lucene36.index.DocumentsWriter;
import com.atlassian.lucene36.index.DocumentsWriterThreadState;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.IndexFileNames;
import com.atlassian.lucene36.index.SegmentWriteState;
import com.atlassian.lucene36.index.StoredFieldsWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class DocFieldProcessor
extends DocConsumer {
    final DocumentsWriter docWriter;
    final FieldInfos fieldInfos;
    final DocFieldConsumer consumer;
    final StoredFieldsWriter fieldsWriter;

    public DocFieldProcessor(DocumentsWriter docWriter, DocFieldConsumer consumer) {
        this.docWriter = docWriter;
        this.consumer = consumer;
        this.fieldInfos = docWriter.getFieldInfos();
        consumer.setFieldInfos(this.fieldInfos);
        this.fieldsWriter = new StoredFieldsWriter(docWriter, this.fieldInfos);
    }

    @Override
    public void flush(Collection<DocConsumerPerThread> threads, SegmentWriteState state) throws IOException {
        HashMap<DocFieldConsumerPerThread, Collection<DocFieldConsumerPerField>> childThreadsAndFields = new HashMap<DocFieldConsumerPerThread, Collection<DocFieldConsumerPerField>>();
        for (DocConsumerPerThread thread : threads) {
            DocFieldProcessorPerThread perThread = (DocFieldProcessorPerThread)thread;
            childThreadsAndFields.put(perThread.consumer, perThread.fields());
            perThread.trimFields(state);
        }
        this.fieldsWriter.flush(state);
        this.consumer.flush(childThreadsAndFields, state);
        String fileName = IndexFileNames.segmentFileName(state.segmentName, "fnm");
        this.fieldInfos.write(state.directory, fileName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void abort() {
        try {
            this.fieldsWriter.abort();
            Object var2_1 = null;
            this.consumer.abort();
        }
        catch (Throwable throwable) {
            Object var2_2 = null;
            this.consumer.abort();
            throw throwable;
        }
    }

    @Override
    public boolean freeRAM() {
        return this.consumer.freeRAM();
    }

    @Override
    public DocConsumerPerThread addThread(DocumentsWriterThreadState threadState) throws IOException {
        return new DocFieldProcessorPerThread(threadState, this);
    }
}


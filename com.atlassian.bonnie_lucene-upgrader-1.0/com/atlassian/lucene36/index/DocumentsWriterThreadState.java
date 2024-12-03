/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.DocConsumerPerThread;
import com.atlassian.lucene36.index.DocumentsWriter;
import java.io.IOException;

final class DocumentsWriterThreadState {
    boolean isIdle = true;
    int numThreads = 1;
    final DocConsumerPerThread consumer;
    final DocumentsWriter.DocState docState;
    final DocumentsWriter docWriter;

    public DocumentsWriterThreadState(DocumentsWriter docWriter) throws IOException {
        this.docWriter = docWriter;
        this.docState = new DocumentsWriter.DocState();
        this.docState.maxFieldLength = docWriter.maxFieldLength;
        this.docState.infoStream = docWriter.infoStream;
        this.docState.similarity = docWriter.similarity;
        this.docState.docWriter = docWriter;
        this.consumer = docWriter.consumer.addThread(this);
    }

    void doAfterFlush() {
        this.numThreads = 0;
    }
}


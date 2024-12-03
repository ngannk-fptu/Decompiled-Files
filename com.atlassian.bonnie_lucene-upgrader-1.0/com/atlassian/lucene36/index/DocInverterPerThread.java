/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.analysis.tokenattributes.CharTermAttribute;
import com.atlassian.lucene36.analysis.tokenattributes.OffsetAttribute;
import com.atlassian.lucene36.index.DocFieldConsumerPerField;
import com.atlassian.lucene36.index.DocFieldConsumerPerThread;
import com.atlassian.lucene36.index.DocFieldProcessorPerThread;
import com.atlassian.lucene36.index.DocInverter;
import com.atlassian.lucene36.index.DocInverterPerField;
import com.atlassian.lucene36.index.DocumentsWriter;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.FieldInvertState;
import com.atlassian.lucene36.index.InvertedDocConsumerPerThread;
import com.atlassian.lucene36.index.InvertedDocEndConsumerPerThread;
import com.atlassian.lucene36.index.ReusableStringReader;
import com.atlassian.lucene36.util.AttributeSource;
import java.io.IOException;

final class DocInverterPerThread
extends DocFieldConsumerPerThread {
    final DocInverter docInverter;
    final InvertedDocConsumerPerThread consumer;
    final InvertedDocEndConsumerPerThread endConsumer;
    final SingleTokenAttributeSource singleToken = new SingleTokenAttributeSource();
    final DocumentsWriter.DocState docState;
    final FieldInvertState fieldState = new FieldInvertState();
    final ReusableStringReader stringReader = new ReusableStringReader();

    public DocInverterPerThread(DocFieldProcessorPerThread docFieldProcessorPerThread, DocInverter docInverter) {
        this.docInverter = docInverter;
        this.docState = docFieldProcessorPerThread.docState;
        this.consumer = docInverter.consumer.addThread(this);
        this.endConsumer = docInverter.endConsumer.addThread(this);
    }

    public void startDocument() throws IOException {
        this.consumer.startDocument();
        this.endConsumer.startDocument();
    }

    public DocumentsWriter.DocWriter finishDocument() throws IOException {
        this.endConsumer.finishDocument();
        return this.consumer.finishDocument();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
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

    public DocFieldConsumerPerField addField(FieldInfo fi) {
        return new DocInverterPerField(this, fi);
    }

    static class SingleTokenAttributeSource
    extends AttributeSource {
        final CharTermAttribute termAttribute = this.addAttribute(CharTermAttribute.class);
        final OffsetAttribute offsetAttribute = this.addAttribute(OffsetAttribute.class);

        private SingleTokenAttributeSource() {
        }

        public void reinit(String stringValue, int startOffset, int endOffset) {
            this.termAttribute.setEmpty().append(stringValue);
            this.offsetAttribute.setOffset(startOffset, endOffset);
        }
    }
}


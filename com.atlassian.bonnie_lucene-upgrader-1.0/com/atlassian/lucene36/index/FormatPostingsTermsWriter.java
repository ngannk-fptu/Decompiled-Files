/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.FormatPostingsDocsConsumer;
import com.atlassian.lucene36.index.FormatPostingsDocsWriter;
import com.atlassian.lucene36.index.FormatPostingsFieldsWriter;
import com.atlassian.lucene36.index.FormatPostingsTermsConsumer;
import com.atlassian.lucene36.index.SegmentWriteState;
import com.atlassian.lucene36.index.TermInfosWriter;
import java.io.Closeable;
import java.io.IOException;

final class FormatPostingsTermsWriter
extends FormatPostingsTermsConsumer
implements Closeable {
    final FormatPostingsFieldsWriter parent;
    final FormatPostingsDocsWriter docsWriter;
    final TermInfosWriter termsOut;
    FieldInfo fieldInfo;
    char[] currentTerm;
    int currentTermStart;
    long freqStart;
    long proxStart;

    FormatPostingsTermsWriter(SegmentWriteState state, FormatPostingsFieldsWriter parent) throws IOException {
        this.parent = parent;
        this.termsOut = parent.termsOut;
        this.docsWriter = new FormatPostingsDocsWriter(state, this);
    }

    void setField(FieldInfo fieldInfo) {
        this.fieldInfo = fieldInfo;
        this.docsWriter.setField(fieldInfo);
    }

    FormatPostingsDocsConsumer addTerm(char[] text, int start) {
        this.currentTerm = text;
        this.currentTermStart = start;
        this.freqStart = this.docsWriter.out.getFilePointer();
        if (this.docsWriter.posWriter.out != null) {
            this.proxStart = this.docsWriter.posWriter.out.getFilePointer();
        }
        this.parent.skipListWriter.resetSkip();
        return this.docsWriter;
    }

    void finish() {
    }

    public void close() throws IOException {
        this.docsWriter.close();
    }
}


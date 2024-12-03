/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.ByteSliceReader;
import com.atlassian.lucene36.index.DocumentsWriter;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.TermVectorsTermsWriter;
import com.atlassian.lucene36.index.TermVectorsTermsWriterPerField;
import com.atlassian.lucene36.index.TermsHashConsumerPerField;
import com.atlassian.lucene36.index.TermsHashConsumerPerThread;
import com.atlassian.lucene36.index.TermsHashPerField;
import com.atlassian.lucene36.index.TermsHashPerThread;
import com.atlassian.lucene36.util.UnicodeUtil;

final class TermVectorsTermsWriterPerThread
extends TermsHashConsumerPerThread {
    final TermVectorsTermsWriter termsWriter;
    final TermsHashPerThread termsHashPerThread;
    final DocumentsWriter.DocState docState;
    TermVectorsTermsWriter.PerDoc doc;
    final ByteSliceReader vectorSliceReader = new ByteSliceReader();
    final UnicodeUtil.UTF8Result[] utf8Results = new UnicodeUtil.UTF8Result[]{new UnicodeUtil.UTF8Result(), new UnicodeUtil.UTF8Result()};
    String lastVectorFieldName;

    public TermVectorsTermsWriterPerThread(TermsHashPerThread termsHashPerThread, TermVectorsTermsWriter termsWriter) {
        this.termsWriter = termsWriter;
        this.termsHashPerThread = termsHashPerThread;
        this.docState = termsHashPerThread.docState;
    }

    public void startDocument() {
        assert (this.clearLastVectorFieldName());
        if (this.doc != null) {
            this.doc.reset();
            this.doc.docID = this.docState.docID;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public DocumentsWriter.DocWriter finishDocument() {
        try {
            TermVectorsTermsWriter.PerDoc perDoc = this.doc;
            Object var3_2 = null;
            this.doc = null;
            return perDoc;
        }
        catch (Throwable throwable) {
            Object var3_3 = null;
            this.doc = null;
            throw throwable;
        }
    }

    public TermsHashConsumerPerField addField(TermsHashPerField termsHashPerField, FieldInfo fieldInfo) {
        return new TermVectorsTermsWriterPerField(termsHashPerField, this, fieldInfo);
    }

    public void abort() {
        if (this.doc != null) {
            this.doc.abort();
            this.doc = null;
        }
    }

    final boolean clearLastVectorFieldName() {
        this.lastVectorFieldName = null;
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final boolean vectorFieldsInOrder(FieldInfo fi) {
        block3: {
            try {
                if (this.lastVectorFieldName == null) break block3;
                boolean bl = this.lastVectorFieldName.compareTo(fi.name) < 0;
                Object var4_4 = null;
                this.lastVectorFieldName = fi.name;
                return bl;
            }
            catch (Throwable throwable) {
                Object var4_6 = null;
                this.lastVectorFieldName = fi.name;
                throw throwable;
            }
        }
        boolean bl = true;
        Object var4_5 = null;
        this.lastVectorFieldName = fi.name;
        return bl;
    }
}


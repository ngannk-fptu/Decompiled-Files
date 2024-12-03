/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.document.Fieldable;
import com.atlassian.lucene36.index.DocumentsWriter;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.FieldsWriter;
import com.atlassian.lucene36.index.StoredFieldsWriter;
import com.atlassian.lucene36.store.IndexOutput;
import java.io.IOException;

final class StoredFieldsWriterPerThread {
    final FieldsWriter localFieldsWriter;
    final StoredFieldsWriter storedFieldsWriter;
    final DocumentsWriter.DocState docState;
    StoredFieldsWriter.PerDoc doc;

    public StoredFieldsWriterPerThread(DocumentsWriter.DocState docState, StoredFieldsWriter storedFieldsWriter) throws IOException {
        this.storedFieldsWriter = storedFieldsWriter;
        this.docState = docState;
        this.localFieldsWriter = new FieldsWriter((IndexOutput)null, (IndexOutput)null, storedFieldsWriter.fieldInfos);
    }

    public void startDocument() {
        if (this.doc != null) {
            this.doc.reset();
            this.doc.docID = this.docState.docID;
        }
    }

    public void addField(Fieldable field, FieldInfo fieldInfo) throws IOException {
        if (this.doc == null) {
            this.doc = this.storedFieldsWriter.getPerDoc();
            this.doc.docID = this.docState.docID;
            this.localFieldsWriter.setFieldsStream(this.doc.fdt);
            assert (this.doc.numStoredFields == 0) : "doc.numStoredFields=" + this.doc.numStoredFields;
            assert (0L == this.doc.fdt.length());
            assert (0L == this.doc.fdt.getFilePointer());
        }
        this.localFieldsWriter.writeField(fieldInfo, field);
        assert (this.docState.testPoint("StoredFieldsWriterPerThread.processFields.writeField"));
        ++this.doc.numStoredFields;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public DocumentsWriter.DocWriter finishDocument() {
        try {
            StoredFieldsWriter.PerDoc perDoc = this.doc;
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

    public void abort() {
        if (this.doc != null) {
            this.doc.abort();
            this.doc = null;
        }
    }
}


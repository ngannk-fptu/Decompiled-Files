/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.document.Document;
import com.atlassian.lucene36.document.Fieldable;
import com.atlassian.lucene36.index.DocConsumerPerThread;
import com.atlassian.lucene36.index.DocFieldConsumerPerField;
import com.atlassian.lucene36.index.DocFieldConsumerPerThread;
import com.atlassian.lucene36.index.DocFieldProcessor;
import com.atlassian.lucene36.index.DocFieldProcessorPerField;
import com.atlassian.lucene36.index.DocumentsWriter;
import com.atlassian.lucene36.index.DocumentsWriterThreadState;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.SegmentWriteState;
import com.atlassian.lucene36.index.StoredFieldsWriterPerThread;
import com.atlassian.lucene36.util.ArrayUtil;
import com.atlassian.lucene36.util.RamUsageEstimator;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class DocFieldProcessorPerThread
extends DocConsumerPerThread {
    float docBoost;
    int fieldGen;
    final DocFieldProcessor docFieldProcessor;
    final FieldInfos fieldInfos;
    final DocFieldConsumerPerThread consumer;
    DocFieldProcessorPerField[] fields = new DocFieldProcessorPerField[1];
    int fieldCount;
    DocFieldProcessorPerField[] fieldHash = new DocFieldProcessorPerField[2];
    int hashMask = 1;
    int totalFieldCount;
    final StoredFieldsWriterPerThread fieldsWriter;
    final DocumentsWriter.DocState docState;
    private static final Comparator<DocFieldProcessorPerField> fieldsComp = new Comparator<DocFieldProcessorPerField>(){

        @Override
        public int compare(DocFieldProcessorPerField o1, DocFieldProcessorPerField o2) {
            return o1.fieldInfo.name.compareTo(o2.fieldInfo.name);
        }
    };
    PerDoc[] docFreeList = new PerDoc[1];
    int freeCount;
    int allocCount;

    public DocFieldProcessorPerThread(DocumentsWriterThreadState threadState, DocFieldProcessor docFieldProcessor) throws IOException {
        this.docState = threadState.docState;
        this.docFieldProcessor = docFieldProcessor;
        this.fieldInfos = docFieldProcessor.fieldInfos;
        this.consumer = docFieldProcessor.consumer.addThread(this);
        this.fieldsWriter = docFieldProcessor.fieldsWriter.addThread(this.docState);
    }

    @Override
    public void abort() {
        Throwable th;
        block13: {
            block12: {
                th = null;
                for (DocFieldProcessorPerField field : this.fieldHash) {
                    while (field != null) {
                        DocFieldProcessorPerField next;
                        block11: {
                            next = field.next;
                            try {
                                field.abort();
                            }
                            catch (Throwable t) {
                                if (th != null) break block11;
                                th = t;
                            }
                        }
                        field = next;
                    }
                }
                try {
                    this.fieldsWriter.abort();
                }
                catch (Throwable t) {
                    if (th != null) break block12;
                    th = t;
                }
            }
            try {
                this.consumer.abort();
            }
            catch (Throwable t) {
                if (th != null) break block13;
                th = t;
            }
        }
        if (th != null) {
            if (th instanceof RuntimeException) {
                throw (RuntimeException)th;
            }
            if (th instanceof Error) {
                throw (Error)th;
            }
            throw new RuntimeException(th);
        }
    }

    public Collection<DocFieldConsumerPerField> fields() {
        HashSet<DocFieldConsumerPerField> fields = new HashSet<DocFieldConsumerPerField>();
        for (int i = 0; i < this.fieldHash.length; ++i) {
            DocFieldProcessorPerField field = this.fieldHash[i];
            while (field != null) {
                fields.add(field.consumer);
                field = field.next;
            }
        }
        assert (fields.size() == this.totalFieldCount);
        return fields;
    }

    void trimFields(SegmentWriteState state) {
        for (int i = 0; i < this.fieldHash.length; ++i) {
            DocFieldProcessorPerField perField = this.fieldHash[i];
            DocFieldProcessorPerField lastPerField = null;
            while (perField != null) {
                if (perField.lastGen == -1) {
                    if (lastPerField == null) {
                        this.fieldHash[i] = perField.next;
                    } else {
                        lastPerField.next = perField.next;
                    }
                    if (state.infoStream != null) {
                        state.infoStream.println("  purge field=" + perField.fieldInfo.name);
                    }
                    perField.consumer.close();
                    --this.totalFieldCount;
                } else {
                    perField.lastGen = -1;
                    lastPerField = perField;
                }
                perField = perField.next;
            }
        }
    }

    private void rehash() {
        int newHashSize = this.fieldHash.length * 2;
        assert (newHashSize > this.fieldHash.length);
        DocFieldProcessorPerField[] newHashArray = new DocFieldProcessorPerField[newHashSize];
        int newHashMask = newHashSize - 1;
        for (int j = 0; j < this.fieldHash.length; ++j) {
            DocFieldProcessorPerField fp0 = this.fieldHash[j];
            while (fp0 != null) {
                int hashPos2 = fp0.fieldInfo.name.hashCode() & newHashMask;
                DocFieldProcessorPerField nextFP0 = fp0.next;
                fp0.next = newHashArray[hashPos2];
                newHashArray[hashPos2] = fp0;
                fp0 = nextFP0;
            }
        }
        this.fieldHash = newHashArray;
        this.hashMask = newHashMask;
    }

    @Override
    public DocumentsWriter.DocWriter processDocument() throws IOException {
        int i;
        this.consumer.startDocument();
        this.fieldsWriter.startDocument();
        Document doc = this.docState.doc;
        assert (this.docFieldProcessor.docWriter.writer.testPoint("DocumentsWriter.ThreadState.init start"));
        this.fieldCount = 0;
        int thisFieldGen = this.fieldGen++;
        List<Fieldable> docFields = doc.getFields();
        int numDocFields = docFields.size();
        for (i = 0; i < numDocFields; ++i) {
            Fieldable field = docFields.get(i);
            String fieldName = field.name();
            int hashPos = fieldName.hashCode() & this.hashMask;
            DocFieldProcessorPerField fp = this.fieldHash[hashPos];
            while (fp != null && !fp.fieldInfo.name.equals(fieldName)) {
                fp = fp.next;
            }
            if (fp == null) {
                FieldInfo fi = this.fieldInfos.add(fieldName, field.isIndexed(), field.isTermVectorStored(), field.getOmitNorms(), false, field.getIndexOptions());
                fp = new DocFieldProcessorPerField(this, fi);
                fp.next = this.fieldHash[hashPos];
                this.fieldHash[hashPos] = fp;
                ++this.totalFieldCount;
                if (this.totalFieldCount >= this.fieldHash.length / 2) {
                    this.rehash();
                }
            } else {
                fp.fieldInfo.update(field.isIndexed(), field.isTermVectorStored(), field.getOmitNorms(), false, field.getIndexOptions());
            }
            if (thisFieldGen != fp.lastGen) {
                fp.fieldCount = 0;
                if (this.fieldCount == this.fields.length) {
                    int newSize = this.fields.length * 2;
                    DocFieldProcessorPerField[] newArray = new DocFieldProcessorPerField[newSize];
                    System.arraycopy(this.fields, 0, newArray, 0, this.fieldCount);
                    this.fields = newArray;
                }
                this.fields[this.fieldCount++] = fp;
                fp.lastGen = thisFieldGen;
            }
            if (fp.fieldCount == fp.fields.length) {
                Fieldable[] newArray = new Fieldable[fp.fields.length * 2];
                System.arraycopy(fp.fields, 0, newArray, 0, fp.fieldCount);
                fp.fields = newArray;
            }
            fp.fields[fp.fieldCount++] = field;
            if (!field.isStored()) continue;
            this.fieldsWriter.addField(field, fp.fieldInfo);
        }
        ArrayUtil.quickSort(this.fields, 0, this.fieldCount, fieldsComp);
        for (i = 0; i < this.fieldCount; ++i) {
            this.fields[i].consumer.processFields(this.fields[i].fields, this.fields[i].fieldCount);
        }
        if (this.docState.maxTermPrefix != null && this.docState.infoStream != null) {
            this.docState.infoStream.println("WARNING: document contains at least one immense term (longer than the max length 16383), all of which were skipped.  Please correct the analyzer to not produce such terms.  The prefix of the first immense term is: '" + this.docState.maxTermPrefix + "...'");
            this.docState.maxTermPrefix = null;
        }
        DocumentsWriter.DocWriter one = this.fieldsWriter.finishDocument();
        DocumentsWriter.DocWriter two = this.consumer.finishDocument();
        if (one == null) {
            return two;
        }
        if (two == null) {
            return one;
        }
        PerDoc both = this.getPerDoc();
        both.docID = this.docState.docID;
        assert (one.docID == this.docState.docID);
        assert (two.docID == this.docState.docID);
        both.one = one;
        both.two = two;
        return both;
    }

    synchronized PerDoc getPerDoc() {
        if (this.freeCount == 0) {
            ++this.allocCount;
            if (this.allocCount > this.docFreeList.length) {
                assert (this.allocCount == 1 + this.docFreeList.length);
                this.docFreeList = new PerDoc[ArrayUtil.oversize(this.allocCount, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
            }
            return new PerDoc();
        }
        return this.docFreeList[--this.freeCount];
    }

    synchronized void freePerDoc(PerDoc perDoc) {
        assert (this.freeCount < this.docFreeList.length);
        this.docFreeList[this.freeCount++] = perDoc;
    }

    class PerDoc
    extends DocumentsWriter.DocWriter {
        DocumentsWriter.DocWriter one;
        DocumentsWriter.DocWriter two;

        PerDoc() {
        }

        public long sizeInBytes() {
            return this.one.sizeInBytes() + this.two.sizeInBytes();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void finish() throws IOException {
            try {
                try {
                    this.one.finish();
                    Object var2_1 = null;
                }
                catch (Throwable throwable) {
                    Object var2_2 = null;
                    this.two.finish();
                    throw throwable;
                }
                this.two.finish();
                Object var4_4 = null;
                DocFieldProcessorPerThread.this.freePerDoc(this);
            }
            catch (Throwable throwable) {
                Object var4_5 = null;
                DocFieldProcessorPerThread.this.freePerDoc(this);
                throw throwable;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void abort() {
            try {
                try {
                    this.one.abort();
                    Object var2_1 = null;
                    this.two.abort();
                }
                catch (Throwable throwable) {
                    Object var2_2 = null;
                    this.two.abort();
                    throw throwable;
                }
                Object var4_4 = null;
                DocFieldProcessorPerThread.this.freePerDoc(this);
            }
            catch (Throwable throwable) {
                Object var4_5 = null;
                DocFieldProcessorPerThread.this.freePerDoc(this);
                throw throwable;
            }
        }
    }
}


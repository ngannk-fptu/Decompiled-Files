/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.FieldInfosWriter;
import org.apache.lucene.index.DocConsumer;
import org.apache.lucene.index.DocFieldConsumer;
import org.apache.lucene.index.DocFieldConsumerPerField;
import org.apache.lucene.index.DocFieldProcessorPerField;
import org.apache.lucene.index.DocumentsWriterPerThread;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.index.StoredFieldsConsumer;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.Counter;

final class DocFieldProcessor
extends DocConsumer {
    final DocFieldConsumer consumer;
    final StoredFieldsConsumer storedConsumer;
    final Codec codec;
    DocFieldProcessorPerField[] fields = new DocFieldProcessorPerField[1];
    int fieldCount;
    DocFieldProcessorPerField[] fieldHash = new DocFieldProcessorPerField[2];
    int hashMask = 1;
    int totalFieldCount;
    int fieldGen;
    final DocumentsWriterPerThread.DocState docState;
    final Counter bytesUsed;
    private static final Comparator<DocFieldProcessorPerField> fieldsComp = new Comparator<DocFieldProcessorPerField>(){

        @Override
        public int compare(DocFieldProcessorPerField o1, DocFieldProcessorPerField o2) {
            return o1.fieldInfo.name.compareTo(o2.fieldInfo.name);
        }
    };

    public DocFieldProcessor(DocumentsWriterPerThread docWriter, DocFieldConsumer consumer, StoredFieldsConsumer storedConsumer) {
        this.docState = docWriter.docState;
        this.codec = docWriter.codec;
        this.bytesUsed = docWriter.bytesUsed;
        this.consumer = consumer;
        this.storedConsumer = storedConsumer;
    }

    @Override
    public void flush(SegmentWriteState state) throws IOException {
        HashMap<String, DocFieldConsumerPerField> childFields = new HashMap<String, DocFieldConsumerPerField>();
        Collection<DocFieldConsumerPerField> fields = this.fields();
        for (DocFieldConsumerPerField f : fields) {
            childFields.put(f.getFieldInfo().name, f);
        }
        assert (fields.size() == this.totalFieldCount);
        this.storedConsumer.flush(state);
        this.consumer.flush(childFields, state);
        FieldInfosWriter infosWriter = this.codec.fieldInfosFormat().getFieldInfosWriter();
        infosWriter.write(state.directory, state.segmentInfo.name, state.fieldInfos, IOContext.DEFAULT);
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
                    this.storedConsumer.abort();
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
    public void processDocument(FieldInfos.Builder fieldInfos) throws IOException {
        this.consumer.startDocument();
        this.storedConsumer.startDocument();
        this.fieldCount = 0;
        int thisFieldGen = this.fieldGen++;
        for (IndexableField indexableField : this.docState.doc) {
            String fieldName = indexableField.name();
            int hashPos = fieldName.hashCode() & this.hashMask;
            DocFieldProcessorPerField fp = this.fieldHash[hashPos];
            while (fp != null && !fp.fieldInfo.name.equals(fieldName)) {
                fp = fp.next;
            }
            if (fp == null) {
                FieldInfo fi = fieldInfos.addOrUpdate(fieldName, indexableField.fieldType());
                fp = new DocFieldProcessorPerField(this, fi);
                fp.next = this.fieldHash[hashPos];
                this.fieldHash[hashPos] = fp;
                ++this.totalFieldCount;
                if (this.totalFieldCount >= this.fieldHash.length / 2) {
                    this.rehash();
                }
            } else {
                fp.fieldInfo.update(indexableField.fieldType());
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
            fp.addField(indexableField);
            this.storedConsumer.addField(this.docState.docID, indexableField, fp.fieldInfo);
        }
        ArrayUtil.introSort(this.fields, 0, this.fieldCount, fieldsComp);
        for (int i = 0; i < this.fieldCount; ++i) {
            DocFieldProcessorPerField docFieldProcessorPerField = this.fields[i];
            docFieldProcessorPerField.consumer.processFields(docFieldProcessorPerField.fields, docFieldProcessorPerField.fieldCount);
        }
        if (this.docState.maxTermPrefix != null && this.docState.infoStream.isEnabled("IW")) {
            this.docState.infoStream.message("IW", "WARNING: document contains at least one immense term (whose UTF8 encoding is longer than the max length 32766), all of which were skipped.  Please correct the analyzer to not produce such terms.  The prefix of the first immense term is: '" + this.docState.maxTermPrefix + "...'");
            this.docState.maxTermPrefix = null;
        }
    }

    @Override
    void finishDocument() throws IOException {
        try {
            this.storedConsumer.finishDocument();
        }
        finally {
            this.consumer.finishDocument();
        }
    }
}


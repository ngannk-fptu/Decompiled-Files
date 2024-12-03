/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import org.apache.lucene.index.DocFieldConsumerPerField;
import org.apache.lucene.index.DocFieldProcessor;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.RamUsageEstimator;

final class DocFieldProcessorPerField {
    final DocFieldConsumerPerField consumer;
    final FieldInfo fieldInfo;
    DocFieldProcessorPerField next;
    int lastGen = -1;
    int fieldCount;
    IndexableField[] fields = new IndexableField[1];

    public DocFieldProcessorPerField(DocFieldProcessor docFieldProcessor, FieldInfo fieldInfo) {
        this.consumer = docFieldProcessor.consumer.addField(fieldInfo);
        this.fieldInfo = fieldInfo;
    }

    public void addField(IndexableField field) {
        if (this.fieldCount == this.fields.length) {
            int newSize = ArrayUtil.oversize(this.fieldCount + 1, RamUsageEstimator.NUM_BYTES_OBJECT_REF);
            IndexableField[] newArray = new IndexableField[newSize];
            System.arraycopy(this.fields, 0, newArray, 0, this.fieldCount);
            this.fields = newArray;
        }
        this.fields[this.fieldCount++] = field;
    }

    public void abort() {
        this.consumer.abort();
    }
}


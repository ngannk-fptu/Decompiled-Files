/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.document.Fieldable;
import com.atlassian.lucene36.index.DocFieldConsumerPerField;
import com.atlassian.lucene36.index.DocFieldProcessorPerThread;
import com.atlassian.lucene36.index.FieldInfo;

final class DocFieldProcessorPerField {
    final DocFieldConsumerPerField consumer;
    final FieldInfo fieldInfo;
    DocFieldProcessorPerField next;
    int lastGen = -1;
    int fieldCount;
    Fieldable[] fields = new Fieldable[1];

    public DocFieldProcessorPerField(DocFieldProcessorPerThread perThread, FieldInfo fieldInfo) {
        this.consumer = perThread.consumer.addField(fieldInfo);
        this.fieldInfo = fieldInfo;
    }

    public void abort() {
        this.consumer.abort();
    }
}


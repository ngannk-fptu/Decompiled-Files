/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.DocFieldConsumerPerField;
import com.atlassian.lucene36.index.DocFieldConsumerPerThread;
import com.atlassian.lucene36.index.DocFieldProcessorPerThread;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.SegmentWriteState;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
abstract class DocFieldConsumer {
    FieldInfos fieldInfos;

    DocFieldConsumer() {
    }

    abstract void flush(Map<DocFieldConsumerPerThread, Collection<DocFieldConsumerPerField>> var1, SegmentWriteState var2) throws IOException;

    abstract void abort();

    abstract DocFieldConsumerPerThread addThread(DocFieldProcessorPerThread var1) throws IOException;

    abstract boolean freeRAM();

    void setFieldInfos(FieldInfos fieldInfos) {
        this.fieldInfos = fieldInfos;
    }
}


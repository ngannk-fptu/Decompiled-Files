/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.DocInverterPerThread;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.InvertedDocEndConsumerPerField;
import com.atlassian.lucene36.index.InvertedDocEndConsumerPerThread;
import com.atlassian.lucene36.index.SegmentWriteState;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
abstract class InvertedDocEndConsumer {
    InvertedDocEndConsumer() {
    }

    abstract InvertedDocEndConsumerPerThread addThread(DocInverterPerThread var1);

    abstract void flush(Map<InvertedDocEndConsumerPerThread, Collection<InvertedDocEndConsumerPerField>> var1, SegmentWriteState var2) throws IOException;

    abstract void abort();

    abstract void setFieldInfos(FieldInfos var1);
}


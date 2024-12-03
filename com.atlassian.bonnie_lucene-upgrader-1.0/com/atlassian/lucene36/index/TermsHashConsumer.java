/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.SegmentWriteState;
import com.atlassian.lucene36.index.TermsHashConsumerPerField;
import com.atlassian.lucene36.index.TermsHashConsumerPerThread;
import com.atlassian.lucene36.index.TermsHashPerThread;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
abstract class TermsHashConsumer {
    FieldInfos fieldInfos;

    TermsHashConsumer() {
    }

    abstract TermsHashConsumerPerThread addThread(TermsHashPerThread var1);

    abstract void flush(Map<TermsHashConsumerPerThread, Collection<TermsHashConsumerPerField>> var1, SegmentWriteState var2) throws IOException;

    abstract void abort();

    void setFieldInfos(FieldInfos fieldInfos) {
        this.fieldInfos = fieldInfos;
    }
}


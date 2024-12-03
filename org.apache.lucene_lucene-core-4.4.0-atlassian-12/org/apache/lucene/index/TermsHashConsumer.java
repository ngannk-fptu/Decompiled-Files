/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.index.TermsHash;
import org.apache.lucene.index.TermsHashConsumerPerField;
import org.apache.lucene.index.TermsHashPerField;

abstract class TermsHashConsumer {
    TermsHashConsumer() {
    }

    abstract void flush(Map<String, TermsHashConsumerPerField> var1, SegmentWriteState var2) throws IOException;

    abstract void abort();

    abstract void startDocument() throws IOException;

    abstract void finishDocument(TermsHash var1) throws IOException;

    public abstract TermsHashConsumerPerField addField(TermsHashPerField var1, FieldInfo var2);
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.ParallelPostingsArray;

abstract class TermsHashConsumerPerField {
    TermsHashConsumerPerField() {
    }

    abstract boolean start(IndexableField[] var1, int var2) throws IOException;

    abstract void finish() throws IOException;

    abstract void skippingLongTerm() throws IOException;

    abstract void start(IndexableField var1);

    abstract void newTerm(int var1) throws IOException;

    abstract void addTerm(int var1) throws IOException;

    abstract int getStreamCount();

    abstract ParallelPostingsArray createPostingsArray(int var1);
}


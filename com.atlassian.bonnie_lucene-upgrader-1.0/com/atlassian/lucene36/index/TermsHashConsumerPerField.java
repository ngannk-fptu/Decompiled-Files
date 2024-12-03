/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.document.Fieldable;
import com.atlassian.lucene36.index.ParallelPostingsArray;
import java.io.IOException;

abstract class TermsHashConsumerPerField {
    TermsHashConsumerPerField() {
    }

    abstract boolean start(Fieldable[] var1, int var2) throws IOException;

    abstract void finish() throws IOException;

    abstract void skippingLongTerm() throws IOException;

    abstract void start(Fieldable var1);

    abstract void newTerm(int var1) throws IOException;

    abstract void addTerm(int var1) throws IOException;

    abstract int getStreamCount();

    abstract ParallelPostingsArray createPostingsArray(int var1);
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.document.Fieldable;
import java.io.IOException;

abstract class InvertedDocConsumerPerField {
    InvertedDocConsumerPerField() {
    }

    abstract boolean start(Fieldable[] var1, int var2) throws IOException;

    abstract void start(Fieldable var1);

    abstract void add() throws IOException;

    abstract void finish() throws IOException;

    abstract void abort();

    abstract void close();
}


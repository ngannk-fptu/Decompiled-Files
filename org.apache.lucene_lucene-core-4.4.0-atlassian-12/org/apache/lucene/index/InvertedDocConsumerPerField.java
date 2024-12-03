/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.index.IndexableField;

abstract class InvertedDocConsumerPerField {
    InvertedDocConsumerPerField() {
    }

    abstract boolean start(IndexableField[] var1, int var2) throws IOException;

    abstract void start(IndexableField var1);

    abstract void add() throws IOException;

    abstract void finish() throws IOException;

    abstract void abort();
}


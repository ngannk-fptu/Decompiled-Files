/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.Closeable;
import java.io.IOException;
import org.apache.lucene.codecs.PostingsConsumer;
import org.apache.lucene.codecs.TermStats;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.store.IndexOutput;

public abstract class PostingsWriterBase
extends PostingsConsumer
implements Closeable {
    protected PostingsWriterBase() {
    }

    public abstract void start(IndexOutput var1) throws IOException;

    public abstract void startTerm() throws IOException;

    public abstract void flushTermsBlock(int var1, int var2) throws IOException;

    public abstract void finishTerm(TermStats var1) throws IOException;

    public abstract void setField(FieldInfo var1);

    @Override
    public abstract void close() throws IOException;
}


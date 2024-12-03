/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.Closeable;
import java.io.IOException;
import org.apache.lucene.index.IndexWriter;

public abstract class MergeScheduler
implements Closeable,
Cloneable {
    protected MergeScheduler() {
    }

    public abstract void merge(IndexWriter var1) throws IOException;

    @Override
    public abstract void close() throws IOException;

    public MergeScheduler clone() {
        try {
            return (MergeScheduler)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.IndexWriter;
import java.io.IOException;

public abstract class MergeScheduler {
    public abstract void merge(IndexWriter var1) throws CorruptIndexException, IOException;

    public abstract void close() throws CorruptIndexException, IOException;
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.IndexWriter;
import com.atlassian.lucene36.index.MergeScheduler;
import java.io.IOException;

public final class NoMergeScheduler
extends MergeScheduler {
    public static final MergeScheduler INSTANCE = new NoMergeScheduler();

    private NoMergeScheduler() {
    }

    public void close() {
    }

    public void merge(IndexWriter writer) throws CorruptIndexException, IOException {
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.IndexWriter;
import com.atlassian.lucene36.index.MergePolicy;
import com.atlassian.lucene36.index.MergeScheduler;
import java.io.IOException;

public class SerialMergeScheduler
extends MergeScheduler {
    public synchronized void merge(IndexWriter writer) throws CorruptIndexException, IOException {
        MergePolicy.OneMerge merge;
        while ((merge = writer.getNextMerge()) != null) {
            writer.merge(merge);
        }
    }

    public void close() {
    }
}


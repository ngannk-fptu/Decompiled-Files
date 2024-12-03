/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.MergeScheduler;

public class SerialMergeScheduler
extends MergeScheduler {
    @Override
    public synchronized void merge(IndexWriter writer) throws IOException {
        MergePolicy.OneMerge merge;
        while ((merge = writer.getNextMerge()) != null) {
            writer.merge(merge);
        }
    }

    @Override
    public void close() {
    }
}


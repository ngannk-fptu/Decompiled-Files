/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.MergeScheduler;

public final class NoMergeScheduler
extends MergeScheduler {
    public static final MergeScheduler INSTANCE = new NoMergeScheduler();

    private NoMergeScheduler() {
    }

    @Override
    public void close() {
    }

    @Override
    public void merge(IndexWriter writer) {
    }

    @Override
    public MergeScheduler clone() {
        return this;
    }
}


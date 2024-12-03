/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.SegmentInfoPerCommit;
import org.apache.lucene.index.SegmentInfos;

public final class NoMergePolicy
extends MergePolicy {
    public static final MergePolicy NO_COMPOUND_FILES = new NoMergePolicy(false);
    public static final MergePolicy COMPOUND_FILES = new NoMergePolicy(true);
    private final boolean useCompoundFile;

    private NoMergePolicy(boolean useCompoundFile) {
        super(useCompoundFile ? 1.0 : 0.0, 0L);
        this.useCompoundFile = useCompoundFile;
    }

    @Override
    public void close() {
    }

    @Override
    public MergePolicy.MergeSpecification findMerges(MergePolicy.MergeTrigger mergeTrigger, SegmentInfos segmentInfos) {
        return null;
    }

    @Override
    public MergePolicy.MergeSpecification findForcedMerges(SegmentInfos segmentInfos, int maxSegmentCount, Map<SegmentInfoPerCommit, Boolean> segmentsToMerge) {
        return null;
    }

    @Override
    public MergePolicy.MergeSpecification findForcedDeletesMerges(SegmentInfos segmentInfos) {
        return null;
    }

    @Override
    public boolean useCompoundFile(SegmentInfos segments, SegmentInfoPerCommit newSegment) {
        return this.useCompoundFile;
    }

    @Override
    public void setIndexWriter(IndexWriter writer) {
    }

    @Override
    protected long size(SegmentInfoPerCommit info) throws IOException {
        return Long.MAX_VALUE;
    }

    public String toString() {
        return "NoMergePolicy";
    }
}


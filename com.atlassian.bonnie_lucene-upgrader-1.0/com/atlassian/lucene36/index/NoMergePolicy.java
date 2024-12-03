/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.IndexWriter;
import com.atlassian.lucene36.index.MergePolicy;
import com.atlassian.lucene36.index.SegmentInfo;
import com.atlassian.lucene36.index.SegmentInfos;
import java.io.IOException;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class NoMergePolicy
extends MergePolicy {
    public static final MergePolicy NO_COMPOUND_FILES = new NoMergePolicy(false);
    public static final MergePolicy COMPOUND_FILES = new NoMergePolicy(true);
    private final boolean useCompoundFile;

    private NoMergePolicy(boolean useCompoundFile) {
        this.useCompoundFile = useCompoundFile;
    }

    @Override
    public void close() {
    }

    @Override
    public MergePolicy.MergeSpecification findMerges(SegmentInfos segmentInfos) throws CorruptIndexException, IOException {
        return null;
    }

    @Override
    public MergePolicy.MergeSpecification findForcedMerges(SegmentInfos segmentInfos, int maxSegmentCount, Map<SegmentInfo, Boolean> segmentsToMerge) throws CorruptIndexException, IOException {
        return null;
    }

    @Override
    public MergePolicy.MergeSpecification findForcedDeletesMerges(SegmentInfos segmentInfos) throws CorruptIndexException, IOException {
        return null;
    }

    @Override
    public boolean useCompoundFile(SegmentInfos segments, SegmentInfo newSegment) {
        return this.useCompoundFile;
    }

    @Override
    public void setIndexWriter(IndexWriter writer) {
    }

    public String toString() {
        return "NoMergePolicy";
    }
}


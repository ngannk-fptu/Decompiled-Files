/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.IndexWriter;
import com.atlassian.lucene36.index.MergePolicy;
import com.atlassian.lucene36.index.SegmentInfo;
import com.atlassian.lucene36.index.SegmentInfos;
import com.atlassian.lucene36.util.Constants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class UpgradeIndexMergePolicy
extends MergePolicy {
    protected final MergePolicy base;

    public UpgradeIndexMergePolicy(MergePolicy base) {
        this.base = base;
    }

    protected boolean shouldUpgradeSegment(SegmentInfo si) {
        return !Constants.LUCENE_MAIN_VERSION.equals(si.getVersion());
    }

    @Override
    public void setIndexWriter(IndexWriter writer) {
        super.setIndexWriter(writer);
        this.base.setIndexWriter(writer);
    }

    @Override
    public MergePolicy.MergeSpecification findMerges(SegmentInfos segmentInfos) throws CorruptIndexException, IOException {
        return this.base.findMerges(segmentInfos);
    }

    @Override
    public MergePolicy.MergeSpecification findForcedMerges(SegmentInfos segmentInfos, int maxSegmentCount, Map<SegmentInfo, Boolean> segmentsToMerge) throws CorruptIndexException, IOException {
        HashMap<SegmentInfo, Boolean> oldSegments = new HashMap<SegmentInfo, Boolean>();
        for (SegmentInfo si : segmentInfos) {
            Boolean v = segmentsToMerge.get(si);
            if (v == null || !this.shouldUpgradeSegment(si)) continue;
            oldSegments.put(si, v);
        }
        if (this.verbose()) {
            this.message("findForcedMerges: segmentsToUpgrade=" + oldSegments);
        }
        if (oldSegments.isEmpty()) {
            return null;
        }
        MergePolicy.MergeSpecification spec = this.base.findForcedMerges(segmentInfos, maxSegmentCount, oldSegments);
        if (spec != null) {
            for (MergePolicy.OneMerge om : spec.merges) {
                oldSegments.keySet().removeAll(om.segments);
            }
        }
        if (!oldSegments.isEmpty()) {
            if (this.verbose()) {
                this.message("findForcedMerges: " + this.base.getClass().getSimpleName() + " does not want to merge all old segments, merge remaining ones into new segment: " + oldSegments);
            }
            ArrayList<SegmentInfo> newInfos = new ArrayList<SegmentInfo>();
            for (SegmentInfo si : segmentInfos) {
                if (!oldSegments.containsKey(si)) continue;
                newInfos.add(si);
            }
            if (spec == null) {
                spec = new MergePolicy.MergeSpecification();
            }
            spec.add(new MergePolicy.OneMerge(newInfos));
        }
        return spec;
    }

    @Override
    public MergePolicy.MergeSpecification findForcedDeletesMerges(SegmentInfos segmentInfos) throws CorruptIndexException, IOException {
        return this.base.findForcedDeletesMerges(segmentInfos);
    }

    @Override
    public boolean useCompoundFile(SegmentInfos segments, SegmentInfo newSegment) throws IOException {
        return this.base.useCompoundFile(segments, newSegment);
    }

    @Override
    public void close() {
        this.base.close();
    }

    public String toString() {
        return "[" + this.getClass().getSimpleName() + "->" + this.base + "]";
    }

    private boolean verbose() {
        IndexWriter w = (IndexWriter)this.writer.get();
        return w != null && w.verbose();
    }

    private void message(String message) {
        if (this.verbose()) {
            ((IndexWriter)this.writer.get()).message("UPGMP: " + message);
        }
    }
}


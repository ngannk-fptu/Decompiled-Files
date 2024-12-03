/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.SegmentInfoPerCommit;
import org.apache.lucene.index.SegmentInfos;
import org.apache.lucene.util.Constants;

public class UpgradeIndexMergePolicy
extends MergePolicy {
    protected final MergePolicy base;

    public UpgradeIndexMergePolicy(MergePolicy base) {
        this.base = base;
    }

    protected boolean shouldUpgradeSegment(SegmentInfoPerCommit si) {
        return !Constants.LUCENE_MAIN_VERSION.equals(si.info.getVersion());
    }

    @Override
    public void setIndexWriter(IndexWriter writer) {
        super.setIndexWriter(writer);
        this.base.setIndexWriter(writer);
    }

    @Override
    public MergePolicy.MergeSpecification findMerges(MergePolicy.MergeTrigger mergeTrigger, SegmentInfos segmentInfos) throws IOException {
        return this.base.findMerges(null, segmentInfos);
    }

    @Override
    public MergePolicy.MergeSpecification findForcedMerges(SegmentInfos segmentInfos, int maxSegmentCount, Map<SegmentInfoPerCommit, Boolean> segmentsToMerge) throws IOException {
        HashMap<SegmentInfoPerCommit, Boolean> oldSegments = new HashMap<SegmentInfoPerCommit, Boolean>();
        for (Object si : segmentInfos) {
            Boolean v = segmentsToMerge.get(si);
            if (v == null || !this.shouldUpgradeSegment((SegmentInfoPerCommit)si)) continue;
            oldSegments.put((SegmentInfoPerCommit)si, v);
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
            ArrayList<SegmentInfoPerCommit> newInfos = new ArrayList<SegmentInfoPerCommit>();
            for (SegmentInfoPerCommit si : segmentInfos) {
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
    public MergePolicy.MergeSpecification findForcedDeletesMerges(SegmentInfos segmentInfos) throws IOException {
        return this.base.findForcedDeletesMerges(segmentInfos);
    }

    @Override
    public boolean useCompoundFile(SegmentInfos segments, SegmentInfoPerCommit newSegment) throws IOException {
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
        return w != null && w.infoStream.isEnabled("UPGMP");
    }

    private void message(String message) {
        ((IndexWriter)this.writer.get()).infoStream.message("UPGMP", message);
    }
}


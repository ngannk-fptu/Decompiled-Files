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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TieredMergePolicy
extends MergePolicy {
    private int maxMergeAtOnce = 10;
    private long maxMergedSegmentBytes = 0x140000000L;
    private int maxMergeAtOnceExplicit = 30;
    private long floorSegmentBytes = 0x200000L;
    private double segsPerTier = 10.0;
    private double forceMergeDeletesPctAllowed = 10.0;
    private boolean useCompoundFile = true;
    private double noCFSRatio = 0.1;
    private double reclaimDeletesWeight = 2.0;
    private final Comparator<SegmentInfo> segmentByteSizeDescending = new SegmentByteSizeDescending();

    public TieredMergePolicy setMaxMergeAtOnce(int v) {
        if (v < 2) {
            throw new IllegalArgumentException("maxMergeAtOnce must be > 1 (got " + v + ")");
        }
        this.maxMergeAtOnce = v;
        return this;
    }

    public int getMaxMergeAtOnce() {
        return this.maxMergeAtOnce;
    }

    public TieredMergePolicy setMaxMergeAtOnceExplicit(int v) {
        if (v < 2) {
            throw new IllegalArgumentException("maxMergeAtOnceExplicit must be > 1 (got " + v + ")");
        }
        this.maxMergeAtOnceExplicit = v;
        return this;
    }

    public int getMaxMergeAtOnceExplicit() {
        return this.maxMergeAtOnceExplicit;
    }

    public TieredMergePolicy setMaxMergedSegmentMB(double v) {
        this.maxMergedSegmentBytes = (long)(v * 1024.0 * 1024.0);
        return this;
    }

    public double getMaxMergedSegmentMB() {
        return (double)(this.maxMergedSegmentBytes / 1024L) / 1024.0;
    }

    public TieredMergePolicy setReclaimDeletesWeight(double v) {
        if (v < 0.0) {
            throw new IllegalArgumentException("reclaimDeletesWeight must be >= 0.0 (got " + v + ")");
        }
        this.reclaimDeletesWeight = v;
        return this;
    }

    public double getReclaimDeletesWeight() {
        return this.reclaimDeletesWeight;
    }

    public TieredMergePolicy setFloorSegmentMB(double v) {
        if (v <= 0.0) {
            throw new IllegalArgumentException("floorSegmentMB must be >= 0.0 (got " + v + ")");
        }
        this.floorSegmentBytes = (long)(v * 1024.0 * 1024.0);
        return this;
    }

    public double getFloorSegmentMB() {
        return (double)this.floorSegmentBytes / 1048576.0;
    }

    public TieredMergePolicy setForceMergeDeletesPctAllowed(double v) {
        if (v < 0.0 || v > 100.0) {
            throw new IllegalArgumentException("forceMergeDeletesPctAllowed must be between 0.0 and 100.0 inclusive (got " + v + ")");
        }
        this.forceMergeDeletesPctAllowed = v;
        return this;
    }

    public double getForceMergeDeletesPctAllowed() {
        return this.forceMergeDeletesPctAllowed;
    }

    public TieredMergePolicy setSegmentsPerTier(double v) {
        if (v < 2.0) {
            throw new IllegalArgumentException("segmentsPerTier must be >= 2.0 (got " + v + ")");
        }
        this.segsPerTier = v;
        return this;
    }

    public double getSegmentsPerTier() {
        return this.segsPerTier;
    }

    public TieredMergePolicy setUseCompoundFile(boolean useCompoundFile) {
        this.useCompoundFile = useCompoundFile;
        return this;
    }

    public boolean getUseCompoundFile() {
        return this.useCompoundFile;
    }

    public TieredMergePolicy setNoCFSRatio(double noCFSRatio) {
        if (noCFSRatio < 0.0 || noCFSRatio > 1.0) {
            throw new IllegalArgumentException("noCFSRatio must be 0.0 to 1.0 inclusive; got " + noCFSRatio);
        }
        this.noCFSRatio = noCFSRatio;
        return this;
    }

    public double getNoCFSRatio() {
        return this.noCFSRatio;
    }

    @Override
    public MergePolicy.MergeSpecification findMerges(SegmentInfos infos) throws IOException {
        MergePolicy.MergeSpecification spec;
        block18: {
            double segCountLevel;
            int tooBigCount;
            if (this.verbose()) {
                this.message("findMerges: " + infos.size() + " segments");
            }
            if (infos.size() == 0) {
                return null;
            }
            Collection<SegmentInfo> merging = ((IndexWriter)this.writer.get()).getMergingSegments();
            HashSet<SegmentInfo> toBeMerged = new HashSet<SegmentInfo>();
            ArrayList<SegmentInfo> infosSorted = new ArrayList<SegmentInfo>(infos.asList());
            Collections.sort(infosSorted, this.segmentByteSizeDescending);
            long totIndexBytes = 0L;
            long minSegmentBytes = Long.MAX_VALUE;
            for (SegmentInfo info : infosSorted) {
                long segBytes = this.size(info);
                if (this.verbose()) {
                    String extra;
                    String string = extra = merging.contains(info) ? " [merging]" : "";
                    if ((double)segBytes >= (double)this.maxMergedSegmentBytes / 2.0) {
                        extra = extra + " [skip: too large]";
                    } else if (segBytes < this.floorSegmentBytes) {
                        extra = extra + " [floored]";
                    }
                    this.message("  seg=" + ((IndexWriter)this.writer.get()).segString(info) + " size=" + String.format("%.3f", (double)(segBytes / 1024L) / 1024.0) + " MB" + extra);
                }
                minSegmentBytes = Math.min(segBytes, minSegmentBytes);
                totIndexBytes += segBytes;
            }
            for (tooBigCount = 0; tooBigCount < infosSorted.size() && (double)this.size((SegmentInfo)infosSorted.get(tooBigCount)) >= (double)this.maxMergedSegmentBytes / 2.0; ++tooBigCount) {
                totIndexBytes -= this.size((SegmentInfo)infosSorted.get(tooBigCount));
            }
            long levelSize = minSegmentBytes = this.floorSize(minSegmentBytes);
            long bytesLeft = totIndexBytes;
            double allowedSegCount = 0.0;
            while (!((segCountLevel = (double)bytesLeft / (double)levelSize) < this.segsPerTier)) {
                allowedSegCount += this.segsPerTier;
                bytesLeft = (long)((double)bytesLeft - this.segsPerTier * (double)levelSize);
                levelSize *= (long)this.maxMergeAtOnce;
            }
            int allowedSegCountInt = (int)(allowedSegCount += Math.ceil(segCountLevel));
            spec = null;
            while (true) {
                long mergingBytes = 0L;
                ArrayList<SegmentInfo> eligible = new ArrayList<SegmentInfo>();
                for (int idx = tooBigCount; idx < infosSorted.size(); ++idx) {
                    SegmentInfo info = (SegmentInfo)infosSorted.get(idx);
                    if (merging.contains(info)) {
                        mergingBytes += info.sizeInBytes(true);
                        continue;
                    }
                    if (toBeMerged.contains(info)) continue;
                    eligible.add(info);
                }
                boolean maxMergeIsRunning = mergingBytes >= this.maxMergedSegmentBytes;
                this.message("  allowedSegmentCount=" + allowedSegCountInt + " vs count=" + infosSorted.size() + " (eligible count=" + eligible.size() + ") tooBigCount=" + tooBigCount);
                if (eligible.size() == 0) {
                    return spec;
                }
                if (eligible.size() < allowedSegCountInt) break block18;
                MergeScore bestScore = null;
                ArrayList<SegmentInfo> best = null;
                boolean bestTooLarge = false;
                long bestMergeBytes = 0L;
                for (int startIdx = 0; startIdx <= eligible.size() - this.maxMergeAtOnce; ++startIdx) {
                    long totAfterMergeBytes = 0L;
                    ArrayList<SegmentInfo> candidate = new ArrayList<SegmentInfo>();
                    boolean hitTooLarge = false;
                    for (int idx = startIdx; idx < eligible.size() && candidate.size() < this.maxMergeAtOnce; ++idx) {
                        SegmentInfo info = (SegmentInfo)eligible.get(idx);
                        long segBytes = this.size(info);
                        if (totAfterMergeBytes + segBytes > this.maxMergedSegmentBytes) {
                            hitTooLarge = true;
                            continue;
                        }
                        candidate.add(info);
                        totAfterMergeBytes += segBytes;
                    }
                    MergeScore score = this.score(candidate, hitTooLarge, mergingBytes);
                    this.message("  maybe=" + ((IndexWriter)this.writer.get()).segString(candidate) + " score=" + score.getScore() + " " + score.getExplanation() + " tooLarge=" + hitTooLarge + " size=" + String.format("%.3f MB", (double)totAfterMergeBytes / 1024.0 / 1024.0));
                    if (bestScore != null && !(score.getScore() < bestScore.getScore()) || hitTooLarge && maxMergeIsRunning) continue;
                    best = candidate;
                    bestScore = score;
                    bestTooLarge = hitTooLarge;
                    bestMergeBytes = totAfterMergeBytes;
                }
                if (best == null) break;
                if (spec == null) {
                    spec = new MergePolicy.MergeSpecification();
                }
                MergePolicy.OneMerge merge = new MergePolicy.OneMerge(best);
                spec.add(merge);
                for (SegmentInfo info : merge.segments) {
                    toBeMerged.add(info);
                }
                if (!this.verbose()) continue;
                this.message("  add merge=" + ((IndexWriter)this.writer.get()).segString(merge.segments) + " size=" + String.format("%.3f MB", (double)bestMergeBytes / 1024.0 / 1024.0) + " score=" + String.format("%.3f", bestScore.getScore()) + " " + bestScore.getExplanation() + (bestTooLarge ? " [max merge]" : ""));
            }
            return spec;
        }
        return spec;
    }

    protected MergeScore score(List<SegmentInfo> candidate, boolean hitTooLarge, long mergingBytes) throws IOException {
        long totBeforeMergeBytes = 0L;
        long totAfterMergeBytes = 0L;
        long totAfterMergeBytesFloored = 0L;
        for (SegmentInfo info : candidate) {
            long segBytes = this.size(info);
            totAfterMergeBytes += segBytes;
            totAfterMergeBytesFloored += this.floorSize(segBytes);
            totBeforeMergeBytes += info.sizeInBytes(true);
        }
        final double skew = hitTooLarge ? 1.0 / (double)this.maxMergeAtOnce : (double)this.floorSize(this.size(candidate.get(0))) / (double)totAfterMergeBytesFloored;
        double mergeScore = skew;
        mergeScore *= Math.pow(totAfterMergeBytes, 0.05);
        final double nonDelRatio = (double)totAfterMergeBytes / (double)totBeforeMergeBytes;
        final double finalMergeScore = mergeScore *= Math.pow(nonDelRatio, this.reclaimDeletesWeight);
        return new MergeScore(){

            public double getScore() {
                return finalMergeScore;
            }

            public String getExplanation() {
                return "skew=" + String.format("%.3f", skew) + " nonDelRatio=" + String.format("%.3f", nonDelRatio);
            }
        };
    }

    @Override
    public MergePolicy.MergeSpecification findForcedMerges(SegmentInfos infos, int maxSegmentCount, Map<SegmentInfo, Boolean> segmentsToMerge) throws IOException {
        int end;
        if (this.verbose()) {
            this.message("findForcedMerges maxSegmentCount=" + maxSegmentCount + " infos=" + ((IndexWriter)this.writer.get()).segString(infos) + " segmentsToMerge=" + segmentsToMerge);
        }
        ArrayList<SegmentInfo> eligible = new ArrayList<SegmentInfo>();
        boolean forceMergeRunning = false;
        Collection<SegmentInfo> merging = ((IndexWriter)this.writer.get()).getMergingSegments();
        boolean segmentIsOriginal = false;
        for (SegmentInfo info : infos) {
            Boolean isOriginal = segmentsToMerge.get(info);
            if (isOriginal == null) continue;
            segmentIsOriginal = isOriginal;
            if (!merging.contains(info)) {
                eligible.add(info);
                continue;
            }
            forceMergeRunning = true;
        }
        if (eligible.size() == 0) {
            return null;
        }
        if (maxSegmentCount > 1 && eligible.size() <= maxSegmentCount || maxSegmentCount == 1 && eligible.size() == 1 && (!segmentIsOriginal || this.isMerged((SegmentInfo)eligible.get(0)))) {
            if (this.verbose()) {
                this.message("already merged");
            }
            return null;
        }
        Collections.sort(eligible, this.segmentByteSizeDescending);
        if (this.verbose()) {
            this.message("eligible=" + eligible);
            this.message("forceMergeRunning=" + forceMergeRunning);
        }
        MergePolicy.MergeSpecification spec = null;
        for (end = eligible.size(); end >= this.maxMergeAtOnceExplicit + maxSegmentCount - 1; end -= this.maxMergeAtOnceExplicit) {
            if (spec == null) {
                spec = new MergePolicy.MergeSpecification();
            }
            MergePolicy.OneMerge merge = new MergePolicy.OneMerge(eligible.subList(end - this.maxMergeAtOnceExplicit, end));
            if (this.verbose()) {
                this.message("add merge=" + ((IndexWriter)this.writer.get()).segString(merge.segments));
            }
            spec.add(merge);
        }
        if (spec == null && !forceMergeRunning) {
            int numToMerge = end - maxSegmentCount + 1;
            MergePolicy.OneMerge merge = new MergePolicy.OneMerge(eligible.subList(end - numToMerge, end));
            if (this.verbose()) {
                this.message("add final merge=" + merge.segString(((IndexWriter)this.writer.get()).getDirectory()));
            }
            spec = new MergePolicy.MergeSpecification();
            spec.add(merge);
        }
        return spec;
    }

    @Override
    public MergePolicy.MergeSpecification findForcedDeletesMerges(SegmentInfos infos) throws CorruptIndexException, IOException {
        if (this.verbose()) {
            this.message("findForcedDeletesMerges infos=" + ((IndexWriter)this.writer.get()).segString(infos) + " forceMergeDeletesPctAllowed=" + this.forceMergeDeletesPctAllowed);
        }
        ArrayList<SegmentInfo> eligible = new ArrayList<SegmentInfo>();
        Collection<SegmentInfo> merging = ((IndexWriter)this.writer.get()).getMergingSegments();
        for (SegmentInfo info : infos) {
            double pctDeletes = 100.0 * (double)((IndexWriter)this.writer.get()).numDeletedDocs(info) / (double)info.docCount;
            if (!(pctDeletes > this.forceMergeDeletesPctAllowed) || merging.contains(info)) continue;
            eligible.add(info);
        }
        if (eligible.size() == 0) {
            return null;
        }
        Collections.sort(eligible, this.segmentByteSizeDescending);
        if (this.verbose()) {
            this.message("eligible=" + eligible);
        }
        int start = 0;
        MergePolicy.MergeSpecification spec = null;
        while (start < eligible.size()) {
            int end = Math.min(start + this.maxMergeAtOnceExplicit, eligible.size());
            if (spec == null) {
                spec = new MergePolicy.MergeSpecification();
            }
            MergePolicy.OneMerge merge = new MergePolicy.OneMerge(eligible.subList(start, end));
            if (this.verbose()) {
                this.message("add merge=" + ((IndexWriter)this.writer.get()).segString(merge.segments));
            }
            spec.add(merge);
            start = end;
        }
        return spec;
    }

    @Override
    public boolean useCompoundFile(SegmentInfos infos, SegmentInfo mergedInfo) throws IOException {
        boolean doCFS;
        if (!this.useCompoundFile) {
            doCFS = false;
        } else if (this.noCFSRatio == 1.0) {
            doCFS = true;
        } else {
            long totalSize = 0L;
            for (SegmentInfo info : infos) {
                totalSize += this.size(info);
            }
            doCFS = (double)this.size(mergedInfo) <= this.noCFSRatio * (double)totalSize;
        }
        return doCFS;
    }

    @Override
    public void close() {
    }

    private boolean isMerged(SegmentInfo info) throws IOException {
        IndexWriter w = (IndexWriter)this.writer.get();
        assert (w != null);
        boolean hasDeletions = w.numDeletedDocs(info) > 0;
        return !hasDeletions && !info.hasSeparateNorms() && info.dir == w.getDirectory() && (info.getUseCompoundFile() == this.useCompoundFile || this.noCFSRatio < 1.0);
    }

    private long size(SegmentInfo info) throws IOException {
        double delRatio;
        long byteSize = info.sizeInBytes(true);
        int delCount = ((IndexWriter)this.writer.get()).numDeletedDocs(info);
        double d = delRatio = info.docCount <= 0 ? 0.0 : (double)delCount / (double)info.docCount;
        assert (delRatio <= 1.0);
        return (long)((double)byteSize * (1.0 - delRatio));
    }

    private long floorSize(long bytes) {
        return Math.max(this.floorSegmentBytes, bytes);
    }

    private boolean verbose() {
        IndexWriter w = (IndexWriter)this.writer.get();
        return w != null && w.verbose();
    }

    private void message(String message) {
        if (this.verbose()) {
            ((IndexWriter)this.writer.get()).message("TMP: " + message);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("[" + this.getClass().getSimpleName() + ": ");
        sb.append("maxMergeAtOnce=").append(this.maxMergeAtOnce).append(", ");
        sb.append("maxMergeAtOnceExplicit=").append(this.maxMergeAtOnceExplicit).append(", ");
        sb.append("maxMergedSegmentMB=").append((double)(this.maxMergedSegmentBytes / 1024L) / 1024.0).append(", ");
        sb.append("floorSegmentMB=").append((double)(this.floorSegmentBytes / 1024L) / 1024.0).append(", ");
        sb.append("forceMergeDeletesPctAllowed=").append(this.forceMergeDeletesPctAllowed).append(", ");
        sb.append("segmentsPerTier=").append(this.segsPerTier).append(", ");
        sb.append("useCompoundFile=").append(this.useCompoundFile).append(", ");
        sb.append("noCFSRatio=").append(this.noCFSRatio);
        return sb.toString();
    }

    protected static abstract class MergeScore {
        protected MergeScore() {
        }

        abstract double getScore();

        abstract String getExplanation();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class SegmentByteSizeDescending
    implements Comparator<SegmentInfo> {
        private SegmentByteSizeDescending() {
        }

        @Override
        public int compare(SegmentInfo o1, SegmentInfo o2) {
            try {
                long sz1 = TieredMergePolicy.this.size(o1);
                long sz2 = TieredMergePolicy.this.size(o2);
                if (sz1 > sz2) {
                    return -1;
                }
                if (sz2 > sz1) {
                    return 1;
                }
                return o1.name.compareTo(o2.name);
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
    }
}


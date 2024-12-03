/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.SegmentInfoPerCommit;
import org.apache.lucene.index.SegmentInfos;

public class TieredMergePolicy
extends MergePolicy {
    public static final double DEFAULT_NO_CFS_RATIO = 0.1;
    private int maxMergeAtOnce = 10;
    private long maxMergedSegmentBytes = 0x140000000L;
    private int maxMergeAtOnceExplicit = 30;
    private long floorSegmentBytes = 0x200000L;
    private double segsPerTier = 10.0;
    private double forceMergeDeletesPctAllowed = 10.0;
    private double reclaimDeletesWeight = 2.0;

    public TieredMergePolicy() {
        super(0.1, Long.MAX_VALUE);
    }

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
        if (v < 0.0) {
            throw new IllegalArgumentException("maxMergedSegmentMB must be >=0 (got " + v + ")");
        }
        this.maxMergedSegmentBytes = (v *= 1048576.0) > 9.223372036854776E18 ? Long.MAX_VALUE : (long)v;
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
        this.floorSegmentBytes = (v *= 1048576.0) > 9.223372036854776E18 ? Long.MAX_VALUE : (long)v;
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

    @Override
    public MergePolicy.MergeSpecification findMerges(MergePolicy.MergeTrigger mergeTrigger, SegmentInfos infos) throws IOException {
        MergePolicy.MergeSpecification spec;
        block20: {
            double segCountLevel;
            int tooBigCount;
            if (this.verbose()) {
                this.message("findMerges: " + infos.size() + " segments");
            }
            if (infos.size() == 0) {
                return null;
            }
            Collection<SegmentInfoPerCommit> merging = ((IndexWriter)this.writer.get()).getMergingSegments();
            HashSet<SegmentInfoPerCommit> toBeMerged = new HashSet<SegmentInfoPerCommit>();
            ArrayList<SegmentInfoPerCommit> infosSorted = new ArrayList<SegmentInfoPerCommit>(infos.asList());
            Collections.sort(infosSorted, new SegmentByteSizeDescending());
            long totIndexBytes = 0L;
            long minSegmentBytes = Long.MAX_VALUE;
            for (SegmentInfoPerCommit info : infosSorted) {
                long segBytes = this.size(info);
                if (this.verbose()) {
                    String extra;
                    String string = extra = merging.contains(info) ? " [merging]" : "";
                    if ((double)segBytes >= (double)this.maxMergedSegmentBytes / 2.0) {
                        extra = extra + " [skip: too large]";
                    } else if (segBytes < this.floorSegmentBytes) {
                        extra = extra + " [floored]";
                    }
                    this.message("  seg=" + ((IndexWriter)this.writer.get()).segString(info) + " size=" + String.format(Locale.ROOT, "%.3f", (double)(segBytes / 1024L) / 1024.0) + " MB" + extra);
                }
                minSegmentBytes = Math.min(segBytes, minSegmentBytes);
                totIndexBytes += segBytes;
            }
            for (tooBigCount = 0; tooBigCount < infosSorted.size() && (double)this.size((SegmentInfoPerCommit)infosSorted.get(tooBigCount)) >= (double)this.maxMergedSegmentBytes / 2.0; ++tooBigCount) {
                totIndexBytes -= this.size((SegmentInfoPerCommit)infosSorted.get(tooBigCount));
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
                boolean maxMergeIsRunning;
                long mergingBytes = 0L;
                ArrayList<SegmentInfoPerCommit> eligible = new ArrayList<SegmentInfoPerCommit>();
                for (int idx = tooBigCount; idx < infosSorted.size(); ++idx) {
                    SegmentInfoPerCommit info = (SegmentInfoPerCommit)infosSorted.get(idx);
                    if (merging.contains(info)) {
                        mergingBytes += info.sizeInBytes();
                        continue;
                    }
                    if (toBeMerged.contains(info)) continue;
                    eligible.add(info);
                }
                boolean bl = maxMergeIsRunning = mergingBytes >= this.maxMergedSegmentBytes;
                if (this.verbose()) {
                    this.message("  allowedSegmentCount=" + allowedSegCountInt + " vs count=" + infosSorted.size() + " (eligible count=" + eligible.size() + ") tooBigCount=" + tooBigCount);
                }
                if (eligible.size() == 0) {
                    return spec;
                }
                if (eligible.size() < allowedSegCountInt) break block20;
                MergeScore bestScore = null;
                ArrayList<SegmentInfoPerCommit> best = null;
                boolean bestTooLarge = false;
                long bestMergeBytes = 0L;
                for (int startIdx = 0; startIdx <= eligible.size() - this.maxMergeAtOnce; ++startIdx) {
                    long totAfterMergeBytes = 0L;
                    ArrayList<SegmentInfoPerCommit> candidate = new ArrayList<SegmentInfoPerCommit>();
                    boolean hitTooLarge = false;
                    for (int idx = startIdx; idx < eligible.size() && candidate.size() < this.maxMergeAtOnce; ++idx) {
                        SegmentInfoPerCommit info = (SegmentInfoPerCommit)eligible.get(idx);
                        long segBytes = this.size(info);
                        if (totAfterMergeBytes + segBytes > this.maxMergedSegmentBytes) {
                            hitTooLarge = true;
                            continue;
                        }
                        candidate.add(info);
                        totAfterMergeBytes += segBytes;
                    }
                    MergeScore score = this.score(candidate, hitTooLarge, mergingBytes);
                    if (this.verbose()) {
                        this.message("  maybe=" + ((IndexWriter)this.writer.get()).segString(candidate) + " score=" + score.getScore() + " " + score.getExplanation() + " tooLarge=" + hitTooLarge + " size=" + String.format(Locale.ROOT, "%.3f MB", (double)totAfterMergeBytes / 1024.0 / 1024.0));
                    }
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
                for (SegmentInfoPerCommit info : merge.segments) {
                    toBeMerged.add(info);
                }
                if (!this.verbose()) continue;
                this.message("  add merge=" + ((IndexWriter)this.writer.get()).segString(merge.segments) + " size=" + String.format(Locale.ROOT, "%.3f MB", (double)bestMergeBytes / 1024.0 / 1024.0) + " score=" + String.format(Locale.ROOT, "%.3f", bestScore.getScore()) + " " + bestScore.getExplanation() + (bestTooLarge ? " [max merge]" : ""));
            }
            return spec;
        }
        return spec;
    }

    protected MergeScore score(List<SegmentInfoPerCommit> candidate, boolean hitTooLarge, long mergingBytes) throws IOException {
        long totBeforeMergeBytes = 0L;
        long totAfterMergeBytes = 0L;
        long totAfterMergeBytesFloored = 0L;
        for (SegmentInfoPerCommit info : candidate) {
            long segBytes = this.size(info);
            totAfterMergeBytes += segBytes;
            totAfterMergeBytesFloored += this.floorSize(segBytes);
            totBeforeMergeBytes += info.sizeInBytes();
        }
        final double skew = hitTooLarge ? 1.0 / (double)this.maxMergeAtOnce : (double)this.floorSize(this.size(candidate.get(0))) / (double)totAfterMergeBytesFloored;
        double mergeScore = skew;
        mergeScore *= Math.pow(totAfterMergeBytes, 0.05);
        final double nonDelRatio = (double)totAfterMergeBytes / (double)totBeforeMergeBytes;
        final double finalMergeScore = mergeScore *= Math.pow(nonDelRatio, this.reclaimDeletesWeight);
        return new MergeScore(){

            @Override
            public double getScore() {
                return finalMergeScore;
            }

            @Override
            public String getExplanation() {
                return "skew=" + String.format(Locale.ROOT, "%.3f", skew) + " nonDelRatio=" + String.format(Locale.ROOT, "%.3f", nonDelRatio);
            }
        };
    }

    @Override
    public MergePolicy.MergeSpecification findForcedMerges(SegmentInfos infos, int maxSegmentCount, Map<SegmentInfoPerCommit, Boolean> segmentsToMerge) throws IOException {
        int end;
        if (this.verbose()) {
            this.message("findForcedMerges maxSegmentCount=" + maxSegmentCount + " infos=" + ((IndexWriter)this.writer.get()).segString(infos) + " segmentsToMerge=" + segmentsToMerge);
        }
        ArrayList<SegmentInfoPerCommit> eligible = new ArrayList<SegmentInfoPerCommit>();
        boolean forceMergeRunning = false;
        Collection<SegmentInfoPerCommit> merging = ((IndexWriter)this.writer.get()).getMergingSegments();
        boolean segmentIsOriginal = false;
        for (SegmentInfoPerCommit info : infos) {
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
        if (maxSegmentCount > 1 && eligible.size() <= maxSegmentCount || maxSegmentCount == 1 && eligible.size() == 1 && (!segmentIsOriginal || this.isMerged((SegmentInfoPerCommit)eligible.get(0)))) {
            if (this.verbose()) {
                this.message("already merged");
            }
            return null;
        }
        Collections.sort(eligible, new SegmentByteSizeDescending());
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
    public MergePolicy.MergeSpecification findForcedDeletesMerges(SegmentInfos infos) throws IOException {
        if (this.verbose()) {
            this.message("findForcedDeletesMerges infos=" + ((IndexWriter)this.writer.get()).segString(infos) + " forceMergeDeletesPctAllowed=" + this.forceMergeDeletesPctAllowed);
        }
        ArrayList<SegmentInfoPerCommit> eligible = new ArrayList<SegmentInfoPerCommit>();
        Collection<SegmentInfoPerCommit> merging = ((IndexWriter)this.writer.get()).getMergingSegments();
        for (SegmentInfoPerCommit info : infos) {
            double pctDeletes = 100.0 * (double)((IndexWriter)this.writer.get()).numDeletedDocs(info) / (double)info.info.getDocCount();
            if (!(pctDeletes > this.forceMergeDeletesPctAllowed) || merging.contains(info)) continue;
            eligible.add(info);
        }
        if (eligible.size() == 0) {
            return null;
        }
        Collections.sort(eligible, new SegmentByteSizeDescending());
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
    public void close() {
    }

    private long floorSize(long bytes) {
        return Math.max(this.floorSegmentBytes, bytes);
    }

    private boolean verbose() {
        IndexWriter w = (IndexWriter)this.writer.get();
        return w != null && w.infoStream.isEnabled("TMP");
    }

    private void message(String message) {
        ((IndexWriter)this.writer.get()).infoStream.message("TMP", message);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("[" + this.getClass().getSimpleName() + ": ");
        sb.append("maxMergeAtOnce=").append(this.maxMergeAtOnce).append(", ");
        sb.append("maxMergeAtOnceExplicit=").append(this.maxMergeAtOnceExplicit).append(", ");
        sb.append("maxMergedSegmentMB=").append((double)(this.maxMergedSegmentBytes / 1024L) / 1024.0).append(", ");
        sb.append("floorSegmentMB=").append((double)(this.floorSegmentBytes / 1024L) / 1024.0).append(", ");
        sb.append("forceMergeDeletesPctAllowed=").append(this.forceMergeDeletesPctAllowed).append(", ");
        sb.append("segmentsPerTier=").append(this.segsPerTier).append(", ");
        sb.append("maxCFSSegmentSizeMB=").append(this.getMaxCFSSegmentSizeMB()).append(", ");
        sb.append("noCFSRatio=").append(this.noCFSRatio);
        return sb.toString();
    }

    protected static abstract class MergeScore {
        protected MergeScore() {
        }

        abstract double getScore();

        abstract String getExplanation();
    }

    private class SegmentByteSizeDescending
    implements Comparator<SegmentInfoPerCommit> {
        private SegmentByteSizeDescending() {
        }

        @Override
        public int compare(SegmentInfoPerCommit o1, SegmentInfoPerCommit o2) {
            try {
                long sz1 = TieredMergePolicy.this.size(o1);
                long sz2 = TieredMergePolicy.this.size(o2);
                if (sz1 > sz2) {
                    return -1;
                }
                if (sz2 > sz1) {
                    return 1;
                }
                return o1.info.name.compareTo(o2.info.name);
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
    }
}


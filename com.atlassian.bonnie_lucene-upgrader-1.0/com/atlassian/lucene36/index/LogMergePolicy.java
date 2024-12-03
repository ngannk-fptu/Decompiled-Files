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
import java.util.List;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class LogMergePolicy
extends MergePolicy {
    public static final double LEVEL_LOG_SPAN = 0.75;
    public static final int DEFAULT_MERGE_FACTOR = 10;
    public static final int DEFAULT_MAX_MERGE_DOCS = Integer.MAX_VALUE;
    public static final double DEFAULT_NO_CFS_RATIO = 0.1;
    protected int mergeFactor = 10;
    protected long minMergeSize;
    protected long maxMergeSize;
    protected long maxMergeSizeForForcedMerge = Long.MAX_VALUE;
    protected int maxMergeDocs = Integer.MAX_VALUE;
    protected double noCFSRatio = 0.1;
    protected boolean calibrateSizeByDeletes = true;
    protected boolean useCompoundFile = true;

    protected boolean verbose() {
        IndexWriter w = (IndexWriter)this.writer.get();
        return w != null && w.verbose();
    }

    public double getNoCFSRatio() {
        return this.noCFSRatio;
    }

    public void setNoCFSRatio(double noCFSRatio) {
        if (noCFSRatio < 0.0 || noCFSRatio > 1.0) {
            throw new IllegalArgumentException("noCFSRatio must be 0.0 to 1.0 inclusive; got " + noCFSRatio);
        }
        this.noCFSRatio = noCFSRatio;
    }

    protected void message(String message) {
        if (this.verbose()) {
            ((IndexWriter)this.writer.get()).message("LMP: " + message);
        }
    }

    public int getMergeFactor() {
        return this.mergeFactor;
    }

    public void setMergeFactor(int mergeFactor) {
        if (mergeFactor < 2) {
            throw new IllegalArgumentException("mergeFactor cannot be less than 2");
        }
        this.mergeFactor = mergeFactor;
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

    public void setUseCompoundFile(boolean useCompoundFile) {
        this.useCompoundFile = useCompoundFile;
    }

    public boolean getUseCompoundFile() {
        return this.useCompoundFile;
    }

    public void setCalibrateSizeByDeletes(boolean calibrateSizeByDeletes) {
        this.calibrateSizeByDeletes = calibrateSizeByDeletes;
    }

    public boolean getCalibrateSizeByDeletes() {
        return this.calibrateSizeByDeletes;
    }

    @Override
    public void close() {
    }

    protected abstract long size(SegmentInfo var1) throws IOException;

    protected long sizeDocs(SegmentInfo info) throws IOException {
        if (this.calibrateSizeByDeletes) {
            int delCount = ((IndexWriter)this.writer.get()).numDeletedDocs(info);
            assert (delCount <= info.docCount);
            return (long)info.docCount - (long)delCount;
        }
        return info.docCount;
    }

    protected long sizeBytes(SegmentInfo info) throws IOException {
        long byteSize = info.sizeInBytes(true);
        if (this.calibrateSizeByDeletes) {
            int delCount = ((IndexWriter)this.writer.get()).numDeletedDocs(info);
            double delRatio = info.docCount <= 0 ? 0.0f : (float)delCount / (float)info.docCount;
            assert (delRatio <= 1.0);
            return info.docCount <= 0 ? byteSize : (long)((double)byteSize * (1.0 - delRatio));
        }
        return byteSize;
    }

    protected boolean isMerged(SegmentInfos infos, int maxNumSegments, Map<SegmentInfo, Boolean> segmentsToMerge) throws IOException {
        int numSegments = infos.size();
        int numToMerge = 0;
        SegmentInfo mergeInfo = null;
        boolean segmentIsOriginal = false;
        for (int i = 0; i < numSegments && numToMerge <= maxNumSegments; ++i) {
            SegmentInfo info = infos.info(i);
            Boolean isOriginal = segmentsToMerge.get(info);
            if (isOriginal == null) continue;
            segmentIsOriginal = isOriginal;
            ++numToMerge;
            mergeInfo = info;
        }
        return numToMerge <= maxNumSegments && (numToMerge != 1 || !segmentIsOriginal || this.isMerged(mergeInfo));
    }

    protected boolean isMerged(SegmentInfo info) throws IOException {
        IndexWriter w = (IndexWriter)this.writer.get();
        assert (w != null);
        boolean hasDeletions = w.numDeletedDocs(info) > 0;
        return !hasDeletions && !info.hasSeparateNorms() && info.dir == w.getDirectory() && (info.getUseCompoundFile() == this.useCompoundFile || this.noCFSRatio < 1.0);
    }

    private MergePolicy.MergeSpecification findForcedMergesSizeLimit(SegmentInfos infos, int maxNumSegments, int last) throws IOException {
        int start;
        MergePolicy.MergeSpecification spec = new MergePolicy.MergeSpecification();
        List<SegmentInfo> segments = infos.asList();
        for (start = last - 1; start >= 0; --start) {
            SegmentInfo info = infos.info(start);
            if (this.size(info) > this.maxMergeSizeForForcedMerge || this.sizeDocs(info) > (long)this.maxMergeDocs) {
                if (this.verbose()) {
                    this.message("findForcedMergesSizeLimit: skip segment=" + info + ": size is > maxMergeSize (" + this.maxMergeSizeForForcedMerge + ") or sizeDocs is > maxMergeDocs (" + this.maxMergeDocs + ")");
                }
                if (last - start - 1 > 1 || start != last - 1 && !this.isMerged(infos.info(start + 1))) {
                    spec.add(new MergePolicy.OneMerge(segments.subList(start + 1, last)));
                }
                last = start;
                continue;
            }
            if (last - start != this.mergeFactor) continue;
            spec.add(new MergePolicy.OneMerge(segments.subList(start, last)));
            last = start;
        }
        if (!(last <= 0 || ++start + 1 >= last && this.isMerged(infos.info(start)))) {
            spec.add(new MergePolicy.OneMerge(segments.subList(start, last)));
        }
        return spec.merges.size() == 0 ? null : spec;
    }

    private MergePolicy.MergeSpecification findForcedMergesMaxNumSegments(SegmentInfos infos, int maxNumSegments, int last) throws IOException {
        MergePolicy.MergeSpecification spec = new MergePolicy.MergeSpecification();
        List<SegmentInfo> segments = infos.asList();
        while (last - maxNumSegments + 1 >= this.mergeFactor) {
            spec.add(new MergePolicy.OneMerge(segments.subList(last - this.mergeFactor, last)));
            last -= this.mergeFactor;
        }
        if (0 == spec.merges.size()) {
            if (maxNumSegments == 1) {
                if (last > 1 || !this.isMerged(infos.info(0))) {
                    spec.add(new MergePolicy.OneMerge(segments.subList(0, last)));
                }
            } else if (last > maxNumSegments) {
                int finalMergeSize = last - maxNumSegments + 1;
                long bestSize = 0L;
                int bestStart = 0;
                for (int i = 0; i < last - finalMergeSize + 1; ++i) {
                    long sumSize = 0L;
                    for (int j = 0; j < finalMergeSize; ++j) {
                        sumSize += this.size(infos.info(j + i));
                    }
                    if (i != 0 && (sumSize >= 2L * this.size(infos.info(i - 1)) || sumSize >= bestSize)) continue;
                    bestStart = i;
                    bestSize = sumSize;
                }
                spec.add(new MergePolicy.OneMerge(segments.subList(bestStart, bestStart + finalMergeSize)));
            }
        }
        return spec.merges.size() == 0 ? null : spec;
    }

    @Override
    public MergePolicy.MergeSpecification findForcedMerges(SegmentInfos infos, int maxNumSegments, Map<SegmentInfo, Boolean> segmentsToMerge) throws IOException {
        assert (maxNumSegments > 0);
        if (this.verbose()) {
            this.message("findForcedMerges: maxNumSegs=" + maxNumSegments + " segsToMerge=" + segmentsToMerge);
        }
        if (this.isMerged(infos, maxNumSegments, segmentsToMerge)) {
            if (this.verbose()) {
                this.message("already merged; skip");
            }
            return null;
        }
        int last = infos.size();
        while (last > 0) {
            SegmentInfo info;
            if (segmentsToMerge.get(info = infos.info(--last)) == null) continue;
            ++last;
            break;
        }
        if (last == 0) {
            return null;
        }
        if (maxNumSegments == 1 && last == 1 && this.isMerged(infos.info(0))) {
            if (this.verbose()) {
                this.message("already 1 seg; skip");
            }
            return null;
        }
        boolean anyTooLarge = false;
        for (int i = 0; i < last; ++i) {
            SegmentInfo info = infos.info(i);
            if (this.size(info) <= this.maxMergeSizeForForcedMerge && this.sizeDocs(info) <= (long)this.maxMergeDocs) continue;
            anyTooLarge = true;
            break;
        }
        if (anyTooLarge) {
            return this.findForcedMergesSizeLimit(infos, maxNumSegments, last);
        }
        return this.findForcedMergesMaxNumSegments(infos, maxNumSegments, last);
    }

    @Override
    public MergePolicy.MergeSpecification findForcedDeletesMerges(SegmentInfos segmentInfos) throws CorruptIndexException, IOException {
        List<SegmentInfo> segments = segmentInfos.asList();
        int numSegments = segments.size();
        if (this.verbose()) {
            this.message("findForcedDeleteMerges: " + numSegments + " segments");
        }
        MergePolicy.MergeSpecification spec = new MergePolicy.MergeSpecification();
        int firstSegmentWithDeletions = -1;
        IndexWriter w = (IndexWriter)this.writer.get();
        assert (w != null);
        for (int i = 0; i < numSegments; ++i) {
            SegmentInfo info = segmentInfos.info(i);
            int delCount = w.numDeletedDocs(info);
            if (delCount > 0) {
                if (this.verbose()) {
                    this.message("  segment " + info.name + " has deletions");
                }
                if (firstSegmentWithDeletions == -1) {
                    firstSegmentWithDeletions = i;
                    continue;
                }
                if (i - firstSegmentWithDeletions != this.mergeFactor) continue;
                if (this.verbose()) {
                    this.message("  add merge " + firstSegmentWithDeletions + " to " + (i - 1) + " inclusive");
                }
                spec.add(new MergePolicy.OneMerge(segments.subList(firstSegmentWithDeletions, i)));
                firstSegmentWithDeletions = i;
                continue;
            }
            if (firstSegmentWithDeletions == -1) continue;
            if (this.verbose()) {
                this.message("  add merge " + firstSegmentWithDeletions + " to " + (i - 1) + " inclusive");
            }
            spec.add(new MergePolicy.OneMerge(segments.subList(firstSegmentWithDeletions, i)));
            firstSegmentWithDeletions = -1;
        }
        if (firstSegmentWithDeletions != -1) {
            if (this.verbose()) {
                this.message("  add merge " + firstSegmentWithDeletions + " to " + (numSegments - 1) + " inclusive");
            }
            spec.add(new MergePolicy.OneMerge(segments.subList(firstSegmentWithDeletions, numSegments)));
        }
        return spec;
    }

    @Override
    public MergePolicy.MergeSpecification findMerges(SegmentInfos infos) throws IOException {
        int numSegments = infos.size();
        if (this.verbose()) {
            this.message("findMerges: " + numSegments + " segments");
        }
        ArrayList<SegmentInfoAndLevel> levels = new ArrayList<SegmentInfoAndLevel>();
        float norm = (float)Math.log(this.mergeFactor);
        Collection<SegmentInfo> mergingSegments = ((IndexWriter)this.writer.get()).getMergingSegments();
        for (int i = 0; i < numSegments; ++i) {
            String extra;
            SegmentInfo info = infos.info(i);
            long size = this.size(info);
            if (size < 1L) {
                size = 1L;
            }
            SegmentInfoAndLevel infoLevel = new SegmentInfoAndLevel(info, (float)Math.log(size) / norm, i);
            levels.add(infoLevel);
            if (!this.verbose()) continue;
            long segBytes = this.sizeBytes(info);
            String string = extra = mergingSegments.contains(info) ? " [merging]" : "";
            if (size >= this.maxMergeSize) {
                extra = extra + " [skip: too large]";
            }
            this.message("seg=" + ((IndexWriter)this.writer.get()).segString(info) + " level=" + infoLevel.level + " size=" + String.format("%.3f MB", (double)(segBytes / 1024L) / 1024.0) + extra);
        }
        float levelFloor = this.minMergeSize <= 0L ? 0.0f : (float)(Math.log(this.minMergeSize) / (double)norm);
        MergePolicy.MergeSpecification spec = null;
        int numMergeableSegments = levels.size();
        int start = 0;
        while (start < numMergeableSegments) {
            int upto;
            float levelBottom;
            float maxLevel = ((SegmentInfoAndLevel)levels.get((int)start)).level;
            for (int i = 1 + start; i < numMergeableSegments; ++i) {
                float level = ((SegmentInfoAndLevel)levels.get((int)i)).level;
                if (!(level > maxLevel)) continue;
                maxLevel = level;
            }
            if (maxLevel <= levelFloor) {
                levelBottom = -1.0f;
            } else {
                levelBottom = (float)((double)maxLevel - 0.75);
                if (levelBottom < levelFloor && maxLevel >= levelFloor) {
                    levelBottom = levelFloor;
                }
            }
            for (upto = numMergeableSegments - 1; upto >= start && !(((SegmentInfoAndLevel)levels.get((int)upto)).level >= levelBottom); --upto) {
            }
            if (this.verbose()) {
                this.message("  level " + levelBottom + " to " + maxLevel + ": " + (1 + upto - start) + " segments");
            }
            int end = start + this.mergeFactor;
            while (end <= 1 + upto) {
                boolean anyTooLarge = false;
                boolean anyMerging = false;
                for (int i = start; i < end; ++i) {
                    SegmentInfo info = ((SegmentInfoAndLevel)levels.get((int)i)).info;
                    anyTooLarge |= this.size(info) >= this.maxMergeSize || this.sizeDocs(info) >= (long)this.maxMergeDocs;
                    if (!mergingSegments.contains(info)) continue;
                    anyMerging = true;
                    break;
                }
                if (!anyMerging) {
                    if (!anyTooLarge) {
                        if (spec == null) {
                            spec = new MergePolicy.MergeSpecification();
                        }
                        ArrayList<SegmentInfo> mergeInfos = new ArrayList<SegmentInfo>();
                        for (int i = start; i < end; ++i) {
                            mergeInfos.add(((SegmentInfoAndLevel)levels.get((int)i)).info);
                            assert (infos.contains(((SegmentInfoAndLevel)levels.get((int)i)).info));
                        }
                        if (this.verbose()) {
                            this.message("  add merge=" + ((IndexWriter)this.writer.get()).segString(mergeInfos) + " start=" + start + " end=" + end);
                        }
                        spec.add(new MergePolicy.OneMerge(mergeInfos));
                    } else if (this.verbose()) {
                        this.message("    " + start + " to " + end + ": contains segment over maxMergeSize or maxMergeDocs; skipping");
                    }
                }
                start = end;
                end = start + this.mergeFactor;
            }
            start = 1 + upto;
        }
        return spec;
    }

    public void setMaxMergeDocs(int maxMergeDocs) {
        this.maxMergeDocs = maxMergeDocs;
    }

    public int getMaxMergeDocs() {
        return this.maxMergeDocs;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("[" + this.getClass().getSimpleName() + ": ");
        sb.append("minMergeSize=").append(this.minMergeSize).append(", ");
        sb.append("mergeFactor=").append(this.mergeFactor).append(", ");
        sb.append("maxMergeSize=").append(this.maxMergeSize).append(", ");
        sb.append("maxMergeSizeForForcedMerge=").append(this.maxMergeSizeForForcedMerge).append(", ");
        sb.append("calibrateSizeByDeletes=").append(this.calibrateSizeByDeletes).append(", ");
        sb.append("maxMergeDocs=").append(this.maxMergeDocs).append(", ");
        sb.append("useCompoundFile=").append(this.useCompoundFile).append(", ");
        sb.append("noCFSRatio=").append(this.noCFSRatio);
        sb.append("]");
        return sb.toString();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class SegmentInfoAndLevel
    implements Comparable<SegmentInfoAndLevel> {
        SegmentInfo info;
        float level;
        int index;

        public SegmentInfoAndLevel(SegmentInfo info, float level, int index) {
            this.info = info;
            this.level = level;
            this.index = index;
        }

        @Override
        public int compareTo(SegmentInfoAndLevel other) {
            if (this.level < other.level) {
                return 1;
            }
            if (this.level > other.level) {
                return -1;
            }
            return 0;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.MergeState;
import org.apache.lucene.index.SegmentInfoPerCommit;
import org.apache.lucene.index.SegmentInfos;
import org.apache.lucene.index.SegmentReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MergeInfo;
import org.apache.lucene.util.FixedBitSet;
import org.apache.lucene.util.SetOnce;

public abstract class MergePolicy
implements Closeable,
Cloneable {
    protected static final double DEFAULT_NO_CFS_RATIO = 1.0;
    protected static final long DEFAULT_MAX_CFS_SEGMENT_SIZE = Long.MAX_VALUE;
    protected SetOnce<IndexWriter> writer;
    protected double noCFSRatio = 1.0;
    protected long maxCFSSegmentSize = Long.MAX_VALUE;

    public MergePolicy clone() {
        MergePolicy clone;
        try {
            clone = (MergePolicy)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        clone.writer = new SetOnce();
        return clone;
    }

    public MergePolicy() {
        this(1.0, Long.MAX_VALUE);
    }

    protected MergePolicy(double defaultNoCFSRatio, long defaultMaxCFSSegmentSize) {
        this.writer = new SetOnce();
        this.noCFSRatio = defaultNoCFSRatio;
        this.maxCFSSegmentSize = defaultMaxCFSSegmentSize;
    }

    public void setIndexWriter(IndexWriter writer) {
        this.writer.set(writer);
    }

    public abstract MergeSpecification findMerges(MergeTrigger var1, SegmentInfos var2) throws IOException;

    public abstract MergeSpecification findForcedMerges(SegmentInfos var1, int var2, Map<SegmentInfoPerCommit, Boolean> var3) throws IOException;

    public abstract MergeSpecification findForcedDeletesMerges(SegmentInfos var1) throws IOException;

    @Override
    public abstract void close();

    public boolean useCompoundFile(SegmentInfos infos, SegmentInfoPerCommit mergedInfo) throws IOException {
        if (this.getNoCFSRatio() == 0.0) {
            return false;
        }
        long mergedInfoSize = this.size(mergedInfo);
        if (mergedInfoSize > this.maxCFSSegmentSize) {
            return false;
        }
        if (this.getNoCFSRatio() >= 1.0) {
            return true;
        }
        long totalSize = 0L;
        for (SegmentInfoPerCommit info : infos) {
            totalSize += this.size(info);
        }
        return (double)mergedInfoSize <= this.getNoCFSRatio() * (double)totalSize;
    }

    protected long size(SegmentInfoPerCommit info) throws IOException {
        long byteSize = info.sizeInBytes();
        int delCount = this.writer.get().numDeletedDocs(info);
        double delRatio = info.info.getDocCount() <= 0 ? 0.0f : (float)delCount / (float)info.info.getDocCount();
        assert (delRatio <= 1.0);
        return info.info.getDocCount() <= 0 ? byteSize : (long)((double)byteSize * (1.0 - delRatio));
    }

    protected final boolean isMerged(SegmentInfoPerCommit info) {
        IndexWriter w = this.writer.get();
        assert (w != null);
        boolean hasDeletions = w.numDeletedDocs(info) > 0;
        return !hasDeletions && !info.info.hasSeparateNorms() && info.info.dir == w.getDirectory() && (this.noCFSRatio > 0.0 && this.noCFSRatio < 1.0 || this.maxCFSSegmentSize < Long.MAX_VALUE);
    }

    public final double getNoCFSRatio() {
        return this.noCFSRatio;
    }

    public final void setNoCFSRatio(double noCFSRatio) {
        if (noCFSRatio < 0.0 || noCFSRatio > 1.0) {
            throw new IllegalArgumentException("noCFSRatio must be 0.0 to 1.0 inclusive; got " + noCFSRatio);
        }
        this.noCFSRatio = noCFSRatio;
    }

    public final double getMaxCFSSegmentSizeMB() {
        return (double)(this.maxCFSSegmentSize / 1024L) / 1024.0;
    }

    public final void setMaxCFSSegmentSizeMB(double v) {
        if (v < 0.0) {
            throw new IllegalArgumentException("maxCFSSegmentSizeMB must be >=0 (got " + v + ")");
        }
        this.maxCFSSegmentSize = (v *= 1048576.0) > 9.223372036854776E18 ? Long.MAX_VALUE : (long)v;
    }

    public static enum MergeTrigger {
        SEGMENT_FLUSH,
        FULL_FLUSH,
        EXPLICIT,
        MERGE_FINISHED;

    }

    public static class MergeAbortedException
    extends IOException {
        public MergeAbortedException() {
            super("merge is aborted");
        }

        public MergeAbortedException(String message) {
            super(message);
        }
    }

    public static class MergeException
    extends RuntimeException {
        private Directory dir;

        public MergeException(String message, Directory dir) {
            super(message);
            this.dir = dir;
        }

        public MergeException(Throwable exc, Directory dir) {
            super(exc);
            this.dir = dir;
        }

        public Directory getDirectory() {
            return this.dir;
        }
    }

    public static class MergeSpecification {
        public final List<OneMerge> merges = new ArrayList<OneMerge>();

        public void add(OneMerge merge) {
            this.merges.add(merge);
        }

        public String segString(Directory dir) {
            StringBuilder b = new StringBuilder();
            b.append("MergeSpec:\n");
            int count = this.merges.size();
            for (int i = 0; i < count; ++i) {
                b.append("  ").append(1 + i).append(": ").append(this.merges.get(i).segString(dir));
            }
            return b.toString();
        }
    }

    public static class OneMerge {
        SegmentInfoPerCommit info;
        boolean registerDone;
        long mergeGen;
        boolean isExternal;
        int maxNumSegments = -1;
        public volatile long estimatedMergeBytes;
        volatile long totalMergeBytes;
        List<SegmentReader> readers;
        public final List<SegmentInfoPerCommit> segments;
        public final int totalDocCount;
        boolean aborted;
        Throwable error;
        boolean paused;

        public OneMerge(List<SegmentInfoPerCommit> segments) {
            if (0 == segments.size()) {
                throw new RuntimeException("segments must include at least one segment");
            }
            this.segments = new ArrayList<SegmentInfoPerCommit>(segments);
            int count = 0;
            for (SegmentInfoPerCommit info : segments) {
                count += info.info.getDocCount();
            }
            this.totalDocCount = count;
        }

        public List<AtomicReader> getMergeReaders() throws IOException {
            if (this.readers == null) {
                throw new IllegalStateException("IndexWriter has not initialized readers from the segment infos yet");
            }
            ArrayList<AtomicReader> readers = new ArrayList<AtomicReader>(this.readers.size());
            for (AtomicReader atomicReader : this.readers) {
                if (atomicReader.numDocs() <= 0) continue;
                readers.add(atomicReader);
            }
            return Collections.unmodifiableList(readers);
        }

        public void setInfo(SegmentInfoPerCommit info) {
            this.info = info;
        }

        public DocMap getDocMap(MergeState mergeState) {
            return new DocMap(){

                @Override
                public int map(int docID) {
                    return docID;
                }
            };
        }

        synchronized void setException(Throwable error) {
            this.error = error;
        }

        synchronized Throwable getException() {
            return this.error;
        }

        synchronized void abort() {
            this.aborted = true;
            this.notifyAll();
        }

        synchronized boolean isAborted() {
            return this.aborted;
        }

        public synchronized void checkAborted(Directory dir) throws MergeAbortedException {
            if (this.aborted) {
                throw new MergeAbortedException("merge is aborted: " + this.segString(dir));
            }
            while (this.paused) {
                try {
                    this.wait(1000L);
                }
                catch (InterruptedException ie) {
                    throw new RuntimeException(ie);
                }
                if (!this.aborted) continue;
                throw new MergeAbortedException("merge is aborted: " + this.segString(dir));
            }
        }

        public synchronized void setPause(boolean paused) {
            this.paused = paused;
            if (!paused) {
                this.notifyAll();
            }
        }

        public synchronized boolean getPause() {
            return this.paused;
        }

        public String segString(Directory dir) {
            StringBuilder b = new StringBuilder();
            int numSegments = this.segments.size();
            for (int i = 0; i < numSegments; ++i) {
                if (i > 0) {
                    b.append(' ');
                }
                b.append(this.segments.get(i).toString(dir, 0));
            }
            if (this.info != null) {
                b.append(" into ").append(this.info.info.name);
            }
            if (this.maxNumSegments != -1) {
                b.append(" [maxNumSegments=" + this.maxNumSegments + "]");
            }
            if (this.aborted) {
                b.append(" [ABORTED]");
            }
            return b.toString();
        }

        public long totalBytesSize() throws IOException {
            return this.totalMergeBytes;
        }

        public int totalNumDocs() throws IOException {
            int total = 0;
            for (SegmentInfoPerCommit info : this.segments) {
                total += info.info.getDocCount();
            }
            return total;
        }

        public MergeInfo getMergeInfo() {
            return new MergeInfo(this.totalDocCount, this.estimatedMergeBytes, this.isExternal, this.maxNumSegments);
        }
    }

    public static abstract class DocMap {
        protected DocMap() {
        }

        public abstract int map(int var1);

        boolean isConsistent(int maxDoc) {
            FixedBitSet targets = new FixedBitSet(maxDoc);
            for (int i = 0; i < maxDoc; ++i) {
                int target = this.map(i);
                if (target < 0 || target >= maxDoc) {
                    assert (false) : "out of range: " + target + " not in [0-" + maxDoc + "[";
                    return false;
                }
                if (!targets.get(target)) continue;
                assert (false) : target + " is already taken (" + i + ")";
                return false;
            }
            return true;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.IndexWriter;
import com.atlassian.lucene36.index.SegmentInfo;
import com.atlassian.lucene36.index.SegmentInfos;
import com.atlassian.lucene36.index.SegmentReader;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.util.SetOnce;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class MergePolicy
implements Closeable {
    protected final SetOnce<IndexWriter> writer = new SetOnce();

    public void setIndexWriter(IndexWriter writer) {
        this.writer.set(writer);
    }

    public abstract MergeSpecification findMerges(SegmentInfos var1) throws CorruptIndexException, IOException;

    public abstract MergeSpecification findForcedMerges(SegmentInfos var1, int var2, Map<SegmentInfo, Boolean> var3) throws CorruptIndexException, IOException;

    public abstract MergeSpecification findForcedDeletesMerges(SegmentInfos var1) throws CorruptIndexException, IOException;

    @Override
    public abstract void close();

    public abstract boolean useCompoundFile(SegmentInfos var1, SegmentInfo var2) throws IOException;

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

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class OneMerge {
        SegmentInfo info;
        boolean registerDone;
        long mergeGen;
        boolean isExternal;
        int maxNumSegments = -1;
        public long estimatedMergeBytes;
        List<SegmentReader> readers;
        List<SegmentReader> readerClones;
        public final List<SegmentInfo> segments;
        public final int totalDocCount;
        boolean aborted;
        Throwable error;
        boolean paused;

        public OneMerge(List<SegmentInfo> segments) {
            if (0 == segments.size()) {
                throw new RuntimeException("segments must include at least one segment");
            }
            this.segments = new ArrayList<SegmentInfo>(segments);
            int count = 0;
            for (SegmentInfo info : segments) {
                count += info.docCount;
            }
            this.totalDocCount = count;
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

        synchronized void checkAborted(Directory dir) throws MergeAbortedException {
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
                b.append(" into ").append(this.info.name);
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
            long total = 0L;
            for (SegmentInfo info : this.segments) {
                total += info.sizeInBytes(true);
            }
            return total;
        }

        public int totalNumDocs() throws IOException {
            int total = 0;
            for (SegmentInfo info : this.segments) {
                total += info.docCount;
            }
            return total;
        }
    }
}


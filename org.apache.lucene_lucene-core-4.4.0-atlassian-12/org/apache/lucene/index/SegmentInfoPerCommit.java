/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.Directory;

public class SegmentInfoPerCommit {
    public final SegmentInfo info;
    private int delCount;
    private long delGen;
    private long nextWriteDelGen;
    private volatile long sizeInBytes = -1L;
    private long bufferedDeletesGen;

    public SegmentInfoPerCommit(SegmentInfo info, int delCount, long delGen) {
        this.info = info;
        this.delCount = delCount;
        this.delGen = delGen;
        this.nextWriteDelGen = delGen == -1L ? 1L : delGen + 1L;
    }

    void advanceDelGen() {
        this.delGen = this.nextWriteDelGen;
        this.nextWriteDelGen = this.delGen + 1L;
        this.sizeInBytes = -1L;
    }

    void advanceNextWriteDelGen() {
        ++this.nextWriteDelGen;
    }

    public long sizeInBytes() throws IOException {
        if (this.sizeInBytes == -1L) {
            long sum = 0L;
            for (String fileName : this.files()) {
                sum += this.info.dir.fileLength(fileName);
            }
            this.sizeInBytes = sum;
        }
        return this.sizeInBytes;
    }

    public Collection<String> files() throws IOException {
        HashSet<String> files = new HashSet<String>(this.info.files());
        this.info.getCodec().liveDocsFormat().files(this, files);
        return files;
    }

    long getBufferedDeletesGen() {
        return this.bufferedDeletesGen;
    }

    void setBufferedDeletesGen(long v) {
        this.bufferedDeletesGen = v;
        this.sizeInBytes = -1L;
    }

    void clearDelGen() {
        this.delGen = -1L;
        this.sizeInBytes = -1L;
    }

    public void setDelGen(long delGen) {
        this.delGen = delGen;
        this.sizeInBytes = -1L;
    }

    public boolean hasDeletions() {
        return this.delGen != -1L;
    }

    public long getNextDelGen() {
        return this.nextWriteDelGen;
    }

    public long getDelGen() {
        return this.delGen;
    }

    public int getDelCount() {
        return this.delCount;
    }

    void setDelCount(int delCount) {
        this.delCount = delCount;
        assert (delCount <= this.info.getDocCount());
    }

    public String toString(Directory dir, int pendingDelCount) {
        return this.info.toString(dir, this.delCount + pendingDelCount);
    }

    public String toString() {
        String s = this.info.toString(this.info.dir, this.delCount);
        if (this.delGen != -1L) {
            s = s + ":delGen=" + this.delGen;
        }
        return s;
    }

    public SegmentInfoPerCommit clone() {
        SegmentInfoPerCommit other = new SegmentInfoPerCommit(this.info, this.delCount, this.delGen);
        other.nextWriteDelGen = this.nextWriteDelGen;
        return other;
    }
}


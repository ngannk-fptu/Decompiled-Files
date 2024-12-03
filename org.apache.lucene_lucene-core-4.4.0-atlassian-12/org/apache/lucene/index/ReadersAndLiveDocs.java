/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.lucene.codecs.LiveDocsFormat;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.SegmentInfoPerCommit;
import org.apache.lucene.index.SegmentReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.TrackingDirectoryWrapper;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.MutableBits;

class ReadersAndLiveDocs {
    public final SegmentInfoPerCommit info;
    private final AtomicInteger refCount = new AtomicInteger(1);
    private final IndexWriter writer;
    private SegmentReader reader;
    private SegmentReader mergeReader;
    private Bits liveDocs;
    private int pendingDeleteCount;
    private boolean shared;

    public ReadersAndLiveDocs(IndexWriter writer, SegmentInfoPerCommit info) {
        this.info = info;
        this.writer = writer;
        this.shared = true;
    }

    public void incRef() {
        int rc = this.refCount.incrementAndGet();
        assert (rc > 1);
    }

    public void decRef() {
        int rc = this.refCount.decrementAndGet();
        assert (rc >= 0);
    }

    public int refCount() {
        int rc = this.refCount.get();
        assert (rc >= 0);
        return rc;
    }

    public synchronized int getPendingDeleteCount() {
        return this.pendingDeleteCount;
    }

    public synchronized boolean verifyDocCounts() {
        int count;
        if (this.liveDocs != null) {
            count = 0;
            for (int docID = 0; docID < this.info.info.getDocCount(); ++docID) {
                if (!this.liveDocs.get(docID)) continue;
                ++count;
            }
        } else {
            count = this.info.info.getDocCount();
        }
        assert (this.info.info.getDocCount() - this.info.getDelCount() - this.pendingDeleteCount == count) : "info.docCount=" + this.info.info.getDocCount() + " info.getDelCount()=" + this.info.getDelCount() + " pendingDeleteCount=" + this.pendingDeleteCount + " count=" + count;
        return true;
    }

    public synchronized SegmentReader getReader(IOContext context) throws IOException {
        if (this.reader == null) {
            this.reader = new SegmentReader(this.info, this.writer.getConfig().getReaderTermsIndexDivisor(), context);
            if (this.liveDocs == null) {
                this.liveDocs = this.reader.getLiveDocs();
            }
        }
        this.reader.incRef();
        return this.reader;
    }

    public synchronized SegmentReader getMergeReader(IOContext context) throws IOException {
        if (this.mergeReader == null) {
            if (this.reader != null) {
                this.reader.incRef();
                this.mergeReader = this.reader;
            } else {
                this.mergeReader = new SegmentReader(this.info, -1, context);
                if (this.liveDocs == null) {
                    this.liveDocs = this.mergeReader.getLiveDocs();
                }
            }
        }
        this.mergeReader.incRef();
        return this.mergeReader;
    }

    public synchronized void release(SegmentReader sr) throws IOException {
        assert (this.info == sr.getSegmentInfo());
        sr.decRef();
    }

    public synchronized boolean delete(int docID) {
        assert (this.liveDocs != null);
        assert (Thread.holdsLock(this.writer));
        assert (docID >= 0 && docID < this.liveDocs.length()) : "out of bounds: docid=" + docID + " liveDocsLength=" + this.liveDocs.length() + " seg=" + this.info.info.name + " docCount=" + this.info.info.getDocCount();
        assert (!this.shared);
        boolean didDelete = this.liveDocs.get(docID);
        if (didDelete) {
            ((MutableBits)this.liveDocs).clear(docID);
            ++this.pendingDeleteCount;
        }
        return didDelete;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void dropReaders() throws IOException {
        block12: {
            try {
                if (this.reader == null) break block12;
                try {
                    this.reader.decRef();
                }
                finally {
                    this.reader = null;
                }
            }
            finally {
                if (this.mergeReader != null) {
                    try {
                        this.mergeReader.decRef();
                    }
                    finally {
                        this.mergeReader = null;
                    }
                }
            }
        }
        this.decRef();
    }

    public synchronized SegmentReader getReadOnlyClone(IOContext context) throws IOException {
        if (this.reader == null) {
            this.getReader(context).decRef();
            assert (this.reader != null);
        }
        this.shared = true;
        if (this.liveDocs != null) {
            return new SegmentReader(this.reader.getSegmentInfo(), this.reader.core, this.liveDocs, this.info.info.getDocCount() - this.info.getDelCount() - this.pendingDeleteCount);
        }
        assert (this.reader.getLiveDocs() == this.liveDocs);
        this.reader.incRef();
        return this.reader;
    }

    public synchronized void initWritableLiveDocs() throws IOException {
        assert (Thread.holdsLock(this.writer));
        assert (this.info.info.getDocCount() > 0);
        if (this.shared) {
            LiveDocsFormat liveDocsFormat = this.info.info.getCodec().liveDocsFormat();
            this.liveDocs = this.liveDocs == null ? liveDocsFormat.newLiveDocs(this.info.info.getDocCount()) : liveDocsFormat.newLiveDocs(this.liveDocs);
            this.shared = false;
        } else assert (this.liveDocs != null);
    }

    public synchronized Bits getLiveDocs() {
        assert (Thread.holdsLock(this.writer));
        return this.liveDocs;
    }

    public synchronized Bits getReadOnlyLiveDocs() {
        assert (Thread.holdsLock(this.writer));
        this.shared = true;
        return this.liveDocs;
    }

    public synchronized void dropChanges() {
        this.pendingDeleteCount = 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized boolean writeLiveDocs(Directory dir) throws IOException {
        if (this.pendingDeleteCount != 0) {
            assert (this.liveDocs.length() == this.info.info.getDocCount());
            TrackingDirectoryWrapper trackingDir = new TrackingDirectoryWrapper(dir);
            boolean success = false;
            try {
                this.info.info.getCodec().liveDocsFormat().writeLiveDocs((MutableBits)this.liveDocs, trackingDir, this.info, this.pendingDeleteCount, IOContext.DEFAULT);
                success = true;
            }
            finally {
                if (!success) {
                    this.info.advanceNextWriteDelGen();
                    for (String fileName : trackingDir.getCreatedFiles()) {
                        try {
                            dir.deleteFile(fileName);
                        }
                        catch (Throwable throwable) {}
                    }
                }
            }
            this.info.advanceDelGen();
            this.info.setDelCount(this.info.getDelCount() + this.pendingDeleteCount);
            this.pendingDeleteCount = 0;
            return true;
        }
        return false;
    }

    public String toString() {
        return "ReadersAndLiveDocs(seg=" + this.info + " pendingDeleteCount=" + this.pendingDeleteCount + " shared=" + this.shared + ")";
    }
}


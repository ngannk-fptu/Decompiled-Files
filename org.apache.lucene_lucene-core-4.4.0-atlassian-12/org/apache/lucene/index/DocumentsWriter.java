/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.index.BufferedDeletesStream;
import org.apache.lucene.index.DocumentsWriterDeleteQueue;
import org.apache.lucene.index.DocumentsWriterFlushControl;
import org.apache.lucene.index.DocumentsWriterFlushQueue;
import org.apache.lucene.index.DocumentsWriterPerThread;
import org.apache.lucene.index.DocumentsWriterPerThreadPool;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.FlushPolicy;
import org.apache.lucene.index.FrozenBufferedDeletes;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.LiveIndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.InfoStream;

final class DocumentsWriter {
    Directory directory;
    private volatile boolean closed;
    final InfoStream infoStream;
    Similarity similarity;
    List<String> newFiles;
    final IndexWriter indexWriter;
    final LiveIndexWriterConfig indexWriterConfig;
    private AtomicInteger numDocsInRAM = new AtomicInteger(0);
    volatile DocumentsWriterDeleteQueue deleteQueue = new DocumentsWriterDeleteQueue();
    private final DocumentsWriterFlushQueue ticketQueue = new DocumentsWriterFlushQueue();
    private volatile boolean pendingChangesInCurrentFullFlush;
    private Collection<String> abortedFiles;
    final DocumentsWriterPerThread.IndexingChain chain;
    final DocumentsWriterPerThreadPool perThreadPool;
    final FlushPolicy flushPolicy;
    final DocumentsWriterFlushControl flushControl;
    final Codec codec;
    private volatile DocumentsWriterDeleteQueue currentFullFlushDelQueue = null;

    DocumentsWriter(Codec codec, LiveIndexWriterConfig config, Directory directory, IndexWriter writer, FieldInfos.FieldNumbers globalFieldNumbers, BufferedDeletesStream bufferedDeletesStream) {
        this.codec = codec;
        this.directory = directory;
        this.indexWriter = writer;
        this.infoStream = config.getInfoStream();
        this.similarity = config.getSimilarity();
        this.indexWriterConfig = writer.getConfig();
        this.perThreadPool = config.getIndexerThreadPool();
        this.chain = config.getIndexingChain();
        this.perThreadPool.initialize(this, globalFieldNumbers, config);
        this.flushPolicy = config.getFlushPolicy();
        assert (this.flushPolicy != null);
        this.flushPolicy.init(this);
        this.flushControl = new DocumentsWriterFlushControl(this, config);
    }

    synchronized void deleteQueries(Query ... queries) throws IOException {
        this.deleteQueue.addDelete(queries);
        this.flushControl.doOnDelete();
        if (this.flushControl.doApplyAllDeletes()) {
            this.applyAllDeletes(this.deleteQueue);
        }
    }

    synchronized void deleteTerms(Term ... terms) throws IOException {
        DocumentsWriterDeleteQueue deleteQueue = this.deleteQueue;
        deleteQueue.addDelete(terms);
        this.flushControl.doOnDelete();
        if (this.flushControl.doApplyAllDeletes()) {
            this.applyAllDeletes(deleteQueue);
        }
    }

    DocumentsWriterDeleteQueue currentDeleteSession() {
        return this.deleteQueue;
    }

    private void applyAllDeletes(DocumentsWriterDeleteQueue deleteQueue) throws IOException {
        if (deleteQueue != null && !this.flushControl.isFullFlush()) {
            this.ticketQueue.addDeletesAndPurge(this, deleteQueue);
        }
        this.indexWriter.applyAllDeletes();
        this.indexWriter.flushCount.incrementAndGet();
    }

    int getNumDocs() {
        return this.numDocsInRAM.get();
    }

    Collection<String> abortedFiles() {
        return this.abortedFiles;
    }

    private void ensureOpen() throws AlreadyClosedException {
        if (this.closed) {
            throw new AlreadyClosedException("this IndexWriter is closed");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    synchronized void abort() {
        boolean success = false;
        try {
            this.deleteQueue.clear();
            if (this.infoStream.isEnabled("DW")) {
                this.infoStream.message("DW", "abort");
            }
            int limit = this.perThreadPool.getActiveThreadState();
            for (int i = 0; i < limit; ++i) {
                DocumentsWriterPerThreadPool.ThreadState perThread = this.perThreadPool.getThreadState(i);
                perThread.lock();
                try {
                    if (perThread.isActive()) {
                        try {
                            perThread.dwpt.abort();
                            continue;
                        }
                        finally {
                            perThread.dwpt.checkAndResetHasAborted();
                            this.flushControl.doOnAbort(perThread);
                        }
                    }
                    if ($assertionsDisabled || this.closed) continue;
                    throw new AssertionError();
                }
                finally {
                    perThread.unlock();
                }
            }
            this.flushControl.abortPendingFlushes();
            this.flushControl.waitForFlush();
            success = true;
        }
        finally {
            if (this.infoStream.isEnabled("DW")) {
                this.infoStream.message("DW", "done abort; abortedFiles=" + this.abortedFiles + " success=" + success);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    synchronized void lockAndAbortAll() {
        assert (this.indexWriter.holdsFullFlushLock());
        if (this.infoStream.isEnabled("DW")) {
            this.infoStream.message("DW", "lockAndAbortAll");
        }
        boolean success = false;
        try {
            this.deleteQueue.clear();
            int limit = this.perThreadPool.getMaxThreadStates();
            for (int i = 0; i < limit; ++i) {
                DocumentsWriterPerThreadPool.ThreadState perThread = this.perThreadPool.getThreadState(i);
                perThread.lock();
                if (!perThread.isActive()) continue;
                try {
                    perThread.dwpt.abort();
                    continue;
                }
                finally {
                    perThread.dwpt.checkAndResetHasAborted();
                    this.flushControl.doOnAbort(perThread);
                }
            }
            this.deleteQueue.clear();
            this.flushControl.abortPendingFlushes();
            this.flushControl.waitForFlush();
            success = true;
        }
        finally {
            if (this.infoStream.isEnabled("DW")) {
                this.infoStream.message("DW", "finished lockAndAbortAll success=" + success);
            }
            if (!success) {
                this.unlockAllAfterAbortAll();
            }
        }
    }

    final synchronized void unlockAllAfterAbortAll() {
        assert (this.indexWriter.holdsFullFlushLock());
        if (this.infoStream.isEnabled("DW")) {
            this.infoStream.message("DW", "unlockAll");
        }
        int limit = this.perThreadPool.getMaxThreadStates();
        for (int i = 0; i < limit; ++i) {
            try {
                DocumentsWriterPerThreadPool.ThreadState perThread = this.perThreadPool.getThreadState(i);
                if (!perThread.isHeldByCurrentThread()) continue;
                perThread.unlock();
                continue;
            }
            catch (Throwable e) {
                if (!this.infoStream.isEnabled("DW")) continue;
                this.infoStream.message("DW", "unlockAll: could not unlock state: " + i + " msg:" + e.getMessage());
            }
        }
    }

    boolean anyChanges() {
        if (this.infoStream.isEnabled("DW")) {
            this.infoStream.message("DW", "anyChanges? numDocsInRam=" + this.numDocsInRAM.get() + " deletes=" + this.anyDeletions() + " hasTickets:" + this.ticketQueue.hasTickets() + " pendingChangesInFullFlush: " + this.pendingChangesInCurrentFullFlush);
        }
        return this.numDocsInRAM.get() != 0 || this.anyDeletions() || this.ticketQueue.hasTickets() || this.pendingChangesInCurrentFullFlush;
    }

    public int getBufferedDeleteTermsSize() {
        return this.deleteQueue.getBufferedDeleteTermsSize();
    }

    public int getNumBufferedDeleteTerms() {
        return this.deleteQueue.numGlobalTermDeletes();
    }

    public boolean anyDeletions() {
        return this.deleteQueue.anyChanges();
    }

    void close() {
        this.closed = true;
        this.flushControl.setClosed();
    }

    private boolean preUpdate() throws IOException {
        this.ensureOpen();
        boolean maybeMerge = false;
        if (this.flushControl.anyStalledThreads() || this.flushControl.numQueuedFlushes() > 0) {
            if (this.infoStream.isEnabled("DW")) {
                this.infoStream.message("DW", "DocumentsWriter has queued dwpt; will hijack this thread to flush pending segment(s)");
            }
            while (true) {
                DocumentsWriterPerThread flushingDWPT;
                if ((flushingDWPT = this.flushControl.nextPendingFlush()) != null) {
                    maybeMerge |= this.doFlush(flushingDWPT);
                    continue;
                }
                if (this.infoStream.isEnabled("DW") && this.flushControl.anyStalledThreads()) {
                    this.infoStream.message("DW", "WARNING DocumentsWriter has stalled threads; waiting");
                }
                this.flushControl.waitIfStalled();
                if (this.flushControl.numQueuedFlushes() == 0) break;
            }
            if (this.infoStream.isEnabled("DW")) {
                this.infoStream.message("DW", "continue indexing after helping out flushing DocumentsWriter is healthy");
            }
        }
        return maybeMerge;
    }

    private boolean postUpdate(DocumentsWriterPerThread flushingDWPT, boolean maybeMerge) throws IOException {
        if (this.flushControl.doApplyAllDeletes()) {
            this.applyAllDeletes(this.deleteQueue);
        }
        if (flushingDWPT != null) {
            maybeMerge |= this.doFlush(flushingDWPT);
        } else {
            DocumentsWriterPerThread nextPendingFlush = this.flushControl.nextPendingFlush();
            if (nextPendingFlush != null) {
                maybeMerge |= this.doFlush(nextPendingFlush);
            }
        }
        return maybeMerge;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean updateDocuments(Iterable<? extends Iterable<? extends IndexableField>> docs, Analyzer analyzer, Term delTerm) throws IOException {
        DocumentsWriterPerThread flushingDWPT;
        boolean maybeMerge = this.preUpdate();
        DocumentsWriterPerThreadPool.ThreadState perThread = this.flushControl.obtainAndLock();
        try {
            if (!perThread.isActive()) {
                this.ensureOpen();
                assert (false) : "perThread is not active but we are still open";
            }
            DocumentsWriterPerThread dwpt = perThread.dwpt;
            try {
                int docCount = dwpt.updateDocuments(docs, analyzer, delTerm);
                this.numDocsInRAM.addAndGet(docCount);
            }
            finally {
                if (dwpt.checkAndResetHasAborted()) {
                    this.flushControl.doOnAbort(perThread);
                }
            }
            boolean isUpdate = delTerm != null;
            flushingDWPT = this.flushControl.doAfterDocument(perThread, isUpdate);
        }
        finally {
            perThread.unlock();
        }
        return this.postUpdate(flushingDWPT, maybeMerge);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean updateDocument(Iterable<? extends IndexableField> doc, Analyzer analyzer, Term delTerm) throws IOException {
        DocumentsWriterPerThread flushingDWPT;
        boolean maybeMerge = this.preUpdate();
        DocumentsWriterPerThreadPool.ThreadState perThread = this.flushControl.obtainAndLock();
        try {
            if (!perThread.isActive()) {
                this.ensureOpen();
                throw new IllegalStateException("perThread is not active but we are still open");
            }
            DocumentsWriterPerThread dwpt = perThread.dwpt;
            try {
                dwpt.updateDocument(doc, analyzer, delTerm);
                this.numDocsInRAM.incrementAndGet();
            }
            finally {
                if (dwpt.checkAndResetHasAborted()) {
                    this.flushControl.doOnAbort(perThread);
                }
            }
            boolean isUpdate = delTerm != null;
            flushingDWPT = this.flushControl.doAfterDocument(perThread, isUpdate);
        }
        finally {
            perThread.unlock();
        }
        return this.postUpdate(flushingDWPT, maybeMerge);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean doFlush(DocumentsWriterPerThread flushingDWPT) throws IOException {
        boolean maybeMerge = false;
        while (flushingDWPT != null) {
            maybeMerge = true;
            boolean success = false;
            DocumentsWriterFlushQueue.SegmentFlushTicket ticket = null;
            try {
                assert (this.currentFullFlushDelQueue == null || flushingDWPT.deleteQueue == this.currentFullFlushDelQueue) : "expected: " + this.currentFullFlushDelQueue + "but was: " + flushingDWPT.deleteQueue + " " + this.flushControl.isFullFlush();
                try {
                    ticket = this.ticketQueue.addFlushTicket(flushingDWPT);
                    DocumentsWriterPerThread.FlushedSegment newSegment = flushingDWPT.flush();
                    this.ticketQueue.addSegment(ticket, newSegment);
                    success = true;
                }
                finally {
                    if (!success && ticket != null) {
                        this.ticketQueue.markTicketFailed(ticket);
                    }
                }
                if (this.ticketQueue.getTicketCount() >= this.perThreadPool.getActiveThreadState()) {
                    this.ticketQueue.forcePurge(this);
                } else {
                    this.ticketQueue.tryPurge(this);
                }
            }
            finally {
                this.flushControl.doAfterFlush(flushingDWPT);
                flushingDWPT.checkAndResetHasAborted();
                this.indexWriter.flushCount.incrementAndGet();
                this.indexWriter.doAfterFlush();
            }
            flushingDWPT = this.flushControl.nextPendingFlush();
        }
        double ramBufferSizeMB = this.indexWriterConfig.getRAMBufferSizeMB();
        if (ramBufferSizeMB != -1.0 && (double)this.flushControl.getDeleteBytesUsed() > 1048576.0 * ramBufferSizeMB / 2.0) {
            if (this.infoStream.isEnabled("DW")) {
                this.infoStream.message("DW", "force apply deletes bytesUsed=" + this.flushControl.getDeleteBytesUsed() + " vs ramBuffer=" + 1048576.0 * ramBufferSizeMB);
            }
            this.applyAllDeletes(this.deleteQueue);
        }
        return maybeMerge;
    }

    void finishFlush(DocumentsWriterPerThread.FlushedSegment newSegment, FrozenBufferedDeletes bufferedDeletes) throws IOException {
        if (newSegment == null) {
            assert (bufferedDeletes != null);
            if (bufferedDeletes != null && bufferedDeletes.any()) {
                this.indexWriter.publishFrozenDeletes(bufferedDeletes);
                if (this.infoStream.isEnabled("DW")) {
                    this.infoStream.message("DW", "flush: push buffered deletes: " + bufferedDeletes);
                }
            }
        } else {
            this.publishFlushedSegment(newSegment, bufferedDeletes);
        }
    }

    final void subtractFlushedNumDocs(int numFlushed) {
        int oldValue = this.numDocsInRAM.get();
        while (!this.numDocsInRAM.compareAndSet(oldValue, oldValue - numFlushed)) {
            oldValue = this.numDocsInRAM.get();
        }
    }

    private void publishFlushedSegment(DocumentsWriterPerThread.FlushedSegment newSegment, FrozenBufferedDeletes globalPacket) throws IOException {
        assert (newSegment != null);
        assert (newSegment.segmentInfo != null);
        FrozenBufferedDeletes segmentDeletes = newSegment.segmentDeletes;
        if (this.infoStream.isEnabled("DW")) {
            this.infoStream.message("DW", "publishFlushedSegment seg-private deletes=" + segmentDeletes);
        }
        if (segmentDeletes != null && this.infoStream.isEnabled("DW")) {
            this.infoStream.message("DW", "flush: push buffered seg private deletes: " + segmentDeletes);
        }
        this.indexWriter.publishFlushedSegment(newSegment.segmentInfo, segmentDeletes, globalPacket);
    }

    private synchronized boolean setFlushingDeleteQueue(DocumentsWriterDeleteQueue session) {
        this.currentFullFlushDelQueue = session;
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final boolean flushAllThreads() throws IOException {
        DocumentsWriterDeleteQueue flushingDeleteQueue;
        if (this.infoStream.isEnabled("DW")) {
            this.infoStream.message("DW", Thread.currentThread().getName() + " startFullFlush");
        }
        DocumentsWriter documentsWriter = this;
        synchronized (documentsWriter) {
            this.pendingChangesInCurrentFullFlush = this.anyChanges();
            flushingDeleteQueue = this.deleteQueue;
            this.flushControl.markForFullFlush();
            assert (this.setFlushingDeleteQueue(flushingDeleteQueue));
        }
        assert (this.currentFullFlushDelQueue != null);
        assert (this.currentFullFlushDelQueue != this.deleteQueue);
        boolean anythingFlushed = false;
        try {
            DocumentsWriterPerThread flushingDWPT;
            while ((flushingDWPT = this.flushControl.nextPendingFlush()) != null) {
                anythingFlushed |= this.doFlush(flushingDWPT);
            }
            this.flushControl.waitForFlush();
            if (!anythingFlushed && flushingDeleteQueue.anyChanges()) {
                if (this.infoStream.isEnabled("DW")) {
                    this.infoStream.message("DW", Thread.currentThread().getName() + ": flush naked frozen global deletes");
                }
                this.ticketQueue.addDeletesAndPurge(this, flushingDeleteQueue);
            } else {
                this.ticketQueue.forcePurge(this);
            }
            assert (!flushingDeleteQueue.anyChanges() && !this.ticketQueue.hasTickets());
        }
        finally {
            assert (flushingDeleteQueue == this.currentFullFlushDelQueue);
        }
        return anythingFlushed;
    }

    final void finishFullFlush(boolean success) {
        try {
            if (this.infoStream.isEnabled("DW")) {
                this.infoStream.message("DW", Thread.currentThread().getName() + " finishFullFlush success=" + success);
            }
            assert (this.setFlushingDeleteQueue(null));
            if (success) {
                this.flushControl.finishFullFlush();
            } else {
                this.flushControl.abortFullFlushes();
            }
        }
        finally {
            this.pendingChangesInCurrentFullFlush = false;
        }
    }
}


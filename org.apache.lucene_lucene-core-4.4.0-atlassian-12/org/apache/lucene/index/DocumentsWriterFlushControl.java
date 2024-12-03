/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.lucene.index.DocumentsWriter;
import org.apache.lucene.index.DocumentsWriterDeleteQueue;
import org.apache.lucene.index.DocumentsWriterPerThread;
import org.apache.lucene.index.DocumentsWriterPerThreadPool;
import org.apache.lucene.index.DocumentsWriterStallControl;
import org.apache.lucene.index.FlushPolicy;
import org.apache.lucene.index.LiveIndexWriterConfig;
import org.apache.lucene.util.ThreadInterruptedException;

final class DocumentsWriterFlushControl {
    private final long hardMaxBytesPerDWPT;
    private long activeBytes = 0L;
    private long flushBytes = 0L;
    private volatile int numPending = 0;
    private int numDocsSinceStalled = 0;
    final AtomicBoolean flushDeletes = new AtomicBoolean(false);
    private boolean fullFlush = false;
    private final Queue<DocumentsWriterPerThread> flushQueue = new LinkedList<DocumentsWriterPerThread>();
    private final Queue<BlockedFlush> blockedFlushes = new LinkedList<BlockedFlush>();
    private final IdentityHashMap<DocumentsWriterPerThread, Long> flushingWriters = new IdentityHashMap();
    double maxConfiguredRamBuffer = 0.0;
    long peakActiveBytes = 0L;
    long peakFlushBytes = 0L;
    long peakNetBytes = 0L;
    long peakDelta = 0L;
    final DocumentsWriterStallControl stallControl;
    private final DocumentsWriterPerThreadPool perThreadPool;
    private final FlushPolicy flushPolicy;
    private boolean closed = false;
    private final DocumentsWriter documentsWriter;
    private final LiveIndexWriterConfig config;
    private final List<DocumentsWriterPerThread> fullFlushBuffer = new ArrayList<DocumentsWriterPerThread>();

    DocumentsWriterFlushControl(DocumentsWriter documentsWriter, LiveIndexWriterConfig config) {
        this.stallControl = new DocumentsWriterStallControl();
        this.perThreadPool = documentsWriter.perThreadPool;
        this.flushPolicy = documentsWriter.flushPolicy;
        this.hardMaxBytesPerDWPT = config.getRAMPerThreadHardLimitMB() * 1024 * 1024;
        this.config = config;
        this.documentsWriter = documentsWriter;
    }

    public synchronized long activeBytes() {
        return this.activeBytes;
    }

    public synchronized long flushBytes() {
        return this.flushBytes;
    }

    public synchronized long netBytes() {
        return this.flushBytes + this.activeBytes;
    }

    private long stallLimitBytes() {
        double maxRamMB = this.config.getRAMBufferSizeMB();
        return maxRamMB != -1.0 ? (long)(2.0 * (maxRamMB * 1024.0 * 1024.0)) : Long.MAX_VALUE;
    }

    private boolean assertMemory() {
        double maxRamMB = this.config.getRAMBufferSizeMB();
        if (maxRamMB != -1.0) {
            this.maxConfiguredRamBuffer = Math.max(maxRamMB, this.maxConfiguredRamBuffer);
            long ram = this.flushBytes + this.activeBytes;
            long ramBufferBytes = (long)(this.maxConfiguredRamBuffer * 1024.0 * 1024.0);
            long expected = 2L * ramBufferBytes + (long)(this.numPending + this.numFlushingDWPT() + this.numBlockedFlushes()) * this.peakDelta + (long)this.numDocsSinceStalled * this.peakDelta;
            if (this.peakDelta < ramBufferBytes >> 1) assert (ram <= expected) : "actual mem: " + ram + " byte, expected mem: " + expected + " byte, flush mem: " + this.flushBytes + ", active mem: " + this.activeBytes + ", pending DWPT: " + this.numPending + ", flushing DWPT: " + this.numFlushingDWPT() + ", blocked DWPT: " + this.numBlockedFlushes() + ", peakDelta mem: " + this.peakDelta + " byte";
        }
        return true;
    }

    private void commitPerThreadBytes(DocumentsWriterPerThreadPool.ThreadState perThread) {
        long delta = perThread.dwpt.bytesUsed() - perThread.bytesUsed;
        perThread.bytesUsed += delta;
        if (perThread.flushPending) {
            this.flushBytes += delta;
        } else {
            this.activeBytes += delta;
        }
        assert (this.updatePeaks(delta));
    }

    private boolean updatePeaks(long delta) {
        this.peakActiveBytes = Math.max(this.peakActiveBytes, this.activeBytes);
        this.peakFlushBytes = Math.max(this.peakFlushBytes, this.flushBytes);
        this.peakNetBytes = Math.max(this.peakNetBytes, this.netBytes());
        this.peakDelta = Math.max(this.peakDelta, delta);
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    synchronized DocumentsWriterPerThread doAfterDocument(DocumentsWriterPerThreadPool.ThreadState perThread, boolean isUpdate) {
        try {
            DocumentsWriterPerThread flushingDWPT;
            this.commitPerThreadBytes(perThread);
            if (!perThread.flushPending) {
                if (isUpdate) {
                    this.flushPolicy.onUpdate(this, perThread);
                } else {
                    this.flushPolicy.onInsert(this, perThread);
                }
                if (!perThread.flushPending && perThread.bytesUsed > this.hardMaxBytesPerDWPT) {
                    this.setFlushPending(perThread);
                }
            }
            if (this.fullFlush) {
                if (perThread.flushPending) {
                    this.checkoutAndBlock(perThread);
                    flushingDWPT = this.nextPendingFlush();
                } else {
                    flushingDWPT = null;
                }
            } else {
                flushingDWPT = this.tryCheckoutForFlush(perThread);
            }
            DocumentsWriterPerThread documentsWriterPerThread = flushingDWPT;
            return documentsWriterPerThread;
        }
        finally {
            boolean stalled = this.updateStallState();
            assert (this.assertNumDocsSinceStalled(stalled) && this.assertMemory());
        }
    }

    private boolean assertNumDocsSinceStalled(boolean stalled) {
        this.numDocsSinceStalled = stalled ? ++this.numDocsSinceStalled : 0;
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    synchronized void doAfterFlush(DocumentsWriterPerThread dwpt) {
        assert (this.flushingWriters.containsKey(dwpt));
        try {
            Long bytes = this.flushingWriters.remove(dwpt);
            this.flushBytes -= bytes.longValue();
            this.perThreadPool.recycle(dwpt);
            assert (this.assertMemory());
        }
        finally {
            try {
                this.updateStallState();
            }
            finally {
                this.notifyAll();
            }
        }
    }

    private final boolean updateStallState() {
        assert (Thread.holdsLock(this));
        long limit = this.stallLimitBytes();
        boolean stall = this.activeBytes + this.flushBytes > limit && this.activeBytes < limit && !this.closed;
        this.stallControl.updateStalled(stall);
        return stall;
    }

    public synchronized void waitForFlush() {
        assert (!Thread.holdsLock(this.documentsWriter.indexWriter)) : "IW lock should never be hold when waiting on flush";
        while (this.flushingWriters.size() != 0) {
            try {
                this.wait();
            }
            catch (InterruptedException e) {
                throw new ThreadInterruptedException(e);
            }
        }
    }

    public synchronized void setFlushPending(DocumentsWriterPerThreadPool.ThreadState perThread) {
        assert (!perThread.flushPending);
        if (perThread.dwpt.getNumDocsInRAM() > 0) {
            perThread.flushPending = true;
            long bytes = perThread.bytesUsed;
            this.flushBytes += bytes;
            this.activeBytes -= bytes;
            ++this.numPending;
            assert (this.assertMemory());
        }
    }

    synchronized void doOnAbort(DocumentsWriterPerThreadPool.ThreadState state) {
        try {
            if (state.flushPending) {
                this.flushBytes -= state.bytesUsed;
            } else {
                this.activeBytes -= state.bytesUsed;
            }
            assert (this.assertMemory());
            this.perThreadPool.replaceForFlush(state, this.closed);
        }
        finally {
            this.updateStallState();
        }
    }

    synchronized DocumentsWriterPerThread tryCheckoutForFlush(DocumentsWriterPerThreadPool.ThreadState perThread) {
        return perThread.flushPending ? this.internalTryCheckOutForFlush(perThread) : null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void checkoutAndBlock(DocumentsWriterPerThreadPool.ThreadState perThread) {
        perThread.lock();
        try {
            assert (perThread.flushPending) : "can not block non-pending threadstate";
            assert (this.fullFlush) : "can not block if fullFlush == false";
            long bytes = perThread.bytesUsed;
            DocumentsWriterPerThread dwpt = this.perThreadPool.replaceForFlush(perThread, this.closed);
            --this.numPending;
            this.blockedFlushes.add(new BlockedFlush(dwpt, bytes));
        }
        finally {
            perThread.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private DocumentsWriterPerThread internalTryCheckOutForFlush(DocumentsWriterPerThreadPool.ThreadState perThread) {
        assert (Thread.holdsLock(this));
        assert (perThread.flushPending);
        try {
            if (perThread.tryLock()) {
                try {
                    if (perThread.isActive()) {
                        assert (perThread.isHeldByCurrentThread());
                        long bytes = perThread.bytesUsed;
                        DocumentsWriterPerThread dwpt = this.perThreadPool.replaceForFlush(perThread, this.closed);
                        assert (!this.flushingWriters.containsKey(dwpt)) : "DWPT is already flushing";
                        this.flushingWriters.put(dwpt, bytes);
                        --this.numPending;
                        DocumentsWriterPerThread documentsWriterPerThread = dwpt;
                        return documentsWriterPerThread;
                    }
                }
                finally {
                    perThread.unlock();
                }
            }
            DocumentsWriterPerThread documentsWriterPerThread = null;
            return documentsWriterPerThread;
        }
        finally {
            this.updateStallState();
        }
    }

    public String toString() {
        return "DocumentsWriterFlushControl [activeBytes=" + this.activeBytes + ", flushBytes=" + this.flushBytes + "]";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    DocumentsWriterPerThread nextPendingFlush() {
        int numPending;
        boolean fullFlush;
        DocumentsWriterFlushControl documentsWriterFlushControl = this;
        synchronized (documentsWriterFlushControl) {
            DocumentsWriterPerThread poll = this.flushQueue.poll();
            if (poll != null) {
                this.updateStallState();
                return poll;
            }
            fullFlush = this.fullFlush;
            numPending = this.numPending;
        }
        if (numPending > 0 && !fullFlush) {
            int limit = this.perThreadPool.getActiveThreadState();
            for (int i = 0; i < limit && numPending > 0; ++i) {
                DocumentsWriterPerThread dwpt;
                DocumentsWriterPerThreadPool.ThreadState next = this.perThreadPool.getThreadState(i);
                if (!next.flushPending || (dwpt = this.tryCheckoutForFlush(next)) == null) continue;
                return dwpt;
            }
        }
        return null;
    }

    synchronized void setClosed() {
        if (!this.closed) {
            this.closed = true;
            this.perThreadPool.deactivateUnreleasedStates();
        }
    }

    public Iterator<DocumentsWriterPerThreadPool.ThreadState> allActiveThreadStates() {
        return this.getPerThreadsIterator(this.perThreadPool.getActiveThreadState());
    }

    private Iterator<DocumentsWriterPerThreadPool.ThreadState> getPerThreadsIterator(final int upto) {
        return new Iterator<DocumentsWriterPerThreadPool.ThreadState>(){
            int i = 0;

            @Override
            public boolean hasNext() {
                return this.i < upto;
            }

            @Override
            public DocumentsWriterPerThreadPool.ThreadState next() {
                return DocumentsWriterFlushControl.this.perThreadPool.getThreadState(this.i++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove() not supported.");
            }
        };
    }

    synchronized void doOnDelete() {
        this.flushPolicy.onDelete(this, null);
    }

    public int getNumGlobalTermDeletes() {
        return this.documentsWriter.deleteQueue.numGlobalTermDeletes() + this.documentsWriter.indexWriter.bufferedDeletesStream.numTerms();
    }

    public long getDeleteBytesUsed() {
        return this.documentsWriter.deleteQueue.bytesUsed() + this.documentsWriter.indexWriter.bufferedDeletesStream.bytesUsed();
    }

    synchronized int numFlushingDWPT() {
        return this.flushingWriters.size();
    }

    public boolean doApplyAllDeletes() {
        return this.flushDeletes.getAndSet(false);
    }

    public void setApplyAllDeletes() {
        this.flushDeletes.set(true);
    }

    int numActiveDWPT() {
        return this.perThreadPool.getActiveThreadState();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    DocumentsWriterPerThreadPool.ThreadState obtainAndLock() {
        DocumentsWriterPerThreadPool.ThreadState perThread = this.perThreadPool.getAndLock(Thread.currentThread(), this.documentsWriter);
        boolean success = false;
        try {
            if (perThread.isActive() && perThread.dwpt.deleteQueue != this.documentsWriter.deleteQueue) {
                this.addFlushableState(perThread);
            }
            success = true;
            DocumentsWriterPerThreadPool.ThreadState threadState = perThread;
            return threadState;
        }
        finally {
            if (!success) {
                perThread.unlock();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void markForFullFlush() {
        DocumentsWriterDeleteQueue flushingQueue;
        DocumentsWriterFlushControl documentsWriterFlushControl = this;
        synchronized (documentsWriterFlushControl) {
            DocumentsWriterDeleteQueue newQueue;
            assert (!this.fullFlush) : "called DWFC#markForFullFlush() while full flush is still running";
            assert (this.fullFlushBuffer.isEmpty()) : "full flush buffer should be empty: " + this.fullFlushBuffer;
            this.fullFlush = true;
            flushingQueue = this.documentsWriter.deleteQueue;
            this.documentsWriter.deleteQueue = newQueue = new DocumentsWriterDeleteQueue(flushingQueue.generation + 1L);
        }
        int limit = this.perThreadPool.getActiveThreadState();
        for (int i = 0; i < limit; ++i) {
            DocumentsWriterPerThreadPool.ThreadState next = this.perThreadPool.getThreadState(i);
            next.lock();
            try {
                if (!next.isActive()) continue;
                assert (next.dwpt.deleteQueue == flushingQueue || next.dwpt.deleteQueue == this.documentsWriter.deleteQueue) : " flushingQueue: " + flushingQueue + " currentqueue: " + this.documentsWriter.deleteQueue + " perThread queue: " + next.dwpt.deleteQueue + " numDocsInRam: " + next.dwpt.getNumDocsInRAM();
                if (next.dwpt.deleteQueue != flushingQueue) continue;
                this.addFlushableState(next);
                continue;
            }
            finally {
                next.unlock();
            }
        }
        DocumentsWriterFlushControl documentsWriterFlushControl2 = this;
        synchronized (documentsWriterFlushControl2) {
            this.pruneBlockedQueue(flushingQueue);
            assert (this.assertBlockedFlushes(this.documentsWriter.deleteQueue));
            this.flushQueue.addAll(this.fullFlushBuffer);
            this.fullFlushBuffer.clear();
            this.updateStallState();
        }
        assert (this.assertActiveDeleteQueue(this.documentsWriter.deleteQueue));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean assertActiveDeleteQueue(DocumentsWriterDeleteQueue queue) {
        int limit = this.perThreadPool.getActiveThreadState();
        for (int i = 0; i < limit; ++i) {
            DocumentsWriterPerThreadPool.ThreadState next = this.perThreadPool.getThreadState(i);
            next.lock();
            try {
                if ($assertionsDisabled || !next.isActive() || next.dwpt.deleteQueue == queue) continue;
                throw new AssertionError();
            }
            finally {
                next.unlock();
            }
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void addFlushableState(DocumentsWriterPerThreadPool.ThreadState perThread) {
        if (this.documentsWriter.infoStream.isEnabled("DWFC")) {
            this.documentsWriter.infoStream.message("DWFC", "addFlushableState " + perThread.dwpt);
        }
        DocumentsWriterPerThread dwpt = perThread.dwpt;
        assert (perThread.isHeldByCurrentThread());
        assert (perThread.isActive());
        assert (this.fullFlush);
        assert (dwpt.deleteQueue != this.documentsWriter.deleteQueue);
        if (dwpt.getNumDocsInRAM() > 0) {
            DocumentsWriterFlushControl documentsWriterFlushControl = this;
            synchronized (documentsWriterFlushControl) {
                if (!perThread.flushPending) {
                    this.setFlushPending(perThread);
                }
                DocumentsWriterPerThread flushingDWPT = this.internalTryCheckOutForFlush(perThread);
                assert (flushingDWPT != null) : "DWPT must never be null here since we hold the lock and it holds documents";
                assert (dwpt == flushingDWPT) : "flushControl returned different DWPT";
                this.fullFlushBuffer.add(flushingDWPT);
            }
        } else if (this.closed) {
            this.perThreadPool.deactivateThreadState(perThread);
        } else {
            this.perThreadPool.reinitThreadState(perThread);
        }
    }

    private void pruneBlockedQueue(DocumentsWriterDeleteQueue flushingQueue) {
        Iterator iterator = this.blockedFlushes.iterator();
        while (iterator.hasNext()) {
            BlockedFlush blockedFlush = (BlockedFlush)iterator.next();
            if (blockedFlush.dwpt.deleteQueue != flushingQueue) continue;
            iterator.remove();
            assert (!this.flushingWriters.containsKey(blockedFlush.dwpt)) : "DWPT is already flushing";
            this.flushingWriters.put(blockedFlush.dwpt, blockedFlush.bytes);
            this.flushQueue.add(blockedFlush.dwpt);
        }
    }

    synchronized void finishFullFlush() {
        assert (this.fullFlush);
        assert (this.flushQueue.isEmpty());
        assert (this.flushingWriters.isEmpty());
        try {
            if (!this.blockedFlushes.isEmpty()) {
                assert (this.assertBlockedFlushes(this.documentsWriter.deleteQueue));
                this.pruneBlockedQueue(this.documentsWriter.deleteQueue);
                assert (this.blockedFlushes.isEmpty());
            }
        }
        finally {
            this.fullFlush = false;
            this.updateStallState();
        }
    }

    boolean assertBlockedFlushes(DocumentsWriterDeleteQueue flushingQueue) {
        for (BlockedFlush blockedFlush : this.blockedFlushes) {
            assert (blockedFlush.dwpt.deleteQueue == flushingQueue);
        }
        return true;
    }

    synchronized void abortFullFlushes() {
        try {
            this.abortPendingFlushes();
        }
        finally {
            this.fullFlush = false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    synchronized void abortPendingFlushes() {
        try {
            for (DocumentsWriterPerThread dwpt : this.flushQueue) {
                try {
                    dwpt.abort();
                }
                catch (Throwable throwable) {}
                continue;
                finally {
                    this.doAfterFlush(dwpt);
                }
            }
            for (BlockedFlush blockedFlush : this.blockedFlushes) {
                try {
                    this.flushingWriters.put(blockedFlush.dwpt, blockedFlush.bytes);
                    blockedFlush.dwpt.abort();
                }
                catch (Throwable throwable) {}
                continue;
                finally {
                    this.doAfterFlush(blockedFlush.dwpt);
                }
            }
        }
        finally {
            this.flushQueue.clear();
            this.blockedFlushes.clear();
            this.updateStallState();
        }
    }

    synchronized boolean isFullFlush() {
        return this.fullFlush;
    }

    synchronized int numQueuedFlushes() {
        return this.flushQueue.size();
    }

    synchronized int numBlockedFlushes() {
        return this.blockedFlushes.size();
    }

    void waitIfStalled() {
        if (this.documentsWriter.infoStream.isEnabled("DWFC")) {
            this.documentsWriter.infoStream.message("DWFC", "waitIfStalled: numFlushesPending: " + this.flushQueue.size() + " netBytes: " + this.netBytes() + " flushBytes: " + this.flushBytes() + " fullFlush: " + this.fullFlush);
        }
        this.stallControl.waitIfStalled();
    }

    boolean anyStalledThreads() {
        return this.stallControl.anyStalledThreads();
    }

    private static class BlockedFlush {
        final DocumentsWriterPerThread dwpt;
        final long bytes;

        BlockedFlush(DocumentsWriterPerThread dwpt, long bytes) {
            this.dwpt = dwpt;
            this.bytes = bytes;
        }
    }
}


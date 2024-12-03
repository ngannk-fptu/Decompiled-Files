/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.util.concurrent.locks.ReentrantLock;
import org.apache.lucene.index.DocumentsWriter;
import org.apache.lucene.index.DocumentsWriterPerThread;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.LiveIndexWriterConfig;
import org.apache.lucene.util.SetOnce;

abstract class DocumentsWriterPerThreadPool
implements Cloneable {
    private ThreadState[] threadStates;
    private volatile int numThreadStatesActive;
    private SetOnce<FieldInfos.FieldNumbers> globalFieldMap = new SetOnce();
    private SetOnce<DocumentsWriter> documentsWriter = new SetOnce();

    DocumentsWriterPerThreadPool(int maxNumThreadStates) {
        if (maxNumThreadStates < 1) {
            throw new IllegalArgumentException("maxNumThreadStates must be >= 1 but was: " + maxNumThreadStates);
        }
        this.threadStates = new ThreadState[maxNumThreadStates];
        this.numThreadStatesActive = 0;
    }

    void initialize(DocumentsWriter documentsWriter, FieldInfos.FieldNumbers globalFieldMap, LiveIndexWriterConfig config) {
        this.documentsWriter.set(documentsWriter);
        this.globalFieldMap.set(globalFieldMap);
        for (int i = 0; i < this.threadStates.length; ++i) {
            FieldInfos.Builder infos = new FieldInfos.Builder(globalFieldMap);
            this.threadStates[i] = new ThreadState(new DocumentsWriterPerThread(documentsWriter.directory, documentsWriter, infos, documentsWriter.chain));
        }
    }

    public DocumentsWriterPerThreadPool clone() {
        DocumentsWriterPerThreadPool clone;
        assert (this.numThreadStatesActive == 0);
        try {
            clone = (DocumentsWriterPerThreadPool)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        clone.documentsWriter = new SetOnce();
        clone.globalFieldMap = new SetOnce();
        clone.threadStates = new ThreadState[this.threadStates.length];
        return clone;
    }

    int getMaxThreadStates() {
        return this.threadStates.length;
    }

    int getActiveThreadState() {
        return this.numThreadStatesActive;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    synchronized ThreadState newThreadState() {
        if (this.numThreadStatesActive < this.threadStates.length) {
            ThreadState threadState = this.threadStates[this.numThreadStatesActive];
            threadState.lock();
            boolean unlock = true;
            try {
                if (threadState.isActive()) {
                    ++this.numThreadStatesActive;
                    assert (threadState.dwpt != null);
                    threadState.dwpt.initialize();
                    unlock = false;
                    ThreadState threadState2 = threadState;
                    return threadState2;
                }
                assert (this.assertUnreleasedThreadStatesInactive());
                ThreadState threadState3 = null;
                return threadState3;
            }
            finally {
                if (unlock) {
                    threadState.unlock();
                }
            }
        }
        return null;
    }

    private synchronized boolean assertUnreleasedThreadStatesInactive() {
        for (int i = this.numThreadStatesActive; i < this.threadStates.length; ++i) {
            assert (this.threadStates[i].tryLock()) : "unreleased threadstate should not be locked";
            try {
                if ($assertionsDisabled || !this.threadStates[i].isActive()) continue;
                throw new AssertionError((Object)"expected unreleased thread state to be inactive");
            }
            finally {
                this.threadStates[i].unlock();
            }
        }
        return true;
    }

    synchronized void deactivateUnreleasedStates() {
        for (int i = this.numThreadStatesActive; i < this.threadStates.length; ++i) {
            ThreadState threadState = this.threadStates[i];
            threadState.lock();
            try {
                threadState.resetWriter(null);
                continue;
            }
            finally {
                threadState.unlock();
            }
        }
    }

    DocumentsWriterPerThread replaceForFlush(ThreadState threadState, boolean closed) {
        assert (threadState.isHeldByCurrentThread());
        assert (this.globalFieldMap.get() != null);
        DocumentsWriterPerThread dwpt = threadState.dwpt;
        if (!closed) {
            FieldInfos.Builder infos = new FieldInfos.Builder(this.globalFieldMap.get());
            DocumentsWriterPerThread newDwpt = new DocumentsWriterPerThread(dwpt, infos);
            newDwpt.initialize();
            threadState.resetWriter(newDwpt);
        } else {
            threadState.resetWriter(null);
        }
        return dwpt;
    }

    void recycle(DocumentsWriterPerThread dwpt) {
    }

    abstract ThreadState getAndLock(Thread var1, DocumentsWriter var2);

    ThreadState getThreadState(int ord) {
        return this.threadStates[ord];
    }

    ThreadState minContendedThreadState() {
        ThreadState minThreadState = null;
        int limit = this.numThreadStatesActive;
        for (int i = 0; i < limit; ++i) {
            ThreadState state = this.threadStates[i];
            if (minThreadState != null && state.getQueueLength() >= minThreadState.getQueueLength()) continue;
            minThreadState = state;
        }
        return minThreadState;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    int numDeactivatedThreadStates() {
        int count = 0;
        for (int i = 0; i < this.threadStates.length; ++i) {
            ThreadState threadState = this.threadStates[i];
            threadState.lock();
            try {
                if (threadState.isActive) continue;
                ++count;
                continue;
            }
            finally {
                threadState.unlock();
            }
        }
        return count;
    }

    void deactivateThreadState(ThreadState threadState) {
        assert (threadState.isActive());
        threadState.resetWriter(null);
    }

    void reinitThreadState(ThreadState threadState) {
        assert (threadState.isActive);
        assert (threadState.dwpt.getNumDocsInRAM() == 0);
        threadState.dwpt.initialize();
    }

    static final class ThreadState
    extends ReentrantLock {
        DocumentsWriterPerThread dwpt;
        volatile boolean flushPending = false;
        long bytesUsed = 0L;
        private boolean isActive = true;

        ThreadState(DocumentsWriterPerThread dpwt) {
            this.dwpt = dpwt;
        }

        private void resetWriter(DocumentsWriterPerThread dwpt) {
            assert (this.isHeldByCurrentThread());
            if (dwpt == null) {
                this.isActive = false;
            }
            this.dwpt = dwpt;
            this.bytesUsed = 0L;
            this.flushPending = false;
        }

        boolean isActive() {
            assert (this.isHeldByCurrentThread());
            return this.isActive;
        }

        public long getBytesUsedPerThread() {
            assert (this.isHeldByCurrentThread());
            return this.bytesUsed;
        }

        public DocumentsWriterPerThread getDocumentsWriterPerThread() {
            assert (this.isHeldByCurrentThread());
            return this.dwpt;
        }

        public boolean isFlushPending() {
            return this.flushPending;
        }
    }
}


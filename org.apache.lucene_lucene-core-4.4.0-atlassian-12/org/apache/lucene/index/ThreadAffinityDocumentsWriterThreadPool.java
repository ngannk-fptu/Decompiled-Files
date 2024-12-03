/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.lucene.index.DocumentsWriter;
import org.apache.lucene.index.DocumentsWriterPerThreadPool;

class ThreadAffinityDocumentsWriterThreadPool
extends DocumentsWriterPerThreadPool {
    private Map<Thread, DocumentsWriterPerThreadPool.ThreadState> threadBindings = new ConcurrentHashMap<Thread, DocumentsWriterPerThreadPool.ThreadState>();

    public ThreadAffinityDocumentsWriterThreadPool(int maxNumPerThreads) {
        super(maxNumPerThreads);
        assert (this.getMaxThreadStates() >= 1);
    }

    @Override
    public DocumentsWriterPerThreadPool.ThreadState getAndLock(Thread requestingThread, DocumentsWriter documentsWriter) {
        DocumentsWriterPerThreadPool.ThreadState threadState = this.threadBindings.get(requestingThread);
        if (threadState != null && threadState.tryLock()) {
            return threadState;
        }
        DocumentsWriterPerThreadPool.ThreadState minThreadState = null;
        minThreadState = this.minContendedThreadState();
        if (minThreadState == null || minThreadState.hasQueuedThreads()) {
            DocumentsWriterPerThreadPool.ThreadState newState = this.newThreadState();
            if (newState != null) {
                assert (newState.isHeldByCurrentThread());
                this.threadBindings.put(requestingThread, newState);
                return newState;
            }
            if (minThreadState == null) {
                minThreadState = this.minContendedThreadState();
            }
        }
        assert (minThreadState != null) : "ThreadState is null";
        minThreadState.lock();
        return minThreadState;
    }

    @Override
    public ThreadAffinityDocumentsWriterThreadPool clone() {
        ThreadAffinityDocumentsWriterThreadPool clone = (ThreadAffinityDocumentsWriterThreadPool)super.clone();
        clone.threadBindings = new ConcurrentHashMap<Thread, DocumentsWriterPerThreadPool.ThreadState>();
        return clone;
    }
}


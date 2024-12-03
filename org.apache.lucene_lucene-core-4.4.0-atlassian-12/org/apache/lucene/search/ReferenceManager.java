/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.lucene.store.AlreadyClosedException;

public abstract class ReferenceManager<G>
implements Closeable {
    private static final String REFERENCE_MANAGER_IS_CLOSED_MSG = "this ReferenceManager is closed";
    protected volatile G current;
    private final Lock refreshLock = new ReentrantLock();
    private final List<RefreshListener> refreshListeners = new CopyOnWriteArrayList<RefreshListener>();

    private void ensureOpen() {
        if (this.current == null) {
            throw new AlreadyClosedException(REFERENCE_MANAGER_IS_CLOSED_MSG);
        }
    }

    private synchronized void swapReference(G newReference) throws IOException {
        this.ensureOpen();
        G oldReference = this.current;
        this.current = newReference;
        this.release(oldReference);
    }

    protected abstract void decRef(G var1) throws IOException;

    protected abstract G refreshIfNeeded(G var1) throws IOException;

    protected abstract boolean tryIncRef(G var1) throws IOException;

    public final G acquire() throws IOException {
        G ref;
        do {
            if ((ref = this.current) != null) continue;
            throw new AlreadyClosedException(REFERENCE_MANAGER_IS_CLOSED_MSG);
        } while (!this.tryIncRef(ref));
        return ref;
    }

    @Override
    public final synchronized void close() throws IOException {
        if (this.current != null) {
            this.swapReference(null);
            this.afterClose();
        }
    }

    protected void afterClose() throws IOException {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void doMaybeRefresh() throws IOException {
        this.refreshLock.lock();
        boolean refreshed = false;
        try {
            block11: {
                G reference = this.acquire();
                try {
                    this.notifyRefreshListenersBefore();
                    G newReference = this.refreshIfNeeded(reference);
                    if (newReference == null) break block11;
                    assert (newReference != reference) : "refreshIfNeeded should return null if refresh wasn't needed";
                    try {
                        this.swapReference(newReference);
                        refreshed = true;
                    }
                    finally {
                        if (!refreshed) {
                            this.release(newReference);
                        }
                    }
                }
                finally {
                    this.release(reference);
                    this.notifyRefreshListenersRefreshed(refreshed);
                }
            }
            this.afterMaybeRefresh();
        }
        finally {
            this.refreshLock.unlock();
        }
    }

    public final boolean maybeRefresh() throws IOException {
        this.ensureOpen();
        boolean doTryRefresh = this.refreshLock.tryLock();
        if (doTryRefresh) {
            try {
                this.doMaybeRefresh();
            }
            finally {
                this.refreshLock.unlock();
            }
        }
        return doTryRefresh;
    }

    public final void maybeRefreshBlocking() throws IOException {
        this.ensureOpen();
        this.refreshLock.lock();
        try {
            this.doMaybeRefresh();
        }
        finally {
            this.refreshLock.unlock();
        }
    }

    protected void afterMaybeRefresh() throws IOException {
    }

    public final void release(G reference) throws IOException {
        assert (reference != null);
        this.decRef(reference);
    }

    private void notifyRefreshListenersBefore() throws IOException {
        for (RefreshListener refreshListener : this.refreshListeners) {
            refreshListener.beforeRefresh();
        }
    }

    private void notifyRefreshListenersRefreshed(boolean didRefresh) throws IOException {
        for (RefreshListener refreshListener : this.refreshListeners) {
            refreshListener.afterRefresh(didRefresh);
        }
    }

    public void addListener(RefreshListener listener) {
        if (listener == null) {
            throw new NullPointerException("Listener cannot be null");
        }
        this.refreshListeners.add(listener);
    }

    public void removeListener(RefreshListener listener) {
        if (listener == null) {
            throw new NullPointerException("Listener cannot be null");
        }
        this.refreshListeners.remove(listener);
    }

    public static interface RefreshListener {
        public void beforeRefresh() throws IOException;

        public void afterRefresh(boolean var1) throws IOException;
    }
}


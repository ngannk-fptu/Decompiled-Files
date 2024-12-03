/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.store.AlreadyClosedException;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Semaphore;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class ReferenceManager<G>
implements Closeable {
    private static final String REFERENCE_MANAGER_IS_CLOSED_MSG = "this ReferenceManager is closed";
    protected volatile G current;
    private final Semaphore reopenLock = new Semaphore(1);

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

    protected abstract boolean tryIncRef(G var1);

    public final G acquire() {
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
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final boolean maybeRefresh() throws IOException {
        this.ensureOpen();
        boolean doTryRefresh = this.reopenLock.tryAcquire();
        if (!doTryRefresh) return doTryRefresh;
        try {
            G reference = this.acquire();
            try {
                block8: {
                    G newReference = this.refreshIfNeeded(reference);
                    if (newReference != null) {
                        assert (newReference != reference) : "refreshIfNeeded should return null if refresh wasn't needed";
                        boolean success = false;
                        try {
                            this.swapReference(newReference);
                            success = true;
                            Object var6_5 = null;
                            if (success) break block8;
                        }
                        catch (Throwable throwable) {
                            Object var6_6 = null;
                            if (success) throw throwable;
                            this.release(newReference);
                            throw throwable;
                        }
                        this.release(newReference);
                    }
                }
                Object var8_8 = null;
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                this.release(reference);
                throw throwable;
            }
            this.release(reference);
            this.afterRefresh();
            Object var10_11 = null;
            this.reopenLock.release();
            return doTryRefresh;
        }
        catch (Throwable throwable) {
            Object var10_12 = null;
            this.reopenLock.release();
            throw throwable;
        }
    }

    protected void afterRefresh() throws IOException {
    }

    public final void release(G reference) throws IOException {
        assert (reference != null);
        this.decRef(reference);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.dispose;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.util.dispose.Disposable;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DisposalDaemon
implements Runnable {
    private static final Logger LOG = Logger.getLogger(DisposalDaemon.class.getName());
    private static ReferenceQueue<Disposable> referenceQueue = new ReferenceQueue();
    private static Set<ReferenceWrapperWithDisposerRunnable> refs = Collections.synchronizedSet(new HashSet());
    private static AtomicLong ctr = new AtomicLong(Long.MIN_VALUE);
    private static final DisposalDaemon disposalDaemon = new DisposalDaemon();
    private static final Thread disposalThread = new Thread((Runnable)disposalDaemon, "BC Disposal Daemon");

    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(){

            @Override
            public void run() {
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Shutdown hook started");
                }
                ReferenceWrapperWithDisposerRunnable item = (ReferenceWrapperWithDisposerRunnable)referenceQueue.poll();
                while (item != null) {
                    refs.remove(item);
                    item.dispose();
                    item = (ReferenceWrapperWithDisposerRunnable)referenceQueue.poll();
                    if (!LOG.isLoggable(Level.FINE)) continue;
                    LOG.fine("Shutdown hook disposed: " + item);
                }
            }
        });
    }

    public static void addDisposable(Disposable disposable) {
        ReferenceWrapperWithDisposerRunnable ref = new ReferenceWrapperWithDisposerRunnable(disposable, referenceQueue);
        refs.add(ref);
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Registered: " + disposable.toString());
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                while (true) {
                    ReferenceWrapperWithDisposerRunnable item = (ReferenceWrapperWithDisposerRunnable)referenceQueue.remove();
                    refs.remove(item);
                    item.dispose();
                    if (!LOG.isLoggable(Level.FINE)) continue;
                    LOG.fine("Disposed: " + item);
                }
            }
            catch (InterruptedException iex) {
                Thread.currentThread().interrupt();
                continue;
            }
            catch (Throwable e) {
                LOG.warning("exception in disposal thread: " + e.getMessage());
                continue;
            }
            break;
        }
    }

    static {
        disposalThread.setDaemon(true);
        disposalThread.start();
        DisposalDaemon.addShutdownHook();
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    private static class ReferenceWrapperWithDisposerRunnable
    extends PhantomReference<Disposable> {
        private final Runnable disposer;
        private final String label;

        public ReferenceWrapperWithDisposerRunnable(Disposable referent, ReferenceQueue<? super Disposable> q) {
            super(referent, q);
            this.label = referent.toString();
            this.disposer = referent.getDisposeAction();
        }

        public void dispose() {
            this.disposer.run();
        }

        public String toString() {
            return this.label;
        }
    }
}


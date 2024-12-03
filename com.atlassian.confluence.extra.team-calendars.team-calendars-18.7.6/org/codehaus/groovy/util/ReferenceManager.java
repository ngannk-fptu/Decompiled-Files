/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.util;

import java.lang.ref.ReferenceQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.codehaus.groovy.util.Reference;
import org.codehaus.groovy.util.ReferenceBundle;

public class ReferenceManager {
    private ReferenceQueue queue;

    public static ReferenceManager createThreadedManager(ReferenceQueue queue) {
        return new ThreadedReferenceManager(queue);
    }

    public static ReferenceManager createIdlingManager(ReferenceQueue queue) {
        return new ReferenceManager(queue);
    }

    public static ReferenceManager createCallBackedManager(ReferenceQueue queue) {
        return new CallBackedManager(queue);
    }

    public static ReferenceManager createThresholdedIdlingManager(final ReferenceQueue queue, final ReferenceManager callback, final int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("threshold must not be below 0.");
        }
        return new ReferenceManager(queue){
            private AtomicInteger refCnt;
            private volatile ReferenceManager manager;
            {
                super(queue2);
                this.refCnt = new AtomicInteger();
                this.manager = 1.createIdlingManager(queue);
            }

            @Override
            public void afterReferenceCreation(Reference r) {
                if (this.manager == callback) {
                    callback.afterReferenceCreation(r);
                    return;
                }
                int count = this.refCnt.incrementAndGet();
                if (count > threshold || count < 0) {
                    this.manager = callback;
                    callback.afterReferenceCreation(r);
                }
            }

            @Override
            public void removeStallEntries() {
                this.manager.removeStallEntries();
            }

            @Override
            public void stopThread() {
                this.manager.stopThread();
            }

            @Override
            public String toString() {
                return "ReferenceManager(thresholded, current manager=" + this.manager + ", threshold=" + this.refCnt.get() + "/" + threshold + ")";
            }
        };
    }

    public ReferenceManager(ReferenceQueue queue) {
        this.queue = queue;
    }

    protected ReferenceQueue getReferenceQueue() {
        return this.queue;
    }

    public void afterReferenceCreation(Reference r) {
    }

    public void removeStallEntries() {
    }

    public void stopThread() {
    }

    public String toString() {
        return "ReferenceManager(idling)";
    }

    @Deprecated
    public static ReferenceBundle getDefaultSoftBundle() {
        return ReferenceBundle.getSoftBundle();
    }

    @Deprecated
    public static ReferenceBundle getDefaultWeakBundle() {
        return ReferenceBundle.getWeakBundle();
    }

    private static class CallBackedManager
    extends ReferenceManager {
        private static final ConcurrentHashMap<ReferenceQueue, ReferenceManager> queuesInProcess = new ConcurrentHashMap(4, 0.9f, 2);

        public CallBackedManager(ReferenceQueue queue) {
            super(queue);
        }

        @Override
        public void removeStallEntries() {
            ReferenceQueue queue = this.getReferenceQueue();
            if (queuesInProcess.putIfAbsent(queue, this) == null) {
                try {
                    CallBackedManager.removeStallEntries0(queue);
                }
                finally {
                    queuesInProcess.remove(queue);
                }
            }
        }

        private static void removeStallEntries0(ReferenceQueue queue) {
            java.lang.ref.Reference r;
            while ((r = queue.poll()) != null) {
                Reference ref;
                Object holder;
                if (r instanceof Reference && (holder = (ref = (Reference)((Object)r)).getHandler()) != null) {
                    holder.finalizeReference();
                }
                r.clear();
                Object var1_1 = null;
            }
        }

        @Override
        public void afterReferenceCreation(Reference r) {
            this.removeStallEntries();
        }

        @Override
        public String toString() {
            return "ReferenceManager(callback)";
        }
    }

    private static class ThreadedReferenceManager
    extends ReferenceManager {
        private final Thread thread = new Thread(){

            @Override
            public void run() {
                ReferenceQueue queue = ThreadedReferenceManager.this.getReferenceQueue();
                java.lang.ref.Reference r = null;
                while (ThreadedReferenceManager.this.shouldRun) {
                    Reference ref;
                    Object holder;
                    try {
                        r = queue.remove(1000L);
                    }
                    catch (InterruptedException e) {
                        break;
                    }
                    if (r == null) continue;
                    if (r instanceof Reference && (holder = (ref = (Reference)((Object)r)).getHandler()) != null) {
                        holder.finalizeReference();
                    }
                    r.clear();
                    r = null;
                }
            }
        };
        private volatile boolean shouldRun = true;

        public ThreadedReferenceManager(ReferenceQueue queue) {
            super(queue);
            this.thread.setContextClassLoader(null);
            this.thread.setDaemon(true);
            this.thread.setName(ThreadedReferenceManager.class.getName());
            this.thread.start();
        }

        @Override
        public void stopThread() {
            this.shouldRun = false;
            this.thread.interrupt();
        }

        @Override
        public String toString() {
            return "ReferenceManager(threaded, thread=" + this.thread + ")";
        }
    }
}


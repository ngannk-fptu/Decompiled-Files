/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

public class CleanerThread
extends Thread {
    static volatile ReferenceQueue queue = null;
    static CleanerThread thread = null;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static ReferenceQueue getReferenceQueue() {
        if (queue != null) return queue;
        Class<CleanerThread> clazz = CleanerThread.class;
        synchronized (CleanerThread.class) {
            queue = new ReferenceQueue();
            thread = new CleanerThread();
            // ** MonitorExit[var0] (shouldn't be in output)
            return queue;
        }
    }

    protected CleanerThread() {
        super("Batik CleanerThread");
        this.setDaemon(true);
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                while (true) {
                    Reference ref;
                    try {
                        ref = queue.remove();
                    }
                    catch (InterruptedException ie) {
                        continue;
                    }
                    if (!(ref instanceof ReferenceCleared)) continue;
                    ReferenceCleared rc = (ReferenceCleared)((Object)ref);
                    rc.cleared();
                }
            }
            catch (ThreadDeath td) {
                throw td;
            }
            catch (Throwable t) {
                t.printStackTrace();
                continue;
            }
            break;
        }
    }

    public static abstract class PhantomReferenceCleared
    extends PhantomReference
    implements ReferenceCleared {
        public PhantomReferenceCleared(Object o) {
            super(o, CleanerThread.getReferenceQueue());
        }
    }

    public static abstract class WeakReferenceCleared
    extends WeakReference
    implements ReferenceCleared {
        public WeakReferenceCleared(Object o) {
            super(o, CleanerThread.getReferenceQueue());
        }
    }

    public static abstract class SoftReferenceCleared
    extends SoftReference
    implements ReferenceCleared {
        public SoftReferenceCleared(Object o) {
            super(o, CleanerThread.getReferenceQueue());
        }
    }

    public static interface ReferenceCleared {
        public void cleared();
    }
}


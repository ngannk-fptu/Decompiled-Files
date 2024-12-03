/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.startup;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

public class SafeForkJoinWorkerThreadFactory
implements ForkJoinPool.ForkJoinWorkerThreadFactory {
    @Override
    public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
        return new SafeForkJoinWorkerThread(pool);
    }

    private static class SafeForkJoinWorkerThread
    extends ForkJoinWorkerThread {
        protected SafeForkJoinWorkerThread(ForkJoinPool pool) {
            super(pool);
            this.setContextClassLoader(ForkJoinPool.class.getClassLoader());
        }
    }
}


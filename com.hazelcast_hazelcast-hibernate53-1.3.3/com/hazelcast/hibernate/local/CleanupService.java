/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.instance.OutOfMemoryErrorDispatcher
 */
package com.hazelcast.hibernate.local;

import com.hazelcast.hibernate.local.LocalRegionCache;
import com.hazelcast.instance.OutOfMemoryErrorDispatcher;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public final class CleanupService {
    private static final long DEFAULT_FIXED_DELAY = 60L;
    private final long fixedDelay;
    private final String name;
    private final ScheduledExecutorService executor;

    public CleanupService(String name) {
        this(name, 60L);
    }

    public CleanupService(String name, long fixedDelay) {
        this.fixedDelay = fixedDelay;
        this.name = name;
        this.executor = Executors.newSingleThreadScheduledExecutor(new CleanupThreadFactory());
    }

    public void registerCache(final LocalRegionCache cache) {
        this.executor.scheduleWithFixedDelay(new Runnable(){

            @Override
            public void run() {
                cache.cleanup();
            }
        }, this.fixedDelay, this.fixedDelay, TimeUnit.SECONDS);
    }

    public void stop() {
        this.executor.shutdownNow();
    }

    private static final class CleanupThread
    extends Thread {
        private CleanupThread(Runnable target, String name) {
            super(target, name);
        }

        @Override
        public void run() {
            try {
                super.run();
            }
            catch (OutOfMemoryError e) {
                OutOfMemoryErrorDispatcher.onOutOfMemory((OutOfMemoryError)e);
            }
        }
    }

    private class CleanupThreadFactory
    implements ThreadFactory {
        private CleanupThreadFactory() {
        }

        @Override
        public Thread newThread(Runnable r) {
            CleanupThread thread = new CleanupThread(r, CleanupService.this.name + ".hibernate.cleanup");
            thread.setDaemon(true);
            return thread;
        }
    }
}


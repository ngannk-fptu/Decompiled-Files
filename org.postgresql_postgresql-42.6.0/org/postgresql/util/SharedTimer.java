/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package org.postgresql.util;

import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.postgresql.jdbc.ResourceLock;
import org.postgresql.util.LazyCleaner;

public class SharedTimer {
    private static final AtomicInteger timerCount = new AtomicInteger(0);
    private static final Logger LOGGER = Logger.getLogger(SharedTimer.class.getName());
    private volatile @Nullable Timer timer;
    private final AtomicInteger refCount = new AtomicInteger(0);
    private final ResourceLock lock = new ResourceLock();
    private @Nullable LazyCleaner.Cleanable<RuntimeException> timerCleanup;

    public int getRefCount() {
        return this.refCount.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Timer getTimer() {
        try (ResourceLock ignore = this.lock.obtain();){
            Timer timer = this.timer;
            if (timer == null) {
                int index = timerCount.incrementAndGet();
                ClassLoader prevContextCL = Thread.currentThread().getContextClassLoader();
                try {
                    Thread.currentThread().setContextClassLoader(null);
                    this.timer = timer = new Timer("PostgreSQL-JDBC-SharedTimer-" + index, true);
                    this.timerCleanup = LazyCleaner.getInstance().register(this.refCount, new TimerCleanup(timer));
                }
                finally {
                    Thread.currentThread().setContextClassLoader(prevContextCL);
                }
            }
            this.refCount.incrementAndGet();
            Timer timer2 = timer;
            return timer2;
        }
    }

    public void releaseTimer() {
        try (ResourceLock ignore = this.lock.obtain();){
            int count = this.refCount.decrementAndGet();
            if (count > 0) {
                LOGGER.log(Level.FINEST, "Outstanding references still exist so not closing shared Timer");
            } else if (count == 0) {
                LOGGER.log(Level.FINEST, "No outstanding references to shared Timer, will cancel and close it");
                if (this.timerCleanup != null) {
                    this.timerCleanup.clean();
                    this.timer = null;
                    this.timerCleanup = null;
                }
            } else {
                LOGGER.log(Level.WARNING, "releaseTimer() called too many times; there is probably a bug in the calling code");
                this.refCount.set(0);
            }
        }
    }

    static class TimerCleanup
    implements LazyCleaner.CleaningAction<RuntimeException> {
        private final Timer timer;

        TimerCleanup(Timer timer) {
            this.timer = timer;
        }

        @Override
        public void onClean(boolean leak) throws RuntimeException {
            this.timer.cancel();
        }
    }
}


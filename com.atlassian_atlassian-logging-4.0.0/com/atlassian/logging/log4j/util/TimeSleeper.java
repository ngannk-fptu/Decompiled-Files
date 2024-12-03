/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  javax.annotation.Nullable
 */
package com.atlassian.logging.log4j.util;

import com.atlassian.annotations.VisibleForTesting;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

public class TimeSleeper {
    private final Object lock = new Object();
    private volatile boolean sleeping = false;
    private volatile int waitInvocationsCounter = 0;
    private PrintStream logOutputStream;

    public TimeSleeper() {
        this(System.err);
    }

    @VisibleForTesting
    TimeSleeper(@Nullable PrintStream logOutputStream) {
        this.logOutputStream = logOutputStream;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void wakeup() {
        if (!this.sleeping) {
            return;
        }
        Object object = this.lock;
        synchronized (object) {
            this.sleeping = false;
            this.lock.notifyAll();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean sleep(long time, TimeUnit timeUnit) {
        Object object = this.lock;
        synchronized (object) {
            boolean bl;
            if (this.sleeping) {
                throw new IllegalStateException("Got sleep request while already sleeping - this is not supported!");
            }
            try {
                long sleepStartTime;
                this.sleeping = true;
                this.waitInvocationsCounter = 0;
                for (long remainingSleepTimeMs = timeUnit.toMillis(time); this.sleeping && remainingSleepTimeMs > 0L; remainingSleepTimeMs -= TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - sleepStartTime)) {
                    sleepStartTime = System.nanoTime();
                    ++this.waitInvocationsCounter;
                    this.lock.wait(remainingSleepTimeMs);
                }
                bl = this.sleeping;
                this.sleeping = false;
            }
            catch (Exception e) {
                boolean bl2;
                try {
                    this.logException("Failed to keep thread asleep", e);
                    bl2 = false;
                    this.sleeping = false;
                }
                catch (Throwable throwable) {
                    this.sleeping = false;
                    throw throwable;
                }
                return bl2;
            }
            return bl;
        }
    }

    @VisibleForTesting
    void logException(String message, Exception exception) {
        if (this.logOutputStream != null) {
            this.logOutputStream.println(this.getClass().getName() + " - ERROR - " + message);
            exception.printStackTrace(this.logOutputStream);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @VisibleForTesting
    void simulateSpuriousWakeUp() {
        Object object = this.lock;
        synchronized (object) {
            this.lock.notifyAll();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @VisibleForTesting
    boolean isSleeping() {
        Object object = this.lock;
        synchronized (object) {
            return this.sleeping;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @VisibleForTesting
    int getWaitInvocationsCounter() {
        Object object = this.lock;
        synchronized (object) {
            return this.waitInvocationsCounter;
        }
    }
}


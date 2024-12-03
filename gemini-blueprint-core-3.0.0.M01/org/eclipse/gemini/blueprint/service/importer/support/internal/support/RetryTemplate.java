/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.support;

import org.eclipse.gemini.blueprint.service.importer.support.internal.support.RetryCallback;
import org.springframework.util.Assert;

public class RetryTemplate {
    private static final int hashCode = RetryTemplate.class.hashCode() * 13;
    public static final long DEFAULT_WAIT_TIME = 1000L;
    private final Object monitor = new Object();
    private final Object notificationLock;
    private long waitTime = 1000L;
    private static final long WAIT_THRESHOLD = 3L;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public RetryTemplate(long waitTime, Object notificationLock) {
        Assert.isTrue((waitTime >= 0L ? 1 : 0) != 0, (String)"waitTime must be positive");
        Assert.notNull((Object)notificationLock, (String)"notificationLock must be non null");
        Object object = this.monitor;
        synchronized (object) {
            this.waitTime = waitTime;
            this.notificationLock = notificationLock;
        }
    }

    public RetryTemplate(Object notificationLock) {
        this(1000L, notificationLock);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> T execute(RetryCallback<T> callback) {
        T result;
        long waitTime;
        Object object = this.monitor;
        synchronized (object) {
            waitTime = this.waitTime;
        }
        boolean retry = false;
        long initialStart = 0L;
        long start = 0L;
        long stop = 0L;
        long waitLeft = waitTime;
        boolean startWaiting = false;
        do {
            Object object2;
            if (callback.isComplete(result = callback.doWithRetry())) {
                if (startWaiting) {
                    this.callbackSucceeded(stop);
                }
                return result;
            }
            if (!startWaiting) {
                startWaiting = true;
                this.onMissingTarget();
                initialStart = System.currentTimeMillis();
            }
            if (waitLeft > 0L) {
                try {
                    start = System.currentTimeMillis();
                    object2 = this.notificationLock;
                    synchronized (object2) {
                        this.notificationLock.wait(waitTime);
                    }
                    stop = System.currentTimeMillis();
                    waitLeft -= stop - start;
                    stop -= initialStart;
                }
                catch (InterruptedException ex) {
                    stop = System.currentTimeMillis() - initialStart;
                    this.callbackFailed(stop);
                    throw new RuntimeException("Retry failed; interrupted while waiting", ex);
                }
            }
            retry = false;
            object2 = this.monitor;
            synchronized (object2) {
                if (waitTime != this.waitTime) {
                    retry = true;
                    waitLeft = waitTime = this.waitTime;
                }
            }
        } while (retry || waitLeft > 3L);
        result = callback.doWithRetry();
        stop = System.currentTimeMillis() - initialStart;
        if (callback.isComplete(result)) {
            this.callbackSucceeded(stop);
            return result;
        }
        this.callbackFailed(stop);
        return null;
    }

    protected void onMissingTarget() {
    }

    protected void callbackSucceeded(long stop) {
    }

    protected void callbackFailed(long stop) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reset(long waitTime) {
        Object object = this.monitor;
        synchronized (object) {
            this.waitTime = waitTime;
        }
        object = this.notificationLock;
        synchronized (object) {
            this.notificationLock.notifyAll();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long getWaitTime() {
        Object object = this.monitor;
        synchronized (object) {
            return this.waitTime;
        }
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof RetryTemplate) {
            RetryTemplate oth = (RetryTemplate)other;
            return this.getWaitTime() == oth.getWaitTime();
        }
        return false;
    }

    public int hashCode() {
        return hashCode;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.tools.ant.util.TimeoutObserver;

public class Watchdog
implements Runnable {
    public static final String ERROR_INVALID_TIMEOUT = "timeout less than 1.";
    private List<TimeoutObserver> observers = Collections.synchronizedList(new ArrayList(1));
    private long timeout = -1L;
    private volatile boolean stopped = false;

    public Watchdog(long timeout) {
        if (timeout < 1L) {
            throw new IllegalArgumentException(ERROR_INVALID_TIMEOUT);
        }
        this.timeout = timeout;
    }

    public void addTimeoutObserver(TimeoutObserver to) {
        this.observers.add(to);
    }

    public void removeTimeoutObserver(TimeoutObserver to) {
        this.observers.remove(to);
    }

    protected final void fireTimeoutOccured() {
        this.observers.forEach(o -> o.timeoutOccured(this));
    }

    public synchronized void start() {
        this.stopped = false;
        Thread t = new Thread((Runnable)this, "WATCHDOG");
        t.setDaemon(true);
        t.start();
    }

    public synchronized void stop() {
        this.stopped = true;
        this.notifyAll();
    }

    @Override
    public synchronized void run() {
        long now = System.currentTimeMillis();
        long until = now + this.timeout;
        try {
            while (!this.stopped && until > now) {
                this.wait(until - now);
                now = System.currentTimeMillis();
            }
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        if (!this.stopped) {
            this.fireTimeoutOccured();
        }
    }
}


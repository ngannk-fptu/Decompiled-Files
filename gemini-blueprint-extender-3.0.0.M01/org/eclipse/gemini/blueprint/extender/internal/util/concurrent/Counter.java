/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.eclipse.gemini.blueprint.extender.internal.util.concurrent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Counter {
    private int counter = 0;
    private static final Log log = LogFactory.getLog(Counter.class);
    private final String name;

    public Counter(String name) {
        this.name = name;
    }

    public synchronized void increment() {
        ++this.counter;
        if (log.isTraceEnabled()) {
            log.trace((Object)("counter [" + this.name + "] incremented to " + this.counter));
        }
    }

    public synchronized void decrement() {
        --this.counter;
        if (log.isTraceEnabled()) {
            log.trace((Object)("counter [" + this.name + "] decremented to " + this.counter));
        }
        this.notifyAll();
    }

    public synchronized boolean decrementAndWait(long timeToWait) {
        this.decrement();
        if (this.counter > 0) {
            return this.waitForZero(timeToWait);
        }
        return true;
    }

    public synchronized boolean isZero() {
        return this.is(0);
    }

    public synchronized boolean is(int value) {
        return this.counter == value;
    }

    public synchronized int getValue() {
        return this.counter;
    }

    public synchronized String toString() {
        return "" + this.counter;
    }

    public synchronized boolean waitForZero(long waitTime) {
        return this.waitFor(0, waitTime);
    }

    public synchronized boolean waitFor(int value, long waitTime) {
        boolean timedout = false;
        long remainingTime = waitTime;
        long startTime = System.currentTimeMillis();
        while (this.counter > value && !timedout) {
            try {
                this.wait(remainingTime);
                remainingTime = waitTime - (System.currentTimeMillis() - startTime);
                timedout = remainingTime <= 0L;
            }
            catch (InterruptedException ex) {
                timedout = true;
            }
        }
        return timedout;
    }
}


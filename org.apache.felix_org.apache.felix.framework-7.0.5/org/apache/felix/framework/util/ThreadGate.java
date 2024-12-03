/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.util;

import java.util.concurrent.TimeUnit;

public class ThreadGate {
    private boolean m_open = false;
    private Object m_msg = null;
    private boolean m_initialized = false;

    public synchronized void open() {
        this.m_open = true;
        this.notifyAll();
    }

    public synchronized Object getMessage() {
        return this.m_msg;
    }

    public synchronized void setMessage(Object msg) {
        if (!this.m_initialized) {
            this.m_msg = msg;
            this.m_initialized = true;
        }
    }

    public synchronized boolean await(long timeout) throws InterruptedException {
        long start = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        long remaining = timeout;
        while (!this.m_open) {
            this.wait(remaining);
            if (timeout <= 0L || (remaining = timeout - (TimeUnit.NANOSECONDS.toMillis(System.nanoTime()) - start)) > 0L) continue;
            break;
        }
        return this.m_open;
    }
}


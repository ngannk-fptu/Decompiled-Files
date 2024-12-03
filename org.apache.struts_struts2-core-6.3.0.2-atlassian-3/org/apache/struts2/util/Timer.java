/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.util;

public class Timer {
    long current;
    long start;

    public Timer() {
        this.start = this.current = System.currentTimeMillis();
    }

    public long getTime() {
        long now = System.currentTimeMillis();
        long time = now - this.current;
        this.current = now;
        return time;
    }

    public long getTotal() {
        return System.currentTimeMillis() - this.start;
    }
}


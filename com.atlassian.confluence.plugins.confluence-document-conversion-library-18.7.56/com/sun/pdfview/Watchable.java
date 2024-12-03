/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

public interface Watchable {
    public static final int UNKNOWN = 0;
    public static final int NOT_STARTED = 1;
    public static final int PAUSED = 2;
    public static final int NEEDS_DATA = 3;
    public static final int RUNNING = 4;
    public static final int STOPPED = 5;
    public static final int COMPLETED = 6;
    public static final int ERROR = 7;

    public int getStatus();

    public void stop();

    public void go();

    public void go(int var1);

    public void go(long var1);
}


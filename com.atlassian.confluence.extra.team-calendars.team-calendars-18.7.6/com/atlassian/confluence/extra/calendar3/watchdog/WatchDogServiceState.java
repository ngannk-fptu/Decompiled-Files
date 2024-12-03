/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.watchdog;

public enum WatchDogServiceState {
    NOT_RUNNING(0),
    RUNNING(1);

    private int state;

    private WatchDogServiceState(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }
}


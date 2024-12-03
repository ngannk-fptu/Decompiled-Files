/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration;

public interface StatusProvider {
    public RunningStatus getStatus();

    public static enum RunningStatus {
        RUNNING,
        NOT_RUNNING;

    }
}


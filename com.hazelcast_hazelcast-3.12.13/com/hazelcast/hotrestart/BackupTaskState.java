/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.hotrestart;

public enum BackupTaskState {
    NO_TASK,
    NOT_STARTED,
    IN_PROGRESS,
    FAILURE,
    SUCCESS;


    public boolean isDone() {
        return this == SUCCESS || this == FAILURE;
    }

    public boolean inProgress() {
        return this == NOT_STARTED || this == IN_PROGRESS;
    }
}


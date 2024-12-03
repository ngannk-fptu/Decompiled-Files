/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.model.backuprestore;

public enum JobState {
    QUEUED(true),
    PROCESSING(true),
    COMPLETING(true),
    FINISHED,
    CANCELLING,
    CANCELLED,
    FAILED;

    final boolean cancellable;

    private JobState() {
        this.cancellable = false;
    }

    private JobState(boolean cancellable) {
        this.cancellable = cancellable;
    }

    public boolean isCancellable() {
        return this.cancellable;
    }

    private static class Constants {
        public static final boolean CANCELLABLE = true;

        private Constants() {
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer;

public enum PauseStatus {
    SUCCESS,
    NOT_STARTED,
    CANCELLED_BEFORE_START,
    NO_EFFECT,
    CANCELLED;


    public boolean isPaused() {
        return this == SUCCESS;
    }

    public boolean isCancelled() {
        return this == CANCELLED || this == CANCELLED_BEFORE_START;
    }

    public boolean unchanged() {
        return this == NOT_STARTED || this == NO_EFFECT;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer;

import com.amazonaws.services.s3.transfer.PauseStatus;

public final class PauseResult<T> {
    private final PauseStatus pauseStatus;
    private final T infoToResume;

    public PauseResult(PauseStatus pauseStatus, T infoToResume) {
        if (pauseStatus == null) {
            throw new IllegalArgumentException();
        }
        this.pauseStatus = pauseStatus;
        this.infoToResume = infoToResume;
    }

    public PauseResult(PauseStatus pauseStatus) {
        this(pauseStatus, null);
    }

    public PauseStatus getPauseStatus() {
        return this.pauseStatus;
    }

    public T getInfoToResume() {
        return this.infoToResume;
    }
}


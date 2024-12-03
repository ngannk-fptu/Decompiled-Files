/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.socket.handler;

import org.springframework.lang.Nullable;
import org.springframework.web.socket.CloseStatus;

public class SessionLimitExceededException
extends RuntimeException {
    private final CloseStatus status;

    public SessionLimitExceededException(String message, @Nullable CloseStatus status) {
        super(message);
        this.status = status != null ? status : CloseStatus.NO_STATUS_CODE;
    }

    public CloseStatus getStatus() {
        return this.status;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.NestedRuntimeException
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.socket.sockjs;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

public class SockJsException
extends NestedRuntimeException {
    @Nullable
    private final String sessionId;

    public SockJsException(String message, @Nullable Throwable cause) {
        this(message, null, cause);
    }

    public SockJsException(String message, @Nullable String sessionId, @Nullable Throwable cause) {
        super(message, cause);
        this.sessionId = sessionId;
    }

    @Nullable
    public String getSockJsSessionId() {
        return this.sessionId;
    }
}


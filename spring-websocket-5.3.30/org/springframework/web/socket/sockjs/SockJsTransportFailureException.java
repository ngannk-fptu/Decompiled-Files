/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.socket.sockjs;

import org.springframework.lang.Nullable;
import org.springframework.web.socket.sockjs.SockJsException;

public class SockJsTransportFailureException
extends SockJsException {
    public SockJsTransportFailureException(String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    public SockJsTransportFailureException(String message, String sessionId, @Nullable Throwable cause) {
        super(message, sessionId, cause);
    }
}


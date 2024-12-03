/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.NestedRuntimeException
 */
package org.springframework.web.socket.server;

import org.springframework.core.NestedRuntimeException;

public class HandshakeFailureException
extends NestedRuntimeException {
    public HandshakeFailureException(String message) {
        super(message);
    }

    public HandshakeFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.PlatformDependent
 */
package io.netty.handler.timeout;

import io.netty.handler.timeout.TimeoutException;
import io.netty.util.internal.PlatformDependent;

public final class WriteTimeoutException
extends TimeoutException {
    private static final long serialVersionUID = -144786655770296065L;
    public static final WriteTimeoutException INSTANCE = PlatformDependent.javaVersion() >= 7 ? new WriteTimeoutException(true) : new WriteTimeoutException();

    public WriteTimeoutException() {
    }

    public WriteTimeoutException(String message) {
        super(message, false);
    }

    private WriteTimeoutException(boolean shared) {
        super(null, shared);
    }
}


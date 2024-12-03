/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelException
 */
package io.netty.handler.timeout;

import io.netty.channel.ChannelException;

public class TimeoutException
extends ChannelException {
    private static final long serialVersionUID = 4673641882869672533L;

    TimeoutException() {
    }

    TimeoutException(String message, boolean shared) {
        super(message, null, shared);
    }

    public Throwable fillInStackTrace() {
        return this;
    }
}


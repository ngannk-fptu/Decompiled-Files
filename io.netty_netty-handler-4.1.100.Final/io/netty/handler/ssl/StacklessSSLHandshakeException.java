/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.ThrowableUtil
 */
package io.netty.handler.ssl;

import io.netty.util.internal.ThrowableUtil;
import javax.net.ssl.SSLHandshakeException;

final class StacklessSSLHandshakeException
extends SSLHandshakeException {
    private static final long serialVersionUID = -1244781947804415549L;

    private StacklessSSLHandshakeException(String reason) {
        super(reason);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

    static StacklessSSLHandshakeException newInstance(String reason, Class<?> clazz, String method) {
        return (StacklessSSLHandshakeException)ThrowableUtil.unknownStackTrace((Throwable)new StacklessSSLHandshakeException(reason), clazz, (String)method);
    }
}


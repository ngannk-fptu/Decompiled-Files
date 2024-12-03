/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting;

import org.springframework.remoting.RemoteAccessException;

public class RemoteProxyFailureException
extends RemoteAccessException {
    public RemoteProxyFailureException(String msg, Throwable cause) {
        super(msg, cause);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting;

import org.springframework.remoting.RemoteAccessException;

public class RemoteTimeoutException
extends RemoteAccessException {
    public RemoteTimeoutException(String msg) {
        super(msg);
    }

    public RemoteTimeoutException(String msg, Throwable cause) {
        super(msg, cause);
    }
}


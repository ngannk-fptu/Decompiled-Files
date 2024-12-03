/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting;

import org.springframework.remoting.RemoteAccessException;

public class RemoteInvocationFailureException
extends RemoteAccessException {
    public RemoteInvocationFailureException(String msg, Throwable cause) {
        super(msg, cause);
    }
}


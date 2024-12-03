/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.rpc;

import com.atlassian.confluence.rpc.RemoteException;

public class AuthenticationFailedException
extends RemoteException {
    public AuthenticationFailedException() {
    }

    public AuthenticationFailedException(String message) {
        super(message);
    }

    public AuthenticationFailedException(Throwable cause) {
        super(cause);
    }

    public AuthenticationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}


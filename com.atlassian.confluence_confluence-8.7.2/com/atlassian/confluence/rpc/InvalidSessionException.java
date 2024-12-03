/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.rpc;

import com.atlassian.confluence.rpc.RemoteException;

public class InvalidSessionException
extends RemoteException {
    public InvalidSessionException() {
    }

    public InvalidSessionException(String message) {
        super(message);
    }

    public InvalidSessionException(Throwable cause) {
        super(cause);
    }

    public InvalidSessionException(String message, Throwable cause) {
        super(message, cause);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.rpc;

import com.atlassian.confluence.rpc.RemoteException;

public class NotPermittedException
extends RemoteException {
    public NotPermittedException() {
    }

    public NotPermittedException(String message) {
        super(message);
    }

    public NotPermittedException(Throwable cause) {
        super(cause);
    }

    public NotPermittedException(String message, Throwable cause) {
        super(message, cause);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.rpc;

import com.atlassian.confluence.rpc.RemoteException;

public final class NotFoundException
extends RemoteException {
    public NotFoundException() {
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}


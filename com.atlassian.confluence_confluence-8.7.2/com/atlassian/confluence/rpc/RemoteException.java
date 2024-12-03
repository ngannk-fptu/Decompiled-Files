/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.rpc;

public class RemoteException
extends Exception {
    public RemoteException() {
    }

    public RemoteException(String message) {
        super(message);
    }

    public RemoteException(Throwable cause) {
        super(cause);
    }

    public RemoteException(String message, Throwable cause) {
        super(message, cause);
    }
}


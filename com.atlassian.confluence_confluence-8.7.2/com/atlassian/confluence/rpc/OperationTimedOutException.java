/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.rpc;

import com.atlassian.confluence.rpc.RemoteException;

public class OperationTimedOutException
extends RemoteException {
    public OperationTimedOutException() {
    }

    public OperationTimedOutException(String message) {
        super(message);
    }

    public OperationTimedOutException(Throwable cause) {
        super(cause);
    }

    public OperationTimedOutException(String message, Throwable cause) {
        super(message, cause);
    }
}


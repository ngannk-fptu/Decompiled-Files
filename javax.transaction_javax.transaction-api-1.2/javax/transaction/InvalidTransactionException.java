/*
 * Decompiled with CFR 0.152.
 */
package javax.transaction;

import java.rmi.RemoteException;

public class InvalidTransactionException
extends RemoteException {
    public InvalidTransactionException() {
    }

    public InvalidTransactionException(String msg) {
        super(msg);
    }
}


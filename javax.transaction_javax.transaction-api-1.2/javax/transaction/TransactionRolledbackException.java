/*
 * Decompiled with CFR 0.152.
 */
package javax.transaction;

import java.rmi.RemoteException;

public class TransactionRolledbackException
extends RemoteException {
    public TransactionRolledbackException() {
    }

    public TransactionRolledbackException(String msg) {
        super(msg);
    }
}


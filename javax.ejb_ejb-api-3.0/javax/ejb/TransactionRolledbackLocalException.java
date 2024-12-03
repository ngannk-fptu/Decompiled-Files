/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import javax.ejb.EJBException;

public class TransactionRolledbackLocalException
extends EJBException {
    public TransactionRolledbackLocalException() {
    }

    public TransactionRolledbackLocalException(String message) {
        super(message);
    }

    public TransactionRolledbackLocalException(String message, Exception ex) {
        super(message, ex);
    }
}


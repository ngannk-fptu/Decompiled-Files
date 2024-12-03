/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import javax.ejb.EJBException;

public class EJBTransactionRolledbackException
extends EJBException {
    public EJBTransactionRolledbackException() {
    }

    public EJBTransactionRolledbackException(String message) {
        super(message);
    }

    public EJBTransactionRolledbackException(String message, Exception ex) {
        super(message, ex);
    }
}


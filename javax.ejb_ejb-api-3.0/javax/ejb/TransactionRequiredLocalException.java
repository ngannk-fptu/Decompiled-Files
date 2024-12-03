/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import javax.ejb.EJBException;

public class TransactionRequiredLocalException
extends EJBException {
    public TransactionRequiredLocalException() {
    }

    public TransactionRequiredLocalException(String message) {
        super(message);
    }
}


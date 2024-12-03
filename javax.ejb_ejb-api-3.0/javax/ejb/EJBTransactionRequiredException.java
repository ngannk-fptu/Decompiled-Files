/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import javax.ejb.EJBException;

public class EJBTransactionRequiredException
extends EJBException {
    public EJBTransactionRequiredException() {
    }

    public EJBTransactionRequiredException(String message) {
        super(message);
    }
}


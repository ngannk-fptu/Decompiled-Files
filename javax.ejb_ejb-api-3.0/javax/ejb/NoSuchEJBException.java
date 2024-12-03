/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import javax.ejb.EJBException;

public class NoSuchEJBException
extends EJBException {
    public NoSuchEJBException() {
    }

    public NoSuchEJBException(String message) {
        super(message);
    }

    public NoSuchEJBException(String message, Exception ex) {
        super(message, ex);
    }
}


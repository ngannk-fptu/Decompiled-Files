/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import javax.ejb.EJBException;

public class NoSuchObjectLocalException
extends EJBException {
    public NoSuchObjectLocalException() {
    }

    public NoSuchObjectLocalException(String message) {
        super(message);
    }

    public NoSuchObjectLocalException(String message, Exception ex) {
        super(message, ex);
    }
}


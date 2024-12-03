/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import javax.ejb.EJBException;

public class ConcurrentAccessException
extends EJBException {
    public ConcurrentAccessException() {
    }

    public ConcurrentAccessException(String message) {
        super(message);
    }

    public ConcurrentAccessException(String message, Exception ex) {
        super(message, ex);
    }
}


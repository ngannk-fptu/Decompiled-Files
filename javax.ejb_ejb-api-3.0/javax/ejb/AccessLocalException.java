/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import javax.ejb.EJBException;

public class AccessLocalException
extends EJBException {
    public AccessLocalException() {
    }

    public AccessLocalException(String message) {
        super(message);
    }

    public AccessLocalException(String message, Exception ex) {
        super(message, ex);
    }
}


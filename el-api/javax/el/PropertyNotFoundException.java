/*
 * Decompiled with CFR 0.152.
 */
package javax.el;

import javax.el.ELException;

public class PropertyNotFoundException
extends ELException {
    private static final long serialVersionUID = -3799200961303506745L;

    public PropertyNotFoundException() {
    }

    public PropertyNotFoundException(String message) {
        super(message);
    }

    public PropertyNotFoundException(Throwable cause) {
        super(cause);
    }

    public PropertyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}


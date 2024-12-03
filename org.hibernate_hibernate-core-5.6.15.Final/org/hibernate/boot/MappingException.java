/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot;

import org.hibernate.boot.jaxb.Origin;

public class MappingException
extends org.hibernate.MappingException {
    private final Origin origin;

    public MappingException(String message, Origin origin) {
        super(message);
        this.origin = origin;
    }

    public MappingException(String message, Throwable root, Origin origin) {
        super(message, root);
        this.origin = origin;
    }

    public String getMessage() {
        String message = super.getMessage();
        if (this.origin != null) {
            message = message + " : origin(" + this.origin.getName() + ")";
        }
        return message;
    }

    public Origin getOrigin() {
        return this.origin;
    }
}


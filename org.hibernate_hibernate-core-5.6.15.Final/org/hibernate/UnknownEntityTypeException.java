/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import org.hibernate.MappingException;

public class UnknownEntityTypeException
extends MappingException {
    public UnknownEntityTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownEntityTypeException(String message) {
        super(message);
    }
}


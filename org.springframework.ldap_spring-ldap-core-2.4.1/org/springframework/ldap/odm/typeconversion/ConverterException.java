/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.odm.typeconversion;

import org.springframework.ldap.NamingException;

public final class ConverterException
extends NamingException {
    public ConverterException(String message) {
        super(message);
    }

    public ConverterException(String message, Throwable e) {
        super(message, e);
    }
}


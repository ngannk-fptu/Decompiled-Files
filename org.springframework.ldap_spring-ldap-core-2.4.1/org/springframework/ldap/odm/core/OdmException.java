/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.odm.core;

import org.springframework.ldap.NamingException;

public class OdmException
extends NamingException {
    public OdmException(String message) {
        super(message);
    }

    public OdmException(String message, Throwable e) {
        super(message, e);
    }
}


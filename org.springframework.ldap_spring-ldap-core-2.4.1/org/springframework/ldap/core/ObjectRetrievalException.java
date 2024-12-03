/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core;

import org.springframework.ldap.NamingException;

public class ObjectRetrievalException
extends NamingException {
    public ObjectRetrievalException(String msg) {
        super(msg);
    }

    public ObjectRetrievalException(String msg, Throwable cause) {
        super(msg, cause);
    }
}


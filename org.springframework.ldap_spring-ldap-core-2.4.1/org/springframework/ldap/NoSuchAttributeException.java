/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class NoSuchAttributeException
extends NamingException {
    public NoSuchAttributeException(String message) {
        super(message);
    }

    public NoSuchAttributeException(javax.naming.directory.NoSuchAttributeException cause) {
        super(cause);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class InvalidSearchFilterException
extends NamingException {
    public InvalidSearchFilterException(javax.naming.directory.InvalidSearchFilterException cause) {
        super(cause);
    }
}


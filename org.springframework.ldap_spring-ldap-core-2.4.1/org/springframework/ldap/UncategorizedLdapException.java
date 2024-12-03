/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class UncategorizedLdapException
extends NamingException {
    public UncategorizedLdapException(String msg) {
        super(msg);
    }

    public UncategorizedLdapException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UncategorizedLdapException(Throwable cause) {
        super("Uncategorized exception occured during LDAP processing", cause);
    }
}


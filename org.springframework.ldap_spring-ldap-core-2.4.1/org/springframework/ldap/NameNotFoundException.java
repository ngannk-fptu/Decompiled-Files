/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class NameNotFoundException
extends NamingException {
    public NameNotFoundException(String msg) {
        super(msg);
    }

    public NameNotFoundException(javax.naming.NameNotFoundException cause) {
        super(cause);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class NamingSecurityException
extends NamingException {
    public NamingSecurityException(javax.naming.NamingSecurityException cause) {
        super(cause);
    }
}


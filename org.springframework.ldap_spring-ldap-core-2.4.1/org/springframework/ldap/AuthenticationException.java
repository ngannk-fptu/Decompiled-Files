/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingSecurityException;

public class AuthenticationException
extends NamingSecurityException {
    public AuthenticationException(javax.naming.AuthenticationException cause) {
        super(cause);
    }

    public AuthenticationException() {
        this((javax.naming.AuthenticationException)null);
    }
}


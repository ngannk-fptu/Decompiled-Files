/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingSecurityException;

public class AuthenticationNotSupportedException
extends NamingSecurityException {
    public AuthenticationNotSupportedException(javax.naming.AuthenticationNotSupportedException cause) {
        super(cause);
    }
}


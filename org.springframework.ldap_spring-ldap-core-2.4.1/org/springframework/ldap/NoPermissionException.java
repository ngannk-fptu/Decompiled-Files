/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingSecurityException;

public class NoPermissionException
extends NamingSecurityException {
    public NoPermissionException(javax.naming.NoPermissionException cause) {
        super(cause);
    }
}


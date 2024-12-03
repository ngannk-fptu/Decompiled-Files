/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class NoInitialContextException
extends NamingException {
    public NoInitialContextException(javax.naming.NoInitialContextException cause) {
        super(cause);
    }
}


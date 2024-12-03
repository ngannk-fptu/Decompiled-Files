/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class ContextNotEmptyException
extends NamingException {
    public ContextNotEmptyException(javax.naming.ContextNotEmptyException cause) {
        super(cause);
    }
}


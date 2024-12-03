/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class PartialResultException
extends NamingException {
    public PartialResultException(javax.naming.PartialResultException cause) {
        super(cause);
    }
}


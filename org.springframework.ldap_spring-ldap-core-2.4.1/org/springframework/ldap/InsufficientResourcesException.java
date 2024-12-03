/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class InsufficientResourcesException
extends NamingException {
    public InsufficientResourcesException(javax.naming.InsufficientResourcesException cause) {
        super(cause);
    }
}


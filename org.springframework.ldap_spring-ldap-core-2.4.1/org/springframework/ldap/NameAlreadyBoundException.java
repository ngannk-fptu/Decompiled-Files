/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class NameAlreadyBoundException
extends NamingException {
    public NameAlreadyBoundException(javax.naming.NameAlreadyBoundException cause) {
        super(cause);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class InvalidNameException
extends NamingException {
    public InvalidNameException(javax.naming.InvalidNameException cause) {
        super(cause);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class InvalidAttributesException
extends NamingException {
    public InvalidAttributesException(javax.naming.directory.InvalidAttributesException cause) {
        super(cause);
    }
}


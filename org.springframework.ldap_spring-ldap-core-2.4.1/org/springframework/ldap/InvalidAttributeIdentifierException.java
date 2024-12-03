/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class InvalidAttributeIdentifierException
extends NamingException {
    public InvalidAttributeIdentifierException(javax.naming.directory.InvalidAttributeIdentifierException cause) {
        super(cause);
    }
}


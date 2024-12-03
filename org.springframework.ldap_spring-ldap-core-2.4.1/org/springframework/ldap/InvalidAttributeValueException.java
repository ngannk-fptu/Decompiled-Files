/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class InvalidAttributeValueException
extends NamingException {
    public InvalidAttributeValueException(javax.naming.directory.InvalidAttributeValueException cause) {
        super(cause);
    }
}


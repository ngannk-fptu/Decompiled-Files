/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class AttributeInUseException
extends NamingException {
    public AttributeInUseException(javax.naming.directory.AttributeInUseException cause) {
        super(cause);
    }
}


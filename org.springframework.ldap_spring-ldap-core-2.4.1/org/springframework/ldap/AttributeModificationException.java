/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class AttributeModificationException
extends NamingException {
    public AttributeModificationException(javax.naming.directory.AttributeModificationException cause) {
        super(cause);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class SchemaViolationException
extends NamingException {
    public SchemaViolationException(javax.naming.directory.SchemaViolationException cause) {
        super(cause);
    }
}


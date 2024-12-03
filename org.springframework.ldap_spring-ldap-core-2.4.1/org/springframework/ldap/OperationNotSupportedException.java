/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class OperationNotSupportedException
extends NamingException {
    public OperationNotSupportedException(javax.naming.OperationNotSupportedException cause) {
        super(cause);
    }
}


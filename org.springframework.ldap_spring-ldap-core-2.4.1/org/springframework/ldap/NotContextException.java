/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class NotContextException
extends NamingException {
    public NotContextException(javax.naming.NotContextException cause) {
        super(cause);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class InterruptedNamingException
extends NamingException {
    public InterruptedNamingException(javax.naming.InterruptedNamingException cause) {
        super(cause);
    }
}


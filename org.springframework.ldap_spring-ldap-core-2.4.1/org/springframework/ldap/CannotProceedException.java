/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class CannotProceedException
extends NamingException {
    public CannotProceedException(javax.naming.CannotProceedException cause) {
        super(cause);
    }
}


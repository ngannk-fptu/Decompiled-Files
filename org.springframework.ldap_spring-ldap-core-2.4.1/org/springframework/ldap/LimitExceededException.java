/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class LimitExceededException
extends NamingException {
    public LimitExceededException(javax.naming.LimitExceededException cause) {
        super(cause);
    }
}


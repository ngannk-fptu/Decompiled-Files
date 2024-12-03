/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class InvalidSearchControlsException
extends NamingException {
    public InvalidSearchControlsException(javax.naming.directory.InvalidSearchControlsException cause) {
        super(cause);
    }
}


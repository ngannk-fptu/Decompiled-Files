/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.ldap.repository;

import com.atlassian.user.impl.RepositoryException;

public class LdapConnectionFailedException
extends RepositoryException {
    public LdapConnectionFailedException() {
    }

    public LdapConnectionFailedException(String message) {
        super(message);
    }

    public LdapConnectionFailedException(Throwable cause) {
        super(cause);
    }

    public LdapConnectionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}


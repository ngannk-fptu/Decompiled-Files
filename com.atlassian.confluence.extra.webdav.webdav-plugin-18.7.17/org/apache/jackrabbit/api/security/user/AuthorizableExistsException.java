/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.api.security.user;

import javax.jcr.RepositoryException;

public class AuthorizableExistsException
extends RepositoryException {
    private static final long serialVersionUID = 7875416346848889564L;

    public AuthorizableExistsException(String msg) {
        super(msg);
    }
}


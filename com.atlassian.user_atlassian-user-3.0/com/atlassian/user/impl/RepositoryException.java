/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl;

import com.atlassian.user.EntityException;

public class RepositoryException
extends EntityException {
    public RepositoryException() {
    }

    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(Throwable cause) {
        super(cause);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}


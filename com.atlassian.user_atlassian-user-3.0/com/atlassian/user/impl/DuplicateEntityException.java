/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl;

import com.atlassian.user.EntityException;

public class DuplicateEntityException
extends EntityException {
    public DuplicateEntityException() {
    }

    public DuplicateEntityException(String message) {
        super(message);
    }

    public DuplicateEntityException(Throwable cause) {
        super(cause);
    }

    public DuplicateEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}


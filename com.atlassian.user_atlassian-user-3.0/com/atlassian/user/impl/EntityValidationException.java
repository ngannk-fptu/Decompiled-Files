/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl;

import com.atlassian.user.EntityException;

public class EntityValidationException
extends EntityException {
    public EntityValidationException() {
    }

    public EntityValidationException(String message) {
        super(message);
    }

    public EntityValidationException(Throwable cause) {
        super(cause);
    }

    public EntityValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}


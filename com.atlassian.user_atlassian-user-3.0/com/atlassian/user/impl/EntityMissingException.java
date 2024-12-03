/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl;

import com.atlassian.user.EntityException;

public class EntityMissingException
extends EntityException {
    public EntityMissingException() {
    }

    public EntityMissingException(String message) {
        super(message);
    }

    public EntityMissingException(Throwable cause) {
        super(cause);
    }

    public EntityMissingException(String message, Throwable cause) {
        super(message, cause);
    }
}


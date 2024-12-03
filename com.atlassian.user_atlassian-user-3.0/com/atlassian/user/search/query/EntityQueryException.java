/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.search.query;

import com.atlassian.user.EntityException;

public class EntityQueryException
extends EntityException {
    public EntityQueryException() {
    }

    public EntityQueryException(String message) {
        super(message);
    }

    public EntityQueryException(Throwable cause) {
        super(cause);
    }

    public EntityQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.impl.web.usercontext;

import java.util.UUID;

public abstract class IdentifiableRuntimeException
extends RuntimeException {
    private final UUID uuid = UUID.randomUUID();

    protected IdentifiableRuntimeException() {
    }

    protected IdentifiableRuntimeException(String message) {
        super(message);
    }

    protected IdentifiableRuntimeException(Throwable cause) {
        super(cause);
    }

    protected IdentifiableRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UUID getUuid() {
        return this.uuid;
    }
}


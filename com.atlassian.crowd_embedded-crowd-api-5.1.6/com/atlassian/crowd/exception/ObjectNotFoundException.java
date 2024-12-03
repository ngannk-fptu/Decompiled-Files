/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.CrowdException;

public class ObjectNotFoundException
extends CrowdException {
    public ObjectNotFoundException() {
    }

    public ObjectNotFoundException(Class entityClass, Object identifier) {
        super(new StringBuilder(64).append("Failed to find entity of type [").append(entityClass.getCanonicalName()).append("] with identifier [").append(identifier).append("]").toString());
    }

    public ObjectNotFoundException(String message) {
        super(message);
    }

    public ObjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectNotFoundException(Throwable throwable) {
        super(throwable);
    }
}


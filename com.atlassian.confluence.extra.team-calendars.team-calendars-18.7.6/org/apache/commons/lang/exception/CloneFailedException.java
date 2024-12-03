/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang.exception;

import org.apache.commons.lang.exception.NestableRuntimeException;

public class CloneFailedException
extends NestableRuntimeException {
    private static final long serialVersionUID = 20091223L;

    public CloneFailedException(String message) {
        super(message);
    }

    public CloneFailedException(Throwable cause) {
        super(cause);
    }

    public CloneFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}


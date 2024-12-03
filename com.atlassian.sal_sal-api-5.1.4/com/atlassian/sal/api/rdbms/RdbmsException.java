/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.rdbms;

public class RdbmsException
extends RuntimeException {
    public RdbmsException(String message) {
        super(message);
    }

    public RdbmsException(String message, Throwable cause) {
        super(message, cause);
    }

    public RdbmsException(Throwable cause) {
        super(cause);
    }
}


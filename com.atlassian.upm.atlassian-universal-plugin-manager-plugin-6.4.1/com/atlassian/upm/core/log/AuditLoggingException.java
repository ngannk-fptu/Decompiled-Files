/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.log;

public class AuditLoggingException
extends RuntimeException {
    public AuditLoggingException(String message) {
        super(message);
    }

    public AuditLoggingException(String message, Throwable cause) {
        super(message, cause);
    }
}


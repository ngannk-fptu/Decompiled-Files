/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.access;

public class AccessDeniedException
extends RuntimeException {
    public AccessDeniedException(String msg) {
        super(msg);
    }

    public AccessDeniedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}


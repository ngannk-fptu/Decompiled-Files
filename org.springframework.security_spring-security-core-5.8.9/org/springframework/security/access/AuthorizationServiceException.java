/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.access;

import org.springframework.security.access.AccessDeniedException;

public class AuthorizationServiceException
extends AccessDeniedException {
    public AuthorizationServiceException(String msg) {
        super(msg);
    }

    public AuthorizationServiceException(String msg, Throwable cause) {
        super(msg, cause);
    }
}


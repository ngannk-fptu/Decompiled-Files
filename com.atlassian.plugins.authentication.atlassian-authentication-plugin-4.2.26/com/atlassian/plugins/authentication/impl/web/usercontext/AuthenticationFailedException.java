/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.impl.web.usercontext;

import com.atlassian.plugins.authentication.impl.web.usercontext.IdentifiableRuntimeException;

public class AuthenticationFailedException
extends IdentifiableRuntimeException {
    public AuthenticationFailedException(String message) {
        super(message);
    }

    public AuthenticationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}


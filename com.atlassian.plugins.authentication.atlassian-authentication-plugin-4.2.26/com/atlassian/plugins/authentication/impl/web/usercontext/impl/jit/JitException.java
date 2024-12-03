/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit;

import com.atlassian.plugins.authentication.impl.web.usercontext.AuthenticationFailedException;

public class JitException
extends AuthenticationFailedException {
    public JitException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public JitException(String message) {
        super(message);
    }
}


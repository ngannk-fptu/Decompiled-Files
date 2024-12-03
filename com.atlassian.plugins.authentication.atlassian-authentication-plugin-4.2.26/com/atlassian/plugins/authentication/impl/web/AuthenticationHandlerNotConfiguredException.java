/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.impl.web;

import com.atlassian.plugins.authentication.impl.web.usercontext.IdentifiableRuntimeException;

public class AuthenticationHandlerNotConfiguredException
extends IdentifiableRuntimeException {
    public AuthenticationHandlerNotConfiguredException(String message) {
        super(message);
    }

    public AuthenticationHandlerNotConfiguredException(String msg, Exception e) {
        super(msg, e);
    }
}


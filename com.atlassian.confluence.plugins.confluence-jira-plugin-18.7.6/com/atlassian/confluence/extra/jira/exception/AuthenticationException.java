/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.jira.exception;

import java.net.ProtocolException;

public class AuthenticationException
extends ProtocolException {
    public AuthenticationException() {
    }

    public AuthenticationException(String message) {
        super(message);
    }
}


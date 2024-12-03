/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.FailedAuthenticationException;

public class InactiveAccountException
extends FailedAuthenticationException {
    private final String name;

    public InactiveAccountException(String name) {
        this(name, null);
    }

    public InactiveAccountException(String name, Throwable e) {
        super(String.format("Account with name <%s> is inactive", name), e);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}


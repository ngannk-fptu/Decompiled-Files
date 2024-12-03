/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.FailedAuthenticationException;

public class AccountNotFoundException
extends FailedAuthenticationException {
    private final String name;

    public AccountNotFoundException(String name) {
        this(name, null);
    }

    public AccountNotFoundException(String name, Throwable e) {
        super(String.format("Account with name <%s> could not be found", name), e);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}


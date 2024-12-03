/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.ObjectNotFoundException;

public class UserNotFoundException
extends ObjectNotFoundException {
    private final String userName;

    public UserNotFoundException(String userName) {
        this(userName, null);
    }

    public UserNotFoundException(String userName, Throwable t) {
        super(String.format("User <%s> does not exist", userName), t);
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }

    public static void throwNotFoundByExternalId(String externalId) throws UserNotFoundException {
        throw UserNotFoundException.forExternalId(externalId);
    }

    public static UserNotFoundException forExternalId(String externalId) {
        return new UserNotFoundException("externalId=" + externalId);
    }
}


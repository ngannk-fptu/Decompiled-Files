/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.exception.CrowdException;

public class InvalidUserException
extends CrowdException {
    private final User user;

    public InvalidUserException(User user, String message) {
        super(message);
        this.user = user;
    }

    public InvalidUserException(User user, Throwable cause) {
        super(cause);
        this.user = user;
    }

    public InvalidUserException(User user, String message, Throwable cause) {
        super(message, cause);
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }
}


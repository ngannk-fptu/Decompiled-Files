/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception.runtime;

import com.atlassian.crowd.exception.runtime.CrowdRuntimeException;

public class UserNotFoundException
extends CrowdRuntimeException {
    private final String userName;

    public UserNotFoundException(String userName) {
        this(userName, null);
    }

    public UserNotFoundException(String userName, Throwable t) {
        super("User <" + userName + "> does not exist", t);
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }
}


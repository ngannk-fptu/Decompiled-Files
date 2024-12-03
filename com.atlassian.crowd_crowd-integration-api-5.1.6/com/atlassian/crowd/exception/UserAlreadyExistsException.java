/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.ObjectAlreadyExistsException;

public final class UserAlreadyExistsException
extends ObjectAlreadyExistsException {
    private final long directoryId;
    private final String userName;

    public UserAlreadyExistsException(long directoryId, String name) {
        super("User already exists in directory [" + directoryId + "] with name [" + name + "]");
        this.directoryId = directoryId;
        this.userName = name;
    }

    public long getDirectoryId() {
        return this.directoryId;
    }

    public String getUserName() {
        return this.userName;
    }
}


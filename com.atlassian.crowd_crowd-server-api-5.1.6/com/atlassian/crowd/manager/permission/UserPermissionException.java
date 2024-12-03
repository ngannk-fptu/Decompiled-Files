/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.permission;

public class UserPermissionException
extends RuntimeException {
    public UserPermissionException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public UserPermissionException(String s) {
        super(s);
    }
}


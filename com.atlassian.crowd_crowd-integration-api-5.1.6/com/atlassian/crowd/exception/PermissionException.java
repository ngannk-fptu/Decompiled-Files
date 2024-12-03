/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

public abstract class PermissionException
extends Exception {
    public PermissionException() {
    }

    public PermissionException(String s) {
        super(s);
    }

    public PermissionException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public PermissionException(Throwable throwable) {
        super(throwable);
    }
}


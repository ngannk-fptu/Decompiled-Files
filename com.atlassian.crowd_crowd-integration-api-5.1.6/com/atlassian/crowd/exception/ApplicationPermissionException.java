/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.PermissionException;

public class ApplicationPermissionException
extends PermissionException {
    public ApplicationPermissionException() {
    }

    public ApplicationPermissionException(String s) {
        super(s);
    }

    public ApplicationPermissionException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ApplicationPermissionException(Throwable throwable) {
        super(throwable);
    }
}


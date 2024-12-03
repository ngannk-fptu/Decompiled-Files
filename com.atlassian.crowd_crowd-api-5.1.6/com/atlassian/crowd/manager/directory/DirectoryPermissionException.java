/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.PermissionException
 */
package com.atlassian.crowd.manager.directory;

import com.atlassian.crowd.exception.PermissionException;

public class DirectoryPermissionException
extends PermissionException {
    public DirectoryPermissionException() {
    }

    public DirectoryPermissionException(String s) {
        super(s);
    }

    public DirectoryPermissionException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public DirectoryPermissionException(Throwable throwable) {
        super(throwable);
    }
}


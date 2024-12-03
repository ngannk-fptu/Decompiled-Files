/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.exception.InfrastructureException
 *  com.google.common.base.Throwables
 */
package com.atlassian.confluence.user;

import com.atlassian.core.exception.InfrastructureException;
import com.google.common.base.Throwables;

public class UserManagementOperationFailedException
extends InfrastructureException {
    public UserManagementOperationFailedException(String msg) {
        super(msg);
    }

    public UserManagementOperationFailedException(Throwable cause) {
        super(Throwables.getRootCause((Throwable)cause));
    }

    public UserManagementOperationFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}


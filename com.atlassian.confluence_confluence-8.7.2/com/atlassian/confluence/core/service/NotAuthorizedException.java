/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.core.service;

import com.atlassian.confluence.security.Permission;
import com.atlassian.user.User;

public class NotAuthorizedException
extends IllegalStateException {
    public NotAuthorizedException(String username) {
        super(username == null ? "anonymous" : username);
    }

    public NotAuthorizedException(User user, Permission permission, Object target) {
        super(user + " is not authorized to perform " + permission + " on " + target);
    }
}


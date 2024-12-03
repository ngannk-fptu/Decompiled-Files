/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.user.ImmutableUser
 *  com.atlassian.crowd.model.user.User
 */
package com.atlassian.crowd.manager.sso;

import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ImmutableApplication;
import com.atlassian.crowd.model.user.ImmutableUser;
import com.atlassian.crowd.model.user.User;

public class ApplicationAccessDeniedException
extends RuntimeException {
    private final ImmutableApplication application;
    private final ImmutableUser user;

    public ApplicationAccessDeniedException(Application application, User user) {
        super(String.format("User %s doesn't have access to application: %s.", user.getName(), application.getName()));
        this.application = ImmutableApplication.from(application);
        this.user = ImmutableUser.from((User)user);
    }

    public Application getApplication() {
        return this.application;
    }

    public User getUser() {
        return this.user;
    }
}


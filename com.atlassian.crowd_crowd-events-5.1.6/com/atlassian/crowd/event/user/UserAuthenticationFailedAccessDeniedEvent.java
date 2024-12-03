/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.ImmutableApplication
 *  com.atlassian.crowd.model.user.ImmutableUser
 *  com.atlassian.crowd.model.user.User
 */
package com.atlassian.crowd.event.user;

import com.atlassian.crowd.event.Event;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ImmutableApplication;
import com.atlassian.crowd.model.user.ImmutableUser;
import com.atlassian.crowd.model.user.User;

public class UserAuthenticationFailedAccessDeniedEvent
extends Event {
    private final ImmutableUser user;
    private final ImmutableApplication application;

    public UserAuthenticationFailedAccessDeniedEvent(Object source, User user, Application application) {
        super(source);
        this.user = ImmutableUser.from((User)user);
        this.application = ImmutableApplication.from((Application)application);
    }

    public User getRemotePrincipal() {
        return this.user;
    }

    public Application getApplication() {
        return this.application;
    }

    public Long getApplicationId() {
        return this.application.getId();
    }
}


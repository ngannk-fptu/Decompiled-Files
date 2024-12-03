/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.ImmutableApplication
 *  com.atlassian.crowd.model.user.ImmutableUser
 *  com.atlassian.crowd.model.user.User
 */
package com.atlassian.crowd.event.user;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.DirectoryEvent;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ImmutableApplication;
import com.atlassian.crowd.model.user.ImmutableUser;
import com.atlassian.crowd.model.user.User;

public class UserAuthenticatedEvent
extends DirectoryEvent {
    private final ImmutableApplication application;
    private final ImmutableUser user;

    public UserAuthenticatedEvent(Object source, Directory directory, Application application, User user) {
        super(source, directory);
        this.application = ImmutableApplication.from((Application)application);
        this.user = ImmutableUser.from((User)user);
    }

    public Application getApplication() {
        return this.application;
    }

    public User getUser() {
        return this.user;
    }

    public Long getApplicationId() {
        return this.application.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        UserAuthenticatedEvent that = (UserAuthenticatedEvent)o;
        if (!this.application.equals((Object)that.application)) {
            return false;
        }
        return this.user.equals((Object)that.user);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.application.hashCode();
        result = 31 * result + this.user.hashCode();
        return result;
    }
}


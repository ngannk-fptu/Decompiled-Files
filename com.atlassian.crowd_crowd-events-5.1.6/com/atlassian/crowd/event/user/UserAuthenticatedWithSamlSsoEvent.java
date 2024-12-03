/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.ImmutableApplication
 *  com.atlassian.crowd.model.user.ImmutableUser
 *  com.atlassian.crowd.model.user.User
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 */
package com.atlassian.crowd.event.user;

import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ImmutableApplication;
import com.atlassian.crowd.model.user.ImmutableUser;
import com.atlassian.crowd.model.user.User;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.util.Objects;

public class UserAuthenticatedWithSamlSsoEvent {
    private final ImmutableApplication application;
    private final ImmutableUser user;
    private final boolean atlassianSamlPlugin;

    public UserAuthenticatedWithSamlSsoEvent(Application application, User user, boolean atlassianSamlPlugin) {
        this(ImmutableApplication.from((Application)application), ImmutableUser.from((User)user), atlassianSamlPlugin);
    }

    public UserAuthenticatedWithSamlSsoEvent(ImmutableApplication application, ImmutableUser user, boolean atlassianSamlPlugin) {
        this.application = (ImmutableApplication)Preconditions.checkNotNull((Object)application);
        this.user = (ImmutableUser)Preconditions.checkNotNull((Object)user);
        this.atlassianSamlPlugin = atlassianSamlPlugin;
    }

    public ImmutableApplication getApplication() {
        return this.application;
    }

    public ImmutableUser getUser() {
        return this.user;
    }

    public boolean getAtlassianSamlPlugin() {
        return this.atlassianSamlPlugin;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        UserAuthenticatedWithSamlSsoEvent that = (UserAuthenticatedWithSamlSsoEvent)o;
        return Objects.equals(this.getApplication(), that.getApplication()) && Objects.equals(this.getUser(), that.getUser()) && Objects.equals(this.getAtlassianSamlPlugin(), that.getAtlassianSamlPlugin());
    }

    public int hashCode() {
        return Objects.hash(this.getApplication(), this.getUser(), this.getAtlassianSamlPlugin());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("application", (Object)this.getApplication()).add("user", (Object)this.getUser()).add("atlassianSamlPlugin", this.getAtlassianSamlPlugin()).toString();
    }
}


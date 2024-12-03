/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.user;

import com.atlassian.crowd.model.user.BaseImmutableUser;
import com.atlassian.crowd.model.user.User;
import java.io.Serializable;

public final class ImmutableUser
extends BaseImmutableUser
implements User,
Serializable {
    public ImmutableUser(User user) {
        super(ImmutableUser.builder(user));
    }

    public ImmutableUser(long directoryId, String name, String displayName, String emailAddress, boolean active, String firstName, String lastName, String externalId) {
        super(directoryId, name, displayName, emailAddress, active, firstName, lastName, externalId);
    }

    protected ImmutableUser(Builder builder) {
        super(builder);
    }

    @Override
    public ImmutableUser withName(String name) {
        return ((Builder)ImmutableUser.builder(this).name(name)).build();
    }

    public static ImmutableUser from(User user) {
        if (user instanceof ImmutableUser) {
            return (ImmutableUser)user;
        }
        return ImmutableUser.builder(user).build();
    }

    public static Builder builder(User user) {
        return new Builder(user);
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static Builder builder(long directoryId, String name) {
        return new Builder(directoryId, name);
    }

    public static class Builder
    extends BaseImmutableUser.Builder<Builder> {
        private Builder(User user) {
            super(user);
        }

        private Builder(String name) {
            super(name);
        }

        private Builder(long directoryId, String name) {
            super(directoryId, name);
        }

        @Override
        public ImmutableUser build() {
            return new ImmutableUser(this);
        }
    }
}


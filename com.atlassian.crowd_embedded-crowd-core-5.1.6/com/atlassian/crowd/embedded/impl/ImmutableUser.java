/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.api.UserComparator
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.crowd.embedded.impl;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.api.UserComparator;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import javax.annotation.Nonnull;

public class ImmutableUser
implements User,
Serializable {
    private static final long serialVersionUID = -4472223017071267465L;
    private final long directoryId;
    private final String name;
    private final boolean active;
    private final String emailAddress;
    private final String displayName;

    public ImmutableUser(long directoryId, @Nonnull String name, String displayName, String emailAddress, boolean active) {
        this.directoryId = directoryId;
        this.name = (String)Preconditions.checkNotNull((Object)name);
        this.displayName = displayName == null ? "" : displayName;
        this.emailAddress = emailAddress;
        this.active = active;
    }

    public static ImmutableUser from(User user) {
        if (user instanceof ImmutableUser) {
            return (ImmutableUser)user;
        }
        return ImmutableUser.newUser(user).toUser();
    }

    public boolean isActive() {
        return this.active;
    }

    public long getDirectoryId() {
        return this.directoryId;
    }

    public String getName() {
        return this.name;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public boolean equals(Object o) {
        return UserComparator.equalsObject((User)this, (Object)o);
    }

    public int hashCode() {
        return UserComparator.hashCode((User)this);
    }

    public int compareTo(User other) {
        return UserComparator.compareTo((User)this, (User)other);
    }

    public static Builder newUser() {
        return new Builder();
    }

    public static Builder newUser(User user) {
        Builder builder = ImmutableUser.newUser().directoryId(user.getDirectoryId());
        builder.name(user.getName());
        builder.active(user.isActive());
        builder.displayName(user.getDisplayName());
        builder.emailAddress(user.getEmailAddress());
        return builder;
    }

    public static final class Builder {
        private long directoryId = -1L;
        private String name;
        private String displayName;
        private String emailAddress;
        private boolean active = true;

        public ImmutableUser toUser() {
            return new ImmutableUser(this.directoryId, this.name, this.displayName, this.emailAddress, this.active);
        }

        public Builder directoryId(long directoryId) {
            this.directoryId = directoryId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder emailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }
    }
}


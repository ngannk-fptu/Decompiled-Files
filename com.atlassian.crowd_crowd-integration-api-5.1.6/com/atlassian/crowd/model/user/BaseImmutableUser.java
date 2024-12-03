/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.api.UserComparator
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 */
package com.atlassian.crowd.model.user;

import com.atlassian.crowd.embedded.api.UserComparator;
import com.atlassian.crowd.model.user.User;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.io.Serializable;

public abstract class BaseImmutableUser
implements User,
Serializable {
    private final long directoryId;
    private final String name;
    private final boolean active;
    private final String emailAddress;
    private final String displayName;
    private final String firstName;
    private final String lastName;
    private final String externalId;

    public BaseImmutableUser(long directoryId, String name, String displayName, String emailAddress, boolean active, String firstName, String lastName, String externalId) {
        this.directoryId = directoryId;
        this.name = (String)Preconditions.checkNotNull((Object)name);
        this.displayName = displayName;
        this.emailAddress = emailAddress;
        this.active = active;
        this.firstName = firstName;
        this.lastName = lastName;
        this.externalId = externalId;
    }

    protected BaseImmutableUser(Builder builder) {
        this.directoryId = builder.directoryId;
        this.name = (String)Preconditions.checkNotNull((Object)builder.name);
        this.displayName = builder.displayName;
        this.emailAddress = builder.emailAddress;
        this.active = builder.active;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.externalId = builder.externalId;
    }

    public boolean isActive() {
        return this.active;
    }

    @Override
    public long getDirectoryId() {
        return this.directoryId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public String getFirstName() {
        return this.firstName;
    }

    @Override
    public String getLastName() {
        return this.lastName;
    }

    @Override
    public String getExternalId() {
        return this.externalId;
    }

    @Override
    public boolean equals(Object o) {
        return UserComparator.equalsObject((com.atlassian.crowd.embedded.api.User)this, (Object)o);
    }

    @Override
    public int hashCode() {
        return UserComparator.hashCode((com.atlassian.crowd.embedded.api.User)this);
    }

    public int compareTo(com.atlassian.crowd.embedded.api.User user) {
        return UserComparator.compareTo((com.atlassian.crowd.embedded.api.User)this, (com.atlassian.crowd.embedded.api.User)user);
    }

    public abstract BaseImmutableUser withName(String var1);

    @Override
    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("directoryId", this.directoryId).add("name", (Object)this.name).add("active", this.active).add("emailAddress", (Object)this.emailAddress).add("displayName", (Object)this.displayName).add("firstName", (Object)this.firstName).add("lastName", (Object)this.lastName).add("externalId", (Object)this.externalId).toString();
    }

    protected static abstract class Builder<T extends Builder> {
        private long directoryId;
        private String name;
        private boolean active;
        private String emailAddress;
        private String displayName;
        private String firstName;
        private String lastName;
        private String externalId;

        public Builder(User user) {
            this.directoryId = user.getDirectoryId();
            this.name = user.getName();
            this.active = user.isActive();
            this.emailAddress = user.getEmailAddress();
            this.displayName = user.getDisplayName();
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.externalId = user.getExternalId();
        }

        public Builder(String name) {
            this.name = name;
        }

        public Builder(long directoryId, String name) {
            this.directoryId = directoryId;
            this.name = name;
        }

        public T directoryId(long directoryId) {
            this.directoryId = directoryId;
            return (T)this;
        }

        public T name(String name) {
            this.name = name;
            return (T)this;
        }

        public T active(boolean active) {
            this.active = active;
            return (T)this;
        }

        public T emailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
            return (T)this;
        }

        public T displayName(String displayName) {
            this.displayName = displayName;
            return (T)this;
        }

        public T firstName(String firstName) {
            this.firstName = firstName;
            return (T)this;
        }

        public T lastName(String lastName) {
            this.lastName = lastName;
            return (T)this;
        }

        public T externalId(String externalId) {
            this.externalId = externalId;
            return (T)this;
        }

        public abstract BaseImmutableUser build();
    }
}


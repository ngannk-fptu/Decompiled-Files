/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.user;

import com.atlassian.crowd.model.user.BaseImmutableUser;
import com.atlassian.crowd.model.user.TimestampedUser;
import java.util.Date;

public final class ImmutableTimestampedUser
extends BaseImmutableUser
implements TimestampedUser {
    private final Date createdDate;
    private final Date updatedDate;

    private ImmutableTimestampedUser(Builder builder) {
        super(builder);
        this.createdDate = builder.createdDate;
        this.updatedDate = builder.updatedDate;
    }

    public static ImmutableTimestampedUser from(TimestampedUser user) {
        if (user instanceof ImmutableTimestampedUser) {
            return (ImmutableTimestampedUser)user;
        }
        return ImmutableTimestampedUser.builder(user).build();
    }

    @Override
    public ImmutableTimestampedUser withName(String name) {
        return ((Builder)ImmutableTimestampedUser.builder(this).name(name)).build();
    }

    public static Builder builder(TimestampedUser user) {
        return new Builder(user);
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    @Override
    public Date getCreatedDate() {
        return this.createdDate == null ? null : new Date(this.createdDate.getTime());
    }

    @Override
    public Date getUpdatedDate() {
        return this.updatedDate == null ? null : new Date(this.updatedDate.getTime());
    }

    public static class Builder
    extends BaseImmutableUser.Builder<Builder> {
        private Date createdDate;
        private Date updatedDate;

        private Builder(TimestampedUser user) {
            super(user);
            this.createdDate(user.getCreatedDate());
            this.updatedDate(user.getUpdatedDate());
        }

        private Builder(String name) {
            super(name);
        }

        public Builder createdDate(Date createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder updatedDate(Date updatedDate) {
            this.updatedDate = updatedDate;
            return this;
        }

        @Override
        public ImmutableTimestampedUser build() {
            return new ImmutableTimestampedUser(this);
        }
    }
}


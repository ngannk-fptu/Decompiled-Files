/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSetMultimap
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.SetMultimap
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.user;

import com.atlassian.crowd.model.user.BaseImmutableUser;
import com.atlassian.crowd.model.user.TimestampedUser;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.SetMultimap;
import java.util.Date;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ImmutableTimestampedUserWithAttributes
extends BaseImmutableUser
implements UserWithAttributes,
TimestampedUser {
    private final ImmutableSetMultimap<String, String> attributes;
    private final Date createdDate;
    private final Date updatedDate;

    private ImmutableTimestampedUserWithAttributes(Builder builder) {
        super(builder);
        this.attributes = builder.attributes;
        this.createdDate = builder.createdDate;
        this.updatedDate = builder.updatedDate;
    }

    public static Builder builder(TimestampedUser user, SetMultimap<String, String> attributes) {
        return new Builder(user, attributes);
    }

    @Nonnull
    public Set<String> getValues(String key) {
        return this.attributes.get((Object)key);
    }

    @Nullable
    public String getValue(String key) {
        return (String)Iterables.getFirst(this.getValues(key), null);
    }

    public Set<String> getKeys() {
        return this.attributes.keySet();
    }

    @Override
    public Date getCreatedDate() {
        return this.createdDate == null ? null : new Date(this.createdDate.getTime());
    }

    @Override
    public Date getUpdatedDate() {
        return this.updatedDate == null ? null : new Date(this.updatedDate.getTime());
    }

    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }

    @Override
    public ImmutableTimestampedUserWithAttributes withName(String name) {
        return ((Builder)ImmutableTimestampedUserWithAttributes.builder(this, this.attributes).name(name)).build();
    }

    public static class Builder
    extends BaseImmutableUser.Builder<Builder> {
        private ImmutableSetMultimap<String, String> attributes;
        private Date createdDate;
        private Date updatedDate;

        public Builder(TimestampedUser user, SetMultimap<String, String> attributes) {
            super(user);
            this.setAttributes(attributes);
            this.setCreatedDate(user.getCreatedDate());
            this.setUpdatedDate(user.getUpdatedDate());
        }

        public Builder setAttributes(SetMultimap<String, String> attributes) {
            this.attributes = ImmutableSetMultimap.copyOf(attributes);
            return this;
        }

        public Builder setCreatedDate(Date createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder setUpdatedDate(Date updatedDate) {
            this.updatedDate = updatedDate;
            return this;
        }

        @Override
        public ImmutableTimestampedUserWithAttributes build() {
            return new ImmutableTimestampedUserWithAttributes(this);
        }
    }
}


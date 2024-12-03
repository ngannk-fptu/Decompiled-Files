/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Attributes
 *  com.google.common.collect.ImmutableSetMultimap
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.SetMultimap
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.user;

import com.atlassian.crowd.attribute.AttributeUtil;
import com.atlassian.crowd.embedded.api.Attributes;
import com.atlassian.crowd.model.user.BaseImmutableUser;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.SetMultimap;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ImmutableUserWithAttributes
extends BaseImmutableUser
implements UserWithAttributes {
    private final ImmutableSetMultimap<String, String> attributes;

    private ImmutableUserWithAttributes(Builder builder) {
        super(builder);
        this.attributes = builder.attributes;
    }

    public static Builder builder(UserWithAttributes user) {
        return ImmutableUserWithAttributes.builder(user, AttributeUtil.toMultimap((Attributes)user));
    }

    public static Builder builder(User user, SetMultimap<String, String> attributes) {
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

    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }

    @Override
    public ImmutableUserWithAttributes withName(String name) {
        return ((Builder)ImmutableUserWithAttributes.builder(this).name(name)).build();
    }

    public static class Builder
    extends BaseImmutableUser.Builder<Builder> {
        private ImmutableSetMultimap<String, String> attributes;

        public Builder(User user, SetMultimap<String, String> attributes) {
            super(user);
            this.setAttributes(attributes);
        }

        public Builder setAttributes(SetMultimap<String, String> attributes) {
            this.attributes = ImmutableSetMultimap.copyOf(attributes);
            return this;
        }

        @Override
        public ImmutableUserWithAttributes build() {
            return new ImmutableUserWithAttributes(this);
        }
    }
}


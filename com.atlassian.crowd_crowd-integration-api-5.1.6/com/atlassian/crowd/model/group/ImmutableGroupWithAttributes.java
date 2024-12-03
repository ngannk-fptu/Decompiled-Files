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
package com.atlassian.crowd.model.group;

import com.atlassian.crowd.attribute.AttributeUtil;
import com.atlassian.crowd.model.group.BaseImmutableGroup;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.SetMultimap;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ImmutableGroupWithAttributes
extends BaseImmutableGroup
implements GroupWithAttributes {
    private final ImmutableSetMultimap<String, String> attributes;

    private ImmutableGroupWithAttributes(Builder builder) {
        super(builder);
        this.attributes = builder.attributes;
    }

    public static ImmutableGroupWithAttributes from(GroupWithAttributes group) {
        if (group instanceof ImmutableGroupWithAttributes) {
            return (ImmutableGroupWithAttributes)group;
        }
        return ImmutableGroupWithAttributes.builder(group).build();
    }

    public static Builder builder(GroupWithAttributes group) {
        return ImmutableGroupWithAttributes.builder(group, AttributeUtil.toMultimap(group));
    }

    public static Builder builder(Group group, SetMultimap<String, String> attributes) {
        return new Builder(group, attributes);
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
    public ImmutableGroupWithAttributes withName(String name) {
        return ((Builder)ImmutableGroupWithAttributes.builder(this).setName(name)).build();
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
        ImmutableGroupWithAttributes that = (ImmutableGroupWithAttributes)o;
        return Objects.equals(this.attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.attributes);
    }

    public static class Builder
    extends BaseImmutableGroup.Builder<Builder> {
        private ImmutableSetMultimap<String, String> attributes;

        public Builder(Group group, SetMultimap<String, String> attributes) {
            super(group);
            this.setAttributes(attributes);
        }

        public Builder setAttributes(SetMultimap<String, String> attributes) {
            this.attributes = ImmutableSetMultimap.copyOf(attributes);
            return this;
        }

        @Override
        public ImmutableGroupWithAttributes build() {
            return new ImmutableGroupWithAttributes(this);
        }
    }
}


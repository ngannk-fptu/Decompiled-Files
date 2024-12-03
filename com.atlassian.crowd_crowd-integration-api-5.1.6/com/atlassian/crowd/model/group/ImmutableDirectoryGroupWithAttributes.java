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

import com.atlassian.crowd.model.group.BaseImmutableGroup;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.group.InternalDirectoryGroup;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.SetMultimap;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ImmutableDirectoryGroupWithAttributes
extends BaseImmutableGroup
implements GroupWithAttributes,
InternalDirectoryGroup {
    private final ImmutableSetMultimap<String, String> attributes;
    private final Date createdDate;
    private final Date updatedDate;
    private final boolean isLocal;

    private ImmutableDirectoryGroupWithAttributes(Builder builder) {
        super(builder);
        this.attributes = builder.attributes;
        this.createdDate = builder.createdDate;
        this.updatedDate = builder.updatedDate;
        this.isLocal = builder.isLocal;
    }

    public static Builder builder(InternalDirectoryGroup group, SetMultimap<String, String> attributes) {
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
    public Date getCreatedDate() {
        return this.createdDate == null ? null : new Date(this.createdDate.getTime());
    }

    @Override
    public Date getUpdatedDate() {
        return this.updatedDate == null ? null : new Date(this.updatedDate.getTime());
    }

    @Override
    public boolean isLocal() {
        return this.isLocal;
    }

    @Override
    public ImmutableDirectoryGroupWithAttributes withName(String name) {
        return ((Builder)ImmutableDirectoryGroupWithAttributes.builder(this, this.attributes).setName(name)).build();
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
        ImmutableDirectoryGroupWithAttributes that = (ImmutableDirectoryGroupWithAttributes)o;
        return this.isLocal == that.isLocal && Objects.equals(this.attributes, that.attributes) && Objects.equals(this.createdDate, that.createdDate) && Objects.equals(this.updatedDate, that.updatedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.attributes, this.createdDate, this.updatedDate, this.isLocal);
    }

    public static class Builder
    extends BaseImmutableGroup.Builder<Builder> {
        private ImmutableSetMultimap<String, String> attributes;
        private Date createdDate;
        private Date updatedDate;
        private boolean isLocal;

        public Builder(InternalDirectoryGroup group, SetMultimap<String, String> attributes) {
            super(group);
            this.setAttributes(attributes);
            this.setCreatedDate(group.getCreatedDate());
            this.setUpdatedDate(group.getUpdatedDate());
            this.setLocal(group.isLocal());
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

        public Builder setLocal(boolean local) {
            this.isLocal = local;
            return this;
        }

        @Override
        public ImmutableDirectoryGroupWithAttributes build() {
            return new ImmutableDirectoryGroupWithAttributes(this);
        }
    }
}


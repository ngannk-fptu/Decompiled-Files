/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.crowd.model.group;

import com.atlassian.crowd.model.group.BaseImmutableGroup;
import com.atlassian.crowd.model.group.InternalDirectoryGroup;
import com.google.common.base.MoreObjects;
import java.util.Date;
import java.util.Objects;

public class ImmutableDirectoryGroup
extends BaseImmutableGroup
implements InternalDirectoryGroup {
    private final Date createdDate;
    private final Date updatedDate;
    private final boolean isLocal;

    private ImmutableDirectoryGroup(Builder builder) {
        super(builder);
        this.createdDate = ImmutableDirectoryGroup.copy(builder.createdDate);
        this.updatedDate = ImmutableDirectoryGroup.copy(builder.updatedDate);
        this.isLocal = builder.isLocal;
    }

    public static ImmutableDirectoryGroup from(InternalDirectoryGroup group) {
        if (group instanceof ImmutableDirectoryGroup) {
            return (ImmutableDirectoryGroup)group;
        }
        return ImmutableDirectoryGroup.builder(group).build();
    }

    @Override
    public ImmutableDirectoryGroup withName(String name) {
        return ((Builder)ImmutableDirectoryGroup.builder(this).setName(name)).build();
    }

    public static Builder builder(InternalDirectoryGroup group) {
        return new Builder(group);
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    @Override
    public Date getCreatedDate() {
        return ImmutableDirectoryGroup.copy(this.createdDate);
    }

    @Override
    public Date getUpdatedDate() {
        return ImmutableDirectoryGroup.copy(this.updatedDate);
    }

    @Override
    public boolean isLocal() {
        return this.isLocal;
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
        ImmutableDirectoryGroup that = (ImmutableDirectoryGroup)o;
        return Objects.equals(this.createdDate, that.createdDate) && Objects.equals(this.updatedDate, that.updatedDate) && this.isLocal == that.isLocal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.createdDate, this.updatedDate, this.isLocal);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("createdDate", (Object)this.getCreatedDate()).add("updatedDate", (Object)this.getUpdatedDate()).add("local", this.isLocal()).add("directoryId", this.getDirectoryId()).add("name", (Object)this.getName()).add("type", (Object)this.getType()).add("active", this.isActive()).add("description", (Object)this.getDescription()).add("externalId", (Object)this.getExternalId()).toString();
    }

    private static Date copy(Date date) {
        return date == null ? null : new Date(date.getTime());
    }

    public static class Builder
    extends BaseImmutableGroup.Builder<Builder> {
        private Date createdDate;
        private Date updatedDate;
        private boolean isLocal;

        private Builder(InternalDirectoryGroup group) {
            super(group);
            this.setCreatedDate(group.getCreatedDate());
            this.setUpdatedDate(group.getUpdatedDate());
            this.setLocal(group.isLocal());
        }

        private Builder(String name) {
            super(name);
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
        public ImmutableDirectoryGroup build() {
            return new ImmutableDirectoryGroup(this);
        }
    }
}


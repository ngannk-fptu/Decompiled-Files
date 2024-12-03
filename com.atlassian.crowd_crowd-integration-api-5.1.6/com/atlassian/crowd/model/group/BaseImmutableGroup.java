/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nonnull
 */
package com.atlassian.crowd.model.group;

import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupComparator;
import com.atlassian.crowd.model.group.GroupType;
import com.google.common.base.MoreObjects;
import java.util.Objects;
import javax.annotation.Nonnull;

public abstract class BaseImmutableGroup
implements Group {
    private final long directoryId;
    private final String name;
    private final GroupType type;
    private final boolean active;
    private final String description;
    private final String externalId;

    protected BaseImmutableGroup(Builder builder) {
        this.directoryId = builder.directoryId;
        this.name = builder.name;
        this.type = builder.type;
        this.active = builder.active;
        this.description = builder.description;
        this.externalId = builder.externalId;
    }

    public abstract BaseImmutableGroup withName(String var1);

    @Override
    public int compareTo(@Nonnull Group other) {
        return GroupComparator.compareTo(this, other);
    }

    @Override
    public long getDirectoryId() {
        return this.directoryId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public GroupType getType() {
        return this.type;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getExternalId() {
        return this.externalId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BaseImmutableGroup that = (BaseImmutableGroup)o;
        return this.directoryId == that.directoryId && this.active == that.active && Objects.equals(this.name, that.name) && this.type == that.type && Objects.equals(this.description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(new Object[]{this.directoryId, this.name, this.type, this.active, this.description});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("directoryId", this.directoryId).add("name", (Object)this.name).add("type", (Object)this.type).add("active", this.active).add("description", (Object)this.description).add("externalId", (Object)this.externalId).toString();
    }

    protected static abstract class Builder<T extends Builder> {
        private long directoryId;
        private String name;
        private GroupType type = GroupType.GROUP;
        private boolean active = true;
        private String description = "";
        private String externalId;

        protected Builder(Group group) {
            Objects.requireNonNull(group, "group");
            this.setDirectoryId(group.getDirectoryId());
            this.setName(group.getName());
            this.setDescription(group.getDescription());
            this.setType(group.getType());
            this.setActive(group.isActive());
            this.setDescription(group.getDescription());
            this.setExternalId(group.getExternalId());
        }

        public Builder(String name) {
            this(-1L, name);
        }

        public Builder(long directoryId, String name) {
            this.directoryId = directoryId;
            this.name = name;
        }

        public T setDirectoryId(long directoryId) {
            this.directoryId = directoryId;
            return (T)this;
        }

        public T setName(String name) {
            this.name = name;
            return (T)this;
        }

        public T setType(GroupType type) {
            this.type = type;
            return (T)this;
        }

        public T setActive(boolean active) {
            this.active = active;
            return (T)this;
        }

        public T setDescription(String description) {
            this.description = description;
            return (T)this;
        }

        public T setExternalId(String externalId) {
            this.externalId = externalId;
            return (T)this;
        }

        public abstract Group build();
    }
}


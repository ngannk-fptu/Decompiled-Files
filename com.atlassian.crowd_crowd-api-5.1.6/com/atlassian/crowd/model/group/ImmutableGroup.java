/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupComparator
 *  com.atlassian.crowd.model.group.GroupType
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.crowd.model.group;

import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupComparator;
import com.atlassian.crowd.model.group.GroupType;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.util.Objects;
import javax.annotation.Nonnull;

public class ImmutableGroup
implements Group {
    private final long directoryId;
    private final String name;
    private final GroupType type;
    private final boolean active;
    private final String description;
    private final String externalId;

    private ImmutableGroup(Builder builder) {
        this.directoryId = builder.directoryId;
        this.name = builder.name;
        this.type = builder.type;
        this.active = builder.active;
        this.description = builder.description;
        this.externalId = builder.externalId;
    }

    public static ImmutableGroup from(Group group) {
        if (group instanceof ImmutableGroup) {
            return (ImmutableGroup)group;
        }
        return ImmutableGroup.builder(group).build();
    }

    public static Builder builder(long directoryId, String name) {
        return new Builder(directoryId, name);
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static Builder builder(Group group) {
        return new Builder(group);
    }

    public int compareTo(@Nonnull Group other) {
        return GroupComparator.compareTo((Group)this, (Group)other);
    }

    public long getDirectoryId() {
        return this.directoryId;
    }

    public String getName() {
        return this.name;
    }

    public GroupType getType() {
        return this.type;
    }

    public boolean isActive() {
        return this.active;
    }

    public String getDescription() {
        return this.description;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ImmutableGroup that = (ImmutableGroup)o;
        return this.directoryId == that.directoryId && this.active == that.active && Objects.equals(this.name, that.name) && this.type == that.type && Objects.equals(this.description, that.description);
    }

    public int hashCode() {
        return Objects.hash(this.directoryId, this.name, this.type, this.active, this.description);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("directoryId", this.directoryId).add("name", (Object)this.name).add("type", (Object)this.type).add("active", this.active).add("description", (Object)this.description).add("externalId", (Object)this.externalId).toString();
    }

    public static class Builder {
        private long directoryId;
        private String name;
        private GroupType type = GroupType.GROUP;
        private boolean active = true;
        private String description = "";
        private String externalId;

        public Builder(Group group) {
            Preconditions.checkNotNull((Object)group, (Object)"group");
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

        public Builder setDirectoryId(long directoryId) {
            this.directoryId = directoryId;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setType(GroupType type) {
            this.type = type;
            return this;
        }

        public Builder setActive(boolean active) {
            this.active = active;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setExternalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        public ImmutableGroup build() {
            return new ImmutableGroup(this);
        }
    }
}


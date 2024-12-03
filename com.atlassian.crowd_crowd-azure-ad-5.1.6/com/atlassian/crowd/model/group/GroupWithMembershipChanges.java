/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.model.group.ImmutableGroup
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.group;

import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.group.ImmutableGroup;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GroupWithMembershipChanges
implements Group {
    private final ImmutableGroup group;
    private final Set<String> userChildrenIdsToAdd;
    private final Set<String> userChildrenIdsToDelete;
    private final Set<String> groupChildrenIdsToAdd;
    private final Set<String> groupChildrenIdsToDelete;

    public int compareTo(@Nonnull Group other) {
        return this.group.compareTo(other);
    }

    public long getDirectoryId() {
        return this.group.getDirectoryId();
    }

    public String getName() {
        return this.group.getName();
    }

    public GroupType getType() {
        return this.group.getType();
    }

    public boolean isActive() {
        return this.group.isActive();
    }

    public String getDescription() {
        return this.group.getDescription();
    }

    @Nullable
    public String getExternalId() {
        return this.group.getExternalId();
    }

    public GroupWithMembershipChanges merge(GroupWithMembershipChanges other) {
        return this.toBuilder().addUserChildrenIdsToAdd(other.getUserChildrenIdsToAdd()).addUserChildrenIdsToDelete(other.getUserChildrenIdsToDelete()).addGroupChildrenIdsToAdd(other.getGroupChildrenIdsToAdd()).addGroupChildrenIdsToDelete(other.getGroupChildrenIdsToDelete()).build();
    }

    public Builder toBuilder() {
        return GroupWithMembershipChanges.builder(this);
    }

    public static Builder builder(Group group) {
        return GroupWithMembershipChanges.builder().setGroup(ImmutableGroup.from((Group)group));
    }

    protected GroupWithMembershipChanges(ImmutableGroup group, Iterable<String> userChildrenIdsToAdd, Iterable<String> userChildrenIdsToDelete, Iterable<String> groupChildrenIdsToAdd, Iterable<String> groupChildrenIdsToDelete) {
        this.group = Objects.requireNonNull(group);
        this.userChildrenIdsToAdd = ImmutableSet.copyOf(userChildrenIdsToAdd);
        this.userChildrenIdsToDelete = ImmutableSet.copyOf(userChildrenIdsToDelete);
        this.groupChildrenIdsToAdd = ImmutableSet.copyOf(groupChildrenIdsToAdd);
        this.groupChildrenIdsToDelete = ImmutableSet.copyOf(groupChildrenIdsToDelete);
    }

    public ImmutableGroup getGroup() {
        return this.group;
    }

    public Set<String> getUserChildrenIdsToAdd() {
        return this.userChildrenIdsToAdd;
    }

    public Set<String> getUserChildrenIdsToDelete() {
        return this.userChildrenIdsToDelete;
    }

    public Set<String> getGroupChildrenIdsToAdd() {
        return this.groupChildrenIdsToAdd;
    }

    public Set<String> getGroupChildrenIdsToDelete() {
        return this.groupChildrenIdsToDelete;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(GroupWithMembershipChanges data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        GroupWithMembershipChanges that = (GroupWithMembershipChanges)o;
        return Objects.equals(this.getGroup(), that.getGroup()) && Objects.equals(this.getUserChildrenIdsToAdd(), that.getUserChildrenIdsToAdd()) && Objects.equals(this.getUserChildrenIdsToDelete(), that.getUserChildrenIdsToDelete()) && Objects.equals(this.getGroupChildrenIdsToAdd(), that.getGroupChildrenIdsToAdd()) && Objects.equals(this.getGroupChildrenIdsToDelete(), that.getGroupChildrenIdsToDelete());
    }

    public int hashCode() {
        return Objects.hash(this.getGroup(), this.getUserChildrenIdsToAdd(), this.getUserChildrenIdsToDelete(), this.getGroupChildrenIdsToAdd(), this.getGroupChildrenIdsToDelete());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("group", (Object)this.getGroup()).add("userChildrenIdsToAdd", this.getUserChildrenIdsToAdd()).add("userChildrenIdsToDelete", this.getUserChildrenIdsToDelete()).add("groupChildrenIdsToAdd", this.getGroupChildrenIdsToAdd()).add("groupChildrenIdsToDelete", this.getGroupChildrenIdsToDelete()).toString();
    }

    public static final class Builder {
        private ImmutableGroup group;
        private Set<String> userChildrenIdsToAdd = new HashSet<String>();
        private Set<String> userChildrenIdsToDelete = new HashSet<String>();
        private Set<String> groupChildrenIdsToAdd = new HashSet<String>();
        private Set<String> groupChildrenIdsToDelete = new HashSet<String>();

        private Builder() {
        }

        private Builder(GroupWithMembershipChanges initialData) {
            this.group = initialData.getGroup();
            this.userChildrenIdsToAdd = new HashSet<String>(initialData.getUserChildrenIdsToAdd());
            this.userChildrenIdsToDelete = new HashSet<String>(initialData.getUserChildrenIdsToDelete());
            this.groupChildrenIdsToAdd = new HashSet<String>(initialData.getGroupChildrenIdsToAdd());
            this.groupChildrenIdsToDelete = new HashSet<String>(initialData.getGroupChildrenIdsToDelete());
        }

        public Builder setGroup(ImmutableGroup group) {
            this.group = group;
            return this;
        }

        public Builder setUserChildrenIdsToAdd(Set<String> userChildrenIdsToAdd) {
            this.userChildrenIdsToAdd = userChildrenIdsToAdd;
            return this;
        }

        public Builder addUserChildrenIdsToAddItem(String userChildrenIdsToAddItem) {
            this.userChildrenIdsToAdd.add(userChildrenIdsToAddItem);
            return this;
        }

        public Builder addUserChildrenIdsToAdd(Iterable<String> userChildrenIdsToAdd) {
            for (String userChildrenIdsToAddItem : userChildrenIdsToAdd) {
                this.addUserChildrenIdsToAddItem(userChildrenIdsToAddItem);
            }
            return this;
        }

        public Builder setUserChildrenIdsToDelete(Set<String> userChildrenIdsToDelete) {
            this.userChildrenIdsToDelete = userChildrenIdsToDelete;
            return this;
        }

        public Builder addUserChildrenIdsToDeleteItem(String userChildrenIdsToDeleteItem) {
            this.userChildrenIdsToDelete.add(userChildrenIdsToDeleteItem);
            return this;
        }

        public Builder addUserChildrenIdsToDelete(Iterable<String> userChildrenIdsToDelete) {
            for (String userChildrenIdsToDeleteItem : userChildrenIdsToDelete) {
                this.addUserChildrenIdsToDeleteItem(userChildrenIdsToDeleteItem);
            }
            return this;
        }

        public Builder setGroupChildrenIdsToAdd(Set<String> groupChildrenIdsToAdd) {
            this.groupChildrenIdsToAdd = groupChildrenIdsToAdd;
            return this;
        }

        public Builder addGroupChildrenIdsToAddItem(String groupChildrenIdsToAddItem) {
            this.groupChildrenIdsToAdd.add(groupChildrenIdsToAddItem);
            return this;
        }

        public Builder addGroupChildrenIdsToAdd(Iterable<String> groupChildrenIdsToAdd) {
            for (String groupChildrenIdsToAddItem : groupChildrenIdsToAdd) {
                this.addGroupChildrenIdsToAddItem(groupChildrenIdsToAddItem);
            }
            return this;
        }

        public Builder setGroupChildrenIdsToDelete(Set<String> groupChildrenIdsToDelete) {
            this.groupChildrenIdsToDelete = groupChildrenIdsToDelete;
            return this;
        }

        public Builder addGroupChildrenIdsToDeleteItem(String groupChildrenIdsToDeleteItem) {
            this.groupChildrenIdsToDelete.add(groupChildrenIdsToDeleteItem);
            return this;
        }

        public Builder addGroupChildrenIdsToDelete(Iterable<String> groupChildrenIdsToDelete) {
            for (String groupChildrenIdsToDeleteItem : groupChildrenIdsToDelete) {
                this.addGroupChildrenIdsToDeleteItem(groupChildrenIdsToDeleteItem);
            }
            return this;
        }

        public GroupWithMembershipChanges build() {
            return new GroupWithMembershipChanges(this.group, this.userChildrenIdsToAdd, this.userChildrenIdsToDelete, this.groupChildrenIdsToAdd, this.groupChildrenIdsToDelete);
        }
    }
}


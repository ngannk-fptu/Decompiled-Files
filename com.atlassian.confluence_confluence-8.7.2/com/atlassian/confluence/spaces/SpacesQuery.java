/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.spaces;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.spaces.SpaceGroup;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class SpacesQuery {
    private final SpaceType spaceType;
    private List<String> spaceKeys;
    private final String permissionType;
    private final User user;
    private final SpaceGroup spaceGroup;
    private List<String> sortBy;
    private final Date creationDate;
    private final Set<SpaceStatus> spaceStatuses;
    private final List<Label> labels;
    private final Optional<Boolean> favourite;
    private final Optional<Boolean> hasRetentionPolicy;
    private final Set<Long> spaceIds;

    public static Builder newQuery() {
        return new Builder();
    }

    private SpacesQuery(Builder builder) {
        this.spaceType = builder.type;
        this.spaceKeys = builder.spaceKeys;
        this.permissionType = builder.permissionType;
        this.user = builder.user;
        this.spaceGroup = builder.spaceGroup;
        this.sortBy = builder.sortBy;
        this.creationDate = builder.creationDate;
        this.spaceStatuses = builder.spaceStatuses;
        this.labels = builder.labels;
        this.favourite = builder.favourite;
        this.hasRetentionPolicy = builder.hasRetentionPolicy;
        this.spaceIds = builder.spaceIds;
    }

    public SpaceType getSpaceType() {
        return this.spaceType;
    }

    public String getPermissionType() {
        return this.permissionType;
    }

    public User getUser() {
        return this.user;
    }

    @Deprecated
    public List<String> getUserGroups() {
        return Collections.emptyList();
    }

    @Deprecated
    public SpaceGroup getSpaceGroup() {
        return this.spaceGroup;
    }

    public List<String> getSpaceKeys() {
        return this.spaceKeys;
    }

    public List<String> getSortBy() {
        return this.sortBy;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public Set<SpaceStatus> getSpaceStatuses() {
        return this.spaceStatuses;
    }

    public List<Label> getLabels() {
        return this.labels;
    }

    public Optional<Boolean> getFavourite() {
        return this.favourite;
    }

    public Optional<Boolean> getHasRetentionPolicy() {
        return this.hasRetentionPolicy;
    }

    public Set<Long> getSpaceIds() {
        return this.spaceIds;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SpacesQuery that = (SpacesQuery)o;
        return Objects.equals(this.creationDate, that.creationDate) && Objects.equals(this.permissionType, that.permissionType) && Objects.equals(this.sortBy, that.sortBy) && Objects.equals(this.spaceGroup, that.spaceGroup) && Objects.equals(this.spaceKeys, that.spaceKeys) && Objects.equals(this.spaceStatuses, that.spaceStatuses) && Objects.equals(this.spaceType, that.spaceType) && Objects.equals(this.user, that.user) && Objects.equals(this.labels, that.labels) && Objects.equals(this.favourite, that.favourite) && Objects.equals(this.spaceIds, that.spaceIds);
    }

    public int hashCode() {
        return Objects.hash(this.spaceType, this.spaceKeys, this.permissionType, this.user, this.spaceGroup, this.sortBy, this.creationDate, this.spaceStatuses, this.labels, this.favourite, this.spaceIds);
    }

    public static class Builder {
        private static final List<String> DEFAULT_SORT_BY = Arrays.asList("name", "key");
        private SpaceType type;
        private List<String> spaceKeys = new ArrayList<String>();
        private String permissionType;
        private User user;
        private Set<SpaceStatus> spaceStatuses = new HashSet<SpaceStatus>();
        private SpaceGroup spaceGroup;
        private List<String> sortBy = null;
        private Date creationDate;
        private List<Label> labels = new ArrayList<Label>();
        private Optional<Boolean> favourite = Optional.empty();
        private Optional<Boolean> hasRetentionPolicy = Optional.empty();
        private Set<Long> spaceIds = new HashSet<Long>();

        private Builder() {
        }

        public Builder withSpaceKey(String spaceKey) {
            Preconditions.checkArgument((spaceKey != null ? 1 : 0) != 0, (Object)"Space key should not be null");
            this.spaceKeys.add(spaceKey);
            return this;
        }

        public Builder withSpaceKeys(Iterable<String> spaceKeys) {
            Preconditions.checkArgument((spaceKeys != null ? 1 : 0) != 0, (Object)"Space keys should not be null");
            for (String spaceKey : spaceKeys) {
                this.withSpaceKey(spaceKey);
            }
            return this;
        }

        public Builder withSpaceType(SpaceType type) {
            Preconditions.checkState((this.type == null ? 1 : 0) != 0, (Object)"You can not List more than one space type on a spaces query");
            Preconditions.checkArgument((type != null ? 1 : 0) != 0, (Object)"Space type should not be null");
            this.type = type;
            return this;
        }

        public Builder withLabel(Label label) {
            Preconditions.checkArgument((label != null ? 1 : 0) != 0, (Object)"Label should not be null");
            this.labels.add(label);
            return this;
        }

        public Builder withLabels(Iterable<Label> labels) {
            Preconditions.checkArgument((labels != null ? 1 : 0) != 0, (Object)"Labels should not be null");
            for (Label label : labels) {
                this.withLabel(label);
            }
            return this;
        }

        public Builder withIsFavourited(boolean isFavourited) {
            this.favourite = Optional.of(isFavourited);
            return this;
        }

        public Builder withHasRetentionPolicy(boolean hasRetentionPolicy) {
            this.hasRetentionPolicy = Optional.of(hasRetentionPolicy);
            return this;
        }

        public Builder forUser(User user) {
            this.permissionType = this.permissionType == null ? "VIEWSPACE" : this.permissionType;
            this.user = user;
            return this;
        }

        public Builder withSpaceIds(Set<Long> spaceIds) {
            this.spaceIds = new HashSet<Long>(spaceIds);
            return this;
        }

        @Deprecated
        public Builder inGroup(Group group) {
            return this;
        }

        @Deprecated
        public Builder inGroups(List<Group> groups) {
            return this;
        }

        public Builder withPermission(String permission) {
            Preconditions.checkArgument((permission != null ? 1 : 0) != 0, (Object)"permission");
            this.permissionType = permission;
            return this;
        }

        @Deprecated
        public Builder inSpaceGroup(SpaceGroup spaceGroup) {
            if (this.spaceGroup != null) {
                throw new IllegalStateException("You can not List more than one space group on a spaces query");
            }
            Preconditions.checkArgument((spaceGroup != null ? 1 : 0) != 0);
            this.spaceGroup = spaceGroup;
            return this;
        }

        public Builder sortBy(String sortColumn) {
            if (this.sortBy == null) {
                this.sortBy = new ArrayList<String>();
            }
            this.sortBy.add(sortColumn);
            return this;
        }

        public Builder unsorted() {
            this.sortBy = Collections.emptyList();
            return this;
        }

        public Builder createdAfter(Date date) {
            Preconditions.checkArgument((date != null ? 1 : 0) != 0, (Object)"Date must not be null");
            this.creationDate = date;
            return this;
        }

        public Builder withSpaceStatus(SpaceStatus status) {
            Preconditions.checkArgument((status != null ? 1 : 0) != 0, (Object)"Status must not be null");
            this.spaceStatuses.add(status);
            return this;
        }

        public SpacesQuery build() {
            if (this.sortBy == null) {
                this.sortBy = DEFAULT_SORT_BY;
            }
            return new SpacesQuery(this);
        }
    }
}


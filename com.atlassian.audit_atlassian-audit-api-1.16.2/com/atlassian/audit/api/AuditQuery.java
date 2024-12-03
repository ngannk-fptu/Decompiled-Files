/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.audit.api;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AuditQuery {
    private final Set<String> actions;
    private final Set<String> categories;
    private final Instant from;
    private final Instant to;
    private final List<AuditResourceIdentifier> resources;
    private final Set<String> userIds;
    private final String searchText;
    private final Long minId;
    private final Long maxId;

    private AuditQuery(Builder builder) {
        this.actions = builder.actions;
        this.categories = builder.categories;
        this.from = builder.from;
        this.to = builder.to;
        this.resources = builder.resources;
        this.userIds = builder.userIds;
        this.searchText = builder.searchText;
        this.minId = builder.minId;
        this.maxId = builder.maxId;
    }

    @Nonnull
    public Set<String> getActions() {
        return this.actions;
    }

    @Nonnull
    public Set<String> getCategories() {
        return this.categories;
    }

    @Nonnull
    public Optional<Instant> getFrom() {
        return Optional.ofNullable(this.from);
    }

    @Nonnull
    public Optional<Instant> getTo() {
        return Optional.ofNullable(this.to);
    }

    @Deprecated
    @Nonnull
    public Optional<String> getResourceId() {
        return this.resources.stream().findFirst().map(AuditResourceIdentifier::getId);
    }

    @Deprecated
    @Nonnull
    public Optional<String> getResourceType() {
        return this.resources.stream().findFirst().map(AuditResourceIdentifier::getType);
    }

    @Nonnull
    public Set<AuditResourceIdentifier> getResources() {
        return new HashSet<AuditResourceIdentifier>(this.resources);
    }

    @Nonnull
    public Set<String> getUserIds() {
        return this.userIds;
    }

    @Nonnull
    public Optional<String> getSearchText() {
        return Optional.ofNullable(this.searchText);
    }

    @Nonnull
    public Optional<Long> getMinId() {
        return Optional.ofNullable(this.minId);
    }

    @Nonnull
    public Optional<Long> getMaxId() {
        return Optional.ofNullable(this.maxId);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(AuditQuery query) {
        return new Builder(query);
    }

    public boolean hasFilter() {
        return !this.getActions().isEmpty() || !this.getCategories().isEmpty() || this.getFrom().isPresent() || this.getTo().isPresent() || !this.getResources().isEmpty() || !this.getUserIds().isEmpty() || this.getSearchText().isPresent();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditQuery query = (AuditQuery)o;
        return this.actions.equals(query.actions) && this.categories.equals(query.categories) && Objects.equals(this.from, query.from) && Objects.equals(this.to, query.to) && this.resources.equals(query.resources) && this.userIds.equals(query.userIds) && Objects.equals(this.searchText, query.searchText) && Objects.equals(this.minId, query.minId) && Objects.equals(this.maxId, query.maxId);
    }

    public int hashCode() {
        return Objects.hash(this.actions, this.categories, this.from, this.to, this.resources, this.userIds, this.searchText, this.minId, this.maxId);
    }

    public static class AuditResourceIdentifier {
        @Nonnull
        private final String type;
        @Nonnull
        private final String id;

        public AuditResourceIdentifier(@Nonnull String type, @Nonnull String id) {
            this.type = Objects.requireNonNull(type, "type");
            this.id = Objects.requireNonNull(id, "id");
        }

        @Nonnull
        public String getType() {
            return this.type;
        }

        @Nonnull
        public String getId() {
            return this.id;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            AuditResourceIdentifier that = (AuditResourceIdentifier)o;
            return this.type.equals(that.type) && this.id.equals(that.id);
        }

        public int hashCode() {
            return Objects.hash(this.type, this.id);
        }

        public String toString() {
            return "{type='" + this.type + '\'' + ", id='" + this.id + '\'' + '}';
        }
    }

    public static class Builder {
        private Set<String> actions = new HashSet<String>();
        private Set<String> categories = new HashSet<String>();
        private List<AuditResourceIdentifier> resources = new ArrayList<AuditResourceIdentifier>();
        private Set<String> userIds = new HashSet<String>();
        private Instant from;
        private Instant to;
        private String searchText;
        private Long minId;
        private Long maxId;

        private Builder() {
        }

        private Builder(AuditQuery query) {
            this.actions = query.getActions();
            this.categories = query.getCategories();
            this.resources = query.resources;
            this.userIds = query.getUserIds();
            this.from = query.getFrom().orElse(null);
            this.to = query.getTo().orElse(null);
            this.searchText = query.getSearchText().orElse(null);
            this.minId = query.getMinId().orElse(null);
            this.maxId = query.getMaxId().orElse(null);
        }

        public Builder actions(String ... actions) {
            if (actions != null && actions.length > 0) {
                this.actions = new HashSet<String>(Arrays.asList(actions));
            }
            return this;
        }

        public Builder from(@Nullable Instant from) {
            this.from = from;
            return this;
        }

        public Builder to(@Nullable Instant to) {
            this.to = to;
            return this;
        }

        public Builder categories(String ... categories) {
            if (categories != null && categories.length > 0) {
                this.categories = new HashSet<String>(Arrays.asList(categories));
            }
            return this;
        }

        public Builder resource(@Nonnull String type, @Nonnull String id) {
            Objects.requireNonNull(type, "type");
            Objects.requireNonNull(id, "id");
            this.resources.add(new AuditResourceIdentifier(type, id));
            return this;
        }

        public Builder resources(@Nonnull List<AuditResourceIdentifier> resources) {
            Objects.requireNonNull(resources, "resources");
            this.resources = new ArrayList<AuditResourceIdentifier>(resources);
            return this;
        }

        public Builder userIds(String ... userIds) {
            if (userIds != null && userIds.length > 0) {
                this.userIds = new HashSet<String>(Arrays.asList(userIds));
            }
            return this;
        }

        public Builder searchText(@Nullable String searchText) {
            this.searchText = searchText;
            return this;
        }

        public Builder minId(@Nonnull Long minId) {
            this.minId = Objects.requireNonNull(minId, "minId");
            return this;
        }

        public Builder maxId(@Nonnull Long maxId) {
            this.maxId = Objects.requireNonNull(maxId, "maxId");
            return this;
        }

        public AuditQuery build() {
            return new AuditQuery(this);
        }
    }
}


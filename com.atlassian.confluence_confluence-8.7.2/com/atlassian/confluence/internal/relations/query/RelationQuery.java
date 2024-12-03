/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.relations.RelationDescriptor
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.internal.relations.query;

import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.internal.relations.RelatableEntity;
import com.atlassian.confluence.internal.relations.RelatableEntityTypeEnum;
import com.atlassian.confluence.spaces.SpaceStatus;
import java.util.Set;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class RelationQuery<E extends RelatableEntity> {
    private final E entity;
    private final RelationDescriptor<?, ?> relationDescriptor;
    private final Set<String> spaceKeysFilter;
    private final Set<String> contentTypeFilters;
    private final Set<SpaceStatus> spaceStatusesFilter;
    private final boolean includeDeleted;

    public static <E extends RelatableEntity> Builder create(@NonNull E entity, @NonNull RelationDescriptor relationDescriptor) {
        return new Builder<E>(entity, relationDescriptor);
    }

    private RelationQuery(Builder<E> builder) {
        this.entity = builder.entity;
        this.relationDescriptor = builder.relationDescriptor;
        this.spaceKeysFilter = builder.spaceKeysFilter;
        this.contentTypeFilters = builder.contentTypeFilters;
        this.spaceStatusesFilter = builder.spaceStatusesFilter;
        this.includeDeleted = builder.includeDeleted;
    }

    public RelatableEntity getEntity() {
        return this.entity;
    }

    public RelationDescriptor<?, ?> getRelationDescriptor() {
        return this.relationDescriptor;
    }

    public Set<String> getSpaceKeysFilter() {
        return this.spaceKeysFilter;
    }

    public Set<String> getContentTypeFilters() {
        return this.contentTypeFilters;
    }

    public Set<SpaceStatus> getSpaceStatusesFilter() {
        return this.spaceStatusesFilter;
    }

    public boolean isIncludeDeleted() {
        return this.includeDeleted;
    }

    public static class Builder<R extends RelatableEntity> {
        private R entity;
        private RelationDescriptor<?, ?> relationDescriptor;
        private Set<String> spaceKeysFilter;
        private Set<String> contentTypeFilters;
        private Set<SpaceStatus> spaceStatusesFilter;
        private boolean includeDeleted;

        public Builder(@NonNull R entity, @NonNull RelationDescriptor<?, ?> relationDescriptor) {
            this.entity = entity;
            this.relationDescriptor = relationDescriptor;
        }

        public Builder entity(@NonNull R entity) {
            this.entity = entity;
            return this;
        }

        public Builder relation(@NonNull RelationDescriptor<?, ?> relationDescriptor) {
            this.relationDescriptor = relationDescriptor;
            return this;
        }

        public Builder spaceKeysFilter(@Nullable Set<String> spaceKeysFilter) {
            this.spaceKeysFilter = spaceKeysFilter != null && !spaceKeysFilter.isEmpty() ? spaceKeysFilter : null;
            return this;
        }

        public Builder contentTypeFilters(@Nullable Set<RelatableEntityTypeEnum> contentTypeFilters) {
            this.contentTypeFilters = contentTypeFilters != null && !contentTypeFilters.isEmpty() ? contentTypeFilters.stream().map(Enum::name).collect(Collectors.toSet()) : null;
            return this;
        }

        public Builder spaceStatusesFilter(@Nullable Set<SpaceStatus> spaceStatusesFilter) {
            this.spaceStatusesFilter = spaceStatusesFilter != null && !spaceStatusesFilter.isEmpty() ? spaceStatusesFilter : null;
            return this;
        }

        public Builder includeDeleted(boolean includeDeleted) {
            this.includeDeleted = includeDeleted;
            return this;
        }

        public RelationQuery<R> build() {
            return new RelationQuery(this);
        }
    }
}


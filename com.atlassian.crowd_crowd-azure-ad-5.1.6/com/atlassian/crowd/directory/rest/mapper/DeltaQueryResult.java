/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.directory.rest.mapper;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;

public class DeltaQueryResult<T> {
    private static final DeltaQueryResult EMPTY = DeltaQueryResult.builder().build();
    private final List<T> changedEntities;
    private final Set<String> deletedEntities;
    private final Set<String> namelessEntities;
    @Nullable
    private final String syncToken;

    public Builder<T> toBuilder() {
        return DeltaQueryResult.builder(this);
    }

    public static <T> Builder<T> builder(String syncToken) {
        return DeltaQueryResult.builder().setSyncToken(syncToken);
    }

    public static <T> DeltaQueryResult<T> empty() {
        return EMPTY;
    }

    protected DeltaQueryResult(Iterable<T> changedEntities, Iterable<String> deletedEntities, Iterable<String> namelessEntities, @Nullable String syncToken) {
        this.changedEntities = ImmutableList.copyOf(changedEntities);
        this.deletedEntities = ImmutableSet.copyOf(deletedEntities);
        this.namelessEntities = ImmutableSet.copyOf(namelessEntities);
        this.syncToken = syncToken;
    }

    public List<T> getChangedEntities() {
        return this.changedEntities;
    }

    public Set<String> getDeletedEntities() {
        return this.deletedEntities;
    }

    public Set<String> getNamelessEntities() {
        return this.namelessEntities;
    }

    public Optional<String> getSyncToken() {
        return Optional.ofNullable(this.syncToken);
    }

    public static <T> Builder<T> builder() {
        return new Builder();
    }

    public static <T> Builder<T> builder(DeltaQueryResult<T> data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DeltaQueryResult that = (DeltaQueryResult)o;
        return Objects.equals(this.getChangedEntities(), that.getChangedEntities()) && Objects.equals(this.getDeletedEntities(), that.getDeletedEntities()) && Objects.equals(this.getNamelessEntities(), that.getNamelessEntities()) && Objects.equals(this.getSyncToken(), that.getSyncToken());
    }

    public int hashCode() {
        return Objects.hash(this.getChangedEntities(), this.getDeletedEntities(), this.getNamelessEntities(), this.getSyncToken());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("changedEntities", this.getChangedEntities()).add("deletedEntities", this.getDeletedEntities()).add("namelessEntities", this.getNamelessEntities()).add("syncToken", this.getSyncToken()).toString();
    }

    public static final class Builder<T> {
        private List<T> changedEntities = new ArrayList<T>();
        private Set<String> deletedEntities = new HashSet<String>();
        private Set<String> namelessEntities = new HashSet<String>();
        private String syncToken;

        private Builder() {
        }

        private Builder(DeltaQueryResult<T> initialData) {
            this.changedEntities = new ArrayList<T>(initialData.getChangedEntities());
            this.deletedEntities = new HashSet<String>(initialData.getDeletedEntities());
            this.namelessEntities = new HashSet<String>(initialData.getNamelessEntities());
            this.syncToken = initialData.getSyncToken().orElse(null);
        }

        public Builder<T> setChangedEntities(List<T> changedEntities) {
            this.changedEntities = changedEntities;
            return this;
        }

        public Builder<T> addChangedEntity(T changedEntity) {
            this.changedEntities.add(changedEntity);
            return this;
        }

        public Builder<T> addChangedEntities(Iterable<T> changedEntities) {
            for (T changedEntity : changedEntities) {
                this.addChangedEntity(changedEntity);
            }
            return this;
        }

        public Builder<T> setDeletedEntities(Set<String> deletedEntities) {
            this.deletedEntities = deletedEntities;
            return this;
        }

        public Builder<T> addDeletedEntity(String deletedEntity) {
            this.deletedEntities.add(deletedEntity);
            return this;
        }

        public Builder<T> addDeletedEntities(Iterable<String> deletedEntities) {
            for (String deletedEntity : deletedEntities) {
                this.addDeletedEntity(deletedEntity);
            }
            return this;
        }

        public Builder<T> setNamelessEntities(Set<String> namelessEntities) {
            this.namelessEntities = namelessEntities;
            return this;
        }

        public Builder<T> addNamelessEntity(String namelessEntity) {
            this.namelessEntities.add(namelessEntity);
            return this;
        }

        public Builder<T> addNamelessEntities(Iterable<String> namelessEntities) {
            for (String namelessEntity : namelessEntities) {
                this.addNamelessEntity(namelessEntity);
            }
            return this;
        }

        public Builder<T> setSyncToken(@Nullable String syncToken) {
            this.syncToken = syncToken;
            return this;
        }

        public DeltaQueryResult<T> build() {
            return new DeltaQueryResult<T>(this.changedEntities, this.deletedEntities, this.namelessEntities, this.syncToken);
        }
    }
}


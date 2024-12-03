/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.crowd.manager.directory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;

public class BulkAddResult<T> {
    private final Collection<T> failedEntities;
    private final Collection<T> existingEntities;
    private final long attemptedToAdd;
    private final boolean overwriteUsed;

    private BulkAddResult(Collection<T> failedEntities, Collection<T> existingEntities, long attemptingToAdd, boolean overwrite) {
        this.failedEntities = ImmutableList.copyOf(failedEntities);
        this.existingEntities = ImmutableList.copyOf(existingEntities);
        this.attemptedToAdd = attemptingToAdd;
        this.overwriteUsed = overwrite;
        Preconditions.checkArgument((attemptingToAdd >= (long)(failedEntities.size() + existingEntities.size()) ? 1 : 0) != 0, (Object)"Should have attempted to add at least as many entities as we have failed plus existing entities");
    }

    public Collection<T> getFailedEntities() {
        return this.failedEntities;
    }

    public Collection<T> getExistingEntities() {
        return this.existingEntities;
    }

    public boolean isOverwriteUsed() {
        return this.overwriteUsed;
    }

    public long getAttemptedToAdd() {
        return this.attemptedToAdd;
    }

    public long getAddedSuccessfully() {
        return this.attemptedToAdd - (long)this.failedEntities.size() - (long)this.existingEntities.size();
    }

    public static <T> Builder<T> builder(long attemptingToAdd) {
        return new Builder(attemptingToAdd);
    }

    public static class Builder<T> {
        private Collection<T> failedEntities = new ArrayList<T>();
        private Collection<T> existingEntities = new ArrayList<T>();
        private long attemptingToAdd;
        private boolean overwrite;

        public Builder(long attemptingToAdd) {
            this.attemptingToAdd = attemptingToAdd;
        }

        public Builder<T> setOverwrite(boolean overwrite) {
            this.overwrite = overwrite;
            return this;
        }

        public Builder<T> addFailedEntities(Collection<T> entities) {
            this.failedEntities.addAll(entities);
            return this;
        }

        public Builder<T> addFailedEntity(T entity) {
            this.failedEntities.add(entity);
            return this;
        }

        public Builder<T> addExistingEntities(Collection<T> entities) {
            this.existingEntities.addAll(entities);
            return this;
        }

        public Builder<T> addExistingEntity(T entity) {
            this.existingEntities.add(entity);
            return this;
        }

        public BulkAddResult<T> build() {
            return new BulkAddResult(this.failedEntities, this.existingEntities, this.attemptingToAdd, this.overwrite);
        }
    }
}


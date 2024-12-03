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

public class BulkRemoveResult<T> {
    private final Collection<T> failedEntities;
    private final Collection<T> missingEntities;
    private final long attemptedToRemove;

    private BulkRemoveResult(Collection<T> failedEntities, Collection<T> missingEntities, long attemptingToRemove) {
        this.failedEntities = ImmutableList.copyOf(failedEntities);
        this.missingEntities = ImmutableList.copyOf(missingEntities);
        this.attemptedToRemove = attemptingToRemove;
        Preconditions.checkArgument((attemptingToRemove >= (long)(failedEntities.size() + missingEntities.size()) ? 1 : 0) != 0, (Object)"Should have attempted to remove at least as many entities as we have failed plus missing entities");
    }

    public Collection<T> getFailedEntities() {
        return this.failedEntities;
    }

    public Collection<T> getMissingEntities() {
        return this.missingEntities;
    }

    public long getAttemptedToRemove() {
        return this.attemptedToRemove;
    }

    public long getRemovedSuccessfully() {
        return this.attemptedToRemove - (long)this.failedEntities.size() - (long)this.missingEntities.size();
    }

    public static <T> Builder<T> builder(long attemptingToRemove) {
        return new Builder(attemptingToRemove);
    }

    public static class Builder<T> {
        private long attemptingToRemove;
        private Collection<T> failedEntities = new ArrayList<T>();
        private Collection<T> missingEntities = new ArrayList<T>();

        public Builder(long attemptingToRemove) {
            this.attemptingToRemove = attemptingToRemove;
        }

        public Builder<T> addFailedEntities(Collection<T> entities) {
            this.failedEntities.addAll(entities);
            return this;
        }

        public Builder<T> addFailedEntity(T entity) {
            this.failedEntities.add(entity);
            return this;
        }

        public Builder<T> addMissingEntities(Collection<T> entities) {
            this.missingEntities.addAll(entities);
            return this;
        }

        public Builder<T> addMissingEntity(T entity) {
            this.missingEntities.add(entity);
            return this;
        }

        public BulkRemoveResult<T> build() {
            return new BulkRemoveResult(this.failedEntities, this.missingEntities, this.attemptingToRemove);
        }
    }
}


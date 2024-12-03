/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ProgressMeter
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.pages.persistence.dao.bulk;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.util.ProgressMeter;
import com.google.common.base.Preconditions;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DefaultBulkOptions {
    public static final String DEFAULT_BATCH_SIZE_PROPERTY = "confluence.cph.batch.size";
    public static final String MAX_PROCESSED_ENTRIES_PROPERTY = "confluence.cph.max.entries";
    private static final int DEFAULT_BATCH_SIZE = Integer.getInteger("confluence.cph.batch.size", 10);
    private static final int DEFAULT_MAX_PROCESSED_ENTRIES = Integer.getInteger("confluence.cph.max.entries", 2000);
    private final int maxProcessedEntries;
    private final int batchSize;
    private final ConfluenceUser user;
    private final ProgressMeter progressMeter;

    public DefaultBulkOptions() {
        this.maxProcessedEntries = DEFAULT_MAX_PROCESSED_ENTRIES;
        this.batchSize = DEFAULT_BATCH_SIZE;
        this.user = null;
        this.progressMeter = new ProgressMeter();
    }

    protected DefaultBulkOptions(int maxProcessedEntries, int batchSize, ConfluenceUser user, ProgressMeter progressMeter) {
        this.maxProcessedEntries = maxProcessedEntries;
        this.batchSize = batchSize;
        this.user = user;
        this.progressMeter = progressMeter;
    }

    protected DefaultBulkOptions(DefaultBulkOptions options) {
        this.maxProcessedEntries = options.getMaxProcessedEntries();
        this.batchSize = options.getBatchSize();
        this.user = options.getUser();
        this.progressMeter = options.getProgressMeter();
    }

    public int getMaxProcessedEntries() {
        return this.maxProcessedEntries;
    }

    public int getBatchSize() {
        return this.batchSize;
    }

    public @Nullable ConfluenceUser getUser() {
        return this.user;
    }

    public ProgressMeter getProgressMeter() {
        return this.progressMeter;
    }

    public static Builder defaultBuilder() {
        return new Builder();
    }

    protected static class Builder
    extends BaseBuilder<Builder> {
        protected Builder() {
        }

        @Override
        protected final Builder builder() {
            return this;
        }

        public DefaultBulkOptions build() {
            return super.buildDefault();
        }
    }

    public static abstract class BaseBuilder<T extends BaseBuilder> {
        protected int maxProcessedEntries;
        protected int batchSize;
        protected ConfluenceUser user;
        protected ProgressMeter progressMeter;

        protected abstract T builder();

        public T withMaxProcessedEntries(int maxProcessedEntries) {
            this.maxProcessedEntries = maxProcessedEntries;
            return this.builder();
        }

        public T withBatchSize(int batchSize) {
            this.batchSize = batchSize;
            return this.builder();
        }

        public T withUser(ConfluenceUser user) {
            this.user = user;
            return this.builder();
        }

        public T withProgressMeter(ProgressMeter progressMeter) {
            this.progressMeter = progressMeter;
            return this.builder();
        }

        protected final DefaultBulkOptions buildDefault() {
            Preconditions.checkNotNull((Object)this.progressMeter);
            return new DefaultBulkOptions(this.maxProcessedEntries <= 0 ? DEFAULT_MAX_PROCESSED_ENTRIES : this.maxProcessedEntries, this.batchSize <= 0 ? DEFAULT_BATCH_SIZE : this.batchSize, this.user, this.progressMeter);
        }
    }
}


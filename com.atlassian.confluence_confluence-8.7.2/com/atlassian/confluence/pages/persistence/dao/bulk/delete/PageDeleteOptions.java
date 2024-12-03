/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 */
package com.atlassian.confluence.pages.persistence.dao.bulk.delete;

import com.atlassian.confluence.pages.persistence.dao.bulk.DefaultBulkOptions;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Set;

public class PageDeleteOptions
extends DefaultBulkOptions {
    private final ImmutableSet<Long> targetPageIds;

    private PageDeleteOptions(DefaultBulkOptions defaultBulkOptions, ImmutableSet<Long> targetPageIds) {
        super(defaultBulkOptions);
        this.targetPageIds = targetPageIds;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Set<Long> getTargetPageIds() {
        return this.targetPageIds;
    }

    public static class Builder
    extends DefaultBulkOptions.BaseBuilder<Builder> {
        private ImmutableSet.Builder<Long> targetPageIds = new ImmutableSet.Builder();

        public Builder withPageId(Long ... pageIds) {
            this.targetPageIds.add((Object[])pageIds);
            return this;
        }

        public Builder withPageIds(Collection<Long> pageIds) {
            this.targetPageIds.addAll(pageIds);
            return this;
        }

        @Override
        protected final Builder builder() {
            return this;
        }

        public PageDeleteOptions build() {
            return new PageDeleteOptions(super.buildDefault(), (ImmutableSet<Long>)this.targetPageIds.build());
        }
    }
}


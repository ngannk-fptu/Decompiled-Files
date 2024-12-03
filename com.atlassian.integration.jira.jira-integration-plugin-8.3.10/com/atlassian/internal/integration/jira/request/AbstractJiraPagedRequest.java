/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.internal.integration.jira.request;

import java.util.OptionalInt;
import javax.annotation.Nonnull;

public abstract class AbstractJiraPagedRequest {
    private final Integer maxResults;
    private final Integer startAt;

    protected AbstractJiraPagedRequest(AbstractBuilder builder) {
        this.maxResults = builder.maxResults;
        this.startAt = builder.startAt;
    }

    @Nonnull
    public OptionalInt getMaxResults() {
        return this.maxResults == null ? OptionalInt.empty() : OptionalInt.of(this.maxResults);
    }

    @Nonnull
    public OptionalInt getStartAt() {
        return this.startAt == null ? OptionalInt.empty() : OptionalInt.of(this.startAt);
    }

    public static abstract class AbstractBuilder<B extends AbstractBuilder<B, R>, R extends AbstractJiraPagedRequest> {
        protected Integer maxResults;
        protected Integer startAt;

        @Nonnull
        public abstract R build();

        @Nonnull
        public B maxResults(int maxResults) {
            this.maxResults = this.requirePositiveOrNullIfZero(maxResults, "maxResults");
            return this.self();
        }

        @Nonnull
        public B startAt(int startAt) {
            this.startAt = this.requirePositiveOrNullIfZero(startAt, "startAt");
            return this.self();
        }

        @Nonnull
        protected abstract B self();

        private Integer requirePositiveOrNullIfZero(int value, String name) {
            if (value < 0) {
                throw new IllegalArgumentException(name + " must be zero or positive");
            }
            if (value == 0) {
                return null;
            }
            return value;
        }
    }
}


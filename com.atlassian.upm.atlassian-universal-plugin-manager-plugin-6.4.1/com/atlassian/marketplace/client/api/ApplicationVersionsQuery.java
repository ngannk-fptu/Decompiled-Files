/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.api.HostingType;
import com.atlassian.marketplace.client.api.QueryBounds;
import com.atlassian.marketplace.client.api.QueryBuilderProperties;
import com.atlassian.marketplace.client.api.QueryProperties;
import com.atlassian.marketplace.client.util.Convert;
import com.google.common.base.Preconditions;
import java.util.Optional;

public final class ApplicationVersionsQuery
implements QueryProperties.Bounds,
QueryProperties.Hosting {
    private static final ApplicationVersionsQuery DEFAULT_QUERY = ApplicationVersionsQuery.builder().build();
    private final Optional<Integer> afterBuildNumber;
    private final Optional<HostingType> hosting;
    private final QueryBounds bounds;

    public static Builder builder() {
        return new Builder();
    }

    public static ApplicationVersionsQuery any() {
        return DEFAULT_QUERY;
    }

    public static Builder builder(ApplicationVersionsQuery query) {
        Builder builder = ((Builder)ApplicationVersionsQuery.builder().afterBuildNumber(query.safeGetAfterBuildNumber()).hosting((Optional)query.safeGetHosting())).bounds(query.getBounds());
        return builder;
    }

    private ApplicationVersionsQuery(Builder builder) {
        this.afterBuildNumber = builder.afterBuildNumber;
        this.hosting = builder.hosting;
        this.bounds = builder.bounds;
    }

    public Optional<Integer> safeGetAfterBuildNumber() {
        return this.afterBuildNumber;
    }

    @Override
    public Optional<HostingType> safeGetHosting() {
        return this.hosting;
    }

    @Override
    public QueryBounds getBounds() {
        return this.bounds;
    }

    public String toString() {
        return QueryProperties.describeParams("ApplicationVersionsQuery", QueryProperties.describeValues("afterBuildNumber", Convert.iterableOf(this.afterBuildNumber)), QueryProperties.describeValues("hosting", Convert.iterableOf(this.hosting)), this.bounds.describe());
    }

    public boolean equals(Object other) {
        return other instanceof ApplicationVersionsQuery ? this.toString().equals(other.toString()) : false;
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public static class Builder
    implements QueryBuilderProperties.Bounds<Builder>,
    QueryBuilderProperties.Hosting<Builder> {
        private Optional<Integer> afterBuildNumber = Optional.empty();
        private Optional<HostingType> hosting = Optional.empty();
        private QueryBounds bounds = QueryBounds.defaultBounds();

        public ApplicationVersionsQuery build() {
            return new ApplicationVersionsQuery(this);
        }

        public Builder afterBuildNumber(Optional<Integer> afterBuildNumber) {
            this.afterBuildNumber = (Optional)Preconditions.checkNotNull(afterBuildNumber);
            return this;
        }

        @Override
        public Builder hosting(Optional<HostingType> hosting) {
            this.hosting = (Optional)Preconditions.checkNotNull(hosting);
            return this;
        }

        @Override
        public Builder bounds(QueryBounds bounds) {
            this.bounds = (QueryBounds)Preconditions.checkNotNull((Object)bounds);
            return this;
        }
    }
}


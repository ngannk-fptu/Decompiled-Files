/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.api.ApplicationKey;
import com.atlassian.marketplace.client.api.Cost;
import com.atlassian.marketplace.client.api.HostingType;
import com.atlassian.marketplace.client.api.QueryBounds;
import com.atlassian.marketplace.client.api.QueryBuilderProperties;
import com.atlassian.marketplace.client.api.QueryProperties;
import com.atlassian.marketplace.client.util.Convert;
import com.google.common.base.Preconditions;
import java.util.Optional;

public final class ProductQuery
implements QueryProperties.ApplicationCriteria,
QueryProperties.Bounds,
QueryProperties.Cost,
QueryProperties.Hosting,
QueryProperties.WithVersion {
    private static final ProductQuery DEFAULT_QUERY = ProductQuery.builder().build();
    private final QueryBuilderProperties.ApplicationCriteriaHelper app;
    private final Optional<Cost> cost;
    private final Optional<HostingType> hosting;
    private final boolean withVersion;
    private final QueryBounds bounds;

    public static Builder builder() {
        return new Builder();
    }

    public static ProductQuery any() {
        return DEFAULT_QUERY;
    }

    public static Builder builder(ProductQuery query) {
        Builder builder = ((Builder)((Builder)((Builder)((Builder)ProductQuery.builder().application((Optional)query.safeGetApplication())).appBuildNumber((Optional)query.safeGetAppBuildNumber())).cost((Optional)query.safeGetCost())).hosting((Optional)query.safeGetHosting())).withVersion(query.isWithVersion()).bounds(query.getBounds());
        return builder;
    }

    private ProductQuery(Builder builder) {
        this.app = builder.app;
        this.cost = builder.cost;
        this.hosting = builder.hosting;
        this.withVersion = builder.withVersion;
        this.bounds = builder.bounds;
    }

    @Override
    public Optional<ApplicationKey> safeGetApplication() {
        return this.app.application;
    }

    @Override
    public Optional<Integer> safeGetAppBuildNumber() {
        return this.app.appBuildNumber;
    }

    @Override
    public Optional<Cost> safeGetCost() {
        return this.cost;
    }

    @Override
    public Optional<HostingType> safeGetHosting() {
        return this.hosting;
    }

    @Override
    public boolean isWithVersion() {
        return this.withVersion;
    }

    @Override
    public QueryBounds getBounds() {
        return this.bounds;
    }

    public String toString() {
        return QueryProperties.describeParams("ProductQuery", this.app.describe(), QueryProperties.describeOptEnum("cost", Convert.iterableOf(this.cost)), QueryProperties.describeOptEnum("hosting", Convert.iterableOf(this.hosting)), QueryProperties.describeOptBoolean("withVersion", this.withVersion), this.bounds.describe());
    }

    public boolean equals(Object other) {
        return other instanceof ProductQuery ? this.toString().equals(other.toString()) : false;
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public static class Builder
    implements QueryBuilderProperties.ApplicationCriteria<Builder>,
    QueryBuilderProperties.Bounds<Builder>,
    QueryBuilderProperties.Cost<Builder>,
    QueryBuilderProperties.Hosting<Builder>,
    QueryBuilderProperties.WithVersion<Builder> {
        private QueryBuilderProperties.ApplicationCriteriaHelper app = new QueryBuilderProperties.ApplicationCriteriaHelper();
        private Optional<Cost> cost = Optional.empty();
        private Optional<HostingType> hosting = Optional.empty();
        private boolean withVersion = false;
        private QueryBounds bounds = QueryBounds.defaultBounds();

        public ProductQuery build() {
            return new ProductQuery(this);
        }

        @Override
        public Builder application(Optional<ApplicationKey> application) {
            this.app = this.app.application(application);
            return this;
        }

        @Override
        public Builder appBuildNumber(Optional<Integer> appBuildNumber) {
            this.app = this.app.appBuildNumber(appBuildNumber);
            return this;
        }

        @Override
        public Builder cost(Optional<Cost> cost) {
            this.cost = (Optional)Preconditions.checkNotNull(cost);
            return this;
        }

        @Override
        public Builder hosting(Optional<HostingType> hosting) {
            this.hosting = (Optional)Preconditions.checkNotNull(hosting);
            return this;
        }

        @Override
        public Builder withVersion(boolean withVersion) {
            this.withVersion = withVersion;
            return this;
        }

        @Override
        public Builder bounds(QueryBounds bounds) {
            this.bounds = (QueryBounds)Preconditions.checkNotNull((Object)bounds);
            return this;
        }
    }
}


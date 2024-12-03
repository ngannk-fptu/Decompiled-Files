/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.api.AddonQuery;
import com.atlassian.marketplace.client.api.ApplicationKey;
import com.atlassian.marketplace.client.api.Cost;
import com.atlassian.marketplace.client.api.HostingType;
import com.atlassian.marketplace.client.api.QueryBounds;
import com.atlassian.marketplace.client.api.QueryBuilderProperties;
import com.atlassian.marketplace.client.api.QueryProperties;
import com.atlassian.marketplace.client.util.Convert;
import com.google.common.base.Preconditions;
import java.util.Optional;

public final class AddonVersionsQuery
implements QueryProperties.AccessToken,
QueryProperties.ApplicationCriteria,
QueryProperties.Bounds,
QueryProperties.Cost,
QueryProperties.Hosting,
QueryProperties.IncludePrivate {
    private static final AddonVersionsQuery DEFAULT_QUERY = AddonVersionsQuery.builder().build();
    private final boolean includePrivate;
    private final Optional<String> accessToken;
    private final QueryBuilderProperties.ApplicationCriteriaHelper app;
    private final Optional<Cost> cost;
    private final Optional<HostingType> hosting;
    private final QueryBounds bounds;
    private final Optional<String> afterVersionName;

    public static Builder builder() {
        return new Builder();
    }

    public static AddonVersionsQuery any() {
        return DEFAULT_QUERY;
    }

    public static Builder fromAddonQuery(AddonQuery aq) {
        return ((Builder)((Builder)((Builder)((Builder)((Builder)AddonVersionsQuery.builder().accessToken((Optional)aq.safeGetAccessToken())).application((Optional)aq.safeGetApplication())).appBuildNumber((Optional)aq.safeGetAppBuildNumber())).cost((Optional)aq.safeGetCost())).hosting((Optional)aq.safeGetHosting())).bounds(aq.getBounds());
    }

    public static Builder builder(AddonVersionsQuery query) {
        Builder builder = ((Builder)((Builder)((Builder)((Builder)AddonVersionsQuery.builder().application((Optional)query.safeGetApplication())).appBuildNumber((Optional)query.safeGetAppBuildNumber())).cost((Optional)query.safeGetCost())).hosting((Optional)query.safeGetHosting())).bounds(query.getBounds());
        return builder;
    }

    private AddonVersionsQuery(Builder builder) {
        this.accessToken = builder.accessToken;
        this.app = builder.app;
        this.cost = builder.cost;
        this.hosting = builder.hosting;
        this.bounds = builder.bounds;
        this.afterVersionName = builder.afterVersionName;
        this.includePrivate = builder.includePrivate;
    }

    @Override
    public Optional<String> safeGetAccessToken() {
        return this.accessToken;
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
    public QueryBounds getBounds() {
        return this.bounds;
    }

    public Optional<String> safeGetAfterVersionName() {
        return this.afterVersionName;
    }

    @Override
    public boolean isIncludePrivate() {
        return this.includePrivate;
    }

    public String toString() {
        return QueryProperties.describeParams("AddonVersionsQuery", QueryProperties.describeValues("accessToken", Convert.iterableOf(this.accessToken)), this.app.describe(), QueryProperties.describeOptEnum("cost", Convert.iterableOf(this.cost)), QueryProperties.describeOptEnum("hosting", Convert.iterableOf(this.hosting)), this.bounds.describe(), QueryProperties.describeValues("afterVersion", Convert.iterableOf(this.afterVersionName)));
    }

    public boolean equals(Object other) {
        return other instanceof AddonVersionsQuery && this.toString().equals(other.toString());
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public static class Builder
    implements QueryBuilderProperties.AccessToken<Builder>,
    QueryBuilderProperties.ApplicationCriteria<Builder>,
    QueryBuilderProperties.Bounds<Builder>,
    QueryBuilderProperties.Cost<Builder>,
    QueryBuilderProperties.Hosting<Builder>,
    QueryBuilderProperties.IncludePrivate<Builder> {
        private boolean includePrivate;
        private Optional<String> accessToken = Optional.empty();
        private QueryBuilderProperties.ApplicationCriteriaHelper app = new QueryBuilderProperties.ApplicationCriteriaHelper();
        private Optional<Cost> cost = Optional.empty();
        private Optional<HostingType> hosting = Optional.empty();
        private QueryBounds bounds = QueryBounds.defaultBounds();
        private Optional<String> afterVersionName = Optional.empty();

        public AddonVersionsQuery build() {
            return new AddonVersionsQuery(this);
        }

        @Override
        public Builder accessToken(Optional<String> accessToken) {
            this.accessToken = (Optional)Preconditions.checkNotNull(accessToken);
            return this;
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
        public Builder includePrivate(boolean includePrivate) {
            this.includePrivate = includePrivate;
            return this;
        }

        public Builder afterVersion(Optional<String> versionName) {
            this.afterVersionName = (Optional)Preconditions.checkNotNull(versionName);
            return this;
        }

        @Override
        public Builder bounds(QueryBounds bounds) {
            this.bounds = (QueryBounds)Preconditions.checkNotNull((Object)bounds);
            return this;
        }
    }
}


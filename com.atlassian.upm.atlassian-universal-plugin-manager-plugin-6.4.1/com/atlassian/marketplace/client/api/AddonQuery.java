/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.api.ApplicationKey;
import com.atlassian.marketplace.client.api.Cost;
import com.atlassian.marketplace.client.api.EnumWithKey;
import com.atlassian.marketplace.client.api.HostingType;
import com.atlassian.marketplace.client.api.QueryBounds;
import com.atlassian.marketplace.client.api.QueryBuilderProperties;
import com.atlassian.marketplace.client.api.QueryProperties;
import com.atlassian.marketplace.client.util.Convert;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;

public final class AddonQuery
implements QueryProperties.AccessToken,
QueryProperties.ApplicationCriteria,
QueryProperties.Bounds,
QueryProperties.Cost,
QueryProperties.MultiHosting,
QueryProperties.IncludePrivate,
QueryProperties.WithVersion {
    private static final AddonQuery DEFAULT_QUERY = AddonQuery.builder().build();
    private final Optional<String> accessToken;
    private final QueryBuilderProperties.ApplicationCriteriaHelper app;
    private final QueryBounds bounds;
    private final Iterable<String> categoryNames;
    private final Optional<Cost> cost;
    private final boolean forThisUserOnly;
    private final List<HostingType> hosting;
    private final Optional<IncludeHiddenType> includeHidden;
    private final boolean includePrivate;
    private final Optional<String> label;
    private final Optional<TreatPartlyFreeAs> treatPartlyFreeAs;
    private final Optional<String> searchText;
    private final Optional<View> view;
    private final boolean withVersion;

    public static Builder builder() {
        return new Builder();
    }

    public static AddonQuery any() {
        return DEFAULT_QUERY;
    }

    public static Builder builder(AddonQuery query) {
        Builder builder = ((Builder)((Builder)((Builder)((Builder)AddonQuery.builder().application((Optional)query.safeGetApplication())).appBuildNumber((Optional)query.safeGetAppBuildNumber())).categoryNames(query.getCategoryNames()).cost((Optional)query.safeGetCost())).forThisUserOnly(query.isForThisUserOnly()).hosting((List)query.getHostings())).includeHidden(query.safeGetIncludeHidden()).includePrivate(query.isIncludePrivate()).label(query.safeGetLabel()).treatPartlyFreeAs(query.safeGetTreatPartlyFreeAs()).view(query.safeGetView()).withVersion(query.isWithVersion()).bounds(query.getBounds()).searchText(query.safeGetSearchText());
        return builder;
    }

    private AddonQuery(Builder builder) {
        this.accessToken = builder.accessToken;
        this.app = builder.app;
        this.bounds = builder.bounds;
        this.categoryNames = builder.categoryNames;
        this.cost = builder.cost;
        this.forThisUserOnly = builder.forThisUserOnly;
        this.hosting = builder.hosting;
        this.includeHidden = builder.includeHidden;
        this.includePrivate = builder.includePrivate;
        this.label = builder.label;
        this.searchText = builder.searchText;
        this.treatPartlyFreeAs = builder.treatPartlyFreeAs;
        this.view = builder.view;
        this.withVersion = builder.withVersion;
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

    public Iterable<String> getCategoryNames() {
        return this.categoryNames;
    }

    @Override
    public Optional<Cost> safeGetCost() {
        return this.cost;
    }

    public boolean isForThisUserOnly() {
        return this.forThisUserOnly;
    }

    public Optional<IncludeHiddenType> safeGetIncludeHidden() {
        return this.includeHidden;
    }

    @Override
    public boolean isIncludePrivate() {
        return this.includePrivate;
    }

    public Optional<HostingType> safeGetHosting() {
        return this.hosting.stream().findFirst();
    }

    @Override
    public List<HostingType> getHostings() {
        return this.hosting;
    }

    public Optional<String> safeGetLabel() {
        return this.label;
    }

    public Optional<TreatPartlyFreeAs> safeGetTreatPartlyFreeAs() {
        return this.treatPartlyFreeAs;
    }

    public Optional<String> safeGetSearchText() {
        return this.searchText;
    }

    public Optional<View> safeGetView() {
        return this.view;
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
        return QueryProperties.describeParams("AddonQuery", QueryProperties.describeValues("accessToken", Convert.iterableOf(this.accessToken)), this.app.describe(), QueryProperties.describeValues("categoryNames", this.categoryNames), QueryProperties.describeOptEnum("cost", Convert.iterableOf(this.cost)), QueryProperties.describeOptBoolean("forThisUserOnly", this.forThisUserOnly), QueryProperties.describeValues("hosting", this.hosting), QueryProperties.describeOptEnum("includeHidden", Convert.iterableOf(this.includeHidden)), QueryProperties.describeOptBoolean("includePrivate", this.includePrivate), QueryProperties.describeValues("label", Convert.iterableOf(this.label)), QueryProperties.describeValues("searchText", Convert.iterableOf(this.searchText)), QueryProperties.describeOptEnum("treatPartlyFreeAs", Convert.iterableOf(this.treatPartlyFreeAs)), QueryProperties.describeOptEnum("view", Convert.iterableOf(this.view)), QueryProperties.describeOptBoolean("withVersion", this.withVersion), this.bounds.describe());
    }

    public boolean equals(Object other) {
        return other instanceof AddonQuery ? this.toString().equals(other.toString()) : false;
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public static enum View implements EnumWithKey
    {
        BY_ATLASSIAN("atlassian"),
        FEATURED("featured"),
        HIGHEST_RATED("highest-rated"),
        POPULAR("popular"),
        RECENTLY_UPDATED("recent"),
        TOP_GROSSING("top-grossing"),
        TRENDING("trending"),
        VERIFIED("verified"),
        TOP_VENDOR("top-vendor");

        private final String key;

        private View(String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return this.key;
        }
    }

    public static enum TreatPartlyFreeAs implements EnumWithKey
    {
        FREE("free"),
        PAID("paid");

        private final String key;

        private TreatPartlyFreeAs(String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return this.key;
        }
    }

    public static enum IncludeHiddenType implements EnumWithKey
    {
        VISIBLE_IN_APP("visibleInApp"),
        ALL("all");

        private final String key;

        private IncludeHiddenType(String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return this.key;
        }
    }

    public static class Builder
    implements QueryBuilderProperties.AccessToken<Builder>,
    QueryBuilderProperties.ApplicationCriteria<Builder>,
    QueryBuilderProperties.Bounds<Builder>,
    QueryBuilderProperties.Cost<Builder>,
    QueryBuilderProperties.MultiHosting<Builder>,
    QueryBuilderProperties.WithVersion<Builder> {
        private Optional<String> accessToken = Optional.empty();
        private QueryBuilderProperties.ApplicationCriteriaHelper app = new QueryBuilderProperties.ApplicationCriteriaHelper();
        private QueryBounds bounds = QueryBounds.defaultBounds();
        private Iterable<String> categoryNames = ImmutableList.of();
        private Optional<Cost> cost = Optional.empty();
        private boolean forThisUserOnly = false;
        private List<HostingType> hosting = ImmutableList.of();
        private Optional<IncludeHiddenType> includeHidden = Optional.empty();
        private boolean includePrivate = false;
        private Optional<String> label = Optional.empty();
        private Optional<TreatPartlyFreeAs> treatPartlyFreeAs = Optional.empty();
        private Optional<String> searchText = Optional.empty();
        private Optional<View> view = Optional.empty();
        private boolean withVersion = false;

        public AddonQuery build() {
            return new AddonQuery(this);
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

        public Builder categoryNames(Iterable<String> categoryNames) {
            this.categoryNames = ImmutableList.copyOf(categoryNames);
            return this;
        }

        @Override
        public Builder cost(Optional<Cost> cost) {
            this.cost = (Optional)Preconditions.checkNotNull(cost);
            return this;
        }

        public Builder forThisUserOnly(boolean forThisUserOnly) {
            this.forThisUserOnly = forThisUserOnly;
            return this;
        }

        public Builder hosting(Optional<HostingType> hosting) {
            this.hosting = (List)hosting.map(ImmutableList::of).orElseGet(ImmutableList::of);
            return this;
        }

        @Override
        public Builder hosting(List<HostingType> hosting) {
            this.hosting = ImmutableList.copyOf(hosting);
            return this;
        }

        public Builder includeHidden(Optional<IncludeHiddenType> includeHidden) {
            this.includeHidden = (Optional)Preconditions.checkNotNull(includeHidden);
            return this;
        }

        public Builder includePrivate(boolean includePrivate) {
            this.includePrivate = includePrivate;
            return this;
        }

        public Builder label(Optional<String> label) {
            this.label = (Optional)Preconditions.checkNotNull(label);
            return this;
        }

        @Deprecated
        public Builder treatPartlyFreeAs(Optional<TreatPartlyFreeAs> treatPartlyFreeAs) {
            this.treatPartlyFreeAs = treatPartlyFreeAs;
            return this;
        }

        public Builder searchText(Optional<String> searchText) {
            this.searchText = (Optional)Preconditions.checkNotNull(searchText);
            return this;
        }

        public Builder view(Optional<View> view) {
            this.view = (Optional)Preconditions.checkNotNull(view);
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


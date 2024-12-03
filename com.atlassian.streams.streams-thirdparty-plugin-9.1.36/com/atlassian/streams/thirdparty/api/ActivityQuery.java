/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.common.Pair
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package com.atlassian.streams.thirdparty.api;

import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.Pair;
import com.google.common.collect.ImmutableList;
import java.util.Date;

public final class ActivityQuery {
    public static final int DEFAULT_START_INDEX = 0;
    public static final int DEFAULT_MAX_RESULTS = 10;
    private Option<Date> startDate;
    private Option<Date> endDate;
    private Iterable<String> userNames;
    private Iterable<String> excludeUserNames;
    private Iterable<Pair<String, String>> entityFilters;
    private Iterable<Pair<String, String>> excludeEntityFilters;
    private Iterable<String> providerKeys;
    private Iterable<String> excludeProviderKeys;
    private int startIndex;
    private int maxResults;

    public static ActivityQuery all() {
        return new ActivityQuery();
    }

    public static Builder builder() {
        return new Builder();
    }

    private ActivityQuery() {
        this.startDate = Option.none();
        this.endDate = Option.none();
        this.userNames = this.excludeUserNames = ImmutableList.of();
        this.entityFilters = this.excludeEntityFilters = ImmutableList.of();
        this.providerKeys = this.excludeProviderKeys = ImmutableList.of();
        this.startIndex = 0;
        this.maxResults = 10;
    }

    private ActivityQuery(Builder builder) {
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.userNames = ImmutableList.copyOf((Iterable)builder.userNames);
        this.excludeUserNames = ImmutableList.copyOf((Iterable)builder.excludeUserNames);
        this.entityFilters = builder.entityFilters.build();
        this.excludeEntityFilters = builder.excludeEntityFilters.build();
        this.providerKeys = ImmutableList.copyOf((Iterable)builder.providerKeys);
        this.excludeProviderKeys = ImmutableList.copyOf((Iterable)builder.excludeProviderKeys);
        this.startIndex = builder.startIndex;
        this.maxResults = builder.maxResults;
    }

    public Option<Date> getStartDate() {
        return this.startDate;
    }

    public Option<Date> getEndDate() {
        return this.endDate;
    }

    public Iterable<String> getUserNames() {
        return this.userNames;
    }

    public Iterable<String> getExcludeUserNames() {
        return this.excludeUserNames;
    }

    public Iterable<Pair<String, String>> getEntityFilters() {
        return this.entityFilters;
    }

    public Iterable<Pair<String, String>> getExcludeEntityFilters() {
        return this.excludeEntityFilters;
    }

    public Iterable<String> getProviderKeys() {
        return this.providerKeys;
    }

    public Iterable<String> getExcludeProviderKeys() {
        return this.excludeProviderKeys;
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public int getMaxResults() {
        return this.maxResults;
    }

    public static final class Builder {
        private Option<Date> startDate = Option.none();
        private Option<Date> endDate = Option.none();
        private Iterable<String> userNames = ImmutableList.of();
        private Iterable<String> excludeUserNames = ImmutableList.of();
        private ImmutableList.Builder<Pair<String, String>> entityFilters = ImmutableList.builder();
        private ImmutableList.Builder<Pair<String, String>> excludeEntityFilters = ImmutableList.builder();
        private Iterable<String> providerKeys = ImmutableList.of();
        private Iterable<String> excludeProviderKeys = ImmutableList.of();
        private int startIndex = 0;
        private int maxResults = 10;

        public Builder startDate(Option<Date> startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(Option<Date> endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder userNames(Iterable<String> userNames) {
            this.userNames = userNames;
            return this;
        }

        public Builder excludeUserNames(Iterable<String> excludeUserNames) {
            this.excludeUserNames = excludeUserNames;
            return this;
        }

        public Builder addEntityFilter(String filterKey, String value) {
            this.entityFilters.add((Object)Pair.pair((Object)filterKey, (Object)value));
            return this;
        }

        public Builder addExcludeEntityFilter(String filterKey, String value) {
            this.excludeEntityFilters.add((Object)Pair.pair((Object)filterKey, (Object)value));
            return this;
        }

        public Builder providerKeys(Iterable<String> providerKeys) {
            this.providerKeys = providerKeys;
            return this;
        }

        public Builder excludeProviderKeys(Iterable<String> excludeProviderKeys) {
            this.excludeProviderKeys = excludeProviderKeys;
            return this;
        }

        public Builder startIndex(int startIndex) {
            this.startIndex = startIndex;
            return this;
        }

        public Builder maxResults(int maxResults) {
            this.maxResults = maxResults;
            return this;
        }

        public ActivityQuery build() {
            return new ActivityQuery(this);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.ActivityRequest
 *  com.atlassian.streams.api.StreamsFilterType$Operator
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.common.Pair
 *  com.atlassian.streams.api.common.uri.Uri
 *  com.google.common.base.Function
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Multimaps
 */
package com.atlassian.streams.internal;

import com.atlassian.streams.api.ActivityRequest;
import com.atlassian.streams.api.StreamsFilterType;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.Pair;
import com.atlassian.streams.api.common.uri.Uri;
import com.atlassian.streams.internal.ActivityProvider;
import com.atlassian.streams.internal.HttpParameters;
import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.net.URI;
import java.util.Collection;
import java.util.Map;

public class ActivityRequestImpl
implements ActivityRequest {
    public static final int DEFAULT_MAX_RESULTS = 10;
    public static final int DEFAULT_TIMEOUT = 10000;
    private final Uri uri;
    private final URI contextUri;
    private final Option<String> key;
    private final Iterable<String> providers;
    private final Multimap<String, Pair<StreamsFilterType.Operator, Iterable<String>>> standardFilters;
    private final Multimap<String, Pair<StreamsFilterType.Operator, Iterable<String>>> providerFilters;
    private final String requestLanguages;
    private final int maxResults;
    private final int timeout;
    private final long requestId;
    private static final Function<String, Iterable<String>> splitOnSpace = new Function<String, Iterable<String>>(){

        public Iterable<String> apply(String delimitedProviders) {
            ImmutableList.Builder builder = ImmutableList.builder();
            ImmutableList providers = ImmutableList.copyOf((Object[])delimitedProviders.split(" "));
            for (String provider : providers) {
                builder.add((Object)provider);
            }
            return builder.build();
        }
    };

    public ActivityRequestImpl(Builder builder) {
        this.uri = builder.uri;
        this.contextUri = builder.contextUri;
        this.key = builder.key;
        this.providers = builder.providers;
        this.standardFilters = Multimaps.unmodifiableMultimap((Multimap)ArrayListMultimap.create((Multimap)builder.standardFilters));
        this.providerFilters = Multimaps.unmodifiableMultimap((Multimap)ArrayListMultimap.create((Multimap)builder.providerFilters));
        this.requestLanguages = builder.requestLanguages;
        this.maxResults = builder.maxResults;
        this.timeout = builder.timeout;
        this.requestId = builder.requestId;
    }

    public Uri getUri() {
        return this.uri;
    }

    public URI getContextUri() {
        return this.contextUri;
    }

    Option<String> getKey() {
        return this.key;
    }

    Iterable<String> getProviders() {
        return this.providers;
    }

    public int getMaxResults() {
        return this.maxResults;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public String getRequestLanguages() {
        return this.requestLanguages;
    }

    public long getRequestId() {
        return this.requestId;
    }

    public Multimap<String, Pair<StreamsFilterType.Operator, Iterable<String>>> getProviderFilters() {
        return this.providerFilters;
    }

    public Map<String, Collection<Pair<StreamsFilterType.Operator, Iterable<String>>>> getProviderFiltersMap() {
        return this.providerFilters.asMap();
    }

    public Multimap<String, Pair<StreamsFilterType.Operator, Iterable<String>>> getStandardFilters() {
        return this.standardFilters;
    }

    public Map<String, Collection<Pair<StreamsFilterType.Operator, Iterable<String>>>> getStandardFiltersMap() {
        return this.standardFilters.asMap();
    }

    public static Builder builder(Uri uri) {
        return new Builder(uri);
    }

    public static final class Builder {
        private final Uri uri;
        public URI contextUri;
        private Option<String> key;
        private Iterable<String> providers = ImmutableList.of();
        private Multimap<String, Pair<StreamsFilterType.Operator, Iterable<String>>> standardFilters = ArrayListMultimap.create();
        private Multimap<String, Pair<StreamsFilterType.Operator, Iterable<String>>> providerFilters = ArrayListMultimap.create();
        private String requestLanguages;
        private int maxResults = 10;
        private int timeout = 10000;
        private long requestId;

        private Builder(Uri uri) {
            this.uri = uri;
        }

        public Builder maxResults(int maxResults) {
            if (maxResults > 0) {
                this.maxResults = maxResults;
            }
            return this;
        }

        public Builder contextUri(URI contextUri) {
            this.contextUri = contextUri;
            return this;
        }

        public Builder timeout(int timeout) {
            if (timeout > 0) {
                this.timeout = timeout;
            }
            return this;
        }

        public Builder standardFilters(Multimap<String, Pair<StreamsFilterType.Operator, Iterable<String>>> filters) {
            this.standardFilters = filters;
            return this;
        }

        public Builder providerFilters(Multimap<String, Pair<StreamsFilterType.Operator, Iterable<String>>> filters) {
            this.providerFilters = filters;
            return this;
        }

        public Builder requestLanguages(String requestLanguages) {
            this.requestLanguages = requestLanguages;
            return this;
        }

        public Builder requestId(long requestId) {
            this.requestId = requestId;
            return this;
        }

        public ActivityRequest build() {
            return new ActivityRequestImpl(this);
        }

        public ActivityRequestImpl build(HttpParameters parameters, ActivityProvider provider) {
            this.maxResults = parameters.parseMaxResults(10);
            this.standardFilters = parameters.parseStandardFilters();
            this.key = parameters.getProviderKey(provider);
            this.providerFilters = parameters.getProviderFilter(this.providerFilters, provider);
            this.providers = Iterables.concat((Iterable)Iterables.transform(parameters.getProviders(), (Function)splitOnSpace));
            return new ActivityRequestImpl(this);
        }
    }
}


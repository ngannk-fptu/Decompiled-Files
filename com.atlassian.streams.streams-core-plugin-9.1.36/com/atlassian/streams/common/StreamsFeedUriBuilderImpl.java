/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.StreamsFilterType$Operator
 *  com.atlassian.streams.api.builder.StreamsFeedUriBuilder
 *  com.atlassian.streams.api.common.Pair
 *  com.atlassian.streams.api.common.uri.Uris
 *  com.atlassian.streams.spi.UriAuthenticationParameterProvider
 *  com.google.common.base.Function
 *  com.google.common.base.Joiner
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Sets
 */
package com.atlassian.streams.common;

import com.atlassian.streams.api.StreamsFilterType;
import com.atlassian.streams.api.builder.StreamsFeedUriBuilder;
import com.atlassian.streams.api.common.Pair;
import com.atlassian.streams.api.common.uri.Uris;
import com.atlassian.streams.spi.UriAuthenticationParameterProvider;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class StreamsFeedUriBuilderImpl
implements StreamsFeedUriBuilder {
    private final String baseUrl;
    private final Multimap<String, String> parameters = ArrayListMultimap.create();
    private final UriAuthenticationParameterProvider authProvider;
    private final Set<String> providers = Sets.newHashSet();
    private Integer maxResults;
    private Integer timeout;
    private static final Function<String, String> escapeValue = new Function<String, String>(){

        public String apply(String from) {
            return from.replace("_", "\\_").replace(" ", "_");
        }
    };

    public StreamsFeedUriBuilderImpl(String baseUrl, UriAuthenticationParameterProvider authProvider) {
        this.baseUrl = baseUrl;
        this.authProvider = authProvider;
    }

    public URI getUri() {
        return this.buildUri("/activity");
    }

    public URI getServletUri() {
        return this.buildUri("/plugins/servlet/streams");
    }

    private URI buildUri(String path) {
        StringBuilder url = new StringBuilder(this.baseUrl);
        url.append(path);
        String sep = "?";
        for (Map.Entry propEntry : this.parameters.entries()) {
            url.append(sep).append(Uris.encode((String)((String)propEntry.getKey()))).append("=").append(Uris.encode((String)((String)propEntry.getValue())));
            sep = "&";
        }
        if (!this.providers.isEmpty()) {
            url.append(sep).append("providers").append('=').append(Uris.encode((String)Joiner.on((char)' ').join(this.providers)));
        }
        if (this.maxResults != null) {
            url.append(sep).append("maxResults").append("=").append(this.maxResults);
        }
        if (this.timeout != null) {
            url.append(sep).append("timeout").append("=").append(this.timeout);
        }
        return URI.create(url.toString()).normalize();
    }

    public StreamsFeedUriBuilder setMaxResults(int maxResults) {
        this.maxResults = maxResults;
        return this;
    }

    public StreamsFeedUriBuilder setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public StreamsFeedUriBuilder addApplication(String name) {
        this.parameters.put((Object)"application", (Object)name);
        return this;
    }

    public StreamsFeedUriBuilder addAuthOnly(boolean authOnly) {
        this.parameters.put((Object)"authOnly", (Object)Boolean.toString(authOnly));
        return this;
    }

    public StreamsFeedUriBuilder addLocalOnly(boolean localOnly) {
        this.parameters.put((Object)"local", (Object)Boolean.toString(localOnly));
        return this;
    }

    public StreamsFeedUriBuilder addProviderFilter(String providerKey, String filterKey, Pair<StreamsFilterType.Operator, Iterable<String>> filter) {
        this.parameters.put((Object)providerKey, (Object)this.toString(filterKey, filter));
        return this;
    }

    public StreamsFeedUriBuilder addStandardFilter(String filterKey, StreamsFilterType.Operator op, String value) {
        return this.addStandardFilter(filterKey, (Pair<StreamsFilterType.Operator, Iterable<String>>)Pair.pair((Object)op, (Object)ImmutableList.of((Object)value)));
    }

    public StreamsFeedUriBuilder addStandardFilter(String filterKey, StreamsFilterType.Operator op, Date date) {
        return this.addStandardFilter(filterKey, (Pair<StreamsFilterType.Operator, Iterable<String>>)Pair.pair((Object)op, (Object)ImmutableList.of((Object)Long.toString(date.getTime()))));
    }

    public StreamsFeedUriBuilder addStandardFilter(String filterKey, StreamsFilterType.Operator op, Iterable<String> values) {
        return this.addStandardFilter(filterKey, (Pair<StreamsFilterType.Operator, Iterable<String>>)Pair.pair((Object)op, values));
    }

    public StreamsFeedUriBuilder addStandardFilter(String filterKey, Pair<StreamsFilterType.Operator, Iterable<String>> filter) {
        this.parameters.put((Object)"streams", (Object)this.toString(filterKey, filter));
        return this;
    }

    public StreamsFeedUriBuilder addAuthenticationParameterIfLoggedIn() {
        for (Pair auth : this.authProvider.get()) {
            this.parameters.put(auth.first(), auth.second());
        }
        return this;
    }

    private String toString(String filterKey, Pair<StreamsFilterType.Operator, Iterable<String>> filter) {
        return filterKey + " " + filter.first() + " " + Joiner.on((char)' ').join(Iterables.transform((Iterable)((Iterable)filter.second()), escapeValue));
    }

    public StreamsFeedUriBuilder addLegacyFilterUser(String filterUser) {
        this.parameters.put((Object)"filterUser", (Object)filterUser);
        return this;
    }

    public StreamsFeedUriBuilder addLegacyKey(String key) {
        this.parameters.put((Object)"key", (Object)key);
        return this;
    }

    public StreamsFeedUriBuilder setLegacyMaxDate(long maxDate) {
        this.parameters.put((Object)"maxDate", (Object)Long.toString(maxDate));
        return this;
    }

    public StreamsFeedUriBuilder setLegacyMinDate(long minDate) {
        this.parameters.put((Object)"minDate", (Object)Long.toString(minDate));
        return this;
    }

    public StreamsFeedUriBuilder addProvider(String key) {
        this.providers.add(key);
        return this;
    }

    public StreamsFeedUriBuilder addUseAcceptLang(boolean useAcceptLang) {
        this.parameters.put((Object)"use-accept-lang", (Object)Boolean.toString(useAcceptLang));
        return this;
    }
}


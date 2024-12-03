/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 */
package com.atlassian.streams.api;

import com.atlassian.streams.api.StreamsFilterType;
import com.atlassian.streams.api.common.Pair;
import com.atlassian.streams.api.common.uri.Uri;
import com.google.common.collect.Multimap;
import java.net.URI;
import java.util.Collection;
import java.util.Map;

public interface ActivityRequest {
    public static final String MAX_RESULTS = "maxResults";
    public static final String PROVIDERS_KEY = "providers";
    public static final String USE_ACCEPT_LANG_KEY = "use-accept-lang";
    public static final String TIMEOUT = "timeout";

    public int getMaxResults();

    public int getTimeout();

    public Uri getUri();

    public URI getContextUri();

    @Deprecated
    public Multimap<String, Pair<StreamsFilterType.Operator, Iterable<String>>> getStandardFilters();

    default public Map<String, Collection<Pair<StreamsFilterType.Operator, Iterable<String>>>> getStandardFiltersMap() {
        throw new UnsupportedOperationException("Please override this method");
    }

    @Deprecated
    public Multimap<String, Pair<StreamsFilterType.Operator, Iterable<String>>> getProviderFilters();

    default public Map<String, Collection<Pair<StreamsFilterType.Operator, Iterable<String>>>> getProviderFiltersMap() {
        throw new UnsupportedOperationException("Please override this method");
    }

    public String getRequestLanguages();

    public long getRequestId();
}


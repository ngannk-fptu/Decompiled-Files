/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.streams.api.builder;

import com.atlassian.streams.api.StreamsFilterType;
import com.atlassian.streams.api.common.Pair;
import java.net.URI;
import java.util.Date;

public interface StreamsFeedUriBuilder {
    public URI getUri();

    public URI getServletUri();

    public StreamsFeedUriBuilder setMaxResults(int var1);

    public StreamsFeedUriBuilder setTimeout(int var1);

    public StreamsFeedUriBuilder addStandardFilter(String var1, StreamsFilterType.Operator var2, String var3);

    public StreamsFeedUriBuilder addStandardFilter(String var1, StreamsFilterType.Operator var2, Date var3);

    public StreamsFeedUriBuilder addStandardFilter(String var1, Pair<StreamsFilterType.Operator, Iterable<String>> var2);

    public StreamsFeedUriBuilder addStandardFilter(String var1, StreamsFilterType.Operator var2, Iterable<String> var3);

    public StreamsFeedUriBuilder addProviderFilter(String var1, String var2, Pair<StreamsFilterType.Operator, Iterable<String>> var3);

    public StreamsFeedUriBuilder addAuthOnly(boolean var1);

    public StreamsFeedUriBuilder addLocalOnly(boolean var1);

    public StreamsFeedUriBuilder addLegacyKey(String var1);

    public StreamsFeedUriBuilder addLegacyFilterUser(String var1);

    public StreamsFeedUriBuilder addUseAcceptLang(boolean var1);

    public StreamsFeedUriBuilder setLegacyMinDate(long var1);

    public StreamsFeedUriBuilder setLegacyMaxDate(long var1);

    public StreamsFeedUriBuilder addAuthenticationParameterIfLoggedIn();

    public StreamsFeedUriBuilder addProvider(String var1);
}


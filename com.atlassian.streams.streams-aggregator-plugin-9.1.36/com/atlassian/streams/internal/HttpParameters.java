/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.streams.api.StreamsFilterType$Operator
 *  com.atlassian.streams.api.common.Either
 *  com.atlassian.streams.api.common.Fold
 *  com.atlassian.streams.api.common.Function2
 *  com.atlassian.streams.api.common.Functions
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.common.Pair
 *  com.atlassian.streams.spi.StandardStreamsFilterOption
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.base.Supplier
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.ImmutableMultimap$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Multimap
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.streams.internal;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.streams.api.StreamsFilterType;
import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.api.common.Fold;
import com.atlassian.streams.api.common.Function2;
import com.atlassian.streams.api.common.Functions;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.Pair;
import com.atlassian.streams.internal.ActivityProvider;
import com.atlassian.streams.internal.ActivityProviders;
import com.atlassian.streams.internal.Predicates;
import com.atlassian.streams.spi.StandardStreamsFilterOption;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

public final class HttpParameters {
    static final String LOCAL_KEY = new String("local");
    public static final String RELATIVE_LINKS_KEY = new String("relativeLinks");
    public static final String TIMEOUT_TEST = new String("timeout.test");
    public static final String PARAM_TITLE = new String("title");
    public static final String PARAM_MODULE = new String("module");
    public static final String MAX_RESULTS = new String("maxResults");
    public static final String AUTH_ONLY = new String("authOnly");
    private static final String LEGACY_MIN_DATE = new String("minDate");
    private static final String LEGACY_MAX_DATE = new String("maxDate");
    private static final String LEGACY_ITEM_KEY = new String("itemKey");
    private static final int DEFAULT_MAX_RESULTS_LIMIT = 1000;
    private static final int MAX_RESULTS_LIMIT = (Integer)((Either)Functions.parseInt().apply((Object)System.getProperty(HttpParameters.class.getName() + ".maxItems"))).right().toOption().getOrElse((Object)1000);
    @VisibleForTesting
    static final String LEGACY_FILTER = new String("filter");
    @VisibleForTesting
    static final String LEGACY_AUTHOR = new String("filterUser");
    private final ImmutableMultimap<String, String> map;
    private static final Function<String, String> unescapeValue = new Function<String, String>(){

        public String apply(String from) {
            return from.replaceAll("(?<!\\\\)\\_", " ").replace("\\_", "_");
        }
    };

    public static HttpParameters parameters(HttpServletRequest request) {
        return new HttpParameters(HttpParameters.buildParams(request));
    }

    static ImmutableMultimap<String, String> buildParams(HttpServletRequest request) {
        ImmutableMultimap.Builder builder = ImmutableMultimap.builder();
        for (Map.Entry<String, String[]> entry : HttpParameters.getParameterMap(request).entrySet()) {
            builder.putAll((Object)entry.getKey(), (Object[])entry.getValue());
        }
        return builder.build();
    }

    @VisibleForTesting
    public static HttpParameters parameters(Multimap<String, String> params) {
        ImmutableMultimap.Builder builder = ImmutableMultimap.builder();
        ImmutableMultimap immutable = builder.putAll(params).build();
        return new HttpParameters((ImmutableMultimap<String, String>)immutable);
    }

    private static Map<String, String[]> getParameterMap(HttpServletRequest request) {
        return request.getParameterMap();
    }

    private HttpParameters(ImmutableMultimap<String, String> map) {
        this.map = map;
    }

    public boolean useAcceptLanguage() {
        return HttpParameters.parseBooleanParameter(this.map, "use-accept-lang", false);
    }

    public boolean allowOnlyAuthorized() {
        return HttpParameters.parseBooleanParameter(this.map, AUTH_ONLY, false);
    }

    public Predicate<ActivityProvider> fetchLocalOnly() {
        return ActivityProviders.localOnly(HttpParameters.parseBooleanParameter(this.map, LOCAL_KEY, false));
    }

    private static boolean parseBooleanParameter(Multimap<String, String> parameters, String key, boolean defVal) {
        return parameters.containsKey((Object)key) ? Boolean.parseBoolean((String)Iterables.get((Iterable)parameters.get((Object)key), (int)0)) : defVal;
    }

    public Option<String> getTitle() {
        return Option.option(this.map.containsKey((Object)PARAM_TITLE) ? (String)Iterables.get((Iterable)this.map.get((Object)PARAM_TITLE), (int)0) : null);
    }

    @VisibleForTesting
    static Iterable<String> getSelectedProviders(Multimap<String, String> parameters) {
        return Arrays.asList(((String)Iterables.get((Iterable)parameters.get((Object)"providers"), (int)0)).split(" "));
    }

    public Predicate<ActivityProvider> isSelectedProvider() {
        if (!this.map.containsKey((Object)"providers")) {
            return com.google.common.base.Predicates.alwaysTrue();
        }
        return ActivityProviders.selectedProvider(HttpParameters.getSelectedProviders(this.map));
    }

    public Predicate<ActivityProvider> module() {
        if (!this.map.containsKey((Object)PARAM_MODULE)) {
            return com.google.common.base.Predicates.alwaysTrue();
        }
        return ActivityProviders.module((String)Iterables.get((Iterable)this.map.get((Object)PARAM_MODULE), (int)0));
    }

    public boolean isTimeoutTest() {
        return this.map.containsEntry((Object)TIMEOUT_TEST, (Object)"true");
    }

    public int parseMaxResults(int defaultValue) {
        Integer maxResults = (Integer)HttpParameters.parseParamAsInt(MAX_RESULTS, this.map).getOrElse((Object)defaultValue);
        return maxResults > MAX_RESULTS_LIMIT ? MAX_RESULTS_LIMIT : maxResults;
    }

    private static Option<Integer> parseParamAsInt(String parameter, Multimap<String, String> parameters) {
        return parameters.containsKey((Object)parameter) ? ((Either)Functions.parseInt().apply(Iterables.get((Iterable)parameters.get((Object)parameter), (int)0))).right().toOption() : Option.none(Integer.class);
    }

    private static Option<String> getProviderKey(Multimap<String, String> parameters, ActivityProvider provider) {
        try {
            return Option.some(((Map.Entry)Iterables.find((Iterable)parameters.entries(), Predicates.whereMapEntryKey(ActivityProviders.matches(provider)))).getKey());
        }
        catch (NoSuchElementException e) {
            return Option.none();
        }
    }

    public Option<String> getProviderKey(ActivityProvider provider) {
        return HttpParameters.getProviderKey(this.map, provider);
    }

    private static Multimap<String, Pair<StreamsFilterType.Operator, Iterable<String>>> parseStandardFilters(Multimap<String, String> parameters) {
        Collection standardFilters = parameters.get((Object)"streams");
        return ImmutableMultimap.builder().putAll(HttpParameters.getLegacyParametersAsFilters(parameters)).putAll(HttpParameters.parseFilters(standardFilters)).build();
    }

    private static Multimap<String, Pair<StreamsFilterType.Operator, Iterable<String>>> parseFilters(Iterable<String> filters) {
        return ((ImmutableMultimap.Builder)Fold.foldl(filters, (Object)ImmutableMultimap.builder(), (Function2)FilterParser.INSTANCE)).build();
    }

    public Multimap<String, Pair<StreamsFilterType.Operator, Iterable<String>>> parseStandardFilters() {
        return HttpParameters.parseStandardFilters(this.map);
    }

    public URI calculateContextUrl(ApplicationProperties applicationProperties, String contextUri) {
        if (HttpParameters.parseBooleanParameter(this.map, RELATIVE_LINKS_KEY, false) && contextUri != null) {
            return URI.create(contextUri);
        }
        return URI.create(applicationProperties.getBaseUrl());
    }

    public Multimap<String, Pair<StreamsFilterType.Operator, Iterable<String>>> getProviderFilter(final Multimap<String, Pair<StreamsFilterType.Operator, Iterable<String>>> providerFilters, ActivityProvider provider) {
        Option<String> providerKey = this.getProviderKey(provider);
        return (Multimap)providerKey.fold((Supplier)new Supplier<Multimap<String, Pair<StreamsFilterType.Operator, Iterable<String>>>>(){

            public Multimap<String, Pair<StreamsFilterType.Operator, Iterable<String>>> get() {
                return providerFilters;
            }
        }, (Function)new Function<String, Multimap<String, Pair<StreamsFilterType.Operator, Iterable<String>>>>(){

            public Multimap<String, Pair<StreamsFilterType.Operator, Iterable<String>>> apply(@Nullable String s) {
                return HttpParameters.parseFilters((Iterable)HttpParameters.this.map.get((Object)s));
            }
        });
    }

    public Collection<String> getProviders() {
        return this.map.get((Object)"providers");
    }

    private static Multimap<String, Pair<StreamsFilterType.Operator, Iterable<String>>> getLegacyParametersAsFilters(Multimap<String, String> parameters) {
        return ImmutableMultimap.builder().putAll(HttpParameters.getLegacyParameterAsFilter(LEGACY_AUTHOR, StandardStreamsFilterOption.USER.getKey(), StreamsFilterType.Operator.IS, parameters)).putAll(HttpParameters.getLegacyParameterAsFilter("key", "key", StreamsFilterType.Operator.IS, parameters)).putAll(HttpParameters.getLegacyIssueKeyParametersAsFilter(parameters)).putAll(HttpParameters.getLegacyDateParametersAsFilter(parameters)).build();
    }

    private static Multimap<? extends String, ? extends Pair<StreamsFilterType.Operator, Iterable<String>>> getLegacyIssueKeyParametersAsFilter(Multimap<String, String> parameters) {
        if (parameters.containsKey((Object)LEGACY_FILTER)) {
            return ImmutableMultimap.of((Object)StandardStreamsFilterOption.ISSUE_KEY.getKey(), (Object)Pair.pair((Object)StreamsFilterType.Operator.IS, (Object)parameters.get((Object)LEGACY_FILTER)));
        }
        if (parameters.containsKey((Object)LEGACY_ITEM_KEY)) {
            return ImmutableMultimap.of((Object)StandardStreamsFilterOption.ISSUE_KEY.getKey(), (Object)Pair.pair((Object)StreamsFilterType.Operator.IS, (Object)parameters.get((Object)LEGACY_ITEM_KEY)));
        }
        return ImmutableMultimap.of();
    }

    private static Multimap<String, Pair<StreamsFilterType.Operator, Iterable<String>>> getLegacyParameterAsFilter(String legacyKey, String newKey, StreamsFilterType.Operator op, Multimap<String, String> parameters) {
        if (parameters.containsKey((Object)legacyKey)) {
            return ImmutableMultimap.of((Object)newKey, (Object)Pair.pair((Object)op, (Object)parameters.get((Object)legacyKey)));
        }
        return ImmutableMultimap.of();
    }

    private static Multimap<String, Pair<StreamsFilterType.Operator, Iterable<String>>> getLegacyDateParametersAsFilter(Multimap<String, String> parameters) {
        ImmutableMultimap.Builder builder = ImmutableMultimap.builder();
        if (parameters.containsKey((Object)LEGACY_MIN_DATE)) {
            builder.put((Object)StandardStreamsFilterOption.UPDATE_DATE.getKey(), (Object)Pair.pair((Object)StreamsFilterType.Operator.AFTER, (Object)parameters.get((Object)LEGACY_MIN_DATE)));
        }
        if (parameters.containsKey((Object)LEGACY_MAX_DATE)) {
            builder.put((Object)StandardStreamsFilterOption.UPDATE_DATE.getKey(), (Object)Pair.pair((Object)StreamsFilterType.Operator.BEFORE, (Object)parameters.get((Object)LEGACY_MAX_DATE)));
        }
        return builder.build();
    }

    private static enum FilterParser implements Function2<String, ImmutableMultimap.Builder<String, Pair<StreamsFilterType.Operator, Iterable<String>>>, ImmutableMultimap.Builder<String, Pair<StreamsFilterType.Operator, Iterable<String>>>>
    {
        INSTANCE;


        public ImmutableMultimap.Builder<String, Pair<StreamsFilterType.Operator, Iterable<String>>> apply(String filter, ImmutableMultimap.Builder<String, Pair<StreamsFilterType.Operator, Iterable<String>>> builder) {
            String[] keyOpValues = filter.split(" ", 3);
            if (keyOpValues.length == 3) {
                String key = keyOpValues[0];
                StreamsFilterType.Operator op = StreamsFilterType.Operator.valueOf((String)keyOpValues[1]);
                ImmutableList values = ImmutableList.copyOf((Object[])keyOpValues[2].split(" "));
                return builder.put((Object)key, (Object)Pair.pair((Object)op, (Object)Iterables.transform((Iterable)values, (Function)unescapeValue)));
            }
            return builder;
        }
    }
}


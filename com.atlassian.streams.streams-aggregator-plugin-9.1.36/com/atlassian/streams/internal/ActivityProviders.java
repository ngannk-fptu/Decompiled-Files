/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.common.uri.Uri
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.base.Supplier
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Ordering
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.internal;

import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.uri.Uri;
import com.atlassian.streams.internal.ActivityProvider;
import com.atlassian.streams.internal.ActivityProviderWithAnalytics;
import com.atlassian.streams.internal.AppLinksActivityProvider;
import com.atlassian.streams.internal.AppLinksActivityProviders;
import com.atlassian.streams.internal.LocalActivityProvider;
import com.atlassian.streams.internal.LocalActivityProviders;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivityProviders {
    private static final Logger log = LoggerFactory.getLogger(ActivityProviders.class);
    private Iterable<Supplier<Iterable<ActivityProvider>>> suppliers;
    private static final Ordering<ActivityProvider> byBaseUrl = new Ordering<ActivityProvider>(){

        public int compare(ActivityProvider provider1, ActivityProvider provider2) {
            return provider2.getBaseUrl().compareTo(provider1.getBaseUrl());
        }
    };

    ActivityProviders(LocalActivityProviders localProviders, AppLinksActivityProviders applinksProviders) {
        this.suppliers = ImmutableList.of((Object)localProviders, (Object)applinksProviders);
    }

    Iterable<ActivityProvider> get(Predicate<ActivityProvider> predicate) {
        return this.get((Iterable<Predicate<ActivityProvider>>)ImmutableList.of(predicate));
    }

    Iterable<ActivityProvider> get() {
        return this.get((Iterable<Predicate<ActivityProvider>>)ImmutableList.of());
    }

    Iterable<ActivityProvider> get(Predicate<ActivityProvider> p1, Predicate<ActivityProvider> p2) {
        return this.get((Iterable<Predicate<ActivityProvider>>)ImmutableList.of(p1, p2));
    }

    Iterable<ActivityProvider> get(Iterable<Predicate<ActivityProvider>> predicates) {
        return Iterables.filter((Iterable)Iterables.concat((Iterable)Iterables.transform(this.suppliers, ActivityProviders.toProviders())), (Predicate)Predicates.and(predicates));
    }

    public Option<ActivityProvider> getProviderForUri(Uri uri) {
        List matchingProviders = this.get(ActivityProviders.matchesUri(uri));
        if (Iterables.isEmpty(matchingProviders)) {
            log.warn("no activity provider found for URI " + uri);
            return Option.none();
        }
        matchingProviders = ActivityProviders.byBaseUrl().sortedCopy(matchingProviders);
        return Option.some((Object)Iterables.get((Iterable)matchingProviders, (int)0));
    }

    public Option<AppLinksActivityProvider> getRemoteProviderForUri(Uri uri) {
        return this.getProviderForUri(uri).flatMap(ActivityProviders.requireRemoteProvider());
    }

    private static Function<Supplier<Iterable<ActivityProvider>>, Iterable<ActivityProvider>> toProviders() {
        return SupplierGetterFunction.INSTANCE;
    }

    static Predicate<ActivityProvider> module(String key) {
        return new ModulePredicate((String)Preconditions.checkNotNull((Object)key, (Object)"key"));
    }

    static Predicate<ActivityProvider> localOnly(boolean local) {
        return local ? ActivityProviders.local() : Predicates.alwaysTrue();
    }

    static Predicate<ActivityProvider> local() {
        return LocalPredicate.INSTANCE;
    }

    static Predicate<ActivityProvider> selectedProvider(Iterable<String> selectedProviderKeys) {
        return new SelectedProviderPredicate(selectedProviderKeys);
    }

    public static Predicate<String> matches(ActivityProvider provider) {
        return new ProviderMatches(provider);
    }

    private static Predicate<ActivityProvider> matchesUri(Uri uri) {
        return new MatchesUriPredicate(uri);
    }

    private static Ordering<ActivityProvider> byBaseUrl() {
        return byBaseUrl;
    }

    private static Function<ActivityProvider, Option<AppLinksActivityProvider>> requireRemoteProvider() {
        return RequireRemoteProvider.INSTANCE;
    }

    public static boolean isActivityProviderWithAnalyticsWithDelegate(ActivityProvider provider, Class<?> delegateClass) {
        return ActivityProviderWithAnalytics.class.isAssignableFrom(provider.getClass()) && delegateClass.isAssignableFrom(((ActivityProviderWithAnalytics)provider).getDelegate().getClass());
    }

    private static enum RequireRemoteProvider implements Function<ActivityProvider, Option<AppLinksActivityProvider>>
    {
        INSTANCE;


        public Option<AppLinksActivityProvider> apply(ActivityProvider provider) {
            if (provider instanceof AppLinksActivityProvider) {
                return Option.some((Object)((AppLinksActivityProvider)provider));
            }
            if (ActivityProviders.isActivityProviderWithAnalyticsWithDelegate(provider, AppLinksActivityProvider.class)) {
                return Option.some((Object)((AppLinksActivityProvider)((ActivityProviderWithAnalytics)provider).getDelegate()));
            }
            return Option.none();
        }
    }

    private static final class MatchesUriPredicate
    implements Predicate<ActivityProvider> {
        private final String uri;

        public MatchesUriPredicate(Uri uri) {
            this.uri = ((Uri)Preconditions.checkNotNull((Object)uri)).toString();
        }

        public boolean apply(ActivityProvider provider) {
            return this.uri.startsWith(provider.getBaseUrl());
        }
    }

    private static final class ProviderMatches
    implements Predicate<String> {
        private final ActivityProvider provider;

        public ProviderMatches(ActivityProvider provider) {
            this.provider = provider;
        }

        public boolean apply(String key) {
            return this.provider.matches(key);
        }
    }

    private static final class SelectedProviderPredicate
    implements Predicate<ActivityProvider> {
        private final Iterable<String> selectedProviderKeys;

        public SelectedProviderPredicate(Iterable<String> selectedProviderKeys) {
            this.selectedProviderKeys = selectedProviderKeys;
        }

        public boolean apply(ActivityProvider provider) {
            return Iterables.any(this.selectedProviderKeys, ActivityProviders.matches(provider));
        }
    }

    private static enum LocalPredicate implements Predicate<ActivityProvider>
    {
        INSTANCE;


        public boolean apply(ActivityProvider provider) {
            return provider instanceof LocalActivityProvider || ActivityProviders.isActivityProviderWithAnalyticsWithDelegate(provider, LocalActivityProvider.class);
        }
    }

    private static final class ModulePredicate
    implements Predicate<ActivityProvider> {
        private final String key;

        public ModulePredicate(String key) {
            this.key = key;
        }

        public boolean apply(ActivityProvider provider) {
            return provider.matches(this.key);
        }
    }

    private static enum SupplierGetterFunction implements Function<Supplier<Iterable<ActivityProvider>>, Iterable<ActivityProvider>>
    {
        INSTANCE;


        public Iterable<ActivityProvider> apply(Supplier<Iterable<ActivityProvider>> supplier) {
            return (Iterable)supplier.get();
        }
    }
}


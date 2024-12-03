/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.failurecache.CacheLoader
 *  com.atlassian.failurecache.ExpiringValue
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.util.concurrent.ListenableFuture
 *  io.atlassian.fugue.Pair
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.navlink.consumer.menu.services;

import com.atlassian.failurecache.CacheLoader;
import com.atlassian.failurecache.ExpiringValue;
import com.atlassian.plugins.navlink.consumer.menu.client.navigation.NavigationClient;
import com.atlassian.plugins.navlink.consumer.menu.services.RemoteApplications;
import com.atlassian.plugins.navlink.producer.capabilities.CapabilityKey;
import com.atlassian.plugins.navlink.producer.capabilities.RemoteApplicationWithCapabilities;
import com.atlassian.plugins.navlink.producer.navigation.ApplicationNavigationLinks;
import com.atlassian.sal.api.message.LocaleResolver;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ListenableFuture;
import io.atlassian.fugue.Pair;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavigationLinksCacheLoader
implements CacheLoader<Pair<RemoteApplicationWithCapabilities, Locale>, ApplicationNavigationLinks> {
    private static final Logger logger = LoggerFactory.getLogger(NavigationLinksCacheLoader.class);
    private final Set<Locale> supportedLocales = new HashSet<Locale>();
    private final RemoteApplications remoteApplications;
    private final LocaleResolver localeResolver;
    private final NavigationClient navigationClient;

    public NavigationLinksCacheLoader(RemoteApplications remoteApplications, LocaleResolver localeResolver, NavigationClient navigationClient) {
        this.remoteApplications = remoteApplications;
        this.localeResolver = localeResolver;
        this.navigationClient = navigationClient;
    }

    public ImmutableSet<Pair<RemoteApplicationWithCapabilities, Locale>> getKeys() {
        Set<RemoteApplicationWithCapabilities> knownApplications = this.remoteApplications.capableOf(CapabilityKey.NAVIGATION.getKey());
        Set locales = Stream.concat(this.getDefaultLocales().stream(), this.supportedLocales.stream()).collect(Collectors.toSet());
        return ImmutableSet.copyOf(this.cartesianProduct(knownApplications, locales));
    }

    public ListenableFuture<ExpiringValue<ApplicationNavigationLinks>> loadValue(Pair<RemoteApplicationWithCapabilities, Locale> key) {
        return this.navigationClient.getNavigationLinks((RemoteApplicationWithCapabilities)key.left(), (Locale)key.right());
    }

    public void cacheMissFor(Locale locale) {
        if (!this.supportedLocales.contains(locale)) {
            logger.debug("Adding locale '{}' to the list of supported languages when fetching navigation links.", (Object)locale);
            this.supportedLocales.add(locale);
        }
    }

    private Set<Locale> getDefaultLocales() {
        Locale locale = this.localeResolver.getLocale();
        return locale.getLanguage().equals(Locale.ENGLISH.getLanguage()) ? Collections.singleton(locale) : new HashSet<Locale>(Arrays.asList(locale, Locale.UK));
    }

    private <L, R> Set<Pair<L, R>> cartesianProduct(Iterable<L> leftValues, Iterable<R> rightValue) {
        HashSet<Pair<L, R>> builder = new HashSet<Pair<L, R>>();
        for (L left : leftValues) {
            for (R right : rightValue) {
                builder.add(Pair.pair(left, right));
            }
        }
        return builder;
    }
}


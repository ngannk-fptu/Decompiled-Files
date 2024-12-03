/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.EntityLinkService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.streams.api.builder.StreamsFeedUriBuilderFactory
 *  com.atlassian.streams.spi.EntityResolver
 *  com.atlassian.streams.spi.StreamsI18nResolver
 *  com.atlassian.streams.spi.StreamsLocaleProvider
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Supplier
 *  com.google.common.collect.Iterables
 */
package com.atlassian.streams.internal;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.EntityLinkService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.streams.api.builder.StreamsFeedUriBuilderFactory;
import com.atlassian.streams.internal.ActivityProvider;
import com.atlassian.streams.internal.ActivityProviderWithAnalytics;
import com.atlassian.streams.internal.AppLinksActivityProvider;
import com.atlassian.streams.internal.applinks.ApplicationLinkServiceExtensions;
import com.atlassian.streams.internal.feed.FeedParser;
import com.atlassian.streams.spi.EntityResolver;
import com.atlassian.streams.spi.StreamsI18nResolver;
import com.atlassian.streams.spi.StreamsLocaleProvider;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import java.util.Set;

public class AppLinksActivityProviders
implements Supplier<Iterable<ActivityProvider>> {
    private final ApplicationLinkService appLinkService;
    private final EntityLinkService entityLinkService;
    private final Iterable<EntityResolver> entityResolvers;
    private final FeedParser feedParser;
    private final StreamsFeedUriBuilderFactory streamsFeedUriBuilderFactory;
    private final StreamsLocaleProvider streamsLocaleProvider;
    private final TransactionTemplate transactionTemplate;
    private final ApplicationLinkServiceExtensions appLinkServiceExtensions;
    private final StreamsI18nResolver i18nResolver;
    private final EventPublisher eventPublisher;

    AppLinksActivityProviders(ApplicationLinkService appLinkService, EntityLinkService entityLinkService, Set<EntityResolver> entityResolvers, FeedParser feedParser, StreamsFeedUriBuilderFactory streamsFeedUriBuilderFactory, StreamsLocaleProvider streamsLocaleProvider, TransactionTemplate transactionTemplate, ApplicationLinkServiceExtensions appLinkServiceExtensions, StreamsI18nResolver i18nResolver, EventPublisher eventPublisher) {
        this.appLinkService = (ApplicationLinkService)Preconditions.checkNotNull((Object)appLinkService, (Object)"appLinkService");
        this.entityLinkService = (EntityLinkService)Preconditions.checkNotNull((Object)entityLinkService, (Object)"entityLinkService");
        this.entityResolvers = (Iterable)Preconditions.checkNotNull(entityResolvers, (Object)"entityResolvers");
        this.feedParser = (FeedParser)Preconditions.checkNotNull((Object)feedParser, (Object)"feedParser");
        this.streamsFeedUriBuilderFactory = (StreamsFeedUriBuilderFactory)Preconditions.checkNotNull((Object)streamsFeedUriBuilderFactory, (Object)"streamsFeedUriBuilderFactory");
        this.streamsLocaleProvider = (StreamsLocaleProvider)Preconditions.checkNotNull((Object)streamsLocaleProvider, (Object)"streamsLocaleProvider");
        this.transactionTemplate = (TransactionTemplate)Preconditions.checkNotNull((Object)transactionTemplate, (Object)"transactionTemplate");
        this.appLinkServiceExtensions = (ApplicationLinkServiceExtensions)Preconditions.checkNotNull((Object)appLinkServiceExtensions, (Object)"appLinkServiceExtensions");
        this.i18nResolver = (StreamsI18nResolver)Preconditions.checkNotNull((Object)i18nResolver, (Object)"i18nResolver");
        this.eventPublisher = (EventPublisher)Preconditions.checkNotNull((Object)eventPublisher, (Object)"eventPublisher");
    }

    public Iterable<ActivityProvider> get() {
        return Iterables.transform((Iterable)Iterables.filter((Iterable)this.appLinkService.getApplicationLinks(), AppLinksActivityProviders.removeOptOuts()), this.toAppLinkProvider());
    }

    static Predicate<? super ApplicationLink> removeOptOuts() {
        return link -> {
            String value = (String)link.getProperty("IS_ACTIVITY_ITEM_PROVIDER");
            return value == null || Boolean.parseBoolean(value);
        };
    }

    private Function<ApplicationLink, ActivityProvider> toAppLinkProvider() {
        return appLink -> {
            AppLinksActivityProvider provider = new AppLinksActivityProvider((ApplicationLink)appLink, this.entityLinkService, this.entityResolvers, this.feedParser, this.streamsFeedUriBuilderFactory, this.streamsLocaleProvider, this.transactionTemplate, this.appLinkServiceExtensions, this.i18nResolver);
            return new ActivityProviderWithAnalytics(provider, this.eventPublisher);
        };
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.streams.api.common.Either
 *  com.atlassian.streams.api.common.Pair
 *  com.atlassian.streams.api.common.uri.Uri
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang3.RandomUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.internal;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.api.common.Pair;
import com.atlassian.streams.api.common.uri.Uri;
import com.atlassian.streams.internal.ActivityProvider;
import com.atlassian.streams.internal.ActivityProviderCancellableTask;
import com.atlassian.streams.internal.ActivityProviders;
import com.atlassian.streams.internal.HttpParameters;
import com.atlassian.streams.internal.StreamsCompletionService;
import com.atlassian.streams.internal.Sys;
import com.atlassian.streams.internal.feed.FeedAggregator;
import com.atlassian.streams.internal.feed.FeedModel;
import com.atlassian.streams.internal.feed.builder.ToFeedCallable;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FeedBuilder {
    private final Logger log = LoggerFactory.getLogger(FeedBuilder.class);
    private final ActivityProviders activityProviders;
    private final FeedAggregator aggregator;
    private final StreamsCompletionService completionService;
    private final ApplicationProperties applicationProperties;
    private final UserManager userManager;

    public FeedBuilder(ActivityProviders activityProviders, FeedAggregator aggregator, StreamsCompletionService completionService, ApplicationProperties applicationProperties, UserManager userManager) {
        this.applicationProperties = applicationProperties;
        this.activityProviders = (ActivityProviders)Preconditions.checkNotNull((Object)activityProviders, (Object)"activityProviders");
        this.aggregator = (FeedAggregator)Preconditions.checkNotNull((Object)aggregator, (Object)"aggregator");
        this.completionService = (StreamsCompletionService)Preconditions.checkNotNull((Object)completionService, (Object)"completionService");
        this.userManager = (UserManager)Preconditions.checkNotNull((Object)userManager, (Object)"userManager");
    }

    public FeedModel getFeed(Uri self, String contextPath, HttpParameters parameters, String requestLanguages) {
        if (parameters.allowOnlyAuthorized() && this.userManager.getRemoteUserKey() == null) {
            Set<Either<ActivityProvider.Error, FeedModel>> empty = Collections.emptySet();
            return this.aggregator.aggregate(empty, self, 0, parameters.getTitle());
        }
        Iterable<ActivityProvider> providers = this.activityProviders.get((Iterable<Predicate<ActivityProvider>>)ImmutableSet.of(parameters.module(), parameters.fetchLocalOnly(), parameters.isSelectedProvider()));
        ImmutableSet banned = ImmutableSet.copyOf((Iterable)Iterables.filter(providers, (Predicate)Predicates.not(this.completionService.reachable())));
        Iterable notBannedProviders = Iterables.filter(providers, (Predicate)Predicates.not((Predicate)Predicates.in((Collection)banned)));
        Iterable callables = Iterables.transform((Iterable)notBannedProviders, this.toFeedCallable((Pair<Uri, HttpParameters>)Pair.pair((Object)self, (Object)parameters), parameters.calculateContextUrl(this.applicationProperties, contextPath), requestLanguages, RandomUtils.nextLong()));
        Iterable<Object> results = Sys.inDevMode() && !parameters.isTimeoutTest() ? this.completionService.execute(callables) : this.completionService.execute(callables, 10000L, TimeUnit.MILLISECONDS);
        return this.aggregator.aggregate(this.transformForAnonymous(results, (ImmutableSet<ActivityProvider>)banned), self, parameters.parseMaxResults(10), parameters.getTitle());
    }

    private List<Either<ActivityProvider.Error, FeedModel>> transformForAnonymous(Iterable<Either<ActivityProvider.Error, FeedModel>> results, ImmutableSet<ActivityProvider> banned) {
        Stream<Either<ActivityProvider.Error, FeedModel>> resultsStream = StreamSupport.stream(results.spliterator(), false);
        if (this.userManager.getRemoteUserKey() == null) {
            return resultsStream.filter(Either::isRight).collect(Collectors.toList());
        }
        Stream<Either> bannedStream = banned.stream().map(activityProvider -> Either.left((Object)ActivityProvider.Error.banned(activityProvider)));
        return Stream.concat(resultsStream, bannedStream).collect(Collectors.toList());
    }

    private final Function<ActivityProvider, ActivityProviderCancellableTask<Either<ActivityProvider.Error, FeedModel>>> toFeedCallable(Pair<Uri, HttpParameters> feedParameters, URI baseUri, String requestLanguages, long requestId) {
        return new ToFeedCallable(feedParameters, baseUri, requestLanguages, requestId);
    }
}


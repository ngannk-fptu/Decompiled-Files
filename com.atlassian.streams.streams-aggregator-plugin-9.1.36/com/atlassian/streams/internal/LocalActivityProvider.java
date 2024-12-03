/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.streams.api.ActivityRequest
 *  com.atlassian.streams.api.StreamsFeed
 *  com.atlassian.streams.api.common.Either
 *  com.atlassian.streams.spi.ActivityProviderModuleDescriptor
 *  com.atlassian.streams.spi.CancellableTask
 *  com.atlassian.streams.spi.CancellableTask$Result
 *  com.atlassian.streams.spi.SessionManager
 *  com.atlassian.streams.spi.StreamsActivityProvider
 *  com.atlassian.streams.spi.StreamsCommentHandler
 *  com.atlassian.streams.spi.StreamsCommentHandler$PostReplyError
 *  com.atlassian.streams.spi.StreamsFilterOptionProvider
 *  com.atlassian.streams.spi.StreamsI18nResolver
 *  com.atlassian.streams.spi.StreamsKeyProvider
 *  com.atlassian.streams.spi.StreamsKeyProvider$StreamsKey
 *  com.atlassian.streams.spi.StreamsValidator
 *  com.google.common.collect.ImmutableList
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.internal;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.streams.api.ActivityRequest;
import com.atlassian.streams.api.StreamsFeed;
import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.internal.ActivityProvider;
import com.atlassian.streams.internal.ActivityRequestImpl;
import com.atlassian.streams.internal.CallThrottler;
import com.atlassian.streams.internal.feed.FeedModel;
import com.atlassian.streams.internal.feed.builder.FeedFetcher;
import com.atlassian.streams.internal.rest.representations.ProviderFilterRepresentation;
import com.atlassian.streams.internal.rest.representations.StreamsKeysRepresentation;
import com.atlassian.streams.spi.ActivityProviderModuleDescriptor;
import com.atlassian.streams.spi.CancellableTask;
import com.atlassian.streams.spi.SessionManager;
import com.atlassian.streams.spi.StreamsActivityProvider;
import com.atlassian.streams.spi.StreamsCommentHandler;
import com.atlassian.streams.spi.StreamsFilterOptionProvider;
import com.atlassian.streams.spi.StreamsI18nResolver;
import com.atlassian.streams.spi.StreamsKeyProvider;
import com.atlassian.streams.spi.StreamsValidator;
import com.google.common.collect.ImmutableList;
import java.net.URI;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LocalActivityProvider
implements ActivityProvider {
    public static final String ACCEPT_LANGUAGE_KEY = "Accept-Language";
    private static final Logger logger = LoggerFactory.getLogger(LocalActivityProvider.class);
    private final String key;
    private final String completeKey;
    private CallThrottler callThrottler;
    private final String name;
    private final StreamsActivityProvider activityProvider;
    private final StreamsFilterOptionProvider filterOptionProvider;
    private final StreamsKeyProvider keyProvider;
    private final StreamsValidator validator;
    private final StreamsCommentHandler commentHandler;
    private final TransactionTemplate transactionTemplate;
    private final StreamsI18nResolver i18nResolver;
    private final SessionManager sessionManager;
    private final ApplicationProperties applicationProperties;

    public LocalActivityProvider(ActivityProviderModuleDescriptor descriptor, SessionManager sessionManager, TransactionTemplate transactionTemplate, StreamsI18nResolver i18nResolver, ApplicationProperties applicationProperties, CallThrottler callThrottler) {
        this.key = Objects.requireNonNull(descriptor.getKey(), "key");
        this.name = Objects.requireNonNull(descriptor.getI18nNameKey() != null ? i18nResolver.getText(descriptor.getI18nNameKey()) : descriptor.getName(), "name");
        this.completeKey = descriptor.getCompleteKey();
        this.activityProvider = Objects.requireNonNull(descriptor.getModule(), "activityProvider");
        this.sessionManager = Objects.requireNonNull(sessionManager, "sessionManager");
        this.transactionTemplate = Objects.requireNonNull(transactionTemplate, "transactionTemplate");
        this.i18nResolver = Objects.requireNonNull(i18nResolver, "i18nResolver");
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.filterOptionProvider = descriptor.getFilterOptionProvider();
        this.keyProvider = descriptor.getKeyProvider();
        this.validator = descriptor.getValidator();
        this.commentHandler = descriptor.getCommentHandler();
        this.callThrottler = callThrottler;
    }

    @Override
    public boolean matches(String key) {
        return this.getKey().equals(key);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getBaseUrl() {
        return this.applicationProperties.getBaseUrl();
    }

    @Override
    public String getType() {
        return this.applicationProperties.getDisplayName();
    }

    @Override
    public CancellableTask<Either<ActivityProvider.Error, FeedModel>> getActivityFeed(final ActivityRequestImpl request) {
        final CancellableTask task = this.activityProvider.getActivityFeed((ActivityRequest)request);
        return new CancellableTask<Either<ActivityProvider.Error, FeedModel>>(){

            public Either<ActivityProvider.Error, FeedModel> call() {
                if (LocalActivityProvider.this.callThrottler.isBudgetExceeded()) {
                    logger.info("Local activity exceeded the allowed budget of {}% time in the past {}", (Object)LocalActivityProvider.this.callThrottler.getAllowedWallClockPercentage(), (Object)LocalActivityProvider.this.callThrottler.getTimeWindow());
                    if (logger.isTraceEnabled()) {
                        logger.trace("Per-provider Activity Stream statistics for the last {}: {}", (Object)LocalActivityProvider.this.callThrottler.getTimeWindow(), LocalActivityProvider.this.callThrottler.getStats());
                    }
                    return Either.left((Object)ActivityProvider.Error.throttled(LocalActivityProvider.this));
                }
                try (CallThrottler.TrackedCall tracker = LocalActivityProvider.this.callThrottler.startTracking(LocalActivityProvider.this.completeKey);){
                    Either either = (Either)LocalActivityProvider.this.sessionManager.withSession(() -> (Either)LocalActivityProvider.this.transactionTemplate.execute((TransactionCallback)LocalActivityProvider.this.fetchFeed(request, (CancellableTask<StreamsFeed>)task)));
                    return either;
                }
            }

            public CancellableTask.Result cancel() {
                return task.cancel();
            }
        };
    }

    @Override
    public Either<ActivityProvider.Error, Iterable<ProviderFilterRepresentation>> getFilters(boolean addApplinkName) {
        if (this.filterOptionProvider == null) {
            return Either.right(Collections.emptyList());
        }
        return Either.right((Object)ImmutableList.of((Object)new ProviderFilterRepresentation(this.key, this.name, "", this.filterOptionProvider, (I18nResolver)this.i18nResolver)));
    }

    @Override
    public StreamsKeysRepresentation getKeys() {
        if (this.keyProvider == null) {
            return new StreamsKeysRepresentation((Iterable<StreamsKeyProvider.StreamsKey>)ImmutableList.of());
        }
        return new StreamsKeysRepresentation(this.keyProvider.getKeys());
    }

    @Override
    public boolean allKeysAreValid(Iterable<String> keys) {
        if (this.validator == null) {
            return false;
        }
        return StreamSupport.stream(keys.spliterator(), false).allMatch(arg_0 -> ((StreamsValidator)this.validator).isValidKey(arg_0));
    }

    public Either<StreamsCommentHandler.PostReplyError, URI> postReply(URI baseUri, Iterable<String> itemPath, String comment) {
        return this.commentHandler.postReply(baseUri, itemPath, comment);
    }

    private FeedFetcher fetchFeed(ActivityRequest request, CancellableTask<StreamsFeed> task) {
        return new FeedFetcher(this.i18nResolver, request, task, this);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LocalActivityProvider that = (LocalActivityProvider)o;
        return Objects.equals(this.key, that.key) && Objects.equals(this.name, that.name);
    }

    public int hashCode() {
        return Objects.hash(this.key, this.name);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.streams.api.common.Either
 *  com.atlassian.streams.spi.CancellableTask
 *  com.atlassian.streams.spi.CancellableTask$Result
 *  org.apache.commons.lang3.time.StopWatch
 */
package com.atlassian.streams.internal;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.internal.ActivityProvider;
import com.atlassian.streams.internal.ActivityRequestImpl;
import com.atlassian.streams.internal.LocalActivityProvider;
import com.atlassian.streams.internal.analytics.StreamStatsEvent;
import com.atlassian.streams.internal.feed.FeedModel;
import com.atlassian.streams.internal.rest.representations.ProviderFilterRepresentation;
import com.atlassian.streams.internal.rest.representations.StreamsKeysRepresentation;
import com.atlassian.streams.spi.CancellableTask;
import java.util.Objects;
import org.apache.commons.lang3.time.StopWatch;

public class ActivityProviderWithAnalytics
implements ActivityProvider {
    private final ActivityProvider delegate;
    private final EventPublisher eventPublisher;

    public ActivityProviderWithAnalytics(ActivityProvider delegate, EventPublisher eventPublisher) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher");
    }

    public ActivityProvider getDelegate() {
        return this.delegate;
    }

    @Override
    public CancellableTask<Either<ActivityProvider.Error, FeedModel>> getActivityFeed(final ActivityRequestImpl request) {
        final CancellableTask<Either<ActivityProvider.Error, FeedModel>> delegateTask = this.delegate.getActivityFeed(request);
        return new CancellableTask<Either<ActivityProvider.Error, FeedModel>>(){

            public Either<ActivityProvider.Error, FeedModel> call() throws Exception {
                StopWatch stopWatch = StopWatch.createStarted();
                boolean requestSuccessful = true;
                try {
                    Either result = (Either)delegateTask.call();
                    requestSuccessful = result.isRight();
                    Either either = result;
                    return either;
                }
                catch (Exception ex) {
                    requestSuccessful = false;
                    throw ex;
                }
                finally {
                    long processingTime = stopWatch.getTime();
                    ActivityProviderWithAnalytics.this.eventPublisher.publish((Object)new StreamStatsEvent(request.getRequestId(), processingTime, ActivityProviderWithAnalytics.this.delegate instanceof LocalActivityProvider, requestSuccessful, ActivityProviderWithAnalytics.this.delegate.getName().toLowerCase(), request.getMaxResults(), request.getTimeout()));
                }
            }

            public CancellableTask.Result cancel() {
                return delegateTask.cancel();
            }
        };
    }

    @Override
    public boolean matches(String key) {
        return this.delegate.matches(key);
    }

    @Override
    public String getName() {
        return this.delegate.getName();
    }

    @Override
    public String getKey() {
        return this.delegate.getKey();
    }

    @Override
    public String getBaseUrl() {
        return this.delegate.getBaseUrl();
    }

    @Override
    public String getType() {
        return this.delegate.getType();
    }

    @Override
    public Either<ActivityProvider.Error, Iterable<ProviderFilterRepresentation>> getFilters(boolean addApplinkName) {
        return this.delegate.getFilters(addApplinkName);
    }

    @Override
    public StreamsKeysRepresentation getKeys() {
        return this.delegate.getKeys();
    }

    @Override
    public boolean allKeysAreValid(Iterable<String> keys) {
        return this.delegate.allKeysAreValid(keys);
    }
}


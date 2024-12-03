/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.common.Either
 *  com.atlassian.streams.api.common.Pair
 *  com.atlassian.streams.api.common.uri.Uri
 *  com.atlassian.streams.spi.CancellableTask
 *  com.atlassian.streams.spi.CancellableTask$Result
 *  com.google.common.base.Function
 *  com.google.common.base.Joiner
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.internal.feed.builder;

import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.api.common.Pair;
import com.atlassian.streams.api.common.uri.Uri;
import com.atlassian.streams.internal.ActivityProvider;
import com.atlassian.streams.internal.ActivityProviderCancellableTask;
import com.atlassian.streams.internal.ActivityRequestImpl;
import com.atlassian.streams.internal.HttpParameters;
import com.atlassian.streams.internal.NoMatchingRemoteKeysException;
import com.atlassian.streams.internal.feed.FeedModel;
import com.atlassian.streams.spi.CancellableTask;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToFeedCallable
implements Function<ActivityProvider, ActivityProviderCancellableTask<Either<ActivityProvider.Error, FeedModel>>> {
    private static final Logger log = LoggerFactory.getLogger(ToFeedCallable.class);
    private final Pair<Uri, HttpParameters> feedParameters;
    private final String requestLanguages;
    private final URI contextUri;
    private final long requestId;

    public ToFeedCallable(Pair<Uri, HttpParameters> feedParameters, URI contextUri, String requestLanguages, long requestId) {
        this.feedParameters = feedParameters;
        this.contextUri = contextUri;
        this.requestLanguages = requestLanguages;
        this.requestId = requestId;
    }

    public ActivityProviderCancellableTask<Either<ActivityProvider.Error, FeedModel>> apply(final ActivityProvider provider) {
        ActivityRequestImpl.Builder builder = ActivityRequestImpl.builder((Uri)this.feedParameters.first()).contextUri(this.contextUri);
        if (((HttpParameters)this.feedParameters.second()).useAcceptLanguage()) {
            // empty if block
        }
        ActivityRequestImpl request = builder.requestId(this.requestId).build((HttpParameters)this.feedParameters.second(), provider);
        final CancellableTask<Either<ActivityProvider.Error, FeedModel>> task = provider.getActivityFeed(request);
        return new ActivityProviderCancellableTask<Either<ActivityProvider.Error, FeedModel>>(){

            @Override
            public Either<ActivityProvider.Error, FeedModel> call() {
                try {
                    return (Either)task.call();
                }
                catch (NoMatchingRemoteKeysException nmrke) {
                    log.info("No keys from " + provider.getName() + " matched " + Joiner.on((String)",").join(nmrke.getKeys()), (Throwable)nmrke);
                }
                catch (Exception e) {
                    log.error("Error fetching feed", (Throwable)e);
                }
                return Either.left((Object)ActivityProvider.Error.other());
            }

            public CancellableTask.Result cancel() {
                return task.cancel();
            }

            @Override
            public ActivityProvider getActivityProvider() {
                return provider;
            }
        };
    }
}


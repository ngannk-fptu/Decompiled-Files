/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.streams.api.ActivityRequest
 *  com.atlassian.streams.api.StreamsEntry
 *  com.atlassian.streams.api.StreamsFeed
 *  com.atlassian.streams.api.common.Either
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.spi.CancellableTask
 *  com.atlassian.streams.spi.CancelledException
 *  com.atlassian.streams.spi.StreamsI18nResolver
 *  com.google.common.collect.Iterables
 *  org.joda.time.DateTime
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.internal.feed.builder;

import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.streams.api.ActivityRequest;
import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.StreamsFeed;
import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.internal.ActivityProvider;
import com.atlassian.streams.internal.feed.FeedEntry;
import com.atlassian.streams.internal.feed.FeedModel;
import com.atlassian.streams.spi.CancellableTask;
import com.atlassian.streams.spi.CancelledException;
import com.atlassian.streams.spi.StreamsI18nResolver;
import com.google.common.collect.Iterables;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeedFetcher
implements TransactionCallback<Either<ActivityProvider.Error, FeedModel>> {
    private final ActivityRequest request;
    private final CancellableTask<StreamsFeed> task;
    private final ActivityProvider activityProvider;
    private final StreamsI18nResolver i18nResolver;
    private static final Logger logger = LoggerFactory.getLogger(FeedFetcher.class);

    public FeedFetcher(StreamsI18nResolver i18nResolver, ActivityRequest request, CancellableTask<StreamsFeed> task, ActivityProvider activityProvider) {
        this.i18nResolver = i18nResolver;
        this.request = request;
        this.task = task;
        this.activityProvider = activityProvider;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Either<ActivityProvider.Error, FeedModel> doInTransaction() {
        try {
            this.i18nResolver.setRequestLanguages(this.request.getRequestLanguages());
            StreamsFeed stream = (StreamsFeed)this.task.call();
            Iterable entries = stream.getEntries();
            FeedModel.Builder builder = FeedModel.builder(this.request.getUri());
            builder = builder.title((Option<String>)Option.option((Object)stream.getTitle()));
            builder = builder.subtitle((Option<String>)stream.getSubtitle());
            Option some = Option.some((Object)this.getUpdatedDate(this.request, entries));
            builder = builder.updated((Option<DateTime>)some);
            builder.addEntries(Iterables.transform((Iterable)entries, FeedEntry.fromStreamsEntry()));
            Either either = Either.right((Object)builder.build());
            return either;
        }
        catch (CancelledException e) {
            Either either = Either.left((Object)ActivityProvider.Error.timeout(this.activityProvider));
            return either;
        }
        catch (Exception e) {
            logger.error("Exception building feed", (Throwable)e);
            Either either = Either.left((Object)ActivityProvider.Error.other(this.activityProvider));
            return either;
        }
        finally {
            this.i18nResolver.setRequestLanguages(null);
        }
    }

    private DateTime getUpdatedDate(ActivityRequest request, Iterable<StreamsEntry> entries) {
        if (Iterables.isEmpty(entries)) {
            return new DateTime();
        }
        return new DateTime(((StreamsEntry)Iterables.get(entries, (int)0)).getPostedDate().getMillis());
    }
}


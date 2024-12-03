/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.DateUtil
 *  com.atlassian.streams.api.common.Either
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.common.Options
 *  com.atlassian.streams.api.common.uri.Uri
 *  com.google.common.collect.Iterables
 *  org.joda.time.DateTime
 */
package com.atlassian.streams.internal.feed;

import com.atlassian.streams.api.DateUtil;
import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.Options;
import com.atlassian.streams.api.common.uri.Uri;
import com.atlassian.streams.internal.ActivityProvider;
import com.atlassian.streams.internal.atom.abdera.AtomConstants;
import com.atlassian.streams.internal.atom.abdera.StreamsAbdera;
import com.atlassian.streams.internal.feed.ActivitySourceBannedFeedHeader;
import com.atlassian.streams.internal.feed.ActivitySourceThrottledFeedHeader;
import com.atlassian.streams.internal.feed.ActivitySourceTimeOutFeedHeader;
import com.atlassian.streams.internal.feed.FeedEntry;
import com.atlassian.streams.internal.feed.FeedHeader;
import com.atlassian.streams.internal.feed.FeedModel;
import com.google.common.collect.Iterables;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.joda.time.DateTime;

public class FeedAggregator {
    private Predicate<FeedHeader> isNonTimezoneOffsetHeader = header -> {
        if (header instanceof StreamsAbdera.AtomParsedFeedHeader) {
            return !((StreamsAbdera.AtomParsedFeedHeader)header).getElement().getQName().equals(AtomConstants.ATLASSIAN_TIMEZONE_OFFSET);
        }
        return true;
    };

    public FeedModel aggregate(Iterable<Either<ActivityProvider.Error, FeedModel>> feedResponses, Uri selfUri, int maxResults, Option<String> title) {
        FeedModel feed2;
        Either onlyResponse;
        if (this.sizeEquals(feedResponses, 1) && (onlyResponse = (Either)Iterables.getOnlyElement(feedResponses)).isRight() && (feed2 = (FeedModel)onlyResponse.right().get()).getUri().equals((Object)selfUri)) {
            return feed2;
        }
        List successfulFeeds = StreamSupport.stream(feedResponses.spliterator(), false).filter(Either::isRight).map(e -> (FeedModel)e.right().get()).collect(Collectors.toList());
        List<FeedHeader> successfulFeedHeaders = successfulFeeds.stream().map(FeedModel::getHeaders).flatMap(it -> StreamSupport.stream(it.spliterator(), false)).filter(this.isNonTimezoneOffsetHeader).collect(Collectors.toList());
        Optional maybeLatest = successfulFeeds.stream().map(FeedModel::getUpdated).flatMap(Options::stream).max(Comparator.naturalOrder());
        Comparator<FeedEntry> reverseChronological = Comparator.comparing(FeedEntry::getEntryDate).reversed();
        List<FeedEntry> sortedFeedEntries = successfulFeeds.stream().flatMap(feed -> StreamSupport.stream(feed.getEntries().spliterator(), false).map(feedEntry -> feedEntry.toAggregatedEntry((Option<FeedModel>)Option.some((Object)feed)))).sorted(reverseChronological).limit(maxResults).collect(Collectors.toList());
        FeedModel.Builder feedModel = FeedModel.builder(selfUri).title(title).addHeaders(this.createErrorFeedHeaders(feedResponses)).addHeaders(successfulFeedHeaders).addEntries(sortedFeedEntries);
        maybeLatest.ifPresent(latest -> feedModel.updated(DateUtil.toZonedDate((DateTime)latest)));
        return feedModel.build();
    }

    private List<FeedHeader> createErrorFeedHeaders(Iterable<Either<ActivityProvider.Error, FeedModel>> feedResponses) {
        return StreamSupport.stream(feedResponses.spliterator(), false).filter(Either::isLeft).map(e -> (ActivityProvider.Error)e.left().get()).map(error -> {
            if (!error.getApplicationLinkName().isDefined()) {
                return Optional.empty();
            }
            String sourceName = (String)error.getApplicationLinkName().get();
            switch (error.getType()) {
                case BANNED: {
                    return Optional.of(new ActivitySourceBannedFeedHeader(sourceName));
                }
                case TIMEOUT: {
                    return Optional.of(new ActivitySourceTimeOutFeedHeader(sourceName));
                }
                case THROTTLED: {
                    return Optional.of(new ActivitySourceThrottledFeedHeader(sourceName));
                }
            }
            return Optional.empty();
        }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    private boolean sizeEquals(Iterable<?> iterable, int size) {
        int i;
        Iterator<?> it = iterable.iterator();
        for (i = 0; it.hasNext() && i < size; ++i) {
            it.next();
        }
        return !it.hasNext() && i == size;
    }
}


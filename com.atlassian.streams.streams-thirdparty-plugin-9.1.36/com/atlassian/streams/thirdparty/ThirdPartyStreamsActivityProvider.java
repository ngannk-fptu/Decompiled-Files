/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.ActivityRequest
 *  com.atlassian.streams.api.StreamsEntry
 *  com.atlassian.streams.api.StreamsException
 *  com.atlassian.streams.api.StreamsFeed
 *  com.atlassian.streams.api.common.Iterables
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.spi.CancellableTask
 *  com.atlassian.streams.spi.CancellableTask$Result
 *  com.atlassian.streams.spi.Filters
 *  com.atlassian.streams.spi.StandardStreamsFilterOption
 *  com.atlassian.streams.spi.StreamsActivityProvider
 *  com.atlassian.streams.spi.StreamsI18nResolver
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Ordering
 */
package com.atlassian.streams.thirdparty;

import com.atlassian.streams.api.ActivityRequest;
import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.StreamsException;
import com.atlassian.streams.api.StreamsFeed;
import com.atlassian.streams.api.common.Iterables;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.spi.CancellableTask;
import com.atlassian.streams.spi.Filters;
import com.atlassian.streams.spi.StandardStreamsFilterOption;
import com.atlassian.streams.spi.StreamsActivityProvider;
import com.atlassian.streams.spi.StreamsI18nResolver;
import com.atlassian.streams.thirdparty.ThirdPartyStreamsEntryBuilder;
import com.atlassian.streams.thirdparty.api.Activity;
import com.atlassian.streams.thirdparty.api.ActivityQuery;
import com.atlassian.streams.thirdparty.api.ActivityService;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import java.util.Date;

public class ThirdPartyStreamsActivityProvider
implements StreamsActivityProvider {
    private final StreamsI18nResolver i18nResolver;
    private final ActivityService activityService;
    private final ThirdPartyStreamsEntryBuilder entryBuilder;
    private static final Ordering<Activity> byPostedDateDescending = new Ordering<Activity>(){

        public int compare(Activity a, Activity b) {
            return b.getPostedDate().compareTo((Object)a.getPostedDate());
        }
    };

    public ThirdPartyStreamsActivityProvider(ActivityService activityService, StreamsI18nResolver i18nResolver, ThirdPartyStreamsEntryBuilder entryBuilder) {
        this.activityService = (ActivityService)Preconditions.checkNotNull((Object)activityService, (Object)"activityService");
        this.i18nResolver = (StreamsI18nResolver)Preconditions.checkNotNull((Object)i18nResolver, (Object)"i18nResolver");
        this.entryBuilder = (ThirdPartyStreamsEntryBuilder)Preconditions.checkNotNull((Object)entryBuilder, (Object)"entryBuilder");
    }

    public CancellableTask<StreamsFeed> getActivityFeed(final ActivityRequest activityRequest) throws StreamsException {
        return new CancellableTask<StreamsFeed>(){

            public StreamsFeed call() throws Exception {
                return new StreamsFeed(ThirdPartyStreamsActivityProvider.this.i18nResolver.getText("streams.thirdparty.title"), ThirdPartyStreamsActivityProvider.this.getEntries(activityRequest), Option.none(String.class));
            }

            public CancellableTask.Result cancel() {
                return CancellableTask.Result.INTERRUPT;
            }
        };
    }

    @VisibleForTesting
    protected Iterable<StreamsEntry> getEntries(ActivityRequest request) {
        Iterable<StreamsEntry> nextEntries = this.buildEntries(request, this.getActivities(request, 0));
        Object allEntries = ImmutableList.copyOf(nextEntries);
        for (int size = com.google.common.collect.Iterables.size(nextEntries); !com.google.common.collect.Iterables.isEmpty(nextEntries) && size < request.getMaxResults(); size += com.google.common.collect.Iterables.size(nextEntries)) {
            nextEntries = this.buildEntries(request, this.getActivities(request, size));
            if (com.google.common.collect.Iterables.isEmpty(nextEntries)) continue;
            allEntries = com.google.common.collect.Iterables.concat((Iterable)allEntries, nextEntries);
        }
        return Iterables.take((int)request.getMaxResults(), (Iterable)allEntries);
    }

    private Iterable<StreamsEntry> buildEntries(ActivityRequest request, Iterable<Activity> activities) {
        return com.google.common.collect.Iterables.transform((Iterable)Iterables.take((int)request.getMaxResults(), (Iterable)ThirdPartyStreamsActivityProvider.byPostedDateDescending().sortedCopy(activities)), this.toStreamsEntry());
    }

    @VisibleForTesting
    protected Iterable<Activity> getActivities(ActivityRequest request, int startIndex) {
        ActivityQuery.Builder query = ActivityQuery.builder().startDate((Option<Date>)Filters.getMinDate((ActivityRequest)request)).endDate((Option<Date>)Filters.getMaxDate((ActivityRequest)request)).userNames(Filters.getIsValues((Iterable)request.getStandardFilters().get((Object)StandardStreamsFilterOption.USER.getKey()))).excludeUserNames(Filters.getNotValues((Iterable)request.getStandardFilters().get((Object)StandardStreamsFilterOption.USER.getKey()))).providerKeys(Filters.getIsValues((Iterable)request.getProviderFilters().get((Object)"provider_name"))).excludeProviderKeys(Filters.getNotValues((Iterable)request.getProviderFilters().get((Object)"provider_name"))).maxResults(request.getMaxResults()).startIndex(startIndex);
        for (String projectKey : Filters.getProjectKeys((ActivityRequest)request)) {
            query.addEntityFilter("key", projectKey);
        }
        for (String projectKey : Filters.getNotProjectKeys((ActivityRequest)request)) {
            query.addExcludeEntityFilter("key", projectKey);
        }
        for (String issueKey : Filters.getIssueKeys((ActivityRequest)request)) {
            query.addEntityFilter(StandardStreamsFilterOption.ISSUE_KEY.getKey(), issueKey);
        }
        for (String issueKey : Filters.getNotIssueKeys((ActivityRequest)request)) {
            query.addExcludeEntityFilter(StandardStreamsFilterOption.ISSUE_KEY.getKey(), issueKey);
        }
        return this.activityService.activities(query.build());
    }

    private Function<Activity, StreamsEntry> toStreamsEntry() {
        return new Function<Activity, StreamsEntry>(){

            public StreamsEntry apply(Activity activity) {
                return ThirdPartyStreamsActivityProvider.this.entryBuilder.buildStreamsEntry(activity);
            }
        };
    }

    private static final Ordering<Activity> byPostedDateDescending() {
        return byPostedDateDescending;
    }
}


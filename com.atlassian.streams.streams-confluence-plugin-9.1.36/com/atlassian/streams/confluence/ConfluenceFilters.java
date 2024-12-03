/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.ActivityRequest
 *  com.atlassian.streams.spi.Filters
 *  com.atlassian.streams.spi.StandardStreamsFilterOption
 *  com.google.common.base.Predicate
 */
package com.atlassian.streams.confluence;

import com.atlassian.streams.api.ActivityRequest;
import com.atlassian.streams.confluence.changereport.ActivityItem;
import com.atlassian.streams.spi.Filters;
import com.atlassian.streams.spi.StandardStreamsFilterOption;
import com.google.common.base.Predicate;
import java.util.Iterator;

public class ConfluenceFilters {
    public static Iterable<String> getSearchTerms(ActivityRequest request) {
        return Filters.getIsValues((Iterable)request.getStandardFilters().get((Object)StandardStreamsFilterOption.ISSUE_KEY.getKey()));
    }

    public static Iterable<String> getExcludedSearchTerms(ActivityRequest request) {
        return Filters.getNotValues((Iterable)request.getStandardFilters().get((Object)StandardStreamsFilterOption.ISSUE_KEY.getKey()));
    }

    public static Predicate<ActivityItem> activityItemSpace(Predicate<String> inProjectPredicate) {
        return new ActivityItemSpace(inProjectPredicate);
    }

    private static final class ActivityItemSpace
    implements Predicate<ActivityItem> {
        private final Predicate<String> inProjectPredicate;

        public ActivityItemSpace(Predicate<String> inProjectPredicate) {
            this.inProjectPredicate = inProjectPredicate;
        }

        public boolean apply(ActivityItem activityItem) {
            Iterator iterator = activityItem.getSpaceKey().iterator();
            if (iterator.hasNext()) {
                String spaceKey = (String)iterator.next();
                return this.inProjectPredicate.apply((Object)spaceKey);
            }
            return false;
        }
    }
}


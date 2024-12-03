/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.user.ConfluenceUser;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface SubCalendarSubscriptionStatisticsAccessor {
    public Set<String> getSubscribingSubCalendarIds(PersistedSubCalendar var1);

    public int getSubscriberCount(PersistedSubCalendar var1);

    public Set<ConfluenceUser> getUsersSubscribingToSubCalendar(PersistedSubCalendar var1, boolean var2);

    public Collection<ContentEntityObject> getContentEmbeddingSubCalendar(PersistedSubCalendar var1);

    public boolean hasPopularSubscriptions(ConfluenceUser var1);

    public List<PopularSubCalendarSubscription> getPopularSubscriptions(ConfluenceUser var1, int var2, int var3);

    public static class PopularSubCalendarSubscription
    implements Comparable<PopularSubCalendarSubscription>,
    Serializable {
        private final PersistedSubCalendar subCalendar;
        private final int subscribeCount;

        public PopularSubCalendarSubscription(PersistedSubCalendar subCalendar, int subscribeCount) {
            this.subCalendar = subCalendar;
            this.subscribeCount = subscribeCount;
        }

        public PersistedSubCalendar getSubCalendar() {
            return this.subCalendar;
        }

        public int getSubscribeCount() {
            return this.subscribeCount;
        }

        @Override
        public int compareTo(PopularSubCalendarSubscription popularSubscription) {
            int popularity = popularSubscription.getSubscribeCount() - this.getSubscribeCount();
            return 0 == popularity ? this.getSubCalendar().getName().compareTo(popularSubscription.getSubCalendar().getName()) : popularity;
        }
    }
}


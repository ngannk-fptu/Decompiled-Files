/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.store;

import com.addonengine.addons.analytics.service.Event;
import com.addonengine.addons.analytics.service.EventCursor;
import com.addonengine.addons.analytics.service.EventQuery;
import com.addonengine.addons.analytics.service.Page;
import com.addonengine.addons.analytics.service.PageRequest;
import com.addonengine.addons.analytics.service.model.AnalyticsEvent;
import com.addonengine.addons.analytics.service.model.ContentRef;
import com.addonengine.addons.analytics.service.model.ContentSortField;
import com.addonengine.addons.analytics.service.model.ContentType;
import com.addonengine.addons.analytics.service.model.CountType;
import com.addonengine.addons.analytics.service.model.DatePeriodOptions;
import com.addonengine.addons.analytics.service.model.GlobalUserSortField;
import com.addonengine.addons.analytics.service.model.SortOrder;
import com.addonengine.addons.analytics.service.model.SpaceLevelUserSortField;
import com.addonengine.addons.analytics.service.model.SpaceSortField;
import com.addonengine.addons.analytics.service.model.SpaceType;
import com.addonengine.addons.analytics.store.model.ContentViewsByUserData;
import com.addonengine.addons.analytics.store.model.EventData;
import com.addonengine.addons.analytics.store.model.EventsByChildContentData;
import com.addonengine.addons.analytics.store.model.EventsByPeriodData;
import com.addonengine.addons.analytics.store.model.FullContentStatistics;
import com.addonengine.addons.analytics.store.model.FullGlobalUserStatistics;
import com.addonengine.addons.analytics.store.model.FullSpaceStatistics;
import com.addonengine.addons.analytics.store.model.FullSpaceUserStatistics;
import com.addonengine.addons.analytics.store.server.TimedEvent;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u00d6\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\u0018\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0003H&J\u0010\u0010\b\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\u0003H&J\u0016\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b2\u0006\u0010\r\u001a\u00020\u000eH&J\n\u0010\u000f\u001a\u0004\u0018\u00010\u0010H&J\b\u0010\u0011\u001a\u00020\u0003H&J0\u0010\u0012\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00140\u000b\u0012\u0004\u0012\u00020\u00150\u00132\u0006\u0010\u0016\u001a\u00020\u00172\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00150\u0019H&J\u0016\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u001b0\u000b2\u0006\u0010\u001c\u001a\u00020\u0003H&J4\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001e0\u000b2\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020 0\u000b2\u0006\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020&H&J4\u0010'\u001a\b\u0012\u0004\u0012\u00020\u001e0\u000b2\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020 0\u000b2\u0006\u0010(\u001a\u00020\u00032\u0006\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020&H&J:\u0010)\u001a\b\u0012\u0004\u0012\u00020\u001e0\u000b2\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020 0\u000b2\u0006\u0010#\u001a\u00020$2\f\u0010*\u001a\b\u0012\u0004\u0012\u00020,0+2\u0006\u0010%\u001a\u00020&H&Jx\u0010-\u001a\b\u0012\u0004\u0012\u00020.0\u000b2\f\u0010/\u001a\b\u0012\u0004\u0012\u00020 0\u000b2\f\u00100\u001a\b\u0012\u0004\u0012\u00020 0\u000b2\u0006\u0010#\u001a\u00020$2\f\u00101\u001a\b\u0012\u0004\u0012\u0002020+2\u0006\u0010!\u001a\u00020\"2\u0006\u00103\u001a\u00020\u00032\u0006\u00104\u001a\u0002052\u0006\u00106\u001a\u0002072\u0006\u00108\u001a\u00020\u00032\u0006\u00109\u001a\u00020:2\u0006\u0010;\u001a\u00020:H&J\u0084\u0001\u0010<\u001a\b\u0012\u0004\u0012\u00020=0\u000b2\f\u0010/\u001a\b\u0012\u0004\u0012\u00020 0\u000b2\f\u0010>\u001a\b\u0012\u0004\u0012\u00020 0\u000b2\f\u0010?\u001a\b\u0012\u0004\u0012\u00020 0\u000b2\u0006\u0010#\u001a\u00020$2\f\u0010*\u001a\b\u0012\u0004\u0012\u00020,0+2\u0006\u00104\u001a\u00020@2\u0006\u00106\u001a\u0002072\u0006\u00108\u001a\u00020\u00032\u0006\u00109\u001a\u00020:2\u0006\u0010;\u001a\u00020:2\f\u0010%\u001a\b\u0012\u0004\u0012\u00020&0+H&J\u0092\u0001\u0010A\u001a\b\u0012\u0004\u0012\u00020B0\u000b2\f\u0010/\u001a\b\u0012\u0004\u0012\u00020 0\u000b2\f\u0010>\u001a\b\u0012\u0004\u0012\u00020 0\u000b2\f\u0010?\u001a\b\u0012\u0004\u0012\u00020 0\u000b2\f\u00100\u001a\b\u0012\u0004\u0012\u00020 0\u000b2\f\u0010C\u001a\b\u0012\u0004\u0012\u00020 0\u000b2\u0006\u0010#\u001a\u00020$2\f\u0010*\u001a\b\u0012\u0004\u0012\u00020,0+2\u0006\u00104\u001a\u00020D2\u0006\u00106\u001a\u0002072\u0006\u00108\u001a\u00020\u00032\u0006\u00109\u001a\u00020:2\u0006\u0010;\u001a\u00020:H&J\u008c\u0001\u0010E\u001a\b\u0012\u0004\u0012\u00020F0\u000b2\f\u0010/\u001a\b\u0012\u0004\u0012\u00020 0\u000b2\f\u0010>\u001a\b\u0012\u0004\u0012\u00020 0\u000b2\f\u0010?\u001a\b\u0012\u0004\u0012\u00020 0\u000b2\f\u00100\u001a\b\u0012\u0004\u0012\u00020 0\u000b2\f\u0010C\u001a\b\u0012\u0004\u0012\u00020 0\u000b2\u0006\u0010#\u001a\u00020$2\u0006\u0010!\u001a\u00020\"2\u0006\u00104\u001a\u00020G2\u0006\u00106\u001a\u0002072\u0006\u00108\u001a\u00020\u00032\u0006\u00109\u001a\u00020:2\u0006\u0010;\u001a\u00020:H&J\n\u0010H\u001a\u0004\u0018\u00010\u0006H&J\n\u0010I\u001a\u0004\u0018\u00010\u0010H&J\u000f\u0010J\u001a\u0004\u0018\u00010\u0003H&\u00a2\u0006\u0002\u0010KJ\u001e\u0010L\u001a\u00020\u00032\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020M0\u000b2\u0006\u0010N\u001a\u00020OH&\u00a8\u0006P"}, d2={"Lcom/addonengine/addons/analytics/store/EventRepository;", "", "clearSampleEvents", "", "deleteEventsBeforeDate", "date", "Ljava/time/Instant;", "numToDelete", "deleteOldestEvents", "batchSize", "getContentViewsByUser", "", "Lcom/addonengine/addons/analytics/store/model/ContentViewsByUserData;", "content", "Lcom/addonengine/addons/analytics/service/model/ContentRef;", "getEarliestEvent", "Lcom/addonengine/addons/analytics/store/server/TimedEvent;", "getEstimatedCount", "getEvents", "Lcom/addonengine/addons/analytics/service/Page;", "Lcom/addonengine/addons/analytics/service/Event;", "Lcom/addonengine/addons/analytics/service/EventCursor;", "query", "Lcom/addonengine/addons/analytics/service/EventQuery;", "pageRequest", "Lcom/addonengine/addons/analytics/service/PageRequest;", "getEventsByChildContent", "Lcom/addonengine/addons/analytics/store/model/EventsByChildContentData;", "containerId", "getEventsByPeriodForContentInSpace", "Lcom/addonengine/addons/analytics/store/model/EventsByPeriodData;", "events", "Lcom/addonengine/addons/analytics/service/model/AnalyticsEvent;", "spaceKey", "", "datePeriodOptions", "Lcom/addonengine/addons/analytics/service/model/DatePeriodOptions;", "countType", "Lcom/addonengine/addons/analytics/service/model/CountType;", "getEventsByPeriodForSingleContent", "contentId", "getEventsByPeriodForSpaces", "spaceTypes", "", "Lcom/addonengine/addons/analytics/service/model/SpaceType;", "getEventsForAllSpaceContent", "Lcom/addonengine/addons/analytics/store/model/FullContentStatistics;", "viewEvents", "commentEvents", "contentTypes", "Lcom/addonengine/addons/analytics/service/model/ContentType;", "spaceId", "sortField", "Lcom/addonengine/addons/analytics/service/model/ContentSortField;", "sortOrder", "Lcom/addonengine/addons/analytics/service/model/SortOrder;", "maxEventId", "offset", "", "limit", "getEventsForAllSpaces", "Lcom/addonengine/addons/analytics/store/model/FullSpaceStatistics;", "createEvents", "updateEvents", "Lcom/addonengine/addons/analytics/service/model/SpaceSortField;", "getEventsForGlobalUsers", "Lcom/addonengine/addons/analytics/store/model/FullGlobalUserStatistics;", "contributorEvents", "Lcom/addonengine/addons/analytics/service/model/GlobalUserSortField;", "getEventsForSpaceUsers", "Lcom/addonengine/addons/analytics/store/model/FullSpaceUserStatistics;", "Lcom/addonengine/addons/analytics/service/model/SpaceLevelUserSortField;", "getFirstEventDate", "getLatestEvent", "getMaximumEventId", "()Ljava/lang/Long;", "insertEvents", "Lcom/addonengine/addons/analytics/store/model/EventData;", "useSampleStore", "", "analytics"})
public interface EventRepository {
    @NotNull
    public List<ContentViewsByUserData> getContentViewsByUser(@NotNull ContentRef var1);

    @NotNull
    public List<EventsByPeriodData> getEventsByPeriodForSpaces(@NotNull List<? extends AnalyticsEvent> var1, @NotNull DatePeriodOptions var2, @NotNull Set<? extends SpaceType> var3, @NotNull CountType var4);

    @NotNull
    public List<EventsByPeriodData> getEventsByPeriodForContentInSpace(@NotNull List<? extends AnalyticsEvent> var1, @NotNull String var2, @NotNull DatePeriodOptions var3, @NotNull CountType var4);

    @NotNull
    public List<EventsByPeriodData> getEventsByPeriodForSingleContent(@NotNull List<? extends AnalyticsEvent> var1, long var2, @NotNull DatePeriodOptions var4, @NotNull CountType var5);

    @NotNull
    public List<FullSpaceStatistics> getEventsForAllSpaces(@NotNull List<? extends AnalyticsEvent> var1, @NotNull List<? extends AnalyticsEvent> var2, @NotNull List<? extends AnalyticsEvent> var3, @NotNull DatePeriodOptions var4, @NotNull Set<? extends SpaceType> var5, @NotNull SpaceSortField var6, @NotNull SortOrder var7, long var8, int var10, int var11, @NotNull Set<? extends CountType> var12);

    @NotNull
    public List<FullContentStatistics> getEventsForAllSpaceContent(@NotNull List<? extends AnalyticsEvent> var1, @NotNull List<? extends AnalyticsEvent> var2, @NotNull DatePeriodOptions var3, @NotNull Set<? extends ContentType> var4, @NotNull String var5, long var6, @NotNull ContentSortField var8, @NotNull SortOrder var9, long var10, int var12, int var13);

    @Nullable
    public Instant getFirstEventDate();

    @NotNull
    public List<EventsByChildContentData> getEventsByChildContent(long var1);

    public long getEstimatedCount();

    public long insertEvents(@NotNull List<EventData> var1, boolean var2);

    public long clearSampleEvents();

    public long deleteEventsBeforeDate(@NotNull Instant var1, long var2);

    @NotNull
    public List<FullGlobalUserStatistics> getEventsForGlobalUsers(@NotNull List<? extends AnalyticsEvent> var1, @NotNull List<? extends AnalyticsEvent> var2, @NotNull List<? extends AnalyticsEvent> var3, @NotNull List<? extends AnalyticsEvent> var4, @NotNull List<? extends AnalyticsEvent> var5, @NotNull DatePeriodOptions var6, @NotNull Set<? extends SpaceType> var7, @NotNull GlobalUserSortField var8, @NotNull SortOrder var9, long var10, int var12, int var13);

    @NotNull
    public List<FullSpaceUserStatistics> getEventsForSpaceUsers(@NotNull List<? extends AnalyticsEvent> var1, @NotNull List<? extends AnalyticsEvent> var2, @NotNull List<? extends AnalyticsEvent> var3, @NotNull List<? extends AnalyticsEvent> var4, @NotNull List<? extends AnalyticsEvent> var5, @NotNull DatePeriodOptions var6, @NotNull String var7, @NotNull SpaceLevelUserSortField var8, @NotNull SortOrder var9, long var10, int var12, int var13);

    @Nullable
    public Long getMaximumEventId();

    public long deleteOldestEvents(long var1);

    @NotNull
    public Page<List<Event>, EventCursor> getEvents(@NotNull EventQuery var1, @NotNull PageRequest<EventCursor> var2);

    @Nullable
    public TimedEvent getEarliestEvent();

    @Nullable
    public TimedEvent getLatestEvent();
}


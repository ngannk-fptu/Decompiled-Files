/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Version
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.people.KnownUser
 *  com.atlassian.confluence.api.model.search.SearchOptions
 *  com.atlassian.confluence.api.model.search.SearchPageResponse
 *  com.atlassian.confluence.api.model.search.SearchResult
 *  com.atlassian.confluence.api.service.search.CQLSearchService
 *  com.atlassian.confluence.core.ListBuilder
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.SpaceStatus
 *  com.atlassian.confluence.spaces.SpaceType
 *  com.atlassian.confluence.spaces.SpacesQuery
 *  com.atlassian.confluence.spaces.SpacesQuery$Builder
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.IntIterator
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.Reflection
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.IntRange
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.addonengine.addons.analytics.store.server;

import com.addonengine.addons.analytics.service.model.AnalyticsEvent;
import com.addonengine.addons.analytics.store.EventRepository;
import com.addonengine.addons.analytics.store.SampleEventRepository;
import com.addonengine.addons.analytics.store.model.EventData;
import com.addonengine.addons.analytics.store.model.SampleDataMetadata;
import com.addonengine.addons.analytics.store.server.settings.Settings;
import com.addonengine.addons.analytics.store.server.settings.model.SampleDataMetadataSetting;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.people.KnownUser;
import com.atlassian.confluence.api.model.search.SearchOptions;
import com.atlassian.confluence.api.model.search.SearchPageResponse;
import com.atlassian.confluence.api.model.search.SearchResult;
import com.atlassian.confluence.api.service.search.CQLSearchService;
import com.atlassian.confluence.core.ListBuilder;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.IntRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\n\b\u0007\u0018\u00002\u00020\u0001B+\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ!\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u001cH\u0016\u00a2\u0006\u0002\u0010\u001eJ-\u0010\u001f\u001a\u000e\u0012\u0004\u0012\u00020!\u0012\u0004\u0012\u00020!0 2\b\u0010\u001b\u001a\u0004\u0018\u00010\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u001cH\u0002\u00a2\u0006\u0002\u0010\"J\u0018\u0010#\u001a\u00020!2\u0006\u0010$\u001a\u00020!2\u0006\u0010%\u001a\u00020!H\u0002J\n\u0010&\u001a\u0004\u0018\u00010\u001aH\u0016J\u001e\u0010'\u001a\b\u0012\u0004\u0012\u00020)0(2\u0006\u0010*\u001a\u00020+2\u0006\u0010,\u001a\u00020\u000fH\u0002J\u0016\u0010-\u001a\b\u0012\u0004\u0012\u00020+0(2\u0006\u0010,\u001a\u00020\u000fH\u0002J\u0016\u0010.\u001a\b\u0012\u0004\u0012\u00020/0(2\u0006\u0010,\u001a\u00020\u000fH\u0002J6\u00100\u001a\u0002012\u0006\u0010*\u001a\u00020+2\u0006\u00102\u001a\u00020)2\f\u00103\u001a\b\u0012\u0004\u0012\u00020/0(2\u0006\u0010$\u001a\u00020!2\u0006\u0010%\u001a\u00020!H\u0002J>\u00104\u001a\u0002012\u0006\u0010*\u001a\u00020+2\u0006\u00102\u001a\u00020)2\f\u00103\u001a\b\u0012\u0004\u0012\u00020/0(2\u0006\u0010$\u001a\u00020!2\u0006\u0010%\u001a\u00020!2\u0006\u00105\u001a\u00020\u000fH\u0002J>\u00106\u001a\u0002012\u0006\u00102\u001a\u00020)2\u0006\u0010$\u001a\u00020!2\u0006\u0010%\u001a\u00020!2\f\u00103\u001a\b\u0012\u0004\u0012\u00020/0(2\u0006\u0010*\u001a\u00020+2\u0006\u00105\u001a\u00020\u000fH\u0002J \u00107\u001a\u00020\u001a2\u0006\u00108\u001a\u00020!2\u0006\u00109\u001a\u00020!2\u0006\u0010:\u001a\u00020!H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000b\u001a\n \r*\u0004\u0018\u00010\f0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u000fX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u000fX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u000fX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u000fX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u000fX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0018X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006;"}, d2={"Lcom/addonengine/addons/analytics/store/server/SampleEventRepositoryServerImpl;", "Lcom/addonengine/addons/analytics/store/SampleEventRepository;", "spaceManager", "Lcom/atlassian/confluence/spaces/SpaceManager;", "cqlSearchService", "Lcom/atlassian/confluence/api/service/search/CQLSearchService;", "eventRepository", "Lcom/addonengine/addons/analytics/store/EventRepository;", "settings", "Lcom/addonengine/addons/analytics/store/server/settings/Settings;", "(Lcom/atlassian/confluence/spaces/SpaceManager;Lcom/atlassian/confluence/api/service/search/CQLSearchService;Lcom/addonengine/addons/analytics/store/EventRepository;Lcom/addonengine/addons/analytics/store/server/settings/Settings;)V", "log", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "maxPageUpdatesPerPage", "", "maxPageViewsPerPage", "maxSampleContentPerSpace", "maxSampleSpaces", "maxSampleUsers", "numberOfDaysToGenerate", "random", "Ljava/util/Random;", "sampleDataMetadataKey", "", "buildSampleEventStore", "Lcom/addonengine/addons/analytics/store/model/SampleDataMetadata;", "fromTime", "", "toTime", "(Ljava/lang/Long;Ljava/lang/Long;)Lcom/addonengine/addons/analytics/store/model/SampleDataMetadata;", "calculateSampleDateRange", "Lkotlin/Pair;", "Ljava/time/Instant;", "(Ljava/lang/Long;Ljava/lang/Long;)Lkotlin/Pair;", "calculateSemiRandomInstant", "fromDate", "toDate", "getSampleDataMetadata", "getSpaceContents", "", "Lcom/atlassian/confluence/api/model/content/Content;", "space", "Lcom/atlassian/confluence/spaces/Space;", "limit", "getSpaces", "getUsers", "Lcom/atlassian/confluence/api/model/people/KnownUser;", "insertPageCreatedEvent", "", "content", "users", "insertPageUpdatedEvent", "maxNumberOfEvents", "insertPageViewEvents", "setSampleDataMetadata", "minDate", "maxDate", "lastUpdatedAt", "analytics"})
@SourceDebugExtension(value={"SMAP\nSampleEventRepositoryServerImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SampleEventRepositoryServerImpl.kt\ncom/addonengine/addons/analytics/store/server/SampleEventRepositoryServerImpl\n+ 2 Settings.kt\ncom/addonengine/addons/analytics/store/server/settings/Settings\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,206:1\n25#2:207\n1855#3:208\n1855#3,2:209\n1856#3:211\n1549#3:212\n1620#3,3:213\n1549#3:216\n1620#3,3:217\n1549#3:220\n1620#3,3:221\n1549#3:224\n1620#3,3:225\n*S KotlinDebug\n*F\n+ 1 SampleEventRepositoryServerImpl.kt\ncom/addonengine/addons/analytics/store/server/SampleEventRepositoryServerImpl\n*L\n51#1:207\n88#1:208\n91#1:209,2\n88#1:211\n112#1:212\n112#1:213,3\n122#1:216\n122#1:217,3\n137#1:220\n137#1:221,3\n158#1:224\n158#1:225,3\n*E\n"})
public final class SampleEventRepositoryServerImpl
implements SampleEventRepository {
    @NotNull
    private final SpaceManager spaceManager;
    @NotNull
    private final CQLSearchService cqlSearchService;
    @NotNull
    private final EventRepository eventRepository;
    @NotNull
    private final Settings settings;
    private final Logger log;
    @NotNull
    private final String sampleDataMetadataKey;
    private final int maxSampleSpaces;
    private final int maxSampleUsers;
    private final int maxSampleContentPerSpace;
    private final int maxPageViewsPerPage;
    private final int maxPageUpdatesPerPage;
    private final int numberOfDaysToGenerate;
    @NotNull
    private final Random random;

    @Autowired
    public SampleEventRepositoryServerImpl(@ComponentImport @NotNull SpaceManager spaceManager, @ComponentImport @NotNull CQLSearchService cqlSearchService, @NotNull EventRepository eventRepository, @NotNull Settings settings) {
        Intrinsics.checkNotNullParameter((Object)spaceManager, (String)"spaceManager");
        Intrinsics.checkNotNullParameter((Object)cqlSearchService, (String)"cqlSearchService");
        Intrinsics.checkNotNullParameter((Object)eventRepository, (String)"eventRepository");
        Intrinsics.checkNotNullParameter((Object)settings, (String)"settings");
        this.spaceManager = spaceManager;
        this.cqlSearchService = cqlSearchService;
        this.eventRepository = eventRepository;
        this.settings = settings;
        this.log = LoggerFactory.getLogger(this.getClass());
        this.sampleDataMetadataKey = "SAMPLE_DATA_METADATA";
        this.maxSampleSpaces = 200;
        this.maxSampleUsers = 200;
        this.maxSampleContentPerSpace = 200;
        this.maxPageViewsPerPage = 50;
        this.maxPageUpdatesPerPage = 10;
        this.numberOfDaysToGenerate = 90;
        this.random = new Random();
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @Nullable
    public SampleDataMetadata getSampleDataMetadata() {
        SampleDataMetadata sampleDataMetadata;
        void this_$iv;
        SampleDataMetadataSetting sampleDataMetadata2;
        Settings settings = this.settings;
        String key$iv = this.sampleDataMetadataKey;
        boolean $i$f$get = false;
        SampleDataMetadataSetting sampleDataMetadataSetting = sampleDataMetadata2 = (SampleDataMetadataSetting)this_$iv.get(key$iv, Reflection.getOrCreateKotlinClass(SampleDataMetadataSetting.class));
        if (sampleDataMetadataSetting != null) {
            SampleDataMetadataSetting it = sampleDataMetadataSetting;
            boolean bl = false;
            Instant instant = Instant.ofEpochMilli(it.getMinDate());
            Intrinsics.checkNotNullExpressionValue((Object)instant, (String)"ofEpochMilli(...)");
            Instant instant2 = Instant.ofEpochMilli(it.getMaxDate());
            Intrinsics.checkNotNullExpressionValue((Object)instant2, (String)"ofEpochMilli(...)");
            Instant instant3 = Instant.ofEpochMilli(it.getLastUpdatedAt());
            Intrinsics.checkNotNullExpressionValue((Object)instant3, (String)"ofEpochMilli(...)");
            sampleDataMetadata = new SampleDataMetadata(instant, instant2, instant3);
        } else {
            sampleDataMetadata = null;
        }
        return sampleDataMetadata;
    }

    private final SampleDataMetadata setSampleDataMetadata(Instant minDate, Instant maxDate, Instant lastUpdatedAt) {
        SampleDataMetadataSetting sampleDataMetadata = new SampleDataMetadataSetting(minDate.toEpochMilli(), maxDate.toEpochMilli(), lastUpdatedAt.toEpochMilli());
        this.settings.set(this.sampleDataMetadataKey, sampleDataMetadata);
        SampleDataMetadataSetting it = sampleDataMetadata;
        boolean bl = false;
        Instant instant = Instant.ofEpochMilli(it.getMinDate());
        Intrinsics.checkNotNullExpressionValue((Object)instant, (String)"ofEpochMilli(...)");
        Instant instant2 = Instant.ofEpochMilli(it.getMaxDate());
        Intrinsics.checkNotNullExpressionValue((Object)instant2, (String)"ofEpochMilli(...)");
        Instant instant3 = Instant.ofEpochMilli(it.getLastUpdatedAt());
        Intrinsics.checkNotNullExpressionValue((Object)instant3, (String)"ofEpochMilli(...)");
        return new SampleDataMetadata(instant, instant2, instant3);
    }

    @Override
    @NotNull
    public SampleDataMetadata buildSampleEventStore(@Nullable Long fromTime, @Nullable Long toTime) {
        long deletedSampleEvents = this.eventRepository.clearSampleEvents();
        this.log.info("Deleted " + deletedSampleEvents + " sample events");
        Pair<Instant, Instant> pair = this.calculateSampleDateRange(fromTime, toTime);
        Instant fromDate = (Instant)pair.component1();
        Instant toDate = (Instant)pair.component2();
        List<Space> spaces = this.getSpaces(this.maxSampleSpaces);
        List<KnownUser> users = this.getUsers(this.maxSampleUsers);
        Iterable $this$forEach$iv = spaces;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            Space space = (Space)element$iv;
            boolean bl = false;
            List<Content> contents = this.getSpaceContents(space, this.maxSampleContentPerSpace);
            Iterable $this$forEach$iv2 = contents;
            boolean $i$f$forEach2 = false;
            for (Object element$iv2 : $this$forEach$iv2) {
                Content content = (Content)element$iv2;
                boolean bl2 = false;
                this.insertPageViewEvents(content, fromDate, toDate, users, space, this.maxPageViewsPerPage);
                this.insertPageCreatedEvent(space, content, users, fromDate, toDate);
                this.insertPageUpdatedEvent(space, content, users, fromDate, toDate, this.maxPageUpdatesPerPage);
            }
        }
        Instant instant = Instant.now();
        Intrinsics.checkNotNullExpressionValue((Object)instant, (String)"now(...)");
        return this.setSampleDataMetadata(fromDate, toDate, instant);
    }

    /*
     * Unable to fully structure code
     */
    private final Pair<Instant, Instant> calculateSampleDateRange(Long fromTime, Long toTime) {
        if (fromTime == null) ** GOTO lbl-1000
        var4_3 = 0L;
        if (fromTime != var4_3) {
            v0 = Instant.ofEpochSecond(fromTime);
        } else lbl-1000:
        // 2 sources

        {
            v0 = fromInstant = Instant.now();
        }
        if (toTime == null) ** GOTO lbl-1000
        var5_6 = 0L;
        if (toTime != var5_6) {
            v1 = Instant.ofEpochSecond(toTime);
        } else lbl-1000:
        // 2 sources

        {
            v1 = fromInstant.plus((long)this.numberOfDaysToGenerate, ChronoUnit.DAYS);
        }
        toInstant = v1;
        return new Pair((Object)fromInstant, (Object)toInstant);
    }

    /*
     * WARNING - void declaration
     */
    private final List<Content> getSpaceContents(Space space, int limit) {
        void $this$mapTo$iv$iv;
        SearchOptions searchOptions = SearchOptions.buildDefault();
        Expansion[] expansionArray = new Expansion[1];
        String[] stringArray = new String[]{"version", "history"};
        expansionArray[0] = new Expansion("content", Expansions.of((String[])stringArray));
        SearchPageResponse results = this.cqlSearchService.search("space.key = '" + space.getKey() + "' and type in (page)", searchOptions, (PageRequest)new SimplePageRequest(0, limit), expansionArray);
        Intrinsics.checkNotNull((Object)results);
        Iterable $this$map$iv = (Iterable)results;
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            SearchResult searchResult = (SearchResult)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            Object object = it.getEntity();
            Intrinsics.checkNotNull((Object)object, (String)"null cannot be cast to non-null type com.atlassian.confluence.api.model.content.Content");
            collection.add((Content)object);
        }
        return (List)destination$iv$iv;
    }

    /*
     * WARNING - void declaration
     */
    private final List<KnownUser> getUsers(int limit) {
        void $this$mapTo$iv$iv;
        SearchOptions searchOptions = SearchOptions.buildDefault();
        SearchPageResponse results = this.cqlSearchService.search("type = user", searchOptions, (PageRequest)new SimplePageRequest(0, limit), new Expansion[0]);
        List list = results.getResults();
        Intrinsics.checkNotNullExpressionValue((Object)list, (String)"getResults(...)");
        Iterable $this$map$iv = list;
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            SearchResult searchResult = (SearchResult)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            Object object = it.getEntity();
            Intrinsics.checkNotNull((Object)object, (String)"null cannot be cast to non-null type com.atlassian.confluence.api.model.people.KnownUser");
            collection.add((KnownUser)object);
        }
        return (List)destination$iv$iv;
    }

    private final List<Space> getSpaces(int limit) {
        SpacesQuery.Builder spacesQueryBuilder = SpacesQuery.newQuery().withSpaceStatus(SpaceStatus.CURRENT).withSpaceType(SpaceType.GLOBAL);
        ListBuilder listBuilder = this.spaceManager.getSpaces(spacesQueryBuilder.build());
        Intrinsics.checkNotNullExpressionValue((Object)listBuilder, (String)"getSpaces(...)");
        ListBuilder spaceListBuilder = listBuilder;
        List list = spaceListBuilder.getRange(0, limit);
        Intrinsics.checkNotNullExpressionValue((Object)list, (String)"getRange(...)");
        return list;
    }

    /*
     * WARNING - void declaration
     */
    private final void insertPageViewEvents(Content content, Instant fromDate, Instant toDate, List<? extends KnownUser> users, Space space, int maxNumberOfEvents) {
        void $this$mapTo$iv$iv;
        int numberOfEvents = this.random.nextInt(maxNumberOfEvents);
        Iterable $this$map$iv = (Iterable)new IntRange(0, numberOfEvents);
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        Iterator iterator = $this$mapTo$iv$iv.iterator();
        while (iterator.hasNext()) {
            int item$iv$iv;
            int n = item$iv$iv = ((IntIterator)iterator).nextInt();
            Collection collection = destination$iv$iv;
            boolean bl = false;
            Instant eventAt = this.calculateSemiRandomInstant(fromDate, toDate);
            KnownUser user = users.get(this.random.nextInt(users.size()));
            AnalyticsEvent analyticsEvent = AnalyticsEvent.PAGE_VIEWED;
            String string = space.getKey();
            long l = content.getId().asLong();
            long l2 = content.getId().asLong();
            Instant instant = Instant.ofEpochMilli(((Version)content.getVersionRef().get()).getWhen().getMillis());
            String string2 = ((UserKey)user.getUserKey().get()).getStringValue();
            collection.add(new EventData(analyticsEvent, eventAt, l, string, string2, l2, instant));
        }
        List events2 = (List)destination$iv$iv;
        this.eventRepository.insertEvents(events2, true);
    }

    /*
     * WARNING - void declaration
     */
    private final void insertPageUpdatedEvent(Space space, Content content, List<? extends KnownUser> users, Instant fromDate, Instant toDate, int maxNumberOfEvents) {
        void $this$mapTo$iv$iv;
        int numberOfEvents = this.random.nextInt(maxNumberOfEvents);
        Iterable $this$map$iv = (Iterable)new IntRange(0, numberOfEvents);
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        Iterator iterator = $this$mapTo$iv$iv.iterator();
        while (iterator.hasNext()) {
            int item$iv$iv;
            int n = item$iv$iv = ((IntIterator)iterator).nextInt();
            Collection collection = destination$iv$iv;
            boolean bl = false;
            Instant eventAt = this.calculateSemiRandomInstant(fromDate, toDate);
            KnownUser user = users.get(this.random.nextInt(users.size()));
            AnalyticsEvent analyticsEvent = AnalyticsEvent.PAGE_UPDATED;
            String string = space.getKey();
            long l = content.getId().asLong();
            long l2 = content.getId().asLong();
            Instant instant = Instant.ofEpochMilli(((Version)content.getVersionRef().get()).getWhen().getMillis());
            String string2 = ((UserKey)user.getUserKey().get()).getStringValue();
            collection.add(new EventData(analyticsEvent, eventAt, l, string, string2, l2, instant));
        }
        List events2 = (List)destination$iv$iv;
        this.eventRepository.insertEvents(events2, true);
    }

    private final void insertPageCreatedEvent(Space space, Content content, List<? extends KnownUser> users, Instant fromDate, Instant toDate) {
        KnownUser user = users.get(this.random.nextInt(users.size()));
        Instant eventAt = this.calculateSemiRandomInstant(fromDate, toDate);
        AnalyticsEvent analyticsEvent = AnalyticsEvent.PAGE_CREATED;
        String string = space.getKey();
        long l = content.getId().asLong();
        long l2 = content.getId().asLong();
        Instant instant = Instant.ofEpochMilli(((Version)content.getVersionRef().get()).getWhen().getMillis());
        String string2 = ((UserKey)user.getUserKey().get()).getStringValue();
        this.eventRepository.insertEvents(CollectionsKt.listOf((Object)new EventData(analyticsEvent, eventAt, l, string, string2, l2, instant)), true);
    }

    private final Instant calculateSemiRandomInstant(Instant fromDate, Instant toDate) {
        long randomMilli = (long)((double)fromDate.toEpochMilli() + this.random.nextDouble() * (double)(toDate.toEpochMilli() - fromDate.toEpochMilli()));
        OffsetDateTime randomDate = Instant.ofEpochMilli(randomMilli).atOffset(ZoneOffset.UTC);
        Object object = new DayOfWeek[]{DayOfWeek.SATURDAY, DayOfWeek.SUNDAY};
        boolean isWeekend = CollectionsKt.listOf((Object[])object).contains(randomDate.getDayOfWeek());
        if (isWeekend && this.random.nextDouble() > 0.2) {
            Instant instant = randomDate.plusDays((long)this.random.nextInt(4) + 2L).toInstant();
            Intrinsics.checkNotNullExpressionValue((Object)instant, (String)"toInstant(...)");
            return instant;
        }
        object = randomDate.toInstant();
        Intrinsics.checkNotNull((Object)object);
        return object;
    }
}


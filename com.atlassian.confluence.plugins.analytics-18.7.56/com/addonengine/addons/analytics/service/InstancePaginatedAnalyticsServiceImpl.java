/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.sequences.Sequence
 *  kotlin.sequences.SequencesKt
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.addonengine.addons.analytics.service;

import com.addonengine.addons.analytics.service.InstancePaginatedAnalyticsService;
import com.addonengine.addons.analytics.service.InstancePaginatedAnalyticsServiceImpl;
import com.addonengine.addons.analytics.service.UtilsKt;
import com.addonengine.addons.analytics.service.confluence.SpaceService;
import com.addonengine.addons.analytics.service.confluence.UserService;
import com.addonengine.addons.analytics.service.confluence.model.Space;
import com.addonengine.addons.analytics.service.model.AnalyticsEvent;
import com.addonengine.addons.analytics.service.model.ContentType;
import com.addonengine.addons.analytics.service.model.CountType;
import com.addonengine.addons.analytics.service.model.DatePeriodOptions;
import com.addonengine.addons.analytics.service.model.GlobalUserActivity;
import com.addonengine.addons.analytics.service.model.GlobalUserSortField;
import com.addonengine.addons.analytics.service.model.LazyFetching;
import com.addonengine.addons.analytics.service.model.SortOrder;
import com.addonengine.addons.analytics.service.model.SpaceActivity;
import com.addonengine.addons.analytics.service.model.SpaceSortField;
import com.addonengine.addons.analytics.service.model.SpaceType;
import com.addonengine.addons.analytics.store.EventRepository;
import com.addonengine.addons.analytics.store.model.FullGlobalUserStatistics;
import com.addonengine.addons.analytics.store.model.FullSpaceStatistics;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

@ConfluenceComponent
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000x\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0003\b\u0007\u0018\u0000 &2\u00020\u0001:\u0001&B\u001f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJj\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\f\u001a\u00020\r2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u000f2\b\u0010\u0013\u001a\u0004\u0018\u00010\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001a2\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001c0\u000f2\u0006\u0010\u001d\u001a\u00020\u001eH\u0016JT\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020 0\n2\u0006\u0010\f\u001a\u00020\r2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u000f2\b\u0010\u0013\u001a\u0004\u0018\u00010\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020!2\u0006\u0010\u0019\u001a\u00020\u001aH\u0016J\u0015\u0010\"\u001a\u0004\u0018\u00010#*\u0004\u0018\u00010$H\u0002\u00a2\u0006\u0002\u0010%R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006'"}, d2={"Lcom/addonengine/addons/analytics/service/InstancePaginatedAnalyticsServiceImpl;", "Lcom/addonengine/addons/analytics/service/InstancePaginatedAnalyticsService;", "spaceService", "Lcom/addonengine/addons/analytics/service/confluence/SpaceService;", "eventRepository", "Lcom/addonengine/addons/analytics/store/EventRepository;", "userService", "Lcom/addonengine/addons/analytics/service/confluence/UserService;", "(Lcom/addonengine/addons/analytics/service/confluence/SpaceService;Lcom/addonengine/addons/analytics/store/EventRepository;Lcom/addonengine/addons/analytics/service/confluence/UserService;)V", "getActivityBySpace", "", "Lcom/addonengine/addons/analytics/service/model/SpaceActivity;", "datePeriodOptions", "Lcom/addonengine/addons/analytics/service/model/DatePeriodOptions;", "spaceTypes", "", "Lcom/addonengine/addons/analytics/service/model/SpaceType;", "contentTypes", "Lcom/addonengine/addons/analytics/service/model/ContentType;", "pageToken", "", "limit", "", "sortField", "Lcom/addonengine/addons/analytics/service/model/SpaceSortField;", "sortOrder", "Lcom/addonengine/addons/analytics/service/model/SortOrder;", "countType", "Lcom/addonengine/addons/analytics/service/model/CountType;", "includeSpaceCategories", "", "getActivityByUser", "Lcom/addonengine/addons/analytics/service/model/GlobalUserActivity;", "Lcom/addonengine/addons/analytics/service/model/GlobalUserSortField;", "toTimestamp", "Ljava/time/Instant;", "", "(Ljava/lang/Long;)Ljava/time/Instant;", "Companion", "analytics"})
@SourceDebugExtension(value={"SMAP\nInstancePaginatedAnalyticsServiceImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InstancePaginatedAnalyticsServiceImpl.kt\ncom/addonengine/addons/analytics/service/InstancePaginatedAnalyticsServiceImpl\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,156:1\n1#2:157\n*E\n"})
public final class InstancePaginatedAnalyticsServiceImpl
implements InstancePaginatedAnalyticsService {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final SpaceService spaceService;
    @NotNull
    private final EventRepository eventRepository;
    @NotNull
    private final UserService userService;
    private static final Integer firstBatchSize = Integer.getInteger("confluence.analytics.pagination.first.batch.size", 500);
    private static final Integer subsequentBatchSize = Integer.getInteger("confluence.analytics.pagination.subsequent.batch.size", 25000);

    @Autowired
    public InstancePaginatedAnalyticsServiceImpl(@NotNull SpaceService spaceService, @NotNull EventRepository eventRepository, @NotNull UserService userService) {
        Intrinsics.checkNotNullParameter((Object)spaceService, (String)"spaceService");
        Intrinsics.checkNotNullParameter((Object)eventRepository, (String)"eventRepository");
        Intrinsics.checkNotNullParameter((Object)userService, (String)"userService");
        this.spaceService = spaceService;
        this.eventRepository = eventRepository;
        this.userService = userService;
    }

    @Override
    @NotNull
    public List<SpaceActivity> getActivityBySpace(@NotNull DatePeriodOptions datePeriodOptions, @NotNull Set<? extends SpaceType> spaceTypes, @NotNull Set<? extends ContentType> contentTypes, @Nullable String pageToken, int limit, @NotNull SpaceSortField sortField, @NotNull SortOrder sortOrder, @NotNull Set<? extends CountType> countType, boolean includeSpaceCategories) {
        Intrinsics.checkNotNullParameter((Object)datePeriodOptions, (String)"datePeriodOptions");
        Intrinsics.checkNotNullParameter(spaceTypes, (String)"spaceTypes");
        Intrinsics.checkNotNullParameter(contentTypes, (String)"contentTypes");
        Intrinsics.checkNotNullParameter((Object)((Object)sortField), (String)"sortField");
        Intrinsics.checkNotNullParameter((Object)((Object)sortOrder), (String)"sortOrder");
        Intrinsics.checkNotNullParameter(countType, (String)"countType");
        Long l = this.eventRepository.getMaximumEventId();
        long maxEventId = l != null ? l : 0L;
        Integer n = firstBatchSize;
        Intrinsics.checkNotNullExpressionValue((Object)n, (String)"firstBatchSize");
        int n2 = ((Number)n).intValue();
        Integer n3 = subsequentBatchSize;
        Intrinsics.checkNotNullExpressionValue((Object)n3, (String)"subsequentBatchSize");
        Sequence allSpacesStatistics2 = LazyFetching.Companion.numerical(n2, ((Number)n3).intValue(), (Function2)new Function2<Integer, Integer, List<? extends FullSpaceStatistics>>(this, contentTypes, datePeriodOptions, spaceTypes, sortField, sortOrder, maxEventId, countType){
            final /* synthetic */ InstancePaginatedAnalyticsServiceImpl this$0;
            final /* synthetic */ Set<ContentType> $contentTypes;
            final /* synthetic */ DatePeriodOptions $datePeriodOptions;
            final /* synthetic */ Set<SpaceType> $spaceTypes;
            final /* synthetic */ SpaceSortField $sortField;
            final /* synthetic */ SortOrder $sortOrder;
            final /* synthetic */ long $maxEventId;
            final /* synthetic */ Set<CountType> $countType;
            {
                this.this$0 = $receiver;
                this.$contentTypes = $contentTypes;
                this.$datePeriodOptions = $datePeriodOptions;
                this.$spaceTypes = $spaceTypes;
                this.$sortField = $sortField;
                this.$sortOrder = $sortOrder;
                this.$maxEventId = $maxEventId;
                this.$countType = $countType;
                super(2);
            }

            @NotNull
            public final List<FullSpaceStatistics> invoke(int offset, int batchLimit) {
                return InstancePaginatedAnalyticsServiceImpl.access$getEventRepository$p(this.this$0).getEventsForAllSpaces(UtilsKt.contentTypesToEvents(this.$contentTypes, AnalyticsEvent.PAGE_VIEWED, AnalyticsEvent.BLOG_VIEWED), UtilsKt.contentTypesToEvents(this.$contentTypes, AnalyticsEvent.PAGE_CREATED, AnalyticsEvent.BLOG_CREATED), UtilsKt.contentTypesToEvents(this.$contentTypes, AnalyticsEvent.PAGE_UPDATED, AnalyticsEvent.BLOG_UPDATED), this.$datePeriodOptions, this.$spaceTypes, this.$sortField, this.$sortOrder, this.$maxEventId, offset, batchLimit, this.$countType);
            }
        }).asSequence();
        CharSequence charSequence = pageToken;
        Sequence currentPageCandidates2 = charSequence == null || charSequence.length() == 0 ? allSpacesStatistics2 : SequencesKt.drop((Sequence)SequencesKt.dropWhile(allSpacesStatistics2, (Function1)((Function1)new Function1<FullSpaceStatistics, Boolean>(pageToken){
            final /* synthetic */ String $pageToken;
            {
                this.$pageToken = $pageToken;
                super(1);
            }

            @NotNull
            public final Boolean invoke(@NotNull FullSpaceStatistics it) {
                Intrinsics.checkNotNullParameter((Object)it, (String)"it");
                return !Intrinsics.areEqual((Object)it.getSpaceKey(), (Object)this.$pageToken);
            }
        })), (int)1);
        return SequencesKt.toList((Sequence)SequencesKt.take((Sequence)SequencesKt.map((Sequence)SequencesKt.filter((Sequence)SequencesKt.map((Sequence)currentPageCandidates2, (Function1)((Function1)new Function1<FullSpaceStatistics, Pair<? extends FullSpaceStatistics, ? extends Space>>(this, includeSpaceCategories){
            final /* synthetic */ InstancePaginatedAnalyticsServiceImpl this$0;
            final /* synthetic */ boolean $includeSpaceCategories;
            {
                this.this$0 = $receiver;
                this.$includeSpaceCategories = $includeSpaceCategories;
                super(1);
            }

            @NotNull
            public final Pair<FullSpaceStatistics, Space> invoke(@NotNull FullSpaceStatistics it) {
                Intrinsics.checkNotNullParameter((Object)it, (String)"it");
                return new Pair((Object)it, (Object)InstancePaginatedAnalyticsServiceImpl.access$getSpaceService$p(this.this$0).getByKeyOrNull(it.getSpaceKey(), this.$includeSpaceCategories));
            }
        })), (Function1)getActivityBySpace.2.INSTANCE), (Function1)((Function1)new Function1<Pair<? extends FullSpaceStatistics, ? extends Space>, SpaceActivity>(this){
            final /* synthetic */ InstancePaginatedAnalyticsServiceImpl this$0;
            {
                this.this$0 = $receiver;
                super(1);
            }

            @NotNull
            public final SpaceActivity invoke(@NotNull Pair<FullSpaceStatistics, Space> it) {
                Space spaceObject;
                Intrinsics.checkNotNullParameter(it, (String)"it");
                FullSpaceStatistics spaceStats = (FullSpaceStatistics)it.getFirst();
                Space space = spaceObject = (Space)it.getSecond();
                Intrinsics.checkNotNull((Object)space);
                Space space2 = space;
                Long l = spaceStats.getViewCount();
                long l2 = spaceStats.getCreateCount();
                long l3 = spaceStats.getUpdateCount();
                Long l4 = spaceStats.getUsersViewed();
                Instant instant = InstancePaginatedAnalyticsServiceImpl.access$toTimestamp(this.this$0, spaceStats.getLastEventAt());
                return new SpaceActivity(space2, l2, l3, instant, l4, l);
            }
        })), (int)limit));
    }

    private final Instant toTimestamp(Long $this$toTimestamp) {
        Instant instant;
        Long l = $this$toTimestamp;
        if (l != null) {
            long it = ((Number)l).longValue();
            boolean bl = false;
            instant = Instant.ofEpochMilli($this$toTimestamp);
        } else {
            instant = null;
        }
        return instant;
    }

    @Override
    @NotNull
    public List<GlobalUserActivity> getActivityByUser(@NotNull DatePeriodOptions datePeriodOptions, @NotNull Set<? extends SpaceType> spaceTypes, @NotNull Set<? extends ContentType> contentTypes, @Nullable String pageToken, int limit, @NotNull GlobalUserSortField sortField, @NotNull SortOrder sortOrder) {
        Sequence eventsByUser2;
        Intrinsics.checkNotNullParameter((Object)datePeriodOptions, (String)"datePeriodOptions");
        Intrinsics.checkNotNullParameter(spaceTypes, (String)"spaceTypes");
        Intrinsics.checkNotNullParameter(contentTypes, (String)"contentTypes");
        Intrinsics.checkNotNullParameter((Object)((Object)sortField), (String)"sortField");
        Intrinsics.checkNotNullParameter((Object)((Object)sortOrder), (String)"sortOrder");
        Long l = this.eventRepository.getMaximumEventId();
        if (l == null) {
            return CollectionsKt.emptyList();
        }
        long maxEventId = l;
        List<AnalyticsEvent> viewEvents = UtilsKt.contentTypesToEvents(contentTypes, AnalyticsEvent.PAGE_VIEWED, AnalyticsEvent.BLOG_VIEWED);
        List<AnalyticsEvent> createEvents = UtilsKt.contentTypesToEvents(contentTypes, AnalyticsEvent.PAGE_CREATED, AnalyticsEvent.BLOG_CREATED);
        List<AnalyticsEvent> updateEvents = UtilsKt.contentTypesToEvents(contentTypes, AnalyticsEvent.PAGE_UPDATED, AnalyticsEvent.BLOG_UPDATED);
        List commentEvents = CollectionsKt.listOf((Object)((Object)AnalyticsEvent.COMMENT_CREATED));
        List contributorEvents = CollectionsKt.toList((Iterable)CollectionsKt.union((Iterable)CollectionsKt.union((Iterable)createEvents, (Iterable)updateEvents), (Iterable)commentEvents));
        Integer n = firstBatchSize;
        Intrinsics.checkNotNullExpressionValue((Object)n, (String)"firstBatchSize");
        int n2 = ((Number)n).intValue();
        Integer n3 = subsequentBatchSize;
        Intrinsics.checkNotNullExpressionValue((Object)n3, (String)"subsequentBatchSize");
        Sequence seq = eventsByUser2 = LazyFetching.Companion.numerical(n2, ((Number)n3).intValue(), (Function2)new Function2<Integer, Integer, List<? extends FullGlobalUserStatistics>>(this, viewEvents, createEvents, updateEvents, (List<? extends AnalyticsEvent>)commentEvents, (List<? extends AnalyticsEvent>)contributorEvents, datePeriodOptions, spaceTypes, sortField, sortOrder, maxEventId){
            final /* synthetic */ InstancePaginatedAnalyticsServiceImpl this$0;
            final /* synthetic */ List<AnalyticsEvent> $viewEvents;
            final /* synthetic */ List<AnalyticsEvent> $createEvents;
            final /* synthetic */ List<AnalyticsEvent> $updateEvents;
            final /* synthetic */ List<AnalyticsEvent> $commentEvents;
            final /* synthetic */ List<AnalyticsEvent> $contributorEvents;
            final /* synthetic */ DatePeriodOptions $datePeriodOptions;
            final /* synthetic */ Set<SpaceType> $spaceTypes;
            final /* synthetic */ GlobalUserSortField $sortField;
            final /* synthetic */ SortOrder $sortOrder;
            final /* synthetic */ long $maxEventId;
            {
                this.this$0 = $receiver;
                this.$viewEvents = $viewEvents;
                this.$createEvents = $createEvents;
                this.$updateEvents = $updateEvents;
                this.$commentEvents = $commentEvents;
                this.$contributorEvents = $contributorEvents;
                this.$datePeriodOptions = $datePeriodOptions;
                this.$spaceTypes = $spaceTypes;
                this.$sortField = $sortField;
                this.$sortOrder = $sortOrder;
                this.$maxEventId = $maxEventId;
                super(2);
            }

            @NotNull
            public final List<FullGlobalUserStatistics> invoke(int offset, int batchLimit) {
                return InstancePaginatedAnalyticsServiceImpl.access$getEventRepository$p(this.this$0).getEventsForGlobalUsers(this.$viewEvents, this.$createEvents, this.$updateEvents, this.$commentEvents, this.$contributorEvents, this.$datePeriodOptions, this.$spaceTypes, this.$sortField, this.$sortOrder, this.$maxEventId, offset, batchLimit);
            }
        }).asSequence();
        boolean bl = false;
        CharSequence charSequence = pageToken;
        return SequencesKt.toList((Sequence)SequencesKt.take((Sequence)SequencesKt.map((Sequence)(charSequence == null || charSequence.length() == 0 ? seq : SequencesKt.drop((Sequence)SequencesKt.dropWhile(seq, (Function1)((Function1)new Function1<FullGlobalUserStatistics, Boolean>(pageToken){
            final /* synthetic */ String $pageToken;
            {
                this.$pageToken = $pageToken;
                super(1);
            }

            @NotNull
            public final Boolean invoke(@NotNull FullGlobalUserStatistics it) {
                Intrinsics.checkNotNullParameter((Object)it, (String)"it");
                return !Intrinsics.areEqual((Object)this.$pageToken, (Object)it.getUserKey());
            }
        })), (int)1)), (Function1)((Function1)new Function1<FullGlobalUserStatistics, GlobalUserActivity>(this){
            final /* synthetic */ InstancePaginatedAnalyticsServiceImpl this$0;
            {
                this.this$0 = $receiver;
                super(1);
            }

            @NotNull
            public final GlobalUserActivity invoke(@NotNull FullGlobalUserStatistics it) {
                Intrinsics.checkNotNullParameter((Object)it, (String)"it");
                return new GlobalUserActivity(it.getUserKey(), InstancePaginatedAnalyticsServiceImpl.access$getUserService$p(this.this$0).getUserType(it.getUserKey()), it.getViewedCount(), it.getCreatedCount(), it.getUpdatedCount(), it.getCommentsCount(), it.getContributorScore());
            }
        })), (int)limit));
    }

    public static final /* synthetic */ EventRepository access$getEventRepository$p(InstancePaginatedAnalyticsServiceImpl $this) {
        return $this.eventRepository;
    }

    public static final /* synthetic */ SpaceService access$getSpaceService$p(InstancePaginatedAnalyticsServiceImpl $this) {
        return $this.spaceService;
    }

    public static final /* synthetic */ Instant access$toTimestamp(InstancePaginatedAnalyticsServiceImpl $this, Long $receiver) {
        return $this.toTimestamp($receiver);
    }

    public static final /* synthetic */ UserService access$getUserService$p(InstancePaginatedAnalyticsServiceImpl $this) {
        return $this.userService;
    }

    @Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0018\u0010\u0003\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0006R\u0018\u0010\u0007\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0006\u00a8\u0006\b"}, d2={"Lcom/addonengine/addons/analytics/service/InstancePaginatedAnalyticsServiceImpl$Companion;", "", "()V", "firstBatchSize", "", "kotlin.jvm.PlatformType", "Ljava/lang/Integer;", "subsequentBatchSize", "analytics"})
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}


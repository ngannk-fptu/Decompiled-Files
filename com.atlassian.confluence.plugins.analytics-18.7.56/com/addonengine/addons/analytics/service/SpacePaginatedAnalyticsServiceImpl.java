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

import com.addonengine.addons.analytics.service.SpacePaginatedAnalyticsService;
import com.addonengine.addons.analytics.service.SpacePaginatedAnalyticsServiceImpl;
import com.addonengine.addons.analytics.service.UtilsKt;
import com.addonengine.addons.analytics.service.confluence.ContentService;
import com.addonengine.addons.analytics.service.confluence.NoSpaceOrNoPermissionException;
import com.addonengine.addons.analytics.service.confluence.SpaceService;
import com.addonengine.addons.analytics.service.confluence.UserService;
import com.addonengine.addons.analytics.service.confluence.model.Content;
import com.addonengine.addons.analytics.service.confluence.model.Space;
import com.addonengine.addons.analytics.service.model.AnalyticsEvent;
import com.addonengine.addons.analytics.service.model.ContentActivity;
import com.addonengine.addons.analytics.service.model.ContentSortField;
import com.addonengine.addons.analytics.service.model.ContentType;
import com.addonengine.addons.analytics.service.model.DatePeriodOptions;
import com.addonengine.addons.analytics.service.model.LazyFetching;
import com.addonengine.addons.analytics.service.model.SortOrder;
import com.addonengine.addons.analytics.service.model.SpaceLevelUserActivity;
import com.addonengine.addons.analytics.service.model.SpaceLevelUserSortField;
import com.addonengine.addons.analytics.store.EventRepository;
import com.addonengine.addons.analytics.store.model.FullContentStatistics;
import com.addonengine.addons.analytics.store.model.FullSpaceUserStatistics;
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
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0003\b\u0007\u0018\u0000 #2\u00020\u0001:\u0001#B'\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJN\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00140\u00132\b\u0010\u0015\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001bH\u0016JN\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001d0\f2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00140\u00132\b\u0010\u0015\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u001e2\u0006\u0010\u001a\u001a\u00020\u001bH\u0016J\u0015\u0010\u001f\u001a\u0004\u0018\u00010 *\u0004\u0018\u00010!H\u0002\u00a2\u0006\u0002\u0010\"R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006$"}, d2={"Lcom/addonengine/addons/analytics/service/SpacePaginatedAnalyticsServiceImpl;", "Lcom/addonengine/addons/analytics/service/SpacePaginatedAnalyticsService;", "contentService", "Lcom/addonengine/addons/analytics/service/confluence/ContentService;", "spaceService", "Lcom/addonengine/addons/analytics/service/confluence/SpaceService;", "eventRepository", "Lcom/addonengine/addons/analytics/store/EventRepository;", "userService", "Lcom/addonengine/addons/analytics/service/confluence/UserService;", "(Lcom/addonengine/addons/analytics/service/confluence/ContentService;Lcom/addonengine/addons/analytics/service/confluence/SpaceService;Lcom/addonengine/addons/analytics/store/EventRepository;Lcom/addonengine/addons/analytics/service/confluence/UserService;)V", "getActivityByContent", "", "Lcom/addonengine/addons/analytics/service/model/ContentActivity;", "datePeriodOptions", "Lcom/addonengine/addons/analytics/service/model/DatePeriodOptions;", "spaceKey", "", "contentTypes", "", "Lcom/addonengine/addons/analytics/service/model/ContentType;", "pageToken", "limit", "", "sortField", "Lcom/addonengine/addons/analytics/service/model/ContentSortField;", "sortOrder", "Lcom/addonengine/addons/analytics/service/model/SortOrder;", "getActivityByUser", "Lcom/addonengine/addons/analytics/service/model/SpaceLevelUserActivity;", "Lcom/addonengine/addons/analytics/service/model/SpaceLevelUserSortField;", "toTimestamp", "Ljava/time/Instant;", "", "(Ljava/lang/Long;)Ljava/time/Instant;", "Companion", "analytics"})
@SourceDebugExtension(value={"SMAP\nSpacePaginatedAnalyticsServiceImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SpacePaginatedAnalyticsServiceImpl.kt\ncom/addonengine/addons/analytics/service/SpacePaginatedAnalyticsServiceImpl\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,151:1\n1#2:152\n*E\n"})
public final class SpacePaginatedAnalyticsServiceImpl
implements SpacePaginatedAnalyticsService {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final ContentService contentService;
    @NotNull
    private final SpaceService spaceService;
    @NotNull
    private final EventRepository eventRepository;
    @NotNull
    private final UserService userService;
    private static final Integer firstBatchSize = Integer.getInteger("confluence.analytics.pagination.first.batch.size", 500);
    private static final Integer subsequentBatchSize = Integer.getInteger("confluence.analytics.pagination.subsequent.batch.size", 25000);

    @Autowired
    public SpacePaginatedAnalyticsServiceImpl(@NotNull ContentService contentService, @NotNull SpaceService spaceService, @NotNull EventRepository eventRepository, @NotNull UserService userService) {
        Intrinsics.checkNotNullParameter((Object)contentService, (String)"contentService");
        Intrinsics.checkNotNullParameter((Object)spaceService, (String)"spaceService");
        Intrinsics.checkNotNullParameter((Object)eventRepository, (String)"eventRepository");
        Intrinsics.checkNotNullParameter((Object)userService, (String)"userService");
        this.contentService = contentService;
        this.spaceService = spaceService;
        this.eventRepository = eventRepository;
        this.userService = userService;
    }

    @Override
    @NotNull
    public List<ContentActivity> getActivityByContent(@NotNull DatePeriodOptions datePeriodOptions, @NotNull String spaceKey, @NotNull Set<? extends ContentType> contentTypes, @Nullable String pageToken, int limit, @NotNull ContentSortField sortField, @NotNull SortOrder sortOrder) {
        Intrinsics.checkNotNullParameter((Object)datePeriodOptions, (String)"datePeriodOptions");
        Intrinsics.checkNotNullParameter((Object)spaceKey, (String)"spaceKey");
        Intrinsics.checkNotNullParameter(contentTypes, (String)"contentTypes");
        Intrinsics.checkNotNullParameter((Object)((Object)sortField), (String)"sortField");
        Intrinsics.checkNotNullParameter((Object)((Object)sortOrder), (String)"sortOrder");
        Space space = SpaceService.DefaultImpls.getByKeyOrNull$default(this.spaceService, spaceKey, false, 2, null);
        if (space == null) {
            throw new NoSpaceOrNoPermissionException(spaceKey);
        }
        long spaceId = space.getId();
        Long l = this.eventRepository.getMaximumEventId();
        long maxEventId = l != null ? l : 0L;
        Integer n = firstBatchSize;
        Intrinsics.checkNotNullExpressionValue((Object)n, (String)"firstBatchSize");
        int n2 = ((Number)n).intValue();
        Integer n3 = subsequentBatchSize;
        Intrinsics.checkNotNullExpressionValue((Object)n3, (String)"subsequentBatchSize");
        Sequence allContentStatistics2 = LazyFetching.Companion.numerical(n2, ((Number)n3).intValue(), (Function2)new Function2<Integer, Integer, List<? extends FullContentStatistics>>(this, contentTypes, datePeriodOptions, spaceKey, spaceId, sortField, sortOrder, maxEventId){
            final /* synthetic */ SpacePaginatedAnalyticsServiceImpl this$0;
            final /* synthetic */ Set<ContentType> $contentTypes;
            final /* synthetic */ DatePeriodOptions $datePeriodOptions;
            final /* synthetic */ String $spaceKey;
            final /* synthetic */ long $spaceId;
            final /* synthetic */ ContentSortField $sortField;
            final /* synthetic */ SortOrder $sortOrder;
            final /* synthetic */ long $maxEventId;
            {
                this.this$0 = $receiver;
                this.$contentTypes = $contentTypes;
                this.$datePeriodOptions = $datePeriodOptions;
                this.$spaceKey = $spaceKey;
                this.$spaceId = $spaceId;
                this.$sortField = $sortField;
                this.$sortOrder = $sortOrder;
                this.$maxEventId = $maxEventId;
                super(2);
            }

            @NotNull
            public final List<FullContentStatistics> invoke(int offset, int batchLimit) {
                return SpacePaginatedAnalyticsServiceImpl.access$getEventRepository$p(this.this$0).getEventsForAllSpaceContent(UtilsKt.contentTypesToEvents(this.$contentTypes, AnalyticsEvent.PAGE_VIEWED, AnalyticsEvent.BLOG_VIEWED), CollectionsKt.listOf((Object)((Object)AnalyticsEvent.COMMENT_CREATED)), this.$datePeriodOptions, this.$contentTypes, this.$spaceKey, this.$spaceId, this.$sortField, this.$sortOrder, this.$maxEventId, offset, batchLimit);
            }
        }).asSequence();
        CharSequence charSequence = pageToken;
        Sequence currentPageCandidates2 = charSequence == null || charSequence.length() == 0 ? allContentStatistics2 : SequencesKt.drop((Sequence)SequencesKt.dropWhile(allContentStatistics2, (Function1)((Function1)new Function1<FullContentStatistics, Boolean>(pageToken){
            final /* synthetic */ String $pageToken;
            {
                this.$pageToken = $pageToken;
                super(1);
            }

            @NotNull
            public final Boolean invoke(@NotNull FullContentStatistics it) {
                Intrinsics.checkNotNullParameter((Object)it, (String)"it");
                return !Intrinsics.areEqual((Object)String.valueOf(it.getContentId()), (Object)this.$pageToken);
            }
        })), (int)1);
        return SequencesKt.toList((Sequence)SequencesKt.take((Sequence)SequencesKt.map((Sequence)SequencesKt.filter((Sequence)SequencesKt.map((Sequence)currentPageCandidates2, (Function1)((Function1)new Function1<FullContentStatistics, Pair<? extends FullContentStatistics, ? extends Content>>(this){
            final /* synthetic */ SpacePaginatedAnalyticsServiceImpl this$0;
            {
                this.this$0 = $receiver;
                super(1);
            }

            @NotNull
            public final Pair<FullContentStatistics, Content> invoke(@NotNull FullContentStatistics it) {
                Intrinsics.checkNotNullParameter((Object)it, (String)"it");
                return new Pair((Object)it, (Object)SpacePaginatedAnalyticsServiceImpl.access$getContentService$p(this.this$0).getByIdOrNull(it.getContentId()));
            }
        })), (Function1)getActivityByContent.2.INSTANCE), (Function1)((Function1)new Function1<Pair<? extends FullContentStatistics, ? extends Content>, ContentActivity>(this){
            final /* synthetic */ SpacePaginatedAnalyticsServiceImpl this$0;
            {
                this.this$0 = $receiver;
                super(1);
            }

            @NotNull
            public final ContentActivity invoke(@NotNull Pair<FullContentStatistics, Content> it) {
                Content contentObject;
                Intrinsics.checkNotNullParameter(it, (String)"it");
                FullContentStatistics contentStat = (FullContentStatistics)it.getFirst();
                Content content = contentObject = (Content)it.getSecond();
                Intrinsics.checkNotNull((Object)content);
                Content content2 = content;
                long l = contentStat.getViewCount();
                long l2 = contentStat.getUsersViewed();
                Instant instant = SpacePaginatedAnalyticsServiceImpl.access$toTimestamp(this.this$0, contentStat.getLastEventAt());
                long l3 = contentStat.getCommentsCount();
                return new ContentActivity(content2, instant, l3, l2, l);
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
    public List<SpaceLevelUserActivity> getActivityByUser(@NotNull DatePeriodOptions datePeriodOptions, @NotNull String spaceKey, @NotNull Set<? extends ContentType> contentTypes, @Nullable String pageToken, int limit, @NotNull SpaceLevelUserSortField sortField, @NotNull SortOrder sortOrder) {
        Sequence eventsByUser2;
        Intrinsics.checkNotNullParameter((Object)datePeriodOptions, (String)"datePeriodOptions");
        Intrinsics.checkNotNullParameter((Object)spaceKey, (String)"spaceKey");
        Intrinsics.checkNotNullParameter(contentTypes, (String)"contentTypes");
        Intrinsics.checkNotNullParameter((Object)((Object)sortField), (String)"sortField");
        Intrinsics.checkNotNullParameter((Object)((Object)sortOrder), (String)"sortOrder");
        if (SpaceService.DefaultImpls.getByKeyOrNull$default(this.spaceService, spaceKey, false, 2, null) == null) {
            throw new NoSpaceOrNoPermissionException(spaceKey);
        }
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
        Sequence seq = eventsByUser2 = LazyFetching.Companion.numerical(n2, ((Number)n3).intValue(), (Function2)new Function2<Integer, Integer, List<? extends FullSpaceUserStatistics>>(this, viewEvents, createEvents, updateEvents, (List<? extends AnalyticsEvent>)commentEvents, (List<? extends AnalyticsEvent>)contributorEvents, datePeriodOptions, spaceKey, sortField, sortOrder, maxEventId){
            final /* synthetic */ SpacePaginatedAnalyticsServiceImpl this$0;
            final /* synthetic */ List<AnalyticsEvent> $viewEvents;
            final /* synthetic */ List<AnalyticsEvent> $createEvents;
            final /* synthetic */ List<AnalyticsEvent> $updateEvents;
            final /* synthetic */ List<AnalyticsEvent> $commentEvents;
            final /* synthetic */ List<AnalyticsEvent> $contributorEvents;
            final /* synthetic */ DatePeriodOptions $datePeriodOptions;
            final /* synthetic */ String $spaceKey;
            final /* synthetic */ SpaceLevelUserSortField $sortField;
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
                this.$spaceKey = $spaceKey;
                this.$sortField = $sortField;
                this.$sortOrder = $sortOrder;
                this.$maxEventId = $maxEventId;
                super(2);
            }

            @NotNull
            public final List<FullSpaceUserStatistics> invoke(int offset, int batchLimit) {
                return SpacePaginatedAnalyticsServiceImpl.access$getEventRepository$p(this.this$0).getEventsForSpaceUsers(this.$viewEvents, this.$createEvents, this.$updateEvents, this.$commentEvents, this.$contributorEvents, this.$datePeriodOptions, this.$spaceKey, this.$sortField, this.$sortOrder, this.$maxEventId, offset, batchLimit);
            }
        }).asSequence();
        boolean bl = false;
        CharSequence charSequence = pageToken;
        return SequencesKt.toList((Sequence)SequencesKt.take((Sequence)SequencesKt.map(charSequence == null || charSequence.length() == 0 ? seq : SequencesKt.drop((Sequence)SequencesKt.dropWhile(seq, (Function1)((Function1)new Function1<FullSpaceUserStatistics, Boolean>(pageToken){
            final /* synthetic */ String $pageToken;
            {
                this.$pageToken = $pageToken;
                super(1);
            }

            @NotNull
            public final Boolean invoke(@NotNull FullSpaceUserStatistics it) {
                Intrinsics.checkNotNullParameter((Object)it, (String)"it");
                return !Intrinsics.areEqual((Object)this.$pageToken, (Object)it.getUserKey());
            }
        })), (int)1), (Function1)((Function1)new Function1<FullSpaceUserStatistics, SpaceLevelUserActivity>(this){
            final /* synthetic */ SpacePaginatedAnalyticsServiceImpl this$0;
            {
                this.this$0 = $receiver;
                super(1);
            }

            @NotNull
            public final SpaceLevelUserActivity invoke(@NotNull FullSpaceUserStatistics it) {
                Intrinsics.checkNotNullParameter((Object)it, (String)"it");
                return new SpaceLevelUserActivity(it.getUserKey(), SpacePaginatedAnalyticsServiceImpl.access$getUserService$p(this.this$0).getUserType(it.getUserKey()), it.getViewedCount(), it.getCreatedCount(), it.getUpdatedCount(), it.getCommentsCount(), it.getContributorScore());
            }
        })), (int)limit));
    }

    public static final /* synthetic */ EventRepository access$getEventRepository$p(SpacePaginatedAnalyticsServiceImpl $this) {
        return $this.eventRepository;
    }

    public static final /* synthetic */ ContentService access$getContentService$p(SpacePaginatedAnalyticsServiceImpl $this) {
        return $this.contentService;
    }

    public static final /* synthetic */ Instant access$toTimestamp(SpacePaginatedAnalyticsServiceImpl $this, Long $receiver) {
        return $this.toTimestamp($receiver);
    }

    public static final /* synthetic */ UserService access$getUserService$p(SpacePaginatedAnalyticsServiceImpl $this) {
        return $this.userService;
    }

    @Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0018\u0010\u0003\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0006R\u0018\u0010\u0007\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0006\u00a8\u0006\b"}, d2={"Lcom/addonengine/addons/analytics/service/SpacePaginatedAnalyticsServiceImpl$Companion;", "", "()V", "firstBatchSize", "", "kotlin.jvm.PlatformType", "Ljava/lang/Integer;", "subsequentBatchSize", "analytics"})
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}


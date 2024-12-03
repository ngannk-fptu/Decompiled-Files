/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsDevService
 *  javax.inject.Inject
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.service;

import com.addonengine.addons.analytics.service.ContentAnalyticsService;
import com.addonengine.addons.analytics.service.confluence.ContentService;
import com.addonengine.addons.analytics.service.confluence.UserService;
import com.addonengine.addons.analytics.service.confluence.model.Attachment;
import com.addonengine.addons.analytics.service.confluence.model.ContentVersion;
import com.addonengine.addons.analytics.service.model.AnalyticsEvent;
import com.addonengine.addons.analytics.service.model.AttachmentViews;
import com.addonengine.addons.analytics.service.model.ContentRef;
import com.addonengine.addons.analytics.service.model.ContentType;
import com.addonengine.addons.analytics.service.model.CountType;
import com.addonengine.addons.analytics.service.model.DatePeriodOptions;
import com.addonengine.addons.analytics.service.model.DatePeriodOptionsKt;
import com.addonengine.addons.analytics.service.model.PeriodActivity;
import com.addonengine.addons.analytics.service.model.UserViews;
import com.addonengine.addons.analytics.store.EventRepository;
import com.addonengine.addons.analytics.store.model.ContentViewsByUserData;
import com.addonengine.addons.analytics.store.model.EventsByChildContentData;
import com.addonengine.addons.analytics.store.model.EventsByPeriodData;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsDevService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import org.jetbrains.annotations.NotNull;

@ExportAsDevService(value={ContentAnalyticsService.class})
@ConfluenceComponent
@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u001f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u0016\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\f\u001a\u00020\rH\u0016J&\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\n2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0016J\u0016\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00170\n2\u0006\u0010\u0012\u001a\u00020\u0013H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2={"Lcom/addonengine/addons/analytics/service/ContentAnalyticsServiceImpl;", "Lcom/addonengine/addons/analytics/service/ContentAnalyticsService;", "contentService", "Lcom/addonengine/addons/analytics/service/confluence/ContentService;", "userService", "Lcom/addonengine/addons/analytics/service/confluence/UserService;", "eventRepository", "Lcom/addonengine/addons/analytics/store/EventRepository;", "(Lcom/addonengine/addons/analytics/service/confluence/ContentService;Lcom/addonengine/addons/analytics/service/confluence/UserService;Lcom/addonengine/addons/analytics/store/EventRepository;)V", "getViewsByAttachment", "", "Lcom/addonengine/addons/analytics/service/model/AttachmentViews;", "containerId", "", "getViewsByPeriod", "Lcom/addonengine/addons/analytics/service/model/PeriodActivity;", "datePeriodOptions", "Lcom/addonengine/addons/analytics/service/model/DatePeriodOptions;", "contentRef", "Lcom/addonengine/addons/analytics/service/model/ContentRef;", "countType", "Lcom/addonengine/addons/analytics/service/model/CountType;", "getViewsByUser", "Lcom/addonengine/addons/analytics/service/model/UserViews;", "analytics"})
@SourceDebugExtension(value={"SMAP\nContentAnalyticsServiceImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ContentAnalyticsServiceImpl.kt\ncom/addonengine/addons/analytics/service/ContentAnalyticsServiceImpl\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,81:1\n1194#2,2:82\n1222#2,4:84\n1549#2:88\n1620#2,3:89\n1549#2:92\n1620#2,3:93\n1194#2,2:96\n1222#2,4:98\n1549#2:102\n1620#2,3:103\n*S KotlinDebug\n*F\n+ 1 ContentAnalyticsServiceImpl.kt\ncom/addonengine/addons/analytics/service/ContentAnalyticsServiceImpl\n*L\n32#1:82,2\n32#1:84,4\n34#1:88\n34#1:89,3\n57#1:92\n57#1:93,3\n66#1:96,2\n66#1:98,4\n68#1:102\n68#1:103,3\n*E\n"})
public final class ContentAnalyticsServiceImpl
implements ContentAnalyticsService {
    @NotNull
    private final ContentService contentService;
    @NotNull
    private final UserService userService;
    @NotNull
    private final EventRepository eventRepository;

    @Inject
    public ContentAnalyticsServiceImpl(@NotNull ContentService contentService, @NotNull UserService userService, @NotNull EventRepository eventRepository) {
        Intrinsics.checkNotNullParameter((Object)contentService, (String)"contentService");
        Intrinsics.checkNotNullParameter((Object)userService, (String)"userService");
        Intrinsics.checkNotNullParameter((Object)eventRepository, (String)"eventRepository");
        this.contentService = contentService;
        this.userService = userService;
        this.eventRepository = eventRepository;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public List<UserViews> getViewsByUser(@NotNull ContentRef contentRef) {
        void $this$mapTo$iv$iv;
        Object object;
        void $this$associateByTo$iv$iv;
        void $this$associateBy$iv;
        Intrinsics.checkNotNullParameter((Object)contentRef, (String)"contentRef");
        List<ContentViewsByUserData> viewsByUser2 = this.eventRepository.getContentViewsByUser(contentRef);
        Iterable iterable = this.contentService.getVersions(contentRef.getId());
        boolean $i$f$associateBy = false;
        int capacity$iv22 = RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associateBy$iv, (int)10)), (int)16);
        void var7_7 = $this$associateBy$iv;
        Map destination$iv$iv = new LinkedHashMap(capacity$iv22);
        boolean $i$f$associateByTo = false;
        for (Object element$iv$iv : $this$associateByTo$iv$iv) {
            void it;
            ContentVersion contentVersion = (ContentVersion)element$iv$iv;
            object = destination$iv$iv;
            boolean bl = false;
            object.put(it.getLastModificationDate(), element$iv$iv);
        }
        Map versionsMap = destination$iv$iv;
        Iterable $this$map$iv = viewsByUser2;
        boolean $i$f$map = false;
        Iterable capacity$iv22 = $this$map$iv;
        Collection destination$iv$iv2 = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            Object element$iv$iv;
            element$iv$iv = (ContentViewsByUserData)item$iv$iv;
            object = destination$iv$iv2;
            boolean bl = false;
            object.add(new UserViews(it.getUserKey(), this.userService.getUserType(it.getUserKey()), (ContentVersion)versionsMap.get(it.getLastVersionViewedModificationDate()), it.getLastViewedAt(), it.getViews()));
        }
        return (List)destination$iv$iv2;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public List<PeriodActivity> getViewsByPeriod(@NotNull DatePeriodOptions datePeriodOptions, @NotNull ContentRef contentRef, @NotNull CountType countType) {
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        AnalyticsEvent analyticsEvent;
        Intrinsics.checkNotNullParameter((Object)datePeriodOptions, (String)"datePeriodOptions");
        Intrinsics.checkNotNullParameter((Object)contentRef, (String)"contentRef");
        Intrinsics.checkNotNullParameter((Object)((Object)countType), (String)"countType");
        this.contentService.getById(contentRef.getId());
        switch (WhenMappings.$EnumSwitchMapping$0[contentRef.getType().ordinal()]) {
            case 1: {
                analyticsEvent = AnalyticsEvent.PAGE_VIEWED;
                break;
            }
            case 2: {
                analyticsEvent = AnalyticsEvent.BLOG_VIEWED;
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
        AnalyticsEvent event = analyticsEvent;
        List<EventsByPeriodData> viewsByPeriod2 = this.eventRepository.getEventsByPeriodForSingleContent(CollectionsKt.listOf((Object)((Object)event)), contentRef.getId(), datePeriodOptions, countType);
        Iterable iterable = viewsByPeriod2;
        DatePeriodOptions datePeriodOptions2 = datePeriodOptions;
        boolean $i$f$map = false;
        void var8_9 = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            EventsByPeriodData eventsByPeriodData = (EventsByPeriodData)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(new PeriodActivity(it.getDate(), it.getTotal()));
        }
        return DatePeriodOptionsKt.fillInMissingDates(datePeriodOptions2, (List)destination$iv$iv);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public List<AttachmentViews> getViewsByAttachment(long containerId) {
        void $this$mapTo$iv$iv;
        Object object;
        void $this$associateByTo$iv$iv;
        void $this$associateBy$iv;
        List<Attachment> attachments = this.contentService.getAttachments(containerId);
        Iterable iterable = this.eventRepository.getEventsByChildContent(containerId);
        boolean $i$f$associateBy = false;
        int capacity$iv22 = RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associateBy$iv, (int)10)), (int)16);
        void var8_7 = $this$associateBy$iv;
        Map destination$iv$iv = new LinkedHashMap(capacity$iv22);
        boolean $i$f$associateByTo = false;
        for (Object element$iv$iv : $this$associateByTo$iv$iv) {
            void it;
            EventsByChildContentData eventsByChildContentData = (EventsByChildContentData)element$iv$iv;
            object = destination$iv$iv;
            boolean bl = false;
            object.put(it.getContentId(), element$iv$iv);
        }
        Map viewsByAttachmentMap = destination$iv$iv;
        Iterable $this$map$iv = attachments;
        boolean $i$f$map = false;
        Iterable capacity$iv22 = $this$map$iv;
        Collection destination$iv$iv2 = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            Object element$iv$iv;
            element$iv$iv = (Attachment)item$iv$iv;
            object = destination$iv$iv2;
            boolean bl = false;
            EventsByChildContentData attachmentViews = (EventsByChildContentData)viewsByAttachmentMap.get(it.getId());
            EventsByChildContentData eventsByChildContentData = attachmentViews;
            EventsByChildContentData eventsByChildContentData2 = attachmentViews;
            object.add(new AttachmentViews(it.getId(), it.getName(), it.getLink(), eventsByChildContentData != null ? eventsByChildContentData.getLastViewedAt() : null, eventsByChildContentData2 != null ? eventsByChildContentData2.getViews() : 0L));
        }
        return (List)destination$iv$iv2;
    }

    @Metadata(mv={1, 9, 0}, k=3, xi=48)
    public final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[ContentType.values().length];
            try {
                nArray[ContentType.PAGE.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[ContentType.BLOG.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}


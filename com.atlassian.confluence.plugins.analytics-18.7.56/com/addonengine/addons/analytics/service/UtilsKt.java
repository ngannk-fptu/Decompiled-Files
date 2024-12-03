/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.service;

import com.addonengine.addons.analytics.service.model.AnalyticsEvent;
import com.addonengine.addons.analytics.service.model.ContentType;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=2, xi=48, d1={"\u0000\u0018\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0002\b\u0006\u001a\u001a\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u00012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u001a*\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00020\u00012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\u0007\u001a\u00020\u00022\u0006\u0010\b\u001a\u00020\u0002\u001a6\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00020\u00012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00020\u00012\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001\u00a8\u0006\u000b"}, d2={"contentTypesToActivityByUserEvents", "", "Lcom/addonengine/addons/analytics/service/model/AnalyticsEvent;", "contentTypes", "", "Lcom/addonengine/addons/analytics/service/model/ContentType;", "contentTypesToEvents", "pageEvent", "blogEvent", "pageEvents", "blogEvents", "analytics"})
public final class UtilsKt {
    @NotNull
    public static final List<AnalyticsEvent> contentTypesToActivityByUserEvents(@NotNull Set<? extends ContentType> contentTypes) {
        Intrinsics.checkNotNullParameter(contentTypes, (String)"contentTypes");
        AnalyticsEvent[] analyticsEventArray = new AnalyticsEvent[]{AnalyticsEvent.PAGE_VIEWED, AnalyticsEvent.PAGE_CREATED, AnalyticsEvent.PAGE_UPDATED};
        List list = CollectionsKt.listOf((Object[])analyticsEventArray);
        analyticsEventArray = new AnalyticsEvent[]{AnalyticsEvent.BLOG_VIEWED, AnalyticsEvent.BLOG_CREATED, AnalyticsEvent.BLOG_UPDATED};
        return CollectionsKt.plus((Collection)UtilsKt.contentTypesToEvents(contentTypes, list, CollectionsKt.listOf((Object[])analyticsEventArray)), (Object)((Object)AnalyticsEvent.COMMENT_CREATED));
    }

    @NotNull
    public static final List<AnalyticsEvent> contentTypesToEvents(@NotNull Set<? extends ContentType> contentTypes, @NotNull AnalyticsEvent pageEvent, @NotNull AnalyticsEvent blogEvent) {
        Intrinsics.checkNotNullParameter(contentTypes, (String)"contentTypes");
        Intrinsics.checkNotNullParameter((Object)((Object)pageEvent), (String)"pageEvent");
        Intrinsics.checkNotNullParameter((Object)((Object)blogEvent), (String)"blogEvent");
        return UtilsKt.contentTypesToEvents(contentTypes, CollectionsKt.listOf((Object)((Object)pageEvent)), CollectionsKt.listOf((Object)((Object)blogEvent)));
    }

    @NotNull
    public static final List<AnalyticsEvent> contentTypesToEvents(@NotNull Set<? extends ContentType> contentTypes, @NotNull List<? extends AnalyticsEvent> pageEvents, @NotNull List<? extends AnalyticsEvent> blogEvents) {
        Intrinsics.checkNotNullParameter(contentTypes, (String)"contentTypes");
        Intrinsics.checkNotNullParameter(pageEvents, (String)"pageEvents");
        Intrinsics.checkNotNullParameter(blogEvents, (String)"blogEvents");
        ContentType[] contentTypeArray = new ContentType[]{ContentType.PAGE, ContentType.BLOG};
        return contentTypes.containsAll(CollectionsKt.listOf((Object[])contentTypeArray)) ? CollectionsKt.plus((Collection)pageEvents, (Iterable)blogEvents) : (contentTypes.contains((Object)ContentType.PAGE) ? pageEvents : (contentTypes.contains((Object)ContentType.BLOG) ? blogEvents : CollectionsKt.emptyList()));
    }
}


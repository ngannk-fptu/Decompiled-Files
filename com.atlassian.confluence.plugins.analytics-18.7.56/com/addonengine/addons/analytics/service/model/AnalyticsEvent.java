/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.service.model;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u000b\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000b\u00a8\u0006\f"}, d2={"Lcom/addonengine/addons/analytics/service/model/AnalyticsEvent;", "", "(Ljava/lang/String;I)V", "PAGE_VIEWED", "PAGE_CREATED", "PAGE_UPDATED", "BLOG_VIEWED", "BLOG_CREATED", "BLOG_UPDATED", "COMMENT_CREATED", "ATTACHMENT_VIEWED", "ATTACHMENT_CREATED", "analytics"})
public final class AnalyticsEvent
extends Enum<AnalyticsEvent> {
    public static final /* enum */ AnalyticsEvent PAGE_VIEWED = new AnalyticsEvent();
    public static final /* enum */ AnalyticsEvent PAGE_CREATED = new AnalyticsEvent();
    public static final /* enum */ AnalyticsEvent PAGE_UPDATED = new AnalyticsEvent();
    public static final /* enum */ AnalyticsEvent BLOG_VIEWED = new AnalyticsEvent();
    public static final /* enum */ AnalyticsEvent BLOG_CREATED = new AnalyticsEvent();
    public static final /* enum */ AnalyticsEvent BLOG_UPDATED = new AnalyticsEvent();
    public static final /* enum */ AnalyticsEvent COMMENT_CREATED = new AnalyticsEvent();
    public static final /* enum */ AnalyticsEvent ATTACHMENT_VIEWED = new AnalyticsEvent();
    public static final /* enum */ AnalyticsEvent ATTACHMENT_CREATED = new AnalyticsEvent();
    private static final /* synthetic */ AnalyticsEvent[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    public static AnalyticsEvent[] values() {
        return (AnalyticsEvent[])$VALUES.clone();
    }

    public static AnalyticsEvent valueOf(String value) {
        return Enum.valueOf(AnalyticsEvent.class, value);
    }

    @NotNull
    public static EnumEntries<AnalyticsEvent> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = analyticsEventArray = new AnalyticsEvent[]{AnalyticsEvent.PAGE_VIEWED, AnalyticsEvent.PAGE_CREATED, AnalyticsEvent.PAGE_UPDATED, AnalyticsEvent.BLOG_VIEWED, AnalyticsEvent.BLOG_CREATED, AnalyticsEvent.BLOG_UPDATED, AnalyticsEvent.COMMENT_CREATED, AnalyticsEvent.ATTACHMENT_VIEWED, AnalyticsEvent.ATTACHMENT_CREATED};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}


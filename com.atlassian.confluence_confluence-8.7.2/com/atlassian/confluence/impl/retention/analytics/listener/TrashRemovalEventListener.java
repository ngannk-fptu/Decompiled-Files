/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.confluence.impl.retention.analytics.listener;

import com.atlassian.confluence.event.events.content.ContentPurgedFromTrashEvent;
import com.atlassian.confluence.impl.event.AttachmentRemovedEvent;
import com.atlassian.confluence.impl.retention.analytics.TrashRemovalJobAnalyticsEvent;
import com.atlassian.confluence.impl.retention.analytics.TrashRemovalJobCompletedEvent;
import com.atlassian.confluence.impl.retention.analytics.TrashRemovalStatisticThreadLocal;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;

public class TrashRemovalEventListener {
    private final EventPublisher eventPublisher;

    public TrashRemovalEventListener(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void onPageOrBlogPurgedEvent(ContentPurgedFromTrashEvent event) {
        TrashRemovalStatisticThreadLocal.getCurrentStatistic().ifPresent(currentStats -> currentStats.pageOrBlogDeleted(event.getContent()));
    }

    @EventListener
    public void onAttachmentPurgedEvent(AttachmentRemovedEvent event) {
        TrashRemovalStatisticThreadLocal.getCurrentStatistic().ifPresent(currentStats -> currentStats.attachmentDeleted(event.getRemovedVersions()));
    }

    @EventListener
    public void onTrashRemovalJobCompletedEvent(TrashRemovalJobCompletedEvent event) {
        this.eventPublisher.publish((Object)new TrashRemovalJobAnalyticsEvent(event));
    }
}


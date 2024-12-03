/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.confluence.impl.retention.analytics.listener;

import com.atlassian.confluence.impl.retention.analytics.CleanupSummaryAnalytics;
import com.atlassian.confluence.impl.retention.schedule.AttachmentVersionRemovalSummary;
import com.atlassian.confluence.impl.retention.schedule.VersionRemovalJobCompletedEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;

public class VersionRemovalJobEventListener {
    private final EventPublisher eventPublisher;

    public VersionRemovalJobEventListener(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void onVersionRemovalJobCompletedEvent(VersionRemovalJobCompletedEvent event) {
        this.eventPublisher.publish((Object)this.buildPageRemovalJobEvent(event));
        this.eventPublisher.publish((Object)this.buildAttachmentRemovalJobEvent(event));
    }

    private CleanupSummaryAnalytics.PageRemovalJobEvent buildPageRemovalJobEvent(VersionRemovalJobCompletedEvent versionRemovalJobCompletedEvent) {
        return new CleanupSummaryAnalytics.PageRemovalJobEvent(versionRemovalJobCompletedEvent.getType().getLabel(), versionRemovalJobCompletedEvent.getPageVersionRemovalSummary().getPagesRemovedByGlobalRules(), versionRemovalJobCompletedEvent.getPageVersionRemovalSummary().getPagesRemovedBySpaceRules());
    }

    private CleanupSummaryAnalytics.AttachmentRemovalJobEvent buildAttachmentRemovalJobEvent(VersionRemovalJobCompletedEvent versionRemovalJobCompletedEvent) {
        AttachmentVersionRemovalSummary summary = versionRemovalJobCompletedEvent.getAttachmentVersionRemovalSummary();
        long totalAttachmentsRemoved = summary.getAttachmentsRemovedByGlobalRules() + summary.getAttachmentsRemovedBySpaceRules();
        return new CleanupSummaryAnalytics.AttachmentRemovalJobEvent(versionRemovalJobCompletedEvent.getType().getLabel(), totalAttachmentsRemoved, summary.getAttachmentSizeRemovedByGlobalRules(), summary.getAttachmentSizeRemovedBySpaceRules());
    }
}


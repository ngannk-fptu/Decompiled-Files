/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.confluence.impl.retention;

import com.atlassian.confluence.impl.retention.rules.CleanupSummary;
import com.atlassian.confluence.impl.retention.schedule.AttachmentVersionRemovalSummary;
import com.atlassian.confluence.impl.retention.schedule.PageVersionRemovalSummary;
import com.atlassian.confluence.impl.retention.schedule.VersionRemovalJobCompletedEvent;
import com.atlassian.confluence.impl.retention.schedule.VersionRemovalJobType;
import com.atlassian.event.api.EventPublisher;

public class VersionRemovalEventPublisher {
    private final EventPublisher eventPublisher;

    public VersionRemovalEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishJobCompletedEvent(CleanupSummary cleanupSummary, VersionRemovalJobType jobType) {
        PageVersionRemovalSummary pageVersionRemovalSummary = new PageVersionRemovalSummary(cleanupSummary.getPageVersionsRemovedByGlobalRules(), cleanupSummary.getPageVersionsRemovedBySpaceRules());
        AttachmentVersionRemovalSummary attachmentVersionRemovalSummary = new AttachmentVersionRemovalSummary(cleanupSummary.getAttachmentVersionsRemovedByGlobalRules(), cleanupSummary.getAttachmentSizeRemovedByGlobalRules(), cleanupSummary.getAttachmentVersionsRemovedBySpaceRules(), cleanupSummary.getAttachmentSizeRemovedBySpaceRules());
        this.eventPublisher.publish((Object)new VersionRemovalJobCompletedEvent(jobType, pageVersionRemovalSummary, attachmentVersionRemovalSummary));
    }
}


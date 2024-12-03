/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.plugin.copyspace.api.event.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.plugin.copyspace.api.event.analytics.CopySpaceLifecycleEvent;
import com.atlassian.event.api.AsynchronousPreferred;
import java.time.Duration;
import java.time.Instant;

@AsynchronousPreferred
@EventName(value="confluence.confluence-copyspace.success")
public class CopySpaceSuccessEvent
extends CopySpaceLifecycleEvent {
    private final long durationInMillis;

    public CopySpaceSuccessEvent(String operationUUID, long originalSpaceId, String originalSpaceKey, boolean copyComments, boolean copyLabels, boolean copyAttachments, boolean keepMetaData, boolean preserveWatchers, boolean copyBlogposts, boolean copyPages, int pagesCount, int commentsCount, int blogPostsCount, int attachmentsCount, Instant startTimestamp) {
        super(operationUUID, originalSpaceId, originalSpaceKey, copyComments, copyLabels, copyAttachments, keepMetaData, preserveWatchers, copyBlogposts, copyPages, pagesCount, commentsCount, blogPostsCount, attachmentsCount);
        this.durationInMillis = Duration.between(startTimestamp, Instant.now()).toMillis();
    }

    public long getDurationInMillis() {
        return this.durationInMillis;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.internal.index.event;

import com.atlassian.confluence.index.status.ReIndexError;
import com.atlassian.confluence.index.status.ReIndexJob;
import com.atlassian.confluence.index.status.ReIndexStage;
import com.atlassian.confluence.internal.index.event.AbstractReindexAnalyticsEvent;
import com.atlassian.confluence.internal.index.event.ReindexFailureAnalyticsEvent;
import com.atlassian.confluence.internal.index.event.space.ReindexSpaceFailureNodeAnalyticsEvent;
import com.atlassian.confluence.internal.index.event.space.ReindexSpaceSuccessAnalyticsEvent;
import com.atlassian.confluence.internal.index.event.space.ReindexSpaceSuccessNodeAnalyticsEvent;
import com.atlassian.event.api.EventPublisher;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ReindexAnalyticsEventPublishingHelper {
    private ReindexAnalyticsEventPublishingHelper() {
        throw new IllegalStateException("This is a utility class for publishing Reindex Analytics Events. Do not instantiate it.");
    }

    public static void publishReindexingAnalyticsEvent(EventPublisher eventPublisher, ReIndexJob reIndexJob) {
        AbstractReindexAnalyticsEvent reindexAnalyticsEvent = ReindexAnalyticsEventPublishingHelper.newReindexAnalyticsEvent(reIndexJob);
        if (reindexAnalyticsEvent != null) {
            eventPublisher.publish((Object)reindexAnalyticsEvent);
        }
    }

    public static void publishSpaceNodeAnalyticsEvent(EventPublisher eventPublisher, ReIndexJob reIndexJob, String nodeId) {
        if (reIndexJob.isSiteReindex()) {
            return;
        }
        reIndexJob.getNodeStatuses().stream().filter(reIndexNodeStatus -> nodeId.equals(reIndexNodeStatus.getNodeId())).findFirst().ifPresent(nodeStatus -> ReindexAnalyticsEventPublishingHelper.publishSpaceNodeAnalyticsEvent(eventPublisher, reIndexJob, nodeId, nodeStatus.getError()));
    }

    public static void publishSpaceNodeAnalyticsEvent(EventPublisher eventPublisher, ReIndexJob reIndexJob, String nodeId, @Nullable ReIndexError reIndexError) {
        if (reIndexJob.isSiteReindex()) {
            return;
        }
        int nodeIdInt = Integer.parseUnsignedInt(nodeId, 16);
        eventPublisher.publish((Object)ReindexAnalyticsEventPublishingHelper.newReindexSpaceNodeAnalyticsEvent(reIndexJob, nodeIdInt, reIndexError));
    }

    private static AbstractReindexAnalyticsEvent newReindexAnalyticsEvent(ReIndexJob reIndexJob) {
        if (reIndexJob.isFailed()) {
            return new ReindexFailureAnalyticsEvent(reIndexJob);
        }
        if (!reIndexJob.isSiteReindex() && reIndexJob.getStage() == ReIndexStage.COMPLETE) {
            return new ReindexSpaceSuccessAnalyticsEvent(reIndexJob);
        }
        return null;
    }

    private static AbstractReindexAnalyticsEvent newReindexSpaceNodeAnalyticsEvent(ReIndexJob reIndexJob, int nodeId, @Nullable ReIndexError reIndexError) {
        if (reIndexError == null) {
            return new ReindexSpaceSuccessNodeAnalyticsEvent(reIndexJob, nodeId);
        }
        return new ReindexSpaceFailureNodeAnalyticsEvent(reIndexJob, nodeId, reIndexError);
    }
}


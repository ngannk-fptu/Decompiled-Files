/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service.stepexecutor.attachment;

import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.service.ClusterLimits;
import com.atlassian.migration.agent.service.MigrationErrorCode;
import com.atlassian.migration.agent.service.SpaceAttachmentCount;
import com.atlassian.migration.agent.service.UploadState;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.analytics.ErrorEvent;
import com.atlassian.migration.agent.service.impl.StepType;
import com.google.common.collect.ImmutableList;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AttachmentMigrationAnalyticsService {
    private static final StepType ATTACHMENT_UPLOAD_STEP_TYPE = StepType.ATTACHMENT_UPLOAD;
    private final AnalyticsEventService analyticsEventService;
    private final AnalyticsEventBuilder analyticsEventBuilder;
    private final ClusterLimits clusterLimits;

    public AttachmentMigrationAnalyticsService(AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, ClusterLimits clusterLimits) {
        this.analyticsEventService = analyticsEventService;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.clusterLimits = clusterLimits;
    }

    public void buildAndSaveAnalyticEventsWhenStepFails(MigrationErrorCode migrationErrorCode, String reason, String migrationId, String cloudId, Step step, String spaceKey) {
        ErrorEvent errorEvent = new ErrorEvent.ErrorEventBuilder(migrationErrorCode, migrationErrorCode.getContainerType(), migrationId, ATTACHMENT_UPLOAD_STEP_TYPE).setCloudid(cloudId).setReason(reason).setSpaceKey(spaceKey).build();
        this.analyticsEventService.sendAnalyticsEvents(() -> ImmutableList.of((Object)this.analyticsEventBuilder.buildFailedSpaceMigration(ATTACHMENT_UPLOAD_STEP_TYPE, migrationErrorCode), (Object)this.analyticsEventBuilder.buildMigrationStepMetrics(ATTACHMENT_UPLOAD_STEP_TYPE, migrationErrorCode.shouldBeTreatedAsGoodEventInReliabilitySlo()), (Object)this.analyticsEventBuilder.buildErrorOperationalEvent(errorEvent), (Object)this.analyticsEventBuilder.buildCompletedStepAnalyticsEvent(step)));
    }

    public void buildAndSaveTimerAndHeapAnalyticsEvent(boolean stepSuccessful, long totalTime, String spaceKey, Step step, long freeHeapSizeAtStart, int attachmentConcurrency) {
        this.analyticsEventService.saveAnalyticsEvents(() -> ImmutableList.of((Object)this.analyticsEventBuilder.buildAttachmentStepTimerEvent(stepSuccessful, totalTime, spaceKey, step), (Object)this.analyticsEventBuilder.buildStepLevelHeapSizeAnalyticsEvent(step, freeHeapSizeAtStart, this.clusterLimits.getClusterConcurrencyLimit(ATTACHMENT_UPLOAD_STEP_TYPE), attachmentConcurrency)));
    }

    public void buildAttachmentMigrationEventSuccessful(long totalTime, SpaceAttachmentCount spaceAttachmentCount, UploadState uploadState, Step step) {
        this.analyticsEventService.sendAnalyticsEvents(() -> ImmutableList.of((Object)this.analyticsEventBuilder.buildAttachmentMigrationEvent(totalTime, spaceAttachmentCount, uploadState), (Object)this.analyticsEventBuilder.buildMigrationStepMetrics(ATTACHMENT_UPLOAD_STEP_TYPE, true), (Object)this.analyticsEventBuilder.buildCompletedStepAnalyticsEvent(step)));
    }

    public void buildAttachmentStartEvent(Step step, long startTime, int batchSize, long totalCountOfAttachments, long totalSizeOfAttachments) {
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildAttachmentMigrationStartEvent(step, startTime, batchSize, totalCountOfAttachments, totalSizeOfAttachments));
    }

    public AnalyticsEventService getAnalyticsEventService() {
        return this.analyticsEventService;
    }

    public AnalyticsEventBuilder getAnalyticsEventBuilder() {
        return this.analyticsEventBuilder;
    }
}


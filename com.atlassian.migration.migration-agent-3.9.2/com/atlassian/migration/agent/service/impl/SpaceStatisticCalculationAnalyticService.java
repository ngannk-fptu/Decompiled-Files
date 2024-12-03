/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.google.common.collect.ImmutableMap
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.impl.SpaceStatisticCalculationService;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceStatisticCalculationAnalyticService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(SpaceStatisticCalculationAnalyticService.class);
    private final AnalyticsEventBuilder analyticsEventBuilder;
    private final AnalyticsEventService analyticsEventService;
    private final CloudSiteService cloudSiteService;
    static final String STAT_CALCULATION_STEP = "calculation";
    static final String STAT_STORAGE_STEP = "storage";

    public SpaceStatisticCalculationAnalyticService(AnalyticsEventBuilder analyticsEventBuilder, AnalyticsEventService analyticsEventService, CloudSiteService cloudSiteService) {
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.analyticsEventService = analyticsEventService;
        this.cloudSiteService = cloudSiteService;
    }

    public void buildAndStoreBatchStepExecutionErrorEvent(String jobId, String executionId, String batchId, String batchStep, int batchSize, boolean includeHistoricalData, Exception exception, String spaceId) {
        if (this.canSendAnalytics()) {
            try {
                EventDto eventDto = this.analyticsEventBuilder.buildSpaceStatisticCalculationBatchStepExecutionErrorEvent(jobId, executionId, batchId, batchStep, batchSize, includeHistoricalData, exception, spaceId);
                this.analyticsEventService.saveAnalyticsEventAsync(() -> eventDto);
            }
            catch (Exception e) {
                log.error("Failed to store analytics of the failure in {} for batchId {}", (Object)batchStep, (Object)batchId);
            }
        }
    }

    public void buildAndStoreBatchExecutionCompletedEvent(String jobId, String executionId, String batchId, int batchSize, boolean includeHistoricalData, SpaceStatisticCalculationService.CalculationResult calculationResult, SpaceStatisticCalculationService.StorageResult storageResult, long startTimeEpochMilli, long readTimeEpochMilli, long endTimeEpochMilli) {
        if (this.canSendAnalytics()) {
            try {
                EventDto eventDto = this.analyticsEventBuilder.buildSpaceStatisticCalculationBatchExecutionCompletedEvent(jobId, executionId, batchId, batchSize, includeHistoricalData, calculationResult.success && storageResult.success, (Map<String, Boolean>)ImmutableMap.of((Object)STAT_CALCULATION_STEP, (Object)calculationResult.success, (Object)STAT_STORAGE_STEP, (Object)storageResult.success), storageResult.errorCount + (calculationResult.success ? 0 : 1), startTimeEpochMilli, readTimeEpochMilli, endTimeEpochMilli);
                this.analyticsEventService.saveAnalyticsEventAsync(() -> eventDto);
            }
            catch (Exception exception) {
                log.error("Failed to store analytics for batchId {}", (Object)batchId);
            }
        }
    }

    public void buildAndStoreExecutionCompletedEvent(String jobId, String executionId, int numberOfBatches, int batchLimit, int spaceCount, boolean includesHistoricalData, long startTimeEpocMilli, long waitingTimeEpocMilli, long endTimeEpocMilli) {
        try {
            EventDto eventDto = this.analyticsEventBuilder.buildSpaceStatisticCalculationJobExecutionCompletedEvent(jobId, executionId, spaceCount, numberOfBatches, batchLimit, includesHistoricalData, startTimeEpocMilli, waitingTimeEpocMilli, endTimeEpocMilli);
            this.analyticsEventService.saveAnalyticsEventAsync(() -> eventDto);
        }
        catch (Exception e) {
            log.error("Failed to store analytics for executionId {}", (Object)executionId);
        }
    }

    private boolean canSendAnalytics() {
        return this.cloudSiteService.getNonFailingToken().isPresent();
    }
}


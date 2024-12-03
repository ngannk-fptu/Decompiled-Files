/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.business.insights.core.service.scheduler;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.business.insights.core.analytics.schedule.ExportScheduleExecutedAnalyticEvent;
import com.atlassian.business.insights.core.analytics.schedule.ExportScheduleFailedAnalyticEvent;
import com.atlassian.business.insights.core.analytics.schedule.ExportScheduleTriggeredAnalyticEvent;
import com.atlassian.business.insights.core.ao.dao.entity.ExportProgressStatus;
import com.atlassian.business.insights.core.service.LicenseChecker;
import com.atlassian.business.insights.core.service.api.DataExportOrchestrator;
import com.atlassian.business.insights.core.service.api.EventPublisherService;
import com.atlassian.business.insights.core.service.api.ExportJobState;
import com.atlassian.business.insights.core.service.api.ScheduleConfigService;
import com.atlassian.business.insights.core.service.scheduler.ExportScheduleNextRunTimeCalculator;
import com.atlassian.business.insights.core.service.scheduler.ScheduleConfig;
import com.atlassian.business.insights.core.util.DateConversionUtil;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExportScheduleJobRunner
implements JobRunner {
    private final DataExportOrchestrator dataExportOrchestrator;
    private final ScheduleConfigService scheduleConfigService;
    private final LicenseChecker licenseChecker;
    private final Function<ZoneId, ZonedDateTime> currentDateTimeFn;
    private final EventPublisherService eventPublisherService;

    public ExportScheduleJobRunner(DataExportOrchestrator dataExportOrchestrator, ScheduleConfigService scheduleConfigService, LicenseChecker licenseChecker, EventPublisherService eventPublisherService) {
        this(dataExportOrchestrator, scheduleConfigService, licenseChecker, eventPublisherService, ZonedDateTime::now);
    }

    @VisibleForTesting
    ExportScheduleJobRunner(DataExportOrchestrator dataExportOrchestrator, ScheduleConfigService scheduleConfigService, LicenseChecker licenseChecker, EventPublisherService eventPublisherService, Function<ZoneId, ZonedDateTime> currentDateTimeFn) {
        this.dataExportOrchestrator = dataExportOrchestrator;
        this.scheduleConfigService = scheduleConfigService;
        this.licenseChecker = licenseChecker;
        this.eventPublisherService = eventPublisherService;
        this.currentDateTimeFn = currentDateTimeFn;
    }

    @Nullable
    public JobRunnerResponse runJob(@Nonnull JobRunnerRequest request) {
        Optional<ScheduleConfig> config = this.scheduleConfigService.getExportSchedule();
        if (config.isPresent() && this.shouldJobRun(config.get())) {
            this.publishScheduleTriggeredAnalyticEvent(config.get());
            String fromDate = (String)request.getJobConfig().getParameters().get("fromDate");
            Instant fromDateParsed = DateConversionUtil.parseIsoOffsetDatetime(fromDate);
            int schemaVersion = this.convertToInt((Serializable)request.getJobConfig().getParameters().get("schemaVersion"));
            ExportJobState exportJobState = this.dataExportOrchestrator.runScheduledExport(schemaVersion, fromDateParsed, false);
            if (exportJobState.getStatus() != ExportProgressStatus.STARTED) {
                this.publishScheduleFailedAnalyticEvent(config.get());
                return JobRunnerResponse.failed((String)("export failed with status: " + (Object)((Object)exportJobState.getStatus())));
            }
            this.publishScheduleExecutedAnalyticEvent(config.get());
            return JobRunnerResponse.success((String)"export executed");
        }
        return null;
    }

    private boolean shouldJobRun(ScheduleConfig config) {
        return this.licenseChecker.isDcLicense() && this.isCurrentWeekWithinRepeatWeeks(config);
    }

    private boolean isCurrentWeekWithinRepeatWeeks(ScheduleConfig config) {
        ZonedDateTime currentTime = this.currentDateTimeFn.apply(ZoneId.of(config.getZoneId()));
        return ExportScheduleNextRunTimeCalculator.getSameOrNextRunWithinRepeatWeeks(currentTime, config).equals(currentTime);
    }

    private void publishScheduleTriggeredAnalyticEvent(ScheduleConfig config) {
        this.eventPublisherService.publish(new ExportScheduleTriggeredAnalyticEvent(this.eventPublisherService.getPluginVersion(), config));
    }

    private void publishScheduleFailedAnalyticEvent(ScheduleConfig config) {
        this.eventPublisherService.publish(new ExportScheduleFailedAnalyticEvent(this.eventPublisherService.getPluginVersion(), config));
    }

    private void publishScheduleExecutedAnalyticEvent(ScheduleConfig config) {
        this.eventPublisherService.publish(new ExportScheduleExecutedAnalyticEvent(this.eventPublisherService.getPluginVersion(), config));
    }

    private int convertToInt(@Nonnull Serializable s) {
        Objects.requireNonNull(s, "Failed to convert to int: value to convert must not be null");
        if (s instanceof Integer) {
            return (Integer)s;
        }
        throw new IllegalArgumentException("Failed to convert to int: value to convert is invalid");
    }
}


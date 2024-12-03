/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.status.JobDetails
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.business.insights.core.service.scheduler;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.audit.api.AuditService;
import com.atlassian.business.insights.core.ao.dao.AoDataPipelineConfigDao;
import com.atlassian.business.insights.core.ao.dao.entity.AoDataPipelineConfig;
import com.atlassian.business.insights.core.audit.AuditEventFactory;
import com.atlassian.business.insights.core.service.api.ScheduleConfigService;
import com.atlassian.business.insights.core.service.scheduler.DefaultExportScheduleService;
import com.atlassian.business.insights.core.service.scheduler.ExportScheduleNextRunTimeCalculator;
import com.atlassian.business.insights.core.service.scheduler.ScheduleConfig;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.status.JobDetails;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.codehaus.jackson.map.ObjectMapper;

public class DefaultScheduleConfigService
implements ScheduleConfigService {
    @VisibleForTesting
    static final String EXPORT_SCHEDULE_CONFIG_KEY = "export.schedule.config";
    private final AoDataPipelineConfigDao aoDataPipelineConfigDao;
    private final ObjectMapper objectMapper;
    private final AuditService auditService;
    private final SchedulerService schedulerService;

    public DefaultScheduleConfigService(@Nonnull AoDataPipelineConfigDao aoDataPipelineConfigDao, @Nonnull ObjectMapper objectMapper, @Nonnull SchedulerService schedulerService, @Nonnull AuditService auditService) {
        this.aoDataPipelineConfigDao = Objects.requireNonNull(aoDataPipelineConfigDao, "aoDataPipelineConfigDao must not be null");
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
        this.schedulerService = Objects.requireNonNull(schedulerService, "schedulerService must not be null");
        this.auditService = Objects.requireNonNull(auditService, "auditService must not be null");
    }

    @Override
    @Nonnull
    public Optional<ScheduleConfig> getExportSchedule() {
        return this.aoDataPipelineConfigDao.get(EXPORT_SCHEDULE_CONFIG_KEY).map(AoDataPipelineConfig::getValue).map(this::convertToScheduleConfig);
    }

    @Override
    public void setExportSchedule(@Nullable ScheduleConfig scheduleConfig) {
        if (scheduleConfig != null) {
            String configString = this.serialiseJson(scheduleConfig);
            this.aoDataPipelineConfigDao.put(EXPORT_SCHEDULE_CONFIG_KEY, configString);
            this.auditService.audit(AuditEventFactory.createScheduleSetAuditEvent(configString));
        } else {
            this.aoDataPipelineConfigDao.delete(EXPORT_SCHEDULE_CONFIG_KEY);
            this.auditService.audit(AuditEventFactory.createScheduleDeletedAuditEvent());
        }
    }

    @Override
    @Nonnull
    public Optional<ZonedDateTime> getNextRunTime() {
        return this.getExportSchedule().flatMap(scheduleConfig -> Optional.ofNullable(this.schedulerService.getJobDetails(DefaultExportScheduleService.SCHEDULE_JOB_ID)).map(this::calculateNextRunTime).map(nextRunTime -> {
            ZonedDateTime runTime = ZonedDateTime.ofInstant(nextRunTime.toInstant(), ZoneId.of(scheduleConfig.getZoneId()));
            return ExportScheduleNextRunTimeCalculator.getSameOrNextRunWithinRepeatWeeks(runTime, scheduleConfig);
        }));
    }

    private Date calculateNextRunTime(JobDetails jobDetails) {
        try {
            return this.schedulerService.calculateNextRunTime(jobDetails.getSchedule());
        }
        catch (SchedulerServiceException e) {
            throw new RuntimeException("Could not calculate nextRunTime for JOB_ID:" + DefaultExportScheduleService.SCHEDULE_JOB_ID);
        }
    }

    private ScheduleConfig convertToScheduleConfig(String config) {
        try {
            return (ScheduleConfig)this.objectMapper.readValue(config, ScheduleConfig.class);
        }
        catch (IOException e) {
            throw new ConvertScheduleConfigurationException("Failed to convert config to ScheduleConfig object. The configuration: " + config, e);
        }
    }

    private String serialiseJson(ScheduleConfig scheduleConfig) {
        try {
            return this.objectMapper.writer().writeValueAsString((Object)scheduleConfig);
        }
        catch (IOException e) {
            throw new ConvertScheduleConfigurationException("Failed to convert ScheduleConfig object to String. The configuration: " + scheduleConfig.toString(), e);
        }
    }

    @VisibleForTesting
    static class ConvertScheduleConfigurationException
    extends RuntimeException {
        ConvertScheduleConfigurationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}


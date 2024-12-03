/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.audit.api.AuditRetentionConfigService
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.retention;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.audit.ao.dao.AuditEntityDao;
import com.atlassian.audit.api.AuditRetentionConfigService;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import java.time.Instant;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetentionJobRunner
implements JobRunner {
    public static final String CLEAN_UP_INTERVAL_IN_HOURS_KEY = "plugin.audit.retention.interval.hours";
    public static final int CLEAN_UP_INTERVAL_IN_HOURS_DEFAULT = 23;
    private static final Logger log = LoggerFactory.getLogger(RetentionJobRunner.class);
    private static final String CLEAN_UP_LAST_RUN_KEY = "com.atlassian.audit.plugin:cleanup.last.run.start";
    private final AuditEntityDao auditEntityDao;
    private final AuditRetentionConfigService auditRetentionConfigService;
    private final PluginSettingsFactory settingsFactory;
    private final int cleanUpInterval;

    @VisibleForTesting
    private RetentionJobRunner(AuditEntityDao auditEntityDao, AuditRetentionConfigService auditRetentionConfigService, PluginSettingsFactory settingsFactory) {
        this(auditEntityDao, auditRetentionConfigService, settingsFactory, 23);
    }

    public RetentionJobRunner(AuditEntityDao auditEntityDao, AuditRetentionConfigService auditRetentionConfigService, PluginSettingsFactory settingsFactory, int cleanUpInterval) {
        this.auditEntityDao = auditEntityDao;
        this.auditRetentionConfigService = auditRetentionConfigService;
        this.settingsFactory = settingsFactory;
        this.cleanUpInterval = cleanUpInterval;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {
        log.info("RetentionJobRunner Started");
        PluginSettings settings = this.settingsFactory.createGlobalSettings();
        long start = jobRunnerRequest.getStartTime().getTime();
        if (this.shouldRun((String)settings.get(CLEAN_UP_LAST_RUN_KEY), start)) {
            try {
                Period period = this.auditRetentionConfigService.getConfig().getPeriod();
                Instant before = ZonedDateTime.now().minusDays(period.getDays()).minusMonths(period.getMonths()).minusYears(period.getYears()).toInstant();
                this.auditEntityDao.removeBefore(before);
                log.info("RetentionJobRunner Finished");
                JobRunnerResponse jobRunnerResponse = JobRunnerResponse.success();
                return jobRunnerResponse;
            }
            catch (Exception e) {
                log.error("Failed to execute RetentionJob ", (Throwable)e);
                JobRunnerResponse jobRunnerResponse = JobRunnerResponse.failed((Throwable)e);
                return jobRunnerResponse;
            }
            finally {
                settings.put(CLEAN_UP_LAST_RUN_KEY, (Object)Long.toString(start));
            }
        }
        return JobRunnerResponse.success();
    }

    private boolean shouldRun(String lastRun, long start) {
        if (StringUtils.isNumeric((CharSequence)lastRun)) {
            boolean shouldRun;
            boolean bl = shouldRun = Long.parseLong(lastRun) + TimeUnit.HOURS.toMillis(this.cleanUpInterval) < start;
            if (!shouldRun) {
                log.trace("Last clean up job was within {} hours, skipping this run.", (Object)this.cleanUpInterval);
            }
            return shouldRun;
        }
        return true;
    }
}


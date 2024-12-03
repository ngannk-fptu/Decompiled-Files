/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  com.google.common.base.Throwables
 *  javax.annotation.Nullable
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.web.saml;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.impl.db.SeenAssertionDao;
import com.atlassian.plugins.authentication.impl.web.saml.provider.InvalidSamlResponse;
import com.atlassian.plugins.authentication.impl.web.saml.provider.SamlResponse;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.google.common.base.Throwables;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class SamlAssertionValidationService
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(SamlAssertionValidationService.class);
    private static final JobRunnerKey SCHEDULER_JOBRUNNER_KEY = JobRunnerKey.of((String)SamlAssertionValidationService.class.getCanonicalName());
    private static final String SCHEDULER_JOB_ID = "assertionId-cleanup";
    private final SeenAssertionDao seenAssertionDao;
    private final SchedulerService schedulerService;

    @Inject
    public SamlAssertionValidationService(SeenAssertionDao seenAssertionDao, @ComponentImport SchedulerService schedulerService) {
        this.seenAssertionDao = seenAssertionDao;
        this.schedulerService = schedulerService;
    }

    @PostConstruct
    public void registerCleanup() throws SchedulerServiceException {
        this.schedulerService.registerJobRunner(SCHEDULER_JOBRUNNER_KEY, (JobRunner)this);
        Date nextRun = Date.from(Instant.now().plus(1L, ChronoUnit.HOURS));
        Schedule hourlySchedule = Schedule.forInterval((long)TimeUnit.HOURS.toMillis(1L), (Date)nextRun);
        this.schedulerService.scheduleJob(JobId.of((String)SCHEDULER_JOB_ID), JobConfig.forJobRunnerKey((JobRunnerKey)SCHEDULER_JOBRUNNER_KEY).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(hourlySchedule));
        log.debug("Scheduled hourly cleanup job ({} - {}), next run at {}", new Object[]{SCHEDULER_JOBRUNNER_KEY, SCHEDULER_JOB_ID, nextRun});
    }

    @PreDestroy
    public void cancelCleanup() {
        this.schedulerService.unregisterJobRunner(SCHEDULER_JOBRUNNER_KEY);
        log.debug("Cancelled cleanup job");
    }

    public void validateAssertionId(SamlResponse samlResponse) throws InvalidSamlResponse {
        try {
            if (this.seenAssertionDao.assertionIdExists(samlResponse.getAssertionId())) {
                throw new InvalidSamlResponse("Assertion with the id " + samlResponse.getAssertionId() + " has already been used");
            }
            Instant latestNotOnOrAfter = (Instant)samlResponse.getNotOnOrAfters().stream().max(Comparator.naturalOrder()).orElseThrow(() -> new InvalidSamlResponse("Assertion doesn't have a NotOnOrAfter value"));
            this.seenAssertionDao.saveAssertionId(samlResponse.getAssertionId(), latestNotOnOrAfter);
        }
        catch (RuntimeException e) {
            Throwables.propagateIfInstanceOf((Throwable)e, InvalidSamlResponse.class);
            throw new InvalidSamlResponse("Failed checking/persisting nonce", e);
        }
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        try {
            Instant now = Instant.now();
            log.debug("Removing assertions older than {}", (Object)now);
            this.seenAssertionDao.removeOlderThan(now);
        }
        catch (Exception e) {
            log.error("Failed cleanup job", (Throwable)e);
            return JobRunnerResponse.failed((Throwable)e);
        }
        return JobRunnerResponse.success();
    }
}


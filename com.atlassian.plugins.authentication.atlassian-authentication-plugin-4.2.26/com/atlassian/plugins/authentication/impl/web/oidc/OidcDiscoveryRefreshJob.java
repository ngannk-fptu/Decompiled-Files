/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  com.atlassian.scheduler.cron.CronSyntaxException
 *  com.atlassian.scheduler.status.JobDetails
 *  com.google.common.base.Strings
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.web.oidc;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.IdpConfigService;
import com.atlassian.plugins.authentication.api.config.SsoConfigService;
import com.atlassian.plugins.authentication.api.config.oidc.OidcConfig;
import com.atlassian.plugins.authentication.event.OidcDiscoveryRefreshCronUpdatedEvent;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.scheduler.cron.CronSyntaxException;
import com.atlassian.scheduler.status.JobDetails;
import com.google.common.base.Strings;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@ExportAsService(value={LifecycleAware.class})
public class OidcDiscoveryRefreshJob
implements LifecycleAware {
    private static final Logger logger = LoggerFactory.getLogger(OidcDiscoveryRefreshJob.class);
    private static final String DEFAULT_CRON_EXPRESSION = "0 0 1 * * ?";
    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)(OidcDiscoveryRefreshJob.class.getName() + "-refresh"));
    private static final JobId JOB_ID = JobId.of((String)"OidcDiscoveryRefresh");
    private static final JobId JOB_ID_FOR_SINGLE_RUN = JobId.of((String)"OidcDiscoveryRefresh:SingleRun");
    private final SchedulerService schedulerService;
    private final SsoConfigService ssoConfigService;
    private final IdpConfigService idpConfigService;
    private final EventPublisher eventPublisher;

    @Inject
    public OidcDiscoveryRefreshJob(SchedulerService schedulerService, SsoConfigService ssoConfigService, IdpConfigService idpConfigService, EventPublisher eventPublisher) {
        this.schedulerService = schedulerService;
        this.ssoConfigService = ssoConfigService;
        this.idpConfigService = idpConfigService;
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void initializeBean() {
        this.schedulerService.registerJobRunner(JOB_RUNNER_KEY, this::runJob);
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void destroyBean() {
        this.schedulerService.unregisterJobRunner(JOB_RUNNER_KEY);
        this.eventPublisher.unregister((Object)this);
    }

    public void onStart() {
        try {
            this.scheduleSingleRunImmediately();
            this.scheduleJobForCronInterval();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void onStop() {
        Stream<JobId> jobIds = this.schedulerService.getJobsByJobRunnerKey(JOB_RUNNER_KEY).stream().map(JobDetails::getJobId).peek(jobId -> logger.debug("Unscheduling job '{}' since the plugin has been disabled", jobId));
        jobIds.forEach(arg_0 -> ((SchedulerService)this.schedulerService).unscheduleJob(arg_0));
    }

    private void scheduleSingleRunImmediately() throws SchedulerServiceException {
        Duration delay = Duration.ofSeconds(10L);
        this.scheduleJob(JOB_ID_FOR_SINGLE_RUN, Schedule.runOnce((Date)Date.from(Instant.now().plus(delay))));
    }

    private void scheduleJobForCronInterval() throws SchedulerServiceException {
        Optional<String> cronFromConfig = this.getCronFromConfig();
        if (cronFromConfig.isPresent()) {
            try {
                this.scheduleJob(JOB_ID, Schedule.forCronExpression((String)cronFromConfig.get()));
                return;
            }
            catch (CronSyntaxException e) {
                logger.info("Invalid cron expression in config '{}', reverting to default cron expression", (Throwable)e);
            }
        }
        this.scheduleJob(JOB_ID, Schedule.forCronExpression((String)DEFAULT_CRON_EXPRESSION));
    }

    private Optional<String> getCronFromConfig() {
        return Optional.ofNullable(Strings.emptyToNull((String)this.ssoConfigService.getSsoConfig().getDiscoveryRefreshCron()));
    }

    private void scheduleJob(JobId id, Schedule schedule) throws SchedulerServiceException {
        JobConfig config = JobConfig.forJobRunnerKey((JobRunnerKey)JOB_RUNNER_KEY).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(schedule);
        Date nextRun = this.schedulerService.calculateNextRunTime(schedule);
        logger.info("Scheduling job {} with next run time: '{}'.", (Object)"OidcDiscoveryRefresh", (Object)nextRun);
        this.schedulerService.scheduleJob(id, config);
    }

    public JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {
        logger.debug("Refreshing IdP configs with discovery");
        List<OidcConfig> oidcConfigsWithDiscoveryEnabled = this.getOidcConfigsWithDiscoveryEnabled();
        oidcConfigsWithDiscoveryEnabled.forEach(oidcConfig -> {
            logger.debug("Now refreshing IdP config [{}]", (Object)oidcConfig.getId());
            this.idpConfigService.refreshIdpConfig((IdpConfig)oidcConfig);
        });
        logger.debug("Finished refreshing IdP configs with discovery");
        return JobRunnerResponse.success();
    }

    private List<OidcConfig> getOidcConfigsWithDiscoveryEnabled() {
        return this.idpConfigService.getIdpConfigs().stream().map(OidcConfig::from).filter(Optional::isPresent).map(Optional::get).filter(OidcConfig::isDiscoveryEnabled).collect(Collectors.toList());
    }

    @EventListener
    public void onOidcDiscoveryRefreshCronUpdated(OidcDiscoveryRefreshCronUpdatedEvent event) {
        this.safelyScheduleJobForCronInterval();
    }

    private void safelyScheduleJobForCronInterval() {
        try {
            this.scheduleJobForCronInterval();
        }
        catch (Exception e) {
            logger.error("Error while scheduling OidcDiscoveryRefreshJob", (Throwable)e);
        }
    }
}


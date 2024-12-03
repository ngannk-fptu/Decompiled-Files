/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.analytics;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.IdpConfigService;
import com.atlassian.plugins.authentication.api.config.SsoConfig;
import com.atlassian.plugins.authentication.api.config.SsoConfigService;
import com.atlassian.plugins.authentication.impl.analytics.AbstractStatisticsCollectionService;
import com.atlassian.plugins.authentication.impl.analytics.events.AuthFallbackStatusAnalyticsEvent;
import com.atlassian.plugins.authentication.impl.analytics.events.IdpConfigStatusAnalyticsEvent;
import com.atlassian.plugins.authentication.impl.analytics.events.LoginFormStatusAnalyticsEvent;
import com.atlassian.plugins.authentication.impl.basicauth.BasicAuthConfig;
import com.atlassian.plugins.authentication.impl.basicauth.analytics.events.BasicAuthStatusEvent;
import com.atlassian.plugins.authentication.impl.basicauth.service.BasicAuthDao;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class ClusterWideStatisticsCollectionService
extends AbstractStatisticsCollectionService {
    private static final Logger log = LoggerFactory.getLogger(ClusterWideStatisticsCollectionService.class);
    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)ClusterWideStatisticsCollectionService.class.getCanonicalName());
    private static final JobId JOB_ID = JobId.of((String)"analytics-collection");
    private final SsoConfigService ssoConfigService;
    private final IdpConfigService idpConfigService;
    private final BasicAuthDao basicAuthDao;

    @Inject
    public ClusterWideStatisticsCollectionService(@ComponentImport EventPublisher eventPublisher, @ComponentImport SchedulerService schedulerService, SsoConfigService ssoConfigService, IdpConfigService idpConfigService, BasicAuthDao basicAuthDao) {
        super(eventPublisher, schedulerService);
        this.ssoConfigService = ssoConfigService;
        this.idpConfigService = idpConfigService;
        this.basicAuthDao = basicAuthDao;
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        SsoConfig ssoConfig = this.ssoConfigService.getSsoConfig();
        List<IdpConfig> idpConfigs = this.idpConfigService.getIdpConfigs();
        BasicAuthConfig basicAuthConfig = this.basicAuthDao.get();
        log.debug("Collecting configuration statistics");
        Stream.concat(idpConfigs.stream().map(IdpConfigStatusAnalyticsEvent::new), Stream.of(new LoginFormStatusAnalyticsEvent(ssoConfig), new AuthFallbackStatusAnalyticsEvent(ssoConfig, idpConfigs), new BasicAuthStatusEvent(basicAuthConfig))).forEach(this::tryPublish);
        return JobRunnerResponse.success();
    }

    @Override
    @Nonnull
    protected RunMode getRunMode() {
        return RunMode.RUN_ONCE_PER_CLUSTER;
    }

    @Override
    protected JobId getJobId() {
        return JOB_ID;
    }

    @Override
    protected JobRunnerKey getJobRunnerKey() {
        return JOB_RUNNER_KEY;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.cmpt.check.mapper.CheckResultMapper
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service.check.token;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.mapper.CheckResultMapper;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.CheckRegistration;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.token.ContainerTokenExpirationCheckContextProvider;
import com.atlassian.migration.agent.service.check.token.ContainerTokenExpirationChecker;
import com.atlassian.migration.agent.service.check.token.ContainerTokenExpirationContext;
import com.atlassian.migration.agent.service.check.token.ContainerTokenExpirationMapper;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.impl.MigrationPlatformService;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ContainerTokenExpirationCheckRegistration
implements CheckRegistration<ContainerTokenExpirationContext> {
    public static final String CONTAINER_TOKEN_EXPIRATION_CHECK = "containerTokenExpirationCheck";
    private final ContainerTokenExpirationChecker containerTokenExpirationChecker;
    private final ContainerTokenExpirationMapper containerTokenExpirationMapper;
    private final ContainerTokenExpirationCheckContextProvider containerTokenExpirationCheckContextProvider;
    private final AnalyticsEventBuilder analyticsEventBuilder;

    public ContainerTokenExpirationCheckRegistration(CloudSiteService cloudSiteService, MigrationPlatformService migrationPlatformService, AnalyticsEventBuilder analyticsEventBuilder) {
        this.containerTokenExpirationChecker = new ContainerTokenExpirationChecker(cloudSiteService, migrationPlatformService);
        this.containerTokenExpirationMapper = new ContainerTokenExpirationMapper();
        this.containerTokenExpirationCheckContextProvider = new ContainerTokenExpirationCheckContextProvider();
        this.analyticsEventBuilder = analyticsEventBuilder;
    }

    @Override
    public CheckType getCheckType() {
        return CheckType.CONTAINER_TOKEN_EXPIRATION;
    }

    @Override
    public Checker<ContainerTokenExpirationContext> getChecker() {
        return this.containerTokenExpirationChecker;
    }

    @Override
    public CheckContextProvider<ContainerTokenExpirationContext> getCheckContextProvider() {
        return this.containerTokenExpirationCheckContextProvider;
    }

    @Override
    public CheckResultMapper getCheckResultMapper() {
        return this.containerTokenExpirationMapper;
    }

    @Override
    public EventDto getAnalyticsEventModel(CheckResult checkResult, long totalTime) {
        return this.analyticsEventBuilder.buildPreFlightContainerTokenExpiration(checkResult.success, totalTime);
    }

    @Override
    public String getFailedToExecuteAnalyticsEventName() {
        return CONTAINER_TOKEN_EXPIRATION_CHECK;
    }
}


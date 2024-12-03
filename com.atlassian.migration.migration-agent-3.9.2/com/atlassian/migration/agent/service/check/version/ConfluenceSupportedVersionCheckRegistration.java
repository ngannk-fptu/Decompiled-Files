/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.cmpt.check.mapper.CheckResultMapper
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service.check.version;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.mapper.CheckResultMapper;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.CheckRegistration;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.version.ConfluenceSupportedVersionCheckContext;
import com.atlassian.migration.agent.service.check.version.ConfluenceSupportedVersionCheckContextProvider;
import com.atlassian.migration.agent.service.check.version.ConfluenceSupportedVersionCheckMapper;
import com.atlassian.migration.agent.service.check.version.ConfluenceSupportedVersionChecker;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ConfluenceSupportedVersionCheckRegistration
implements CheckRegistration<ConfluenceSupportedVersionCheckContext> {
    private final ConfluenceSupportedVersionChecker checker;
    private final ConfluenceSupportedVersionCheckContextProvider provider;
    private final ConfluenceSupportedVersionCheckMapper resultMapper;
    private final AnalyticsEventBuilder analyticsEventBuilder;

    public ConfluenceSupportedVersionCheckRegistration(AnalyticsEventBuilder analyticsEventBuilder, SystemInformationService systemInformationService, MigrationAgentConfiguration migrationAgentConfiguration) {
        this.checker = new ConfluenceSupportedVersionChecker(migrationAgentConfiguration);
        this.provider = new ConfluenceSupportedVersionCheckContextProvider(systemInformationService);
        this.resultMapper = new ConfluenceSupportedVersionCheckMapper();
        this.analyticsEventBuilder = analyticsEventBuilder;
    }

    @Override
    public CheckType getCheckType() {
        return CheckType.CONFLUENCE_SUPPORTED_VERSION;
    }

    @Override
    public Checker<ConfluenceSupportedVersionCheckContext> getChecker() {
        return this.checker;
    }

    @Override
    public CheckContextProvider<ConfluenceSupportedVersionCheckContext> getCheckContextProvider() {
        return this.provider;
    }

    @Override
    public CheckResultMapper getCheckResultMapper() {
        return this.resultMapper;
    }

    @Override
    public EventDto getAnalyticsEventModel(CheckResult checkResult, long totalTime) {
        return this.analyticsEventBuilder.buildPreflightConfluenceSupportedVersionCheck(checkResult.success, totalTime);
    }

    @Override
    public String getFailedToExecuteAnalyticsEventName() {
        return CheckType.CONFLUENCE_SUPPORTED_VERSION.value();
    }
}


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
package com.atlassian.migration.agent.service.check.edition;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.mapper.CheckResultMapper;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.CheckRegistration;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.edition.CloudPremiumEditionChecker;
import com.atlassian.migration.agent.service.check.edition.CloudPremiumEditionContext;
import com.atlassian.migration.agent.service.check.edition.CloudPremiumEditionContextProvider;
import com.atlassian.migration.agent.service.check.edition.CloudPremiumEditionMapper;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.user.RetryingUsersMigrationService;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CloudPremiumEditionRegistration
implements CheckRegistration<CloudPremiumEditionContext> {
    private final CloudPremiumEditionChecker checker;
    private final CloudPremiumEditionContextProvider contextProvider;
    private final CloudPremiumEditionMapper resultMapper;
    private final AnalyticsEventBuilder analyticsEventBuilder;

    public CloudPremiumEditionRegistration(AnalyticsEventBuilder analyticsEventBuilder, CloudSiteService cloudSiteService, RetryingUsersMigrationService usersMigrationService) {
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.checker = new CloudPremiumEditionChecker(cloudSiteService, usersMigrationService);
        this.resultMapper = new CloudPremiumEditionMapper();
        this.contextProvider = new CloudPremiumEditionContextProvider();
    }

    @Override
    public CheckType getCheckType() {
        return CheckType.CLOUD_PREMIUM_EDITION;
    }

    @Override
    public Checker<CloudPremiumEditionContext> getChecker() {
        return this.checker;
    }

    @Override
    public CheckContextProvider<CloudPremiumEditionContext> getCheckContextProvider() {
        return this.contextProvider;
    }

    @Override
    public CheckResultMapper getCheckResultMapper() {
        return this.resultMapper;
    }

    @Override
    public EventDto getAnalyticsEventModel(CheckResult checkResult, long totalTime) {
        String edition = (String)checkResult.details.get("edition");
        return this.analyticsEventBuilder.buildPreflightCloudPremiumEditionCheck(checkResult.success, totalTime, edition);
    }

    @Override
    public String getFailedToExecuteAnalyticsEventName() {
        return "cloudPremiumEditionCheck";
    }
}


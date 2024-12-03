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
package com.atlassian.migration.agent.service.check.app.notinstalled;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.mapper.CheckResultMapper;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.app.AppAssessmentInfoService;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.CheckRegistration;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.app.CloudAppKeyFetcher;
import com.atlassian.migration.agent.service.check.app.notinstalled.AppsNotInstalledOnCloudChecker;
import com.atlassian.migration.agent.service.check.app.notinstalled.AppsNotInstalledOnCloudContext;
import com.atlassian.migration.agent.service.check.app.notinstalled.AppsNotInstalledOnCloudContextProvider;
import com.atlassian.migration.agent.service.check.app.notinstalled.AppsNotInstalledOnCloudMapper;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.impl.AppAssessmentFacade;
import com.atlassian.migration.agent.service.impl.MigrationAppAggregatorService;
import com.atlassian.migration.app.AppAssessmentClient;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AppsNotInstalledOnCloudCheckRegistration
implements CheckRegistration<AppsNotInstalledOnCloudContext> {
    private final AppsNotInstalledOnCloudChecker checker;
    private final AppsNotInstalledOnCloudContextProvider provider;
    private final AppsNotInstalledOnCloudMapper resultMapper;
    private final AnalyticsEventBuilder analyticsEventBuilder;

    public AppsNotInstalledOnCloudCheckRegistration(MigrationAppAggregatorService appAggregatorService, AppAssessmentClient appAssessmentClient, AppAssessmentInfoService appAssessmentInfoService, AppAssessmentFacade appAssessmentFacade, CloudSiteService cloudSiteService, AnalyticsEventBuilder analyticsEventBuilder, CloudAppKeyFetcher cloudAppKeyFetcher) {
        this.checker = new AppsNotInstalledOnCloudChecker(appAggregatorService, appAssessmentClient, cloudSiteService, appAssessmentFacade, cloudAppKeyFetcher);
        this.provider = new AppsNotInstalledOnCloudContextProvider(appAssessmentInfoService);
        this.resultMapper = new AppsNotInstalledOnCloudMapper();
        this.analyticsEventBuilder = analyticsEventBuilder;
    }

    @Override
    public CheckType getCheckType() {
        return CheckType.APPS_NOT_INSTALLED_ON_CLOUD;
    }

    @Override
    public Checker<AppsNotInstalledOnCloudContext> getChecker() {
        return this.checker;
    }

    @Override
    public CheckContextProvider<AppsNotInstalledOnCloudContext> getCheckContextProvider() {
        return this.provider;
    }

    @Override
    public CheckResultMapper getCheckResultMapper() {
        return this.resultMapper;
    }

    @Override
    public EventDto getAnalyticsEventModel(CheckResult checkResult, long totalTime) {
        return this.analyticsEventBuilder.buildPreflightAppsNotInstalledOnCloud(checkResult.success, AppsNotInstalledOnCloudChecker.retrieveAppsNotInstalledOnCloud(checkResult.details).size(), totalTime);
    }

    @Override
    public String getFailedToExecuteAnalyticsEventName() {
        return "appsNotInstalledOnCloudCheck";
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.cmpt.check.mapper.CheckResultMapper
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.check.app.license;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.mapper.CheckResultMapper;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.CheckRegistration;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.app.license.AppLicenseChecker;
import com.atlassian.migration.agent.service.check.app.license.AppLicenseContext;
import com.atlassian.migration.agent.service.check.app.license.AppLicenseContextProvider;
import com.atlassian.migration.agent.service.check.app.license.AppLicenseMapper;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Generated;

public class AppLicenseCheckRegistration
implements CheckRegistration<AppLicenseContext> {
    private final AppLicenseChecker checker;
    private final AppLicenseContextProvider provider;
    private final AppLicenseMapper resultMapper;
    private final AnalyticsEventBuilder analyticsEventBuilder;

    @Override
    public CheckType getCheckType() {
        return CheckType.APP_LICENSE_CHECK;
    }

    @Override
    public Checker<AppLicenseContext> getChecker() {
        return this.checker;
    }

    @Override
    public CheckContextProvider<AppLicenseContext> getCheckContextProvider() {
        return this.provider;
    }

    @Override
    public CheckResultMapper getCheckResultMapper() {
        return this.resultMapper;
    }

    @Override
    public EventDto getAnalyticsEventModel(CheckResult checkResult, long totalTime) {
        Set<String> appsSucceeded = AppLicenseChecker.retrieveAppsWithLicenses(checkResult.details);
        Set<String> appsFailed = AppLicenseChecker.retrieveAppsNoLicenseViolations(checkResult.details).stream().map(dto -> dto.key).collect(Collectors.toSet());
        return this.analyticsEventBuilder.buildPreflightAppLicenseCheck(checkResult.success, appsSucceeded, appsFailed, totalTime);
    }

    @Override
    public String getFailedToExecuteAnalyticsEventName() {
        return "appLicenseCheck";
    }

    @Generated
    public AppLicenseCheckRegistration(AppLicenseChecker checker, AppLicenseContextProvider provider, AppLicenseMapper resultMapper, AnalyticsEventBuilder analyticsEventBuilder) {
        this.checker = checker;
        this.provider = provider;
        this.resultMapper = resultMapper;
        this.analyticsEventBuilder = analyticsEventBuilder;
    }
}


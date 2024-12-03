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
package com.atlassian.migration.agent.service.check.app.vendorcheck;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.mapper.CheckResultMapper;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.CheckRegistration;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.app.vendorcheck.AppVendorCheckContext;
import com.atlassian.migration.agent.service.check.app.vendorcheck.AppVendorCheckContextProvider;
import com.atlassian.migration.agent.service.check.app.vendorcheck.AppVendorCheckMapper;
import com.atlassian.migration.agent.service.check.app.vendorcheck.AppVendorChecker;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AppVendorCheckRegistration
implements CheckRegistration<AppVendorCheckContext> {
    private final AppVendorChecker checker;
    private final AppVendorCheckContextProvider contextProvider;
    private final AppVendorCheckMapper mapper;
    private final AnalyticsEventBuilder analyticsEventBuilder;

    public AppVendorCheckRegistration(AppVendorChecker appVendorCheckChecker, AppVendorCheckContextProvider contextProvider, AnalyticsEventBuilder analyticsEventBuilder) {
        this.checker = appVendorCheckChecker;
        this.contextProvider = contextProvider;
        this.mapper = new AppVendorCheckMapper();
        this.analyticsEventBuilder = analyticsEventBuilder;
    }

    @Override
    public CheckType getCheckType() {
        return CheckType.APP_VENDOR_CHECK;
    }

    @Override
    public Checker<AppVendorCheckContext> getChecker() {
        return this.checker;
    }

    @Override
    public CheckContextProvider<AppVendorCheckContext> getCheckContextProvider() {
        return this.contextProvider;
    }

    @Override
    public CheckResultMapper getCheckResultMapper() {
        return this.mapper;
    }

    @Override
    public EventDto getAnalyticsEventModel(CheckResult checkResult, long totalTime) {
        return this.analyticsEventBuilder.buildPreflightAppVendorCheck(checkResult, totalTime);
    }

    @Override
    public String getFailedToExecuteAnalyticsEventName() {
        return "appVendorCheck";
    }
}


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
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.atlassian.migration.agent.service.check.app.teamcalendars;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.mapper.CheckResultMapper;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.CheckRegistration;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.app.teamcalendars.TcVersionChecker;
import com.atlassian.migration.agent.service.check.app.teamcalendars.TcVersionContext;
import com.atlassian.migration.agent.service.check.app.teamcalendars.TcVersionContextProvider;
import com.atlassian.migration.agent.service.check.app.teamcalendars.TcVersionMapper;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.web.util.UriComponentsBuilder;

@ParametersAreNonnullByDefault
public class TcVersionCheckRegistration
implements CheckRegistration<TcVersionContext> {
    private static final String UPM_PATH = "/plugins/servlet/upm";
    private final AnalyticsEventBuilder analyticsEventBuilder;
    private final TcVersionChecker checker;
    private final TcVersionContextProvider contextProvider;
    private final TcVersionMapper mapper;

    public TcVersionCheckRegistration(AnalyticsEventBuilder analyticsEventBuilder, TcVersionChecker checker, TcVersionContextProvider contextProvider, SystemInformationService sysInfoService) {
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.checker = checker;
        this.contextProvider = contextProvider;
        this.mapper = new TcVersionMapper(UriComponentsBuilder.fromHttpUrl((String)sysInfoService.getConfluenceInfo().getBaseUrl()).path(UPM_PATH).toUriString());
    }

    @Override
    public CheckType getCheckType() {
        return CheckType.TEAM_CALENDARS_APP_VERSION;
    }

    @Override
    public Checker<TcVersionContext> getChecker() {
        return this.checker;
    }

    @Override
    public CheckContextProvider<TcVersionContext> getCheckContextProvider() {
        return this.contextProvider;
    }

    @Override
    public CheckResultMapper getCheckResultMapper() {
        return this.mapper;
    }

    @Override
    public EventDto getAnalyticsEventModel(CheckResult checkResult, long totalTime) {
        String tcAppVersion = (String)checkResult.details.get("tcAppVersion");
        return this.analyticsEventBuilder.buildPreflightTcAppVersion(checkResult.success, totalTime, tcAppVersion);
    }

    @Override
    public String getFailedToExecuteAnalyticsEventName() {
        return "tcAppVersionCheck";
    }
}


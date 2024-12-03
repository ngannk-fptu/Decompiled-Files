/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.cmpt.check.mapper.AppOutdatedMapper
 *  com.atlassian.cmpt.check.mapper.CheckResultMapper
 *  com.atlassian.cmpt.check.version.AppOutdatedChecker
 *  com.atlassian.cmpt.check.version.AppOutdatedContext
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.atlassian.migration.agent.service.check.version;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.mapper.AppOutdatedMapper;
import com.atlassian.cmpt.check.mapper.CheckResultMapper;
import com.atlassian.cmpt.check.version.AppOutdatedChecker;
import com.atlassian.cmpt.check.version.AppOutdatedContext;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.migration.agent.service.PluginInfoService;
import com.atlassian.migration.agent.service.PluginVersionCheckResult;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.CheckRegistration;
import com.atlassian.migration.agent.service.check.CheckType;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.web.util.UriComponentsBuilder;

@ParametersAreNonnullByDefault
public class AppOutdatedCheckRegistration
implements CheckRegistration<AppOutdatedContext> {
    private static final String UPM_PATH = "/plugins/servlet/upm";
    private final PluginInfoService pluginInfoService;
    private final AppOutdatedChecker checker;
    private final AppOutdatedMapper resultMapper;
    private final AnalyticsEventBuilder analyticsEventBuilder;

    public AppOutdatedCheckRegistration(PluginInfoService pluginInfoService, SystemInformationService sysInfoService, AnalyticsEventBuilder analyticsEventBuilder) {
        this.pluginInfoService = pluginInfoService;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.checker = new AppOutdatedChecker();
        this.resultMapper = new AppOutdatedMapper(UriComponentsBuilder.fromHttpUrl((String)sysInfoService.getConfluenceInfo().getBaseUrl()).path(UPM_PATH).toUriString());
    }

    @Override
    public CheckType getCheckType() {
        return CheckType.APP_OUTDATED;
    }

    @Override
    public Checker<AppOutdatedContext> getChecker() {
        return this.checker;
    }

    @Override
    public CheckContextProvider<AppOutdatedContext> getCheckContextProvider() {
        return params -> Optional.ofNullable(params.getOrDefault("cloudId", null)).map(cloudId -> this.pluginInfoService.checkPluginVersion((String)cloudId)).map(this::toAppOutdatedContext).orElseThrow(() -> new IllegalArgumentException("cloudId not found in the parameters"));
    }

    @Override
    public CheckResultMapper getCheckResultMapper() {
        return this.resultMapper;
    }

    @Override
    public EventDto getAnalyticsEventModel(CheckResult checkResult, long totalTime) {
        return this.analyticsEventBuilder.buildPreflightAppOutdated(checkResult.success, totalTime);
    }

    @Override
    public String getFailedToExecuteAnalyticsEventName() {
        return "migrationAssistantOutdatedCheck";
    }

    private AppOutdatedContext toAppOutdatedContext(PluginVersionCheckResult pluginInfo) {
        return new AppOutdatedContext(pluginInfo.getResult() == PluginVersionCheckResult.Result.OUTDATED, pluginInfo.getUpgradeBy());
    }
}


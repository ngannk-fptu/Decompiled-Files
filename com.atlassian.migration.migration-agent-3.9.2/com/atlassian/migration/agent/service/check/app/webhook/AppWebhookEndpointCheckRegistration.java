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
package com.atlassian.migration.agent.service.check.app.webhook;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.mapper.CheckResultMapper;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.CheckRegistration;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.app.webhook.AppWebhookEndpointCheckContext;
import com.atlassian.migration.agent.service.check.app.webhook.AppWebhookEndpointCheckContextProvider;
import com.atlassian.migration.agent.service.check.app.webhook.AppWebhookEndpointCheckMapper;
import com.atlassian.migration.agent.service.check.app.webhook.AppWebhookEndpointChecker;
import lombok.Generated;

public class AppWebhookEndpointCheckRegistration
implements CheckRegistration<AppWebhookEndpointCheckContext> {
    private final AppWebhookEndpointChecker appWebhookEndpointChecker;
    private final AppWebhookEndpointCheckContextProvider appWebhookEndpointCheckContextProvider;
    private final AppWebhookEndpointCheckMapper appWebhookEndpointCheckMapper;
    private final AnalyticsEventBuilder analyticsEventBuilder;

    @Override
    public CheckType getCheckType() {
        return CheckType.APP_WEBHOOK_ENDPOINT_CHECK;
    }

    @Override
    public Checker<AppWebhookEndpointCheckContext> getChecker() {
        return this.appWebhookEndpointChecker;
    }

    @Override
    public CheckContextProvider<AppWebhookEndpointCheckContext> getCheckContextProvider() {
        return this.appWebhookEndpointCheckContextProvider;
    }

    @Override
    public CheckResultMapper getCheckResultMapper() {
        return this.appWebhookEndpointCheckMapper;
    }

    @Override
    public EventDto getAnalyticsEventModel(CheckResult checkResult, long totalTime) {
        AppWebhookEndpointCheckContext appWebhookEndpointCheckContext = this.appWebhookEndpointChecker.getAppWebhookEndpointCheckContext();
        return this.analyticsEventBuilder.buildPreflightAppWebhookEndpointCheck(checkResult, totalTime, appWebhookEndpointCheckContext.appKeys, appWebhookEndpointCheckContext.cloudId);
    }

    @Override
    public String getFailedToExecuteAnalyticsEventName() {
        return "appWebhookEndpointCheck";
    }

    @Generated
    public AppWebhookEndpointCheckRegistration(AppWebhookEndpointChecker appWebhookEndpointChecker, AppWebhookEndpointCheckContextProvider appWebhookEndpointCheckContextProvider, AppWebhookEndpointCheckMapper appWebhookEndpointCheckMapper, AnalyticsEventBuilder analyticsEventBuilder) {
        this.appWebhookEndpointChecker = appWebhookEndpointChecker;
        this.appWebhookEndpointCheckContextProvider = appWebhookEndpointCheckContextProvider;
        this.appWebhookEndpointCheckMapper = appWebhookEndpointCheckMapper;
        this.analyticsEventBuilder = analyticsEventBuilder;
    }
}


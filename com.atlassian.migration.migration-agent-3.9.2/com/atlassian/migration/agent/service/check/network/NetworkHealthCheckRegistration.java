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
package com.atlassian.migration.agent.service.check.network;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.mapper.CheckResultMapper;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.catalogue.PlatformService;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.CheckRegistration;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.network.ConnectivityTester;
import com.atlassian.migration.agent.service.check.network.NetworkHealthCheckContextProvider;
import com.atlassian.migration.agent.service.check.network.NetworkHealthChecker;
import com.atlassian.migration.agent.service.check.network.NetworkHealthContext;
import com.atlassian.migration.agent.service.check.network.NetworkHealthMapper;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class NetworkHealthCheckRegistration
implements CheckRegistration<NetworkHealthContext> {
    private final NetworkHealthChecker checker;
    private final NetworkHealthCheckContextProvider contextProvider;
    private final NetworkHealthMapper resultMapper;
    private final AnalyticsEventBuilder analyticsEventBuilder;

    public NetworkHealthCheckRegistration(PlatformService platformService, AnalyticsEventBuilder analyticsEventBuilder, ConnectivityTester connectivityTester, AnalyticsEventService analyticsEventService, MigrationAgentConfiguration migrationAgentConfiguration) {
        this.checker = new NetworkHealthChecker(platformService, connectivityTester, analyticsEventService, analyticsEventBuilder, migrationAgentConfiguration);
        this.contextProvider = new NetworkHealthCheckContextProvider();
        this.resultMapper = new NetworkHealthMapper();
        this.analyticsEventBuilder = analyticsEventBuilder;
    }

    @Override
    public CheckType getCheckType() {
        return CheckType.NETWORK_HEALTH;
    }

    @Override
    public Checker<NetworkHealthContext> getChecker() {
        return this.checker;
    }

    @Override
    public CheckContextProvider<NetworkHealthContext> getCheckContextProvider() {
        return this.contextProvider;
    }

    @Override
    public CheckResultMapper getCheckResultMapper() {
        return this.resultMapper;
    }

    @Override
    public EventDto getAnalyticsEventModel(CheckResult checkResult, long totalTime) {
        return this.analyticsEventBuilder.buildPreflightNetworkHealth(checkResult.success, NetworkHealthChecker.retrieveFailedNetworkHealthUrls(checkResult.details), totalTime);
    }

    @Override
    public String getFailedToExecuteAnalyticsEventName() {
        return "networkHealthCheck";
    }
}


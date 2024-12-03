/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.cmpt.check.mapper.CheckResultMapper
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.migration.agent.service.check.domain;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.mapper.CheckResultMapper;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.CheckRegistration;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.domain.TrustedDomainChecker;
import com.atlassian.migration.agent.service.check.domain.TrustedDomainContext;
import com.atlassian.migration.agent.service.check.domain.TrustedDomainContextProvider;
import com.atlassian.migration.agent.service.check.domain.TrustedDomainMapper;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@ParametersAreNonnullByDefault
public class TrustedDomainCheckRegistration
implements CheckRegistration<TrustedDomainContext> {
    private final TrustedDomainChecker checker;
    private final TrustedDomainContextProvider contextProvider;
    private final TrustedDomainMapper mapper;
    private final AnalyticsEventBuilder analyticsEventBuilder;

    @Inject
    public TrustedDomainCheckRegistration(TrustedDomainChecker trustedDomainChecker, AnalyticsEventBuilder analyticsEventBuilder) {
        this.checker = trustedDomainChecker;
        this.contextProvider = new TrustedDomainContextProvider();
        this.mapper = new TrustedDomainMapper();
        this.analyticsEventBuilder = analyticsEventBuilder;
    }

    @Override
    public CheckType getCheckType() {
        return CheckType.TRUSTED_DOMAINS;
    }

    @Override
    public Checker<TrustedDomainContext> getChecker() {
        return this.checker;
    }

    @Override
    public CheckContextProvider<TrustedDomainContext> getCheckContextProvider() {
        return this.contextProvider;
    }

    @Override
    public CheckResultMapper getCheckResultMapper() {
        return this.mapper;
    }

    @Override
    public EventDto getAnalyticsEventModel(CheckResult checkResult, long totalTime) {
        return this.analyticsEventBuilder.buildPreflightTrustedDomain(checkResult.success, totalTime);
    }

    @Override
    public String getFailedToExecuteAnalyticsEventName() {
        return "trustedDomainCheck";
    }
}


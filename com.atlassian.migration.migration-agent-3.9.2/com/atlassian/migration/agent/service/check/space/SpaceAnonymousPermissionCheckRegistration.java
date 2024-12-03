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
package com.atlassian.migration.agent.service.check.space;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.mapper.CheckResultMapper;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.CheckRegistration;
import com.atlassian.migration.agent.service.check.CheckResultFileManager;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.space.SpaceAnonymousPermissionCheckContextProvider;
import com.atlassian.migration.agent.service.check.space.SpaceAnonymousPermissionChecker;
import com.atlassian.migration.agent.service.check.space.SpaceAnonymousPermissionContext;
import com.atlassian.migration.agent.service.check.space.SpaceAnonymousPermissionMapper;
import com.atlassian.migration.agent.store.impl.SpacePermissionStore;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.web.util.UriComponentsBuilder;

@ParametersAreNonnullByDefault
public class SpaceAnonymousPermissionCheckRegistration
implements CheckRegistration<SpaceAnonymousPermissionContext> {
    private static final String PERMISSIONS_URL = "/admin/permissions/viewdefaultspacepermissions.action";
    private final SpaceAnonymousPermissionCheckContextProvider provider;
    private final SpaceAnonymousPermissionChecker checker;
    private final SpaceAnonymousPermissionMapper resultMapper;
    private final AnalyticsEventBuilder analyticsEventBuilder;

    public SpaceAnonymousPermissionCheckRegistration(SpaceAnonymousPermissionCheckContextProvider provider, AnalyticsEventBuilder analyticsEventBuilder, SystemInformationService systemInformationService, SpacePermissionStore spacePermissionStore, CheckResultFileManager checkResultFileManager) {
        this.provider = provider;
        this.checker = new SpaceAnonymousPermissionChecker(spacePermissionStore, checkResultFileManager);
        this.resultMapper = new SpaceAnonymousPermissionMapper(UriComponentsBuilder.fromHttpUrl((String)systemInformationService.getConfluenceInfo().getBaseUrl()).path(PERMISSIONS_URL).toUriString());
        this.analyticsEventBuilder = analyticsEventBuilder;
    }

    @Override
    public CheckType getCheckType() {
        return CheckType.SPACE_ANONYMOUS_PERMISSIONS;
    }

    @Override
    public Checker<SpaceAnonymousPermissionContext> getChecker() {
        return this.checker;
    }

    @Override
    public CheckContextProvider<SpaceAnonymousPermissionContext> getCheckContextProvider() {
        return this.provider;
    }

    @Override
    public CheckResultMapper getCheckResultMapper() {
        return this.resultMapper;
    }

    @Override
    public EventDto getAnalyticsEventModel(CheckResult checkResult, long totalTime) {
        return this.analyticsEventBuilder.buildPreflightSpaceAnonymousAccess(checkResult.success, SpaceAnonymousPermissionChecker.retrieveSpaceWithAnonymousAccessCount(checkResult.details), totalTime);
    }

    @Override
    public String getFailedToExecuteAnalyticsEventName() {
        return "spaceAnonymousPermissionCheck";
    }
}


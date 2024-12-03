/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.cmpt.check.mapper.CheckResultMapper
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  io.atlassian.util.concurrent.ThreadFactories
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service.check.licence;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.mapper.CheckResultMapper;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.migration.agent.service.FileServiceManager;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.catalogue.MigrationCatalogueStorageService;
import com.atlassian.migration.agent.service.catalogue.PlatformService;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.CheckRegistration;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.licence.LicenceCheckContext;
import com.atlassian.migration.agent.service.check.licence.LicenceCheckContextProvider;
import com.atlassian.migration.agent.service.check.licence.LicenceCheckMapper;
import com.atlassian.migration.agent.service.check.licence.LicenceChecker;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.user.UserMigrationViaEGService;
import com.atlassian.migration.agent.service.user.UsersMigrationRequestBuilder;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.concurrent.Executors;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class LicenceCheckRegistration
implements CheckRegistration<LicenceCheckContext> {
    private final LicenceChecker checker;
    private final LicenceCheckContextProvider contextProvider;
    private final LicenceCheckMapper resultMapper;
    private final AnalyticsEventBuilder analyticsEventBuilder;

    public LicenceCheckRegistration(AnalyticsEventBuilder analyticsEventBuilder, CloudSiteService cloudSiteService, UsersMigrationRequestBuilder usersMigrationRequestBuilder, PlatformService platformService, MigrationCatalogueStorageService migrationCatalogueStorageService, UserMigrationViaEGService userMigrationViaEGService, FileServiceManager fileServiceManager, SystemInformationService systemInformationService) {
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.checker = new LicenceChecker(cloudSiteService, platformService, migrationCatalogueStorageService, userMigrationViaEGService, Executors.newCachedThreadPool(ThreadFactories.namedThreadFactory((String)this.getClass().getName())), fileServiceManager, systemInformationService);
        this.resultMapper = new LicenceCheckMapper();
        this.contextProvider = new LicenceCheckContextProvider(usersMigrationRequestBuilder);
    }

    @Override
    public CheckType getCheckType() {
        return CheckType.CLOUD_FREE_USERS_CONFLICT;
    }

    @Override
    public Checker<LicenceCheckContext> getChecker() {
        return this.checker;
    }

    @Override
    public CheckContextProvider<LicenceCheckContext> getCheckContextProvider() {
        return this.contextProvider;
    }

    @Override
    public CheckResultMapper getCheckResultMapper() {
        return this.resultMapper;
    }

    @Override
    public EventDto getAnalyticsEventModel(CheckResult checkResult, long totalTime) {
        return this.analyticsEventBuilder.buildPreflightCloudFreeUsersCheck(checkResult.success, totalTime);
    }

    @Override
    public String getFailedToExecuteAnalyticsEventName() {
        return "cloudFreeUsersConflictCheck";
    }
}


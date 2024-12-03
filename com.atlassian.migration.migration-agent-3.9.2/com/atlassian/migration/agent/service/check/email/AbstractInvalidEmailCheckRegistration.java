/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.cmpt.check.email.EmailFormatChecker
 *  com.atlassian.cmpt.check.mapper.CheckResultMapper
 *  com.atlassian.cmpt.check.mapper.EmailFormatDataProvider
 *  com.atlassian.cmpt.check.mapper.EmailFormatMapper
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo
 *  io.atlassian.util.concurrent.ThreadFactories
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.atlassian.migration.agent.service.check.email;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.email.EmailFormatChecker;
import com.atlassian.cmpt.check.mapper.CheckResultMapper;
import com.atlassian.cmpt.check.mapper.EmailFormatDataProvider;
import com.atlassian.cmpt.check.mapper.EmailFormatMapper;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.service.FileServiceManager;
import com.atlassian.migration.agent.service.ObjectStorageService;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.catalogue.MigrationCatalogueStorageService;
import com.atlassian.migration.agent.service.catalogue.PlatformService;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.CheckRegistration;
import com.atlassian.migration.agent.service.check.email.InvalidEmailCheckContext;
import com.atlassian.migration.agent.service.check.email.InvalidEmailCheckContextProvider;
import com.atlassian.migration.agent.service.check.email.InvalidEmailChecker;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.user.UserMigrationViaEGService;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.Map;
import java.util.concurrent.Executors;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.web.util.UriComponentsBuilder;

@ParametersAreNonnullByDefault
abstract class AbstractInvalidEmailCheckRegistration
implements CheckRegistration<InvalidEmailCheckContext> {
    private static final String PATH = "/admin/users/edituser.action";
    private final InvalidEmailChecker checker;
    private final InvalidEmailCheckContextProvider contextProvider;
    private final EmailFormatMapper resultMapper;
    private final AnalyticsEventBuilder analyticsEventBuilder;

    AbstractInvalidEmailCheckRegistration(InvalidEmailCheckContextProvider contextProvider, SystemInformationService systemInformationService, AnalyticsEventBuilder analyticsEventBuilder, PlatformService platformService, CloudSiteService cloudSiteService, MigrationCatalogueStorageService migrationCatalogueStorageService, UserMigrationViaEGService userMigrationViaEGService, FileServiceManager fileServiceManager, ObjectStorageService objectStorageService, MigrationDarkFeaturesManager darkFeaturesManager) {
        this.contextProvider = contextProvider;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.checker = new InvalidEmailChecker(platformService, cloudSiteService, migrationCatalogueStorageService, userMigrationViaEGService, Executors.newCachedThreadPool(ThreadFactories.namedThreadFactory((String)this.getClass().getName())), fileServiceManager, objectStorageService, darkFeaturesManager);
        ConfluenceInfo confluenceInfo = systemInformationService.getConfluenceInfo();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl((String)confluenceInfo.getBaseUrl()).path(PATH);
        this.resultMapper = this.buildEmailFormatMapper(username -> uriBuilder.replaceQueryParam("username", new Object[]{username}).toUriString());
    }

    abstract EmailFormatMapper buildEmailFormatMapper(EmailFormatDataProvider var1);

    @Override
    public Checker<InvalidEmailCheckContext> getChecker() {
        return this.checker;
    }

    @Override
    public CheckResultMapper getCheckResultMapper() {
        return this.resultMapper;
    }

    @Override
    public CheckContextProvider<InvalidEmailCheckContext> getCheckContextProvider() {
        return this.contextProvider;
    }

    @Override
    public EventDto getAnalyticsEventModel(CheckResult checkResult, long totalTime) {
        return this.analyticsEventBuilder.buildPreflightInvalidEmails(checkResult.success, EmailFormatChecker.retrieveInvalidEmails((Map)checkResult.details), totalTime);
    }

    @Override
    public String getFailedToExecuteAnalyticsEventName() {
        return "invalidEmailsCheck";
    }
}


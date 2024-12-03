/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.dto.CheckResultDto
 *  com.atlassian.cmpt.check.dto.Status
 *  com.atlassian.cmpt.check.mapper.EmailFormatDataProvider
 *  com.atlassian.cmpt.check.mapper.EmailFormatMapper
 *  com.atlassian.confluence.status.service.SystemInformationService
 */
package com.atlassian.migration.agent.service.check.email;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.dto.Status;
import com.atlassian.cmpt.check.mapper.EmailFormatDataProvider;
import com.atlassian.cmpt.check.mapper.EmailFormatMapper;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.service.FileServiceManager;
import com.atlassian.migration.agent.service.ObjectStorageService;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.catalogue.MigrationCatalogueStorageService;
import com.atlassian.migration.agent.service.catalogue.PlatformService;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.email.AbstractInvalidEmailCheckRegistration;
import com.atlassian.migration.agent.service.check.email.InvalidEmailCheckContextProvider;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.user.UserMigrationViaEGService;

public class SpacesInvalidEmailCheckRegistration
extends AbstractInvalidEmailCheckRegistration {
    public SpacesInvalidEmailCheckRegistration(InvalidEmailCheckContextProvider contextProvider, SystemInformationService systemInformationService, AnalyticsEventBuilder analyticsEventBuilder, PlatformService platformService, CloudSiteService cloudSiteService, MigrationCatalogueStorageService migrationCatalogueStorageService, UserMigrationViaEGService userMigrationViaEGService, FileServiceManager fileServiceManager, ObjectStorageService objectStorageService, MigrationDarkFeaturesManager darkFeaturesManager) {
        super(contextProvider, systemInformationService, analyticsEventBuilder, platformService, cloudSiteService, migrationCatalogueStorageService, userMigrationViaEGService, fileServiceManager, objectStorageService, darkFeaturesManager);
    }

    @Override
    EmailFormatMapper buildEmailFormatMapper(EmailFormatDataProvider emailFormatDataProvider) {
        return new EmailFormatMapper(emailFormatDataProvider){

            public void inject(CheckResultDto dto, CheckResult checkResult) {
                super.inject(dto, checkResult);
                if (dto.getStatus() == Status.ERROR) {
                    dto.setStatus(Status.WARNING);
                }
            }
        };
    }

    @Override
    public CheckType getCheckType() {
        return CheckType.SPACES_INVALID_EMAILS;
    }
}


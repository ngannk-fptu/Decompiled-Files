/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.email.EmailData
 *  io.atlassian.util.concurrent.ThreadFactories
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.email;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.email.EmailData;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.service.FileServiceManager;
import com.atlassian.migration.agent.service.ObjectStorageService;
import com.atlassian.migration.agent.service.catalogue.MigrationCatalogueStorageService;
import com.atlassian.migration.agent.service.catalogue.PlatformService;
import com.atlassian.migration.agent.service.check.email.InvalidEmailCheckContext;
import com.atlassian.migration.agent.service.check.email.InvalidEmailChecker;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.impl.MigrationUser;
import com.atlassian.migration.agent.service.user.MigrationUserDto;
import com.atlassian.migration.agent.service.user.UserMigrationViaEGService;
import com.atlassian.migration.agent.service.user.request.v2.UsersMigrationV2FilePayload;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class InvalidEmailValidator {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(InvalidEmailValidator.class);
    private final InvalidEmailChecker checker;

    public InvalidEmailValidator(PlatformService platformService, CloudSiteService cloudSiteService, MigrationCatalogueStorageService migrationCatalogueStorageService, UserMigrationViaEGService userMigrationViaEGService, FileServiceManager fileServiceManager, ObjectStorageService objectStorageService, MigrationDarkFeaturesManager darkFeaturesManager) {
        this.checker = new InvalidEmailChecker(platformService, cloudSiteService, migrationCatalogueStorageService, userMigrationViaEGService, Executors.newCachedThreadPool(ThreadFactories.namedThreadFactory((String)this.getClass().getName())), fileServiceManager, objectStorageService, darkFeaturesManager);
    }

    private CheckResult validateInvalidEmails(String executionId, String cloudId, Collection<MigrationUser> users) {
        return this.checker.check(this.buildContext(executionId, cloudId, users));
    }

    private InvalidEmailCheckContext buildContext(String executionId, String cloudId, Collection<MigrationUser> users) {
        List<MigrationUserDto> migrationUserDtoList = users.stream().map(MigrationUserDto::from).collect(Collectors.toList());
        return new InvalidEmailCheckContext(cloudId, executionId, Collections.emptySet(), new UsersMigrationV2FilePayload(migrationUserDtoList, Collections.emptyList(), Collections.emptyMap()));
    }

    public List<EmailData> getInvalidEmails(String scanId, String cloudId, Collection<MigrationUser> allUsers) {
        CheckResult checkResult = this.validateInvalidEmails(scanId, cloudId, allUsers);
        if (checkResult.details.containsKey("executionErrorDetails")) {
            throw new IllegalStateException("Error while validating invalid emails, code: " + checkResult.details.get("executionErrorDetails"));
        }
        return checkResult.details.getOrDefault("violations", Collections.emptyList());
    }
}


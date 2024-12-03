/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.cmpt.check.email.EmailData
 *  com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator
 *  com.google.common.annotations.VisibleForTesting
 *  lombok.Generated
 *  org.codehaus.jackson.type.TypeReference
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.check.email;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.email.EmailData;
import com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.okhttp.HttpException;
import com.atlassian.migration.agent.service.FileServiceManager;
import com.atlassian.migration.agent.service.ObjectStorageService;
import com.atlassian.migration.agent.service.PreflightErrorCode;
import com.atlassian.migration.agent.service.catalogue.MigrationCatalogueStorageService;
import com.atlassian.migration.agent.service.catalogue.PlatformService;
import com.atlassian.migration.agent.service.catalogue.model.MigrationCatalogueStorageFile;
import com.atlassian.migration.agent.service.check.email.InvalidEmailCheckContext;
import com.atlassian.migration.agent.service.check.email.InvalidEmailCheckRequest;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.user.EmailCheckStatusResponse;
import com.atlassian.migration.agent.service.user.EmailError;
import com.atlassian.migration.agent.service.user.InvalidEmail;
import com.atlassian.migration.agent.service.user.InvalidEmailsResponse;
import com.atlassian.migration.agent.service.user.MigrationUserDto;
import com.atlassian.migration.agent.service.user.UserMigrationViaEGService;
import com.google.common.annotations.VisibleForTesting;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import lombok.Generated;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;

public class InvalidEmailChecker
implements Checker<InvalidEmailCheckContext> {
    private final PlatformService platformService;
    private final CloudSiteService cloudSiteService;
    private final MigrationCatalogueStorageService migrationCatalogueStorageService;
    private final UserMigrationViaEGService userMigrationViaEGService;
    private final ExecutorService executorService;
    private final FileServiceManager fileServiceManager;
    private final ObjectStorageService objectStorageService;
    private final MigrationDarkFeaturesManager darkFeaturesManager;
    private static final Logger log = ContextLoggerFactory.getLogger(InvalidEmailChecker.class);
    public static final String VIOLATIONS_KEY = "violations";
    private static final String FILE_PATH = "users";

    public CheckResult check(InvalidEmailCheckContext ctx) {
        String cloudId = ctx.getCloudId();
        Optional<CloudSite> cloudSite = this.cloudSiteService.getByCloudId(cloudId);
        if (!cloudSite.isPresent()) {
            PreflightErrorCode errorCode = PreflightErrorCode.CLOUD_ERROR;
            log.error("Error code- {} : {}. Cloud id: {}", new Object[]{errorCode.getCode(), errorCode.getMessage(), cloudId});
            return Checker.buildCheckResultWithExecutionError((int)errorCode.getCode());
        }
        CloudSite cloudSiteEntity = cloudSite.get();
        try {
            log.info("Performing invalid emails check via UMS");
            return this.getCheckResult(ctx, cloudId, cloudSiteEntity);
        }
        catch (Exception e) {
            log.error("An error occurred when checking for invalid emails for cloudId: {}", (Object)cloudId, (Object)e);
            return Checker.buildCheckResultWithExecutionError((int)PreflightErrorCode.GENERIC_ERROR.getCode());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private CheckResult getCheckResult(InvalidEmailCheckContext ctx, String cloudId, CloudSite cloudSiteEntity) throws ExecutionException, InterruptedException, TimeoutException {
        InvalidEmailsResponse invalidEmailsResponse;
        String downloadUrl;
        String fileId;
        String taskId;
        MigrationCatalogueStorageFile migrationCatalogueStorageFile;
        String migrationScopeId;
        try {
            migrationScopeId = this.platformService.createMigrationScopeInMcs(cloudSiteEntity);
        }
        catch (HttpException e) {
            log.error("An error occurred when creating migration scope id for cloudId: {}", (Object)cloudId);
            return Checker.buildCheckResultWithExecutionError((int)PreflightErrorCode.MIGRATION_SCOPE_CREATION_ERROR.getCode());
        }
        String executionId = ctx.getExecutionId();
        Path savedFile = this.fileServiceManager.saveToFileInSharedHome(FILE_PATH, executionId, ctx.getPayload());
        try {
            log.info("Uploading file: {} for cloudId: {} and migrationScopeId: {}", new Object[]{savedFile, cloudId, migrationScopeId});
            migrationCatalogueStorageFile = this.migrationCatalogueStorageService.uploadFileToMCSByMigrationScopeId(cloudId, migrationScopeId, savedFile);
        }
        catch (HttpException e) {
            PreflightErrorCode errorCode = PreflightErrorCode.UPLOAD_FILE_TO_MCS_ERROR;
            log.error("Error code- {} : {}. Cloud id: {}", new Object[]{errorCode.getCode(), errorCode.getMessage(), cloudId});
            CheckResult checkResult = Checker.buildCheckResultWithExecutionError((int)errorCode.getCode());
            return checkResult;
        }
        finally {
            this.fileServiceManager.cleanUp(savedFile);
        }
        try {
            taskId = this.userMigrationViaEGService.startEmailCheck(cloudId, migrationScopeId, new InvalidEmailCheckRequest(migrationCatalogueStorageFile.getFileId()));
        }
        catch (HttpException e) {
            PreflightErrorCode errorCode = PreflightErrorCode.INVALID_EMAILS_CHECK_ERROR;
            log.error("Error code- {} : {}. Cloud id: {}", new Object[]{errorCode.getCode(), errorCode.getMessage(), cloudId});
            return Checker.buildCheckResultWithExecutionError((int)errorCode.getCode());
        }
        Future<EmailCheckStatusResponse> future = null;
        try {
            future = this.executorService.submit(() -> {
                EmailCheckStatusResponse response;
                while (!(response = this.userMigrationViaEGService.getEmailCheckStatus(cloudId, migrationScopeId, taskId)).isComplete()) {
                    try {
                        this.doSleep(2500);
                    }
                    catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                return response;
            });
            EmailCheckStatusResponse emailCheckStatusResponse = future.get(5L, TimeUnit.MINUTES);
            Optional<CheckResult> checkResultFromStatusResponse = InvalidEmailChecker.getCheckResultFromStatusResponse(emailCheckStatusResponse);
            if (checkResultFromStatusResponse.isPresent()) {
                CheckResult checkResult = checkResultFromStatusResponse.get();
                return checkResult;
            }
            fileId = emailCheckStatusResponse.getEmailValidationResult().getFileId();
        }
        finally {
            if (future != null) {
                future.cancel(true);
            }
        }
        try {
            downloadUrl = this.migrationCatalogueStorageService.getFileDownloadUrlFromMCSByMigrationScopeId(cloudId, migrationScopeId, fileId);
        }
        catch (HttpException e) {
            PreflightErrorCode errorCode = PreflightErrorCode.GET_DOWNLOAD_URL_FOR_INVALID_EMAIL_CHECKS_ERROR;
            log.error("Error code- {} : {}. Cloud id: {}", new Object[]{errorCode.getCode(), errorCode.getMessage(), cloudId});
            return Checker.buildCheckResultWithExecutionError((int)errorCode.getCode());
        }
        try {
            invalidEmailsResponse = (InvalidEmailsResponse)this.objectStorageService.download(downloadUrl, new TypeReference<InvalidEmailsResponse>(){});
        }
        catch (HttpException e) {
            PreflightErrorCode errorCode = PreflightErrorCode.DOWNLOADING_INVALID_EMAIL_CHECKS_RESULT;
            log.error("Error code- {} : {}. Cloud id: {}", new Object[]{errorCode.getCode(), errorCode.getMessage(), cloudId});
            return Checker.buildCheckResultWithExecutionError((int)PreflightErrorCode.DOWNLOADING_INVALID_EMAIL_CHECKS_RESULT.getCode());
        }
        Set<String> invalidEmails = invalidEmailsResponse.getInvalidEmails().stream().map(InvalidEmail::getEmail).collect(Collectors.toSet());
        Map<String, List<String>> emailToUserNames = ctx.getPayload().getUsers().stream().collect(Collectors.groupingBy(v -> IdentityAcceptedEmailValidator.cleanse((String)v.getEmail()), Collectors.mapping(MigrationUserDto::getUserName, Collectors.toList())));
        List<EmailData> emailViolations = this.getEmailViolations(emailToUserNames, invalidEmails);
        return new CheckResult(false, Collections.singletonMap(VIOLATIONS_KEY, emailViolations));
    }

    private List<EmailData> getEmailViolations(Map<String, List<String>> emailToUserNames, Set<String> invalidEmails) {
        ArrayList<EmailData> violations = new ArrayList<EmailData>();
        for (String invalidEmail : invalidEmails) {
            List<String> userNames = emailToUserNames.get(invalidEmail);
            violations.addAll(userNames.stream().map(userName -> new EmailData(userName, invalidEmail)).collect(Collectors.toList()));
        }
        return violations;
    }

    private static Optional<CheckResult> getCheckResultFromStatusResponse(EmailCheckStatusResponse emailCheckStatusResponse) {
        if (!emailCheckStatusResponse.getErrors().isEmpty()) {
            List<EmailError> umsErrors = emailCheckStatusResponse.getErrors();
            List umsErrorCodes = umsErrors.stream().map(EmailError::getCode).collect(Collectors.toList());
            List umsErrorMessages = umsErrors.stream().filter(Objects::nonNull).map(EmailError::getMessage).collect(Collectors.toList());
            log.error("Error codes received from upstream for invalid email check: {}, Error messages: {}", umsErrorCodes, umsErrorMessages);
            return Optional.of(Checker.buildCheckResultWithExecutionError((int)PreflightErrorCode.INVALID_EMAILS_CHECK_STATUS_ERROR.getCode()));
        }
        if (emailCheckStatusResponse.getEmailValidationResult().getInvalidEmailsCount() == 0) {
            return Optional.of(new CheckResult(true, Collections.singletonMap(VIOLATIONS_KEY, Collections.emptyList())));
        }
        return Optional.empty();
    }

    @VisibleForTesting
    public void doSleep(int milliseconds) throws InterruptedException {
        Thread.sleep(milliseconds);
    }

    @Generated
    public InvalidEmailChecker(PlatformService platformService, CloudSiteService cloudSiteService, MigrationCatalogueStorageService migrationCatalogueStorageService, UserMigrationViaEGService userMigrationViaEGService, ExecutorService executorService, FileServiceManager fileServiceManager, ObjectStorageService objectStorageService, MigrationDarkFeaturesManager darkFeaturesManager) {
        this.platformService = platformService;
        this.cloudSiteService = cloudSiteService;
        this.migrationCatalogueStorageService = migrationCatalogueStorageService;
        this.userMigrationViaEGService = userMigrationViaEGService;
        this.executorService = executorService;
        this.fileServiceManager = fileServiceManager;
        this.objectStorageService = objectStorageService;
        this.darkFeaturesManager = darkFeaturesManager;
    }
}


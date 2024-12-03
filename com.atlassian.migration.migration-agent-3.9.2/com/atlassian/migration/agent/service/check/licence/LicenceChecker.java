/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.cmpt.check.dto.Status
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  org.slf4j.Logger
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.atlassian.migration.agent.service.check.licence;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.dto.Status;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.okhttp.ErrorResponse;
import com.atlassian.migration.agent.okhttp.HttpException;
import com.atlassian.migration.agent.service.FileServiceManager;
import com.atlassian.migration.agent.service.PreflightErrorCode;
import com.atlassian.migration.agent.service.catalogue.MigrationCatalogueStorageService;
import com.atlassian.migration.agent.service.catalogue.PlatformService;
import com.atlassian.migration.agent.service.catalogue.model.MigrationCatalogueStorageFile;
import com.atlassian.migration.agent.service.check.licence.LicenceCheckContext;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.user.LicenceCheckRequest;
import com.atlassian.migration.agent.service.user.LicenceCheckResult;
import com.atlassian.migration.agent.service.user.LicenceCheckResultStatus;
import com.atlassian.migration.agent.service.user.LicenceCheckStatusResponse;
import com.atlassian.migration.agent.service.user.UserMigrationViaEGService;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.springframework.web.util.UriComponentsBuilder;

public class LicenceChecker
implements Checker<LicenceCheckContext> {
    private static final Logger log = ContextLoggerFactory.getLogger(LicenceChecker.class);
    private static final String USERS_LIMIT_KEY = "usersLimit";
    private static final String LICENCE_TYPE_KEY = "licenceType";
    private static final String REQUESTED_LICENCE_SEATS_KEY = "requestedLicenceSeats";
    private static final String AVAILABLE_LICENCE_SEATS_KEY = "availableLicenceSeats";
    private static final String STATUS_KEY = "status";
    private static final String CLOUD_PLANS_KEY = "cloudPlans";
    private static final String USERS_MGT_CLOUD_KEY = "usersManagementInCloud";
    private static final String CLOUD_UPGRADE_KEY = "cloudPlanUpgrade";
    private static final String USERS_MGT_SERVER_KEY = "usersManagementInServer";
    private static final String LINKS_KEY = "links";
    private static final String ADMIN_ATLASSIAN_PROD_URL = "https://admin.atlassian.com/s/";
    private static final String ADMIN_ATLASSIAN_STAGING_URL = "https://admin.stg.atlassian.com/s/";
    private static final String USERS_PAGE_PATH = "/admin/users/showallusers.action";
    private final String serverBaseUrl;
    private final CloudSiteService cloudSiteService;
    private final PlatformService platformService;
    private final MigrationCatalogueStorageService migrationCatalogueStorageService;
    private final UserMigrationViaEGService userMigrationViaEGService;
    private final ExecutorService executorService;
    private final FileServiceManager fileServiceManager;
    private static final String FILE_PATH = "license";

    public LicenceChecker(CloudSiteService cloudSiteService, PlatformService platformService, MigrationCatalogueStorageService migrationCatalogueStorageService, UserMigrationViaEGService userMigrationViaEGService, ExecutorService executorService, FileServiceManager fileServiceManager, SystemInformationService systemInformationService) {
        this.cloudSiteService = cloudSiteService;
        this.platformService = platformService;
        this.migrationCatalogueStorageService = migrationCatalogueStorageService;
        this.userMigrationViaEGService = userMigrationViaEGService;
        this.executorService = executorService;
        this.fileServiceManager = fileServiceManager;
        this.serverBaseUrl = systemInformationService.getConfluenceInfo().getBaseUrl();
    }

    public CheckResult check(LicenceCheckContext ctx) {
        String cloudId = ctx.getCloudId();
        Optional<CloudSite> cloudSite = this.cloudSiteService.getByCloudId(cloudId);
        if (!cloudSite.isPresent()) {
            PreflightErrorCode errorCode = PreflightErrorCode.CLOUD_ERROR;
            log.error("Error code- {} : {} Cloud id: {}", new Object[]{errorCode.getCode(), errorCode.getMessage(), cloudId});
            return Checker.buildCheckResultWithExecutionError((int)errorCode.getCode());
        }
        CloudSite cloudSiteEntity = cloudSite.get();
        try {
            log.info("Performing licence check via UMS");
            return this.getCheckResult(ctx, cloudId, cloudSiteEntity);
        }
        catch (Exception e) {
            log.error("An error occurred when checking for licence for cloudId: {}", (Object)cloudId, (Object)e);
            return Checker.buildCheckResultWithExecutionError((int)PreflightErrorCode.GENERIC_ERROR.getCode());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private CheckResult getCheckResult(LicenceCheckContext ctx, String cloudId, CloudSite cloudSiteEntity) throws ExecutionException, InterruptedException, TimeoutException {
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
            taskId = this.userMigrationViaEGService.startLicenceCheck(cloudId, migrationScopeId, new LicenceCheckRequest(migrationCatalogueStorageFile.getFileId()));
        }
        catch (HttpException e) {
            PreflightErrorCode errorCode = PreflightErrorCode.LICENSE_CHECK_ERROR;
            log.error("Error code- {} : {}. Cloud id: {}", new Object[]{errorCode.getCode(), errorCode.getMessage(), cloudId});
            return Checker.buildCheckResultWithExecutionError((int)errorCode.getCode());
        }
        Future<LicenceCheckStatusResponse> future = null;
        try {
            future = this.executorService.submit(() -> {
                LicenceCheckStatusResponse response;
                while (!(response = this.userMigrationViaEGService.getLicenceCheckStatus(cloudId, migrationScopeId, taskId)).isComplete()) {
                    try {
                        this.doSleep(2500);
                    }
                    catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                return response;
            });
            LicenceCheckStatusResponse licenceCheckStatusResponse = future.get(5L, TimeUnit.MINUTES);
            if (!licenceCheckStatusResponse.getErrors().isEmpty()) {
                PreflightErrorCode errorCode = PreflightErrorCode.LICENSE_CHECK_STATUS_ERROR;
                log.error("Error code- {} : {}. Cloud id: {}.", new Object[]{errorCode.getCode(), errorCode.getMessage(), cloudId});
                List<ErrorResponse> umsErrors = licenceCheckStatusResponse.getErrors();
                List umsErrorCodes = umsErrors.stream().map(error -> error.code).collect(Collectors.toList());
                List umsErrorMessages = umsErrors.stream().filter(Objects::nonNull).map(error -> error.message).collect(Collectors.toList());
                log.error("Error codes received from upstream for licence check: {}, Error messages: {}", umsErrorCodes, umsErrorMessages);
                CheckResult checkResult = Checker.buildCheckResultWithExecutionError((int)errorCode.getCode());
                return checkResult;
            }
            LicenceCheckResult licenceCheckResult = licenceCheckStatusResponse.getLicencesCheckResult().get(0);
            if (licenceCheckResult.getResult() == LicenceCheckResultStatus.TRUE) {
                CheckResult umsErrors = new CheckResult(true);
                return umsErrors;
            }
            Map<String, Object> details = this.createResultDetails(cloudId, cloudSiteEntity.getCloudUrl(), licenceCheckResult);
            CheckResult checkResult = new CheckResult(false, details);
            return checkResult;
        }
        finally {
            if (future != null) {
                future.cancel(true);
            }
        }
    }

    private Map<String, Object> createResultDetails(String cloudId, String cloudUrl, LicenceCheckResult licenceCheckResult) {
        HashMap<String, Object> details = new HashMap<String, Object>();
        details.put(USERS_LIMIT_KEY, licenceCheckResult.getUsersCount());
        details.put(LICENCE_TYPE_KEY, licenceCheckResult.getEdition());
        details.put(AVAILABLE_LICENCE_SEATS_KEY, licenceCheckResult.getAvailableLicenceSeats());
        details.put(REQUESTED_LICENCE_SEATS_KEY, licenceCheckResult.getRequestedLicenceSeats());
        String cloudUrlPath = LicenceChecker.isCloudUrlInProduction(cloudUrl) ? ADMIN_ATLASSIAN_PROD_URL : ADMIN_ATLASSIAN_STAGING_URL;
        details.put(STATUS_KEY, Status.ERROR);
        details.put(LINKS_KEY, ImmutableMap.of((Object)CLOUD_PLANS_KEY, (Object)"https://confluence.atlassian.com/confcloud/confluence-cloud-plans-972334171.html", (Object)USERS_MGT_CLOUD_KEY, (Object)UriComponentsBuilder.fromHttpUrl((String)cloudUrlPath).path(cloudId).path("/users").toUriString(), (Object)CLOUD_UPGRADE_KEY, (Object)UriComponentsBuilder.fromHttpUrl((String)cloudUrlPath).path(cloudId).path("/billing/applications/change-edition/confluence.ondemand").toUriString(), (Object)USERS_MGT_SERVER_KEY, (Object)UriComponentsBuilder.fromHttpUrl((String)this.serverBaseUrl).path(USERS_PAGE_PATH).toUriString()));
        return details;
    }

    static Integer retrieveUsersLimit(Map<String, Object> details) {
        return (Integer)details.getOrDefault(USERS_LIMIT_KEY, 0);
    }

    public static Integer retrieveAvailableLicenceSeats(Map<String, Object> details) {
        return (Integer)details.getOrDefault(AVAILABLE_LICENCE_SEATS_KEY, 0);
    }

    public static Integer retrieveRequestedLicenceSeats(Map<String, Object> details) {
        return (Integer)details.getOrDefault(REQUESTED_LICENCE_SEATS_KEY, 0);
    }

    public static String retrieveLicenceType(Map<String, Object> details) {
        return (String)details.getOrDefault(LICENCE_TYPE_KEY, "FREE");
    }

    private static boolean isCloudUrlInProduction(String cloudUrl) {
        return cloudUrl.endsWith("atlassian.net");
    }

    @VisibleForTesting
    public void doSleep(int milliseconds) throws InterruptedException {
        Thread.sleep(milliseconds);
    }
}


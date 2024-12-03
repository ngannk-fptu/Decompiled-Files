/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.migration.app.dto.AppWebhookEndpointCheckRequest
 *  com.atlassian.migration.app.dto.AppWebhookEndpointCheckResponse
 *  com.atlassian.migration.app.dto.AppWebhookEndpointCheckResponse$AppWebhookEndpointCheckResult
 *  com.atlassian.migration.app.dto.MigrationPath
 *  com.google.common.collect.ImmutableMap
 *  lombok.Generated
 *  lombok.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.check.app.webhook;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.migration.agent.okhttp.HttpException;
import com.atlassian.migration.agent.service.MigrationAppAggregatorResponse;
import com.atlassian.migration.agent.service.PreflightErrorCode;
import com.atlassian.migration.agent.service.catalogue.PlatformService;
import com.atlassian.migration.agent.service.check.app.webhook.AppWebhookEndpointCheckContext;
import com.atlassian.migration.agent.service.check.app.webhook.AppWebhookEndpointCheckResultDto;
import com.atlassian.migration.agent.service.check.app.webhook.AppWebhookEndpointCheckServiceClient;
import com.atlassian.migration.agent.service.impl.MigrationAppAggregatorService;
import com.atlassian.migration.app.AbstractCloudMigrationRegistrar;
import com.atlassian.migration.app.dto.AppWebhookEndpointCheckRequest;
import com.atlassian.migration.app.dto.AppWebhookEndpointCheckResponse;
import com.atlassian.migration.app.dto.MigrationPath;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Generated;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppWebhookEndpointChecker
implements Checker<AppWebhookEndpointCheckContext> {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AppWebhookEndpointChecker.class);
    @NonNull
    private final AppWebhookEndpointCheckServiceClient appWebhookEndpointCheckServiceClient;
    @NonNull
    private final PlatformService platformService;
    @NonNull
    private final AbstractCloudMigrationRegistrar cloudMigrationRegistrar;
    @NonNull
    private final MigrationAppAggregatorService appAggregatorService;
    public static final String MISSING_WEBHOOKS_CHECK_RESULT_DETAILS_KEY = "appKeysMissingWebhooks";
    public static final String DEFAULT_STEPS_TO_RESOLVE_KEY = "stepsToResolve";
    private AppWebhookEndpointCheckContext appWebhookEndpointCheckContext;

    public CheckResult check(AppWebhookEndpointCheckContext ctx) {
        this.setAppWebhookEndpointCheckContext(ctx);
        Set<Object> appKeysWithMissingWebhooks = new HashSet();
        AppWebhookEndpointCheckResponse appWebhookEndpointCheckResponse = new AppWebhookEndpointCheckResponse(Collections.emptySet(), "");
        PreflightErrorCode errorCode = null;
        try {
            Set cloudKeys = ctx.appKeys.stream().filter(appKey -> this.appAggregatorService.getCachedServerAppData((String)appKey).getMigrationPath().equals((Object)MigrationPath.AUTOMATED)).flatMap(appKey -> this.cloudMigrationRegistrar.getRegisteredCloudKeys((String)appKey).stream()).collect(Collectors.toSet());
            if (cloudKeys.isEmpty()) {
                return new CheckResult(true);
            }
            AppWebhookEndpointCheckRequest request = new AppWebhookEndpointCheckRequest(cloudKeys, "confluence", this.platformService.getHosting().name());
            appWebhookEndpointCheckResponse = this.appWebhookEndpointCheckServiceClient.retrieveRegisteredWebhooks(ctx.cloudId, request);
            Set appWebhookEndpointCheckResults = appWebhookEndpointCheckResponse.getAppEndpoints();
            if (appWebhookEndpointCheckResults.size() == 1 && appWebhookEndpointCheckResults.contains(new AppWebhookEndpointCheckResponse.AppWebhookEndpointCheckResult("APP_WEBHOOK_CHECK_DISABLED", true))) {
                log.warn("App webhook check is disabled in App migration service for cloud id: {}", (Object)ctx.cloudId);
                return new CheckResult(true);
            }
            appKeysWithMissingWebhooks = appWebhookEndpointCheckResults.stream().filter(appWebhookEndpointCheckResult -> !appWebhookEndpointCheckResult.getAvailable()).map(AppWebhookEndpointCheckResponse.AppWebhookEndpointCheckResult::getAppKey).collect(Collectors.toSet());
        }
        catch (HttpException e) {
            errorCode = PreflightErrorCode.APP_WEBHOOK_CHECK_ERROR;
            log.error("Error code- {}. {}", new Object[]{errorCode.getCode(), errorCode.getMessage(), e});
        }
        catch (Exception e) {
            errorCode = PreflightErrorCode.GENERIC_ERROR;
            log.error("Error executing apps webhook check.", (Throwable)e);
        }
        if (errorCode != null) {
            return Checker.buildCheckResultWithExecutionError((int)errorCode.getCode());
        }
        if (!appKeysWithMissingWebhooks.isEmpty()) {
            Set appMissingWebhookEndpointResult = appKeysWithMissingWebhooks.stream().map(appKey -> {
                MigrationAppAggregatorResponse cachedCloudAppDataResponse = this.appAggregatorService.getCachedCloudAppData((String)appKey);
                String cloudAppName = appKey;
                String contactSupportUrl = null;
                if (cachedCloudAppDataResponse != null) {
                    if (cachedCloudAppDataResponse.getName() != null) {
                        cloudAppName = cachedCloudAppDataResponse.getName();
                    }
                    if (cachedCloudAppDataResponse.getContactSupportUrl() != null) {
                        contactSupportUrl = cachedCloudAppDataResponse.getContactSupportUrl();
                    }
                }
                return new AppWebhookEndpointCheckResultDto(cloudAppName, (String)appKey, contactSupportUrl);
            }).collect(Collectors.toSet());
            return new CheckResult(false, (Map)ImmutableMap.of((Object)MISSING_WEBHOOKS_CHECK_RESULT_DETAILS_KEY, appMissingWebhookEndpointResult, (Object)DEFAULT_STEPS_TO_RESOLVE_KEY, (Object)appWebhookEndpointCheckResponse.getStepsToResolve()));
        }
        return new CheckResult(true);
    }

    private void setAppWebhookEndpointCheckContext(AppWebhookEndpointCheckContext ctx) {
        this.appWebhookEndpointCheckContext = ctx;
    }

    public AppWebhookEndpointCheckContext getAppWebhookEndpointCheckContext() {
        return this.appWebhookEndpointCheckContext;
    }

    @Generated
    public AppWebhookEndpointChecker(@NonNull AppWebhookEndpointCheckServiceClient appWebhookEndpointCheckServiceClient, @NonNull PlatformService platformService, @NonNull AbstractCloudMigrationRegistrar cloudMigrationRegistrar, @NonNull MigrationAppAggregatorService appAggregatorService) {
        if (appWebhookEndpointCheckServiceClient == null) {
            throw new NullPointerException("appWebhookEndpointCheckServiceClient is marked non-null but is null");
        }
        if (platformService == null) {
            throw new NullPointerException("platformService is marked non-null but is null");
        }
        if (cloudMigrationRegistrar == null) {
            throw new NullPointerException("cloudMigrationRegistrar is marked non-null but is null");
        }
        if (appAggregatorService == null) {
            throw new NullPointerException("appAggregatorService is marked non-null but is null");
        }
        this.appWebhookEndpointCheckServiceClient = appWebhookEndpointCheckServiceClient;
        this.platformService = platformService;
        this.cloudMigrationRegistrar = cloudMigrationRegistrar;
        this.appAggregatorService = appAggregatorService;
    }
}


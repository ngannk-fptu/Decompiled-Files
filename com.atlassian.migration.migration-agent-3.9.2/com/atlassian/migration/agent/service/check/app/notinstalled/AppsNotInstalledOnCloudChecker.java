/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.check.app.notinstalled;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.migration.agent.okhttp.HttpException;
import com.atlassian.migration.agent.service.MigrationAppAggregatorResponse;
import com.atlassian.migration.agent.service.PreflightErrorCode;
import com.atlassian.migration.agent.service.check.app.CloudAppKeyFetcher;
import com.atlassian.migration.agent.service.check.app.notinstalled.AppsNotInstalledOnCloudContext;
import com.atlassian.migration.agent.service.check.app.notinstalled.AppsNotInstalledOnCloudDto;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.impl.AppAssessmentFacade;
import com.atlassian.migration.agent.service.impl.MigrationAppAggregatorService;
import com.atlassian.migration.app.AppAssessmentClient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class AppsNotInstalledOnCloudChecker
implements Checker<AppsNotInstalledOnCloudContext> {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AppsNotInstalledOnCloudChecker.class);
    private final MigrationAppAggregatorService appAggregatorService;
    private final AppAssessmentClient appAssessmentClient;
    private final CloudSiteService cloudSiteService;
    private final AppAssessmentFacade appAssessmentFacade;
    private final CloudAppKeyFetcher cloudAppKeyFetcher;
    private static final String VIOLATIONS_KEY = "violations";

    public AppsNotInstalledOnCloudChecker(MigrationAppAggregatorService appAggregatorService, AppAssessmentClient appAssessmentClient, CloudSiteService cloudSiteService, AppAssessmentFacade appAssessmentFacade, CloudAppKeyFetcher cloudAppKeyFetcher) {
        this.appAggregatorService = appAggregatorService;
        this.appAssessmentClient = appAssessmentClient;
        this.cloudSiteService = cloudSiteService;
        this.appAssessmentFacade = appAssessmentFacade;
        this.cloudAppKeyFetcher = cloudAppKeyFetcher;
    }

    public CheckResult check(AppsNotInstalledOnCloudContext ctx) {
        if (ctx.appKeys.isEmpty()) {
            return new CheckResult(true);
        }
        if (!this.cloudSiteService.getByCloudId(ctx.cloudId).isPresent()) {
            PreflightErrorCode errorCode = PreflightErrorCode.CLOUD_ERROR;
            log.error("Error code- {} : {} Cloud id: {}", new Object[]{errorCode.getCode(), errorCode.getMessage(), ctx.cloudId});
            return Checker.buildCheckResultWithExecutionError((int)errorCode.getCode());
        }
        try {
            Set cloudKeys = ctx.appKeys.stream().map(this.cloudAppKeyFetcher::getCloudAppKey).collect(Collectors.toSet());
            List appsNotInstalledOnCloud = this.appAssessmentClient.getAppInfoForSite(ctx.cloudId, new ArrayList<String>(cloudKeys)).getApps().parallelStream().filter(app -> !app.getInstalled()).map(app -> this.getDto(app.getKey())).sorted(Comparator.comparing(AppsNotInstalledOnCloudDto::getName)).collect(Collectors.toList());
            return new CheckResult(appsNotInstalledOnCloud.isEmpty(), Collections.singletonMap(VIOLATIONS_KEY, appsNotInstalledOnCloud));
        }
        catch (HttpException e) {
            PreflightErrorCode errorCode = PreflightErrorCode.APP_INFO_ERROR;
            log.error("Error code- {}. {}", new Object[]{errorCode.getCode(), errorCode.getMessage(), e});
            return Checker.buildCheckResultWithExecutionError((int)errorCode.getCode());
        }
        catch (Exception e) {
            log.error("Error executing apps not installed on cloud check.", (Throwable)e);
            return Checker.buildCheckResultWithExecutionError((int)PreflightErrorCode.GENERIC_ERROR.getCode());
        }
    }

    private AppsNotInstalledOnCloudDto getDto(String appKey) {
        MigrationAppAggregatorResponse appInfo = this.appAggregatorService.getCachedCloudAppData(appKey);
        if (appInfo == null) {
            log.error("No cloud app aggregator data found for key {}", (Object)appKey);
            throw new RuntimeException(String.format("No cloud app aggregator data found for key %s", appKey));
        }
        return AppsNotInstalledOnCloudDto.builder().key(appKey).name(this.appAssessmentFacade.getAppName(appKey, appInfo)).url(appInfo.getCloudUrl()).build();
    }

    static List<AppsNotInstalledOnCloudDto> retrieveAppsNotInstalledOnCloud(Map<String, Object> details) {
        return details.getOrDefault(VIOLATIONS_KEY, Collections.emptyList());
    }
}


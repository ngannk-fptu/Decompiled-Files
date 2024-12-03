/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.migration.app.dto.AppsLicenseDetailDto
 *  com.atlassian.migration.app.dto.AppsLicenseResponseDto
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.check.app.license;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.migration.agent.okhttp.HttpException;
import com.atlassian.migration.agent.service.MigrationAppAggregatorResponse;
import com.atlassian.migration.agent.service.PreflightErrorCode;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.app.CloudAppKeyFetcher;
import com.atlassian.migration.agent.service.check.app.license.AppLicenseContext;
import com.atlassian.migration.agent.service.check.app.license.AppLicenseDto;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.impl.AppAssessmentFacade;
import com.atlassian.migration.agent.service.impl.MigrationAppAggregatorService;
import com.atlassian.migration.app.AppAssessmentClient;
import com.atlassian.migration.app.dto.AppsLicenseDetailDto;
import com.atlassian.migration.app.dto.AppsLicenseResponseDto;
import com.google.common.collect.ImmutableMap;
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
public class AppLicenseChecker
implements Checker<AppLicenseContext> {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AppLicenseChecker.class);
    private final MigrationAppAggregatorService appAggregatorService;
    private final AppAssessmentClient appAssessmentClient;
    private final CloudSiteService cloudSiteService;
    private final AppAssessmentFacade appAssessmentFacade;
    private final CloudAppKeyFetcher cloudAppKeyFetcher;
    private static final String VIOLATIONS_KEY = "violations";
    private static final String SUCCESSES_KEY = "successes";

    public AppLicenseChecker(MigrationAppAggregatorService appAggregatorService, AppAssessmentClient appAssessmentClient, CloudSiteService cloudSiteService, AppAssessmentFacade appAssessmentFacade, CloudAppKeyFetcher cloudAppKeyFetcher) {
        this.appAggregatorService = appAggregatorService;
        this.appAssessmentClient = appAssessmentClient;
        this.cloudSiteService = cloudSiteService;
        this.appAssessmentFacade = appAssessmentFacade;
        this.cloudAppKeyFetcher = cloudAppKeyFetcher;
    }

    public CheckResult check(AppLicenseContext ctx) {
        log.debug("Executing {} for appKeys {}", (Object)CheckType.APP_LICENSE_CHECK.value(), ctx.appKeys);
        if (!this.cloudSiteService.getByCloudId(ctx.cloudId).isPresent()) {
            PreflightErrorCode errorCode = PreflightErrorCode.CLOUD_ERROR;
            log.error("Error code- {} : {} Cloud id: {}", new Object[]{errorCode.getCode(), errorCode.getMessage(), ctx.cloudId});
            return new CheckResult(false, AppLicenseChecker.createAppsLicenseCheckDetails(errorCode.getCode()));
        }
        try {
            Set cloudKeys = ctx.appKeys.stream().map(this.cloudAppKeyFetcher::getCloudAppKey).collect(Collectors.toSet());
            AppsLicenseResponseDto response = this.appAssessmentClient.getAppsLicense(ctx.cloudId, new ArrayList<String>(cloudKeys));
            List appLicenseDetails = response.getAppLicenses();
            if (appLicenseDetails.size() == 1 && ((AppsLicenseDetailDto)appLicenseDetails.get(0)).getAppKey().equals("APP_LICENSE_CHECK_DISABLED")) {
                log.warn("App license check is disabled in App migration service for cloud id: {}", (Object)ctx.cloudId);
                return new CheckResult(true);
            }
            List<AppLicenseDto> appsNoLicense = appLicenseDetails.parallelStream().filter(app -> !app.getLicensed()).map(app -> this.getDto(app.getAppKey())).sorted(Comparator.comparing(AppLicenseDto::getName)).collect(Collectors.toList());
            Set<String> appsWithLicense = appLicenseDetails.parallelStream().filter(AppsLicenseDetailDto::getLicensed).map(AppsLicenseDetailDto::getAppKey).collect(Collectors.toSet());
            return new CheckResult(appsNoLicense.isEmpty(), AppLicenseChecker.createAppsLicenseCheckDetails(appsNoLicense, appsWithLicense));
        }
        catch (HttpException e) {
            PreflightErrorCode errorCode = PreflightErrorCode.APP_LICENSE_CHECK_ERROR;
            log.error("Error code- {}. {}", new Object[]{errorCode.getCode(), errorCode.getMessage(), e});
            return new CheckResult(false, AppLicenseChecker.createAppsLicenseCheckDetails(errorCode.getCode()));
        }
        catch (Exception e) {
            log.error("Error executing apps no license check.", (Throwable)e);
            return new CheckResult(false, AppLicenseChecker.createAppsLicenseCheckDetails(PreflightErrorCode.GENERIC_ERROR.getCode()));
        }
    }

    private AppLicenseDto getDto(String appKey) {
        MigrationAppAggregatorResponse appInfo = this.appAggregatorService.getCachedCloudAppData(appKey);
        if (appInfo == null) {
            log.warn("No cloud app aggregator data found for key {} returning appKey for name", (Object)appKey);
            return AppLicenseDto.builder().key(appKey).name(appKey).build();
        }
        return AppLicenseDto.builder().key(appKey).name(this.appAssessmentFacade.getAppName(appKey, appInfo)).url(appInfo.getCloudUrl()).build();
    }

    static List<AppLicenseDto> retrieveAppsNoLicenseViolations(Map<String, Object> details) {
        return details.getOrDefault(VIOLATIONS_KEY, Collections.emptyList());
    }

    static Set<String> retrieveAppsWithLicenses(Map<String, Object> details) {
        return (Set)((Object)details.getOrDefault(SUCCESSES_KEY, Collections.emptyList()));
    }

    static Map<String, Object> createAppsLicenseCheckDetails(List<AppLicenseDto> violations, Set<String> successes) {
        return ImmutableMap.of((Object)VIOLATIONS_KEY, violations, (Object)SUCCESSES_KEY, successes);
    }

    static Map<String, Object> createAppsLicenseCheckDetails(int errorCode) {
        return Collections.singletonMap("executionErrorDetails", errorCode);
    }
}


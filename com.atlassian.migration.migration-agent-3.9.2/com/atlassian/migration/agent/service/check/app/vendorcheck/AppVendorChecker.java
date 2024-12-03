/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.migration.app.check.Container
 *  com.atlassian.migration.app.check.MigrationPlanContext
 *  com.atlassian.migration.app.dto.check.AppPreflightCheckResponse
 *  com.atlassian.migration.app.dto.check.CheckStatus
 *  com.atlassian.migration.app.dto.check.CsvFileContent
 *  com.atlassian.migration.app.dto.check.ParentAppPreflightChecksResponse
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.check.app.vendorcheck;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.service.PreflightErrorCode;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.check.app.vendorcheck.AppVendorCheckContext;
import com.atlassian.migration.agent.service.check.app.vendorcheck.AppVendorCheckResultDto;
import com.atlassian.migration.agent.service.check.app.vendorcheck.SerializableCsvFileContentDto;
import com.atlassian.migration.agent.store.CloudSiteStore;
import com.atlassian.migration.app.AppPreflightExecutorImpl;
import com.atlassian.migration.app.ContainerType;
import com.atlassian.migration.app.check.Container;
import com.atlassian.migration.app.check.MigrationPlanContext;
import com.atlassian.migration.app.dto.check.AppPreflightCheckResponse;
import com.atlassian.migration.app.dto.check.CheckStatus;
import com.atlassian.migration.app.dto.check.CsvFileContent;
import com.atlassian.migration.app.dto.check.ParentAppPreflightChecksResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class AppVendorChecker
implements Checker<AppVendorCheckContext> {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AppVendorChecker.class);
    private final AppPreflightExecutorImpl appPreflightExecutor;
    private final CloudSiteStore cloudSiteStore;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;
    private final AnalyticsEventService analyticsEventService;
    private final AnalyticsEventBuilder analyticsEventBuilder;

    public AppVendorChecker(CloudSiteStore cloudSiteStore, AppPreflightExecutorImpl appPreflightExecutor, MigrationDarkFeaturesManager migrationDarkFeaturesManager, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder) {
        this.cloudSiteStore = cloudSiteStore;
        this.appPreflightExecutor = appPreflightExecutor;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
        this.analyticsEventService = analyticsEventService;
        this.analyticsEventBuilder = analyticsEventBuilder;
    }

    public CheckResult check(AppVendorCheckContext appVendorCheckContext) {
        if (this.migrationDarkFeaturesManager.appVendorCheckDisabled()) {
            log.info("Returning empty result as App vendor check is disabled.");
            return new CheckResult(true, Collections.emptyMap());
        }
        log.debug("Performing AppVendorCheck");
        Optional<MigrationPlanContext> optionalMigrationPlanContext = this.createMigrationPlanContext(appVendorCheckContext);
        if (!optionalMigrationPlanContext.isPresent()) {
            log.warn("Unable to create MigrationPlanContext from AppVendorCheckContext: {}", (Object)appVendorCheckContext);
            return new CheckResult(true, Collections.emptyMap());
        }
        try {
            Instant startTime = Instant.now();
            Set<ParentAppPreflightChecksResponse> parentAppPreflightChecksResponse = this.appPreflightExecutor.executePreflightChecks(optionalMigrationPlanContext.get(), appVendorCheckContext.appKeys);
            boolean overallParentAppVendorCheckResponseStatus = parentAppPreflightChecksResponse.stream().allMatch(allAppPreflightCheckResult -> allAppPreflightCheckResult.getAppPreflightCheckResponse().stream().allMatch(it -> it.getStatus().equals((Object)CheckStatus.SUCCESS)));
            Map<String, Object> appVendorCheckResult = this.getAppVendorCheckResult(parentAppPreflightChecksResponse);
            long totalTime = ChronoUnit.MILLIS.between(startTime, Instant.now());
            this.sendIndividualAppVendorCheckEvents(appVendorCheckResult, appVendorCheckContext.planId, appVendorCheckContext.planMigrationTag, totalTime);
            return new CheckResult(overallParentAppVendorCheckResponseStatus, appVendorCheckResult);
        }
        catch (Exception e) {
            log.error("Failed to run App Vendor Checks", (Throwable)e);
            return AppVendorChecker.getGenericErrorCheckResult();
        }
    }

    private static CheckResult getGenericErrorCheckResult() {
        int errorCode = PreflightErrorCode.GENERIC_ERROR.getCode();
        return Checker.buildCheckResultWithExecutionError((int)errorCode);
    }

    private Optional<MigrationPlanContext> createMigrationPlanContext(AppVendorCheckContext ctx) {
        Set containers = ctx.spaceKeys.stream().map(spaceKey -> new Container(spaceKey, ContainerType.ConfluenceSpace.toString())).collect(Collectors.toSet());
        return this.cloudSiteStore.getByCloudId(ctx.cloudId).map(cloudSite -> {
            try {
                return new MigrationPlanContext(ctx.planId, ctx.planName, new URL(cloudSite.getCloudUrl()), containers);
            }
            catch (MalformedURLException e) {
                log.warn("Retrieved cloudSiteUrl: {} for cloudId: {} is not a valid URL", new Object[]{cloudSite.getCloudUrl(), ctx.cloudId, e});
                return null;
            }
        });
    }

    private Map<String, Object> getAppVendorCheckResult(Set<ParentAppPreflightChecksResponse> parentAppVendorCheckResponse) {
        return parentAppVendorCheckResponse.stream().collect(Collectors.toMap(ParentAppPreflightChecksResponse::getServerAppKey, eachParentAppVendorCheckResponse -> eachParentAppVendorCheckResponse.getAppPreflightCheckResponse().stream().collect(Collectors.toMap(AppPreflightCheckResponse::getCheckId, appPreflightCheckResponse -> new AppVendorCheckResultDto(appPreflightCheckResponse.getTitle(), appPreflightCheckResponse.getStepsToResolve(), appPreflightCheckResponse.getStatus(), appPreflightCheckResponse.getCsvFileContent() != null, appPreflightCheckResponse.getCheckId(), this.getSerialisedCsvContent(appPreflightCheckResponse.getCsvFileContent()), appPreflightCheckResponse.getCheckDetails())))));
    }

    private SerializableCsvFileContentDto getSerialisedCsvContent(CsvFileContent csvFileContent) {
        if (csvFileContent != null) {
            return new SerializableCsvFileContentDto(csvFileContent.getColumnHeaders(), csvFileContent.getRows());
        }
        return new SerializableCsvFileContentDto(null, null);
    }

    private void sendIndividualAppVendorCheckEvents(Map<String, Object> appVendorCheckResult, String planId, String planMigrationTag, long totalTime) {
        log.info("Sending individual app vendor check events");
        try {
            List<EventDto> events = this.analyticsEventBuilder.buildPreflightAppVendorCheckIndividual(appVendorCheckResult, this.migrationDarkFeaturesManager.appMigrationDevMode(), planId, planMigrationTag, totalTime);
            this.analyticsEventService.saveAnalyticsEvents(() -> events);
        }
        catch (Exception e) {
            log.error("Failed to send individual app vendor check events", (Throwable)e);
        }
    }
}


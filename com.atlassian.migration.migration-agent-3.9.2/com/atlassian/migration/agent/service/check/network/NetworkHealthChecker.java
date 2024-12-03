/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  lombok.Generated
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.check.network;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.catalogue.PlatformService;
import com.atlassian.migration.agent.service.catalogue.model.MigrationDomainsAllowlistResponse;
import com.atlassian.migration.agent.service.check.network.ConnectivityTester;
import com.atlassian.migration.agent.service.check.network.NetworkCheckResult;
import com.atlassian.migration.agent.service.check.network.NetworkHealthContext;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Generated;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkHealthChecker
implements Checker<NetworkHealthContext> {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(NetworkHealthChecker.class);
    public static final String HEALTH_CHECK_FAILS_KEY = "failedDomains";
    public static final String GET = "GET";
    public static final int TIMEOUT = 3;
    public static final TimeUnit TIMEOUT_TIME_UNIT = TimeUnit.SECONDS;
    public static final String DEFAULT_TYPE = "default";
    public static final String V1 = "v1";
    public static final Set<String> DOMAIN_NAMES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("uploading-attachments", "checking-plugin-version", "atlassian-app-migration-platform", "atlassian-migration-platform", "migration-catalogue-storage", "app-migration-service-ams", "migration-orchestrator-task-data", "app-migration-service-ams")));
    public static final String MIGRATION_CATALOGUE_SERVICE = "migration-catalogue-service";
    private final PlatformService platformService;
    private final ConnectivityTester connectivityTester;
    private final AnalyticsEventService analyticsEventService;
    private final AnalyticsEventBuilder analyticsEventBuilder;
    private final MigrationAgentConfiguration migrationAgentConfiguration;
    private final Map<String, Function<MigrationDomainsAllowlistResponse.Entry, NetworkCheckResult>> isReachablePredicateMap = this.isReachablePredicateMap();

    public NetworkHealthChecker(PlatformService platformService, ConnectivityTester connectivityTester, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, MigrationAgentConfiguration migrationAgentConfiguration) {
        this.platformService = platformService;
        this.connectivityTester = connectivityTester;
        this.analyticsEventService = analyticsEventService;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.migrationAgentConfiguration = migrationAgentConfiguration;
    }

    public CheckResult check(NetworkHealthContext ctx) {
        List<MigrationDomainsAllowlistResponse.Entry> domainDescriptorList;
        Instant startTime = Instant.now();
        try {
            domainDescriptorList = this.getDomainDescriptorListToTest(ctx.cloudId);
        }
        catch (Exception e) {
            log.info("Error happened during getting IP whitelist", (Throwable)e);
            NetworkCheckResult networkCheckResult = NetworkCheckResult.failed(MIGRATION_CATALOGUE_SERVICE, this.migrationAgentConfiguration.getMigrationCatalogueServiceUrl(V1));
            List<NetworkCheckResult> failedNetworkCheckResults = Collections.singletonList(networkCheckResult);
            this.sendAnalyticsEvent(false, failedNetworkCheckResults, startTime);
            return new CheckResult(false, Collections.singletonMap(HEALTH_CHECK_FAILS_KEY, failedNetworkCheckResults));
        }
        List<NetworkCheckResult> failedNetworkCheckResults = domainDescriptorList.stream().filter(Objects::nonNull).filter(entry -> DOMAIN_NAMES.contains(entry.getName())).map(this::isReachable).filter(entry -> !entry.isSuccess()).collect(Collectors.toList());
        if (!failedNetworkCheckResults.isEmpty()) {
            this.sendAnalyticsEvent(false, failedNetworkCheckResults, startTime);
        }
        return new CheckResult(failedNetworkCheckResults.isEmpty(), Collections.singletonMap(HEALTH_CHECK_FAILS_KEY, failedNetworkCheckResults));
    }

    private void sendAnalyticsEvent(boolean success, List<NetworkCheckResult> failedNetworkCheckResults, Instant startTime) {
        this.analyticsEventService.sendAnalyticsEventsAsync(() -> Collections.singletonList(this.analyticsEventBuilder.buildPreMigrationPreflightNetworkHealth(success, failedNetworkCheckResults, ChronoUnit.MILLIS.between(startTime, Instant.now()))));
    }

    @VisibleForTesting
    private NetworkCheckResult isReachable(MigrationDomainsAllowlistResponse.Entry entry) {
        String type = entry.getType();
        if (!this.isReachablePredicateMap.containsKey(type)) {
            return NetworkCheckResult.success(entry.getName());
        }
        return this.isReachablePredicateMap.get(type).apply(entry);
    }

    private List<MigrationDomainsAllowlistResponse.Entry> getDomainDescriptorListToTest(String cloudId) {
        MigrationDomainsAllowlistResponse migrationDomainsAllowlistResponse = this.platformService.getDomainAllowList(cloudId);
        return migrationDomainsAllowlistResponse.getConfigs();
    }

    public static List<NetworkCheckResult> retrieveFailedNetworkHealthUrls(Map<String, Object> details) {
        return details.getOrDefault(HEALTH_CHECK_FAILS_KEY, Collections.emptyList());
    }

    private Map<String, Function<MigrationDomainsAllowlistResponse.Entry, NetworkCheckResult>> isReachablePredicateMap() {
        HashMap<String, Function<MigrationDomainsAllowlistResponse.Entry, NetworkCheckResult>> map = new HashMap<String, Function<MigrationDomainsAllowlistResponse.Entry, NetworkCheckResult>>();
        map.put(DEFAULT_TYPE, entry -> this.checkDomainsByUrlEntry((MigrationDomainsAllowlistResponse.UrlEntry)entry));
        return map;
    }

    @NotNull
    private NetworkCheckResult checkDomainsByUrlEntry(MigrationDomainsAllowlistResponse.UrlEntry entry) {
        List<String> urls = entry.getUrls();
        List<String> failedDomains = urls.stream().filter(url -> !this.connectivityTester.isReachable((String)url, 3, TIMEOUT_TIME_UNIT)).collect(Collectors.toList());
        if (!failedDomains.isEmpty()) {
            return NetworkCheckResult.failed(entry.getName(), failedDomains);
        }
        return NetworkCheckResult.success(entry.getName());
    }
}


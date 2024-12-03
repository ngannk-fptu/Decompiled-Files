/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service.check.app.consent;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.migration.agent.service.app.AppAccessScopeService;
import com.atlassian.migration.agent.service.app.AppAssessmentInfoService;
import com.atlassian.migration.agent.service.app.PluginManager;
import com.atlassian.migration.agent.service.check.app.consent.AppDataMigrationConsentContext;
import com.atlassian.migration.agent.service.check.app.consent.NotConsentedApp;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AppDataMigrationConsentChecker
implements Checker<AppDataMigrationConsentContext> {
    private static final String VIOLATIONS_KEY = "violations";
    private final AppAssessmentInfoService appAssessmentInfoService;
    private final AppAccessScopeService appConsentService;
    private final PluginManager pluginManager;

    public AppDataMigrationConsentChecker(AppAccessScopeService appConsentService, AppAssessmentInfoService appAssessmentInfoService, PluginManager pluginManager) {
        this.appConsentService = appConsentService;
        this.appAssessmentInfoService = appAssessmentInfoService;
        this.pluginManager = pluginManager;
    }

    public CheckResult check(AppDataMigrationConsentContext ctx) {
        if (ctx.appKeys.isEmpty()) {
            return new CheckResult(true);
        }
        List appsNotConsented = ctx.appKeys.stream().filter(this::appNotConsentedOrScopesOutdated).map(this::createAppDataConsent).sorted(Comparator.comparing(notConsentedApp -> notConsentedApp.name)).collect(Collectors.toList());
        return new CheckResult(appsNotConsented.isEmpty(), Collections.singletonMap(VIOLATIONS_KEY, appsNotConsented));
    }

    private NotConsentedApp createAppDataConsent(String appKey) {
        String name = this.pluginManager.getPlugin(appKey).getName();
        return NotConsentedApp.builder().name(name).key(appKey).build();
    }

    private boolean appNotConsentedOrScopesOutdated(String appKey) {
        boolean consented = this.appAssessmentInfoService.isAppConsented(appKey);
        boolean consentedAccessScopesUpdated = this.appConsentService.savedAccessScopesAreCurrent(appKey);
        return !consented || !consentedAccessScopesUpdated;
    }

    static List<NotConsentedApp> retrieveNotConsentedApps(Map<String, Object> details) {
        return details.getOrDefault(VIOLATIONS_KEY, Collections.emptyList());
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.retention.RetentionFeatureChecker
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.retention;

import com.atlassian.confluence.api.service.retention.RetentionFeatureChecker;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.sal.api.features.DarkFeatureManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRetentionFeatureChecker
implements RetentionFeatureChecker {
    private static final Logger logger = LoggerFactory.getLogger(DefaultRetentionFeatureChecker.class);
    private final DarkFeatureManager darkFeatureManager;
    private final LicenseService licenseService;
    public static final String RETENTION_RULES_DARK_FEATURE_KEY = "confluence.retention.rules";
    public static final String RETENTION_RULES_DRY_RUN_DARK_FEATURE_KEY = "confluence.retention.rules.dry.run";

    public DefaultRetentionFeatureChecker(DarkFeatureManager darkFeatureManager, LicenseService licenseService) {
        this.darkFeatureManager = darkFeatureManager;
        this.licenseService = licenseService;
    }

    public boolean isFeatureAvailable() {
        boolean isDarkFeatureEnabled = this.darkFeatureManager.isEnabledForAllUsers(RETENTION_RULES_DARK_FEATURE_KEY).orElse(false);
        boolean hasAccessToDCFeatures = this.licenseService.isLicensedForDataCenterOrExempt();
        logger.debug("[{}] feature enabled : [{}]", (Object)RETENTION_RULES_DARK_FEATURE_KEY, (Object)isDarkFeatureEnabled);
        logger.debug("licence allows DC features : [{}]", (Object)hasAccessToDCFeatures);
        return isDarkFeatureEnabled && hasAccessToDCFeatures;
    }

    public boolean isDryRunModeEnabled() {
        boolean isDeletionEnabled = this.darkFeatureManager.isEnabledForAllUsers(RETENTION_RULES_DRY_RUN_DARK_FEATURE_KEY).orElse(false);
        logger.debug("[{}] feature enabled : [{}]", (Object)RETENTION_RULES_DRY_RUN_DARK_FEATURE_KEY, (Object)isDeletionEnabled);
        return isDeletionEnabled;
    }
}


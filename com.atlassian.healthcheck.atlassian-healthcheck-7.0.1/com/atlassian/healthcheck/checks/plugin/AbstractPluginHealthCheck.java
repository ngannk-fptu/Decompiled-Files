/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.healthcheck.spi.HealthCheckWhitelist
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 */
package com.atlassian.healthcheck.checks.plugin;

import com.atlassian.healthcheck.checks.plugin.ConfigProvider;
import com.atlassian.healthcheck.checks.plugin.OnceOnlyLogger;
import com.atlassian.healthcheck.checks.plugin.PluginHealthCheckMode;
import com.atlassian.healthcheck.checks.plugin.SystemPropertyConfigProviderImpl;
import com.atlassian.healthcheck.core.Application;
import com.atlassian.healthcheck.core.HealthCheck;
import com.atlassian.healthcheck.core.HealthStatus;
import com.atlassian.healthcheck.core.HealthStatusExtended;
import com.atlassian.healthcheck.core.HealthStatusFactory;
import com.atlassian.healthcheck.spi.HealthCheckWhitelist;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

abstract class AbstractPluginHealthCheck
implements HealthCheck {
    private final ConfigProvider configProvider;
    private final HealthStatusFactory healthStatusFactory;
    private final OnceOnlyLogger logger;
    private final HealthCheckWhitelist healthCheckWhitelist;

    protected AbstractPluginHealthCheck(OnceOnlyLogger logger, HealthCheckWhitelist healthCheckWhitelist) {
        this(new SystemPropertyConfigProviderImpl(), logger, healthCheckWhitelist);
    }

    @VisibleForTesting
    AbstractPluginHealthCheck(ConfigProvider configProvider, OnceOnlyLogger logger, HealthCheckWhitelist healthCheckWhitelist) {
        this.configProvider = (ConfigProvider)Preconditions.checkNotNull((Object)configProvider);
        this.logger = (OnceOnlyLogger)Preconditions.checkNotNull((Object)logger);
        this.healthCheckWhitelist = (HealthCheckWhitelist)Preconditions.checkNotNull((Object)healthCheckWhitelist);
        this.healthStatusFactory = new HealthStatusFactory(Application.PlatformHealthCheck, "");
    }

    abstract String getHardFailPropertyName();

    abstract String getForceNoFailPropertyName();

    abstract String getDisableCheckPropertyName();

    abstract List<String> getItemsFailingCheck();

    abstract String getWhitelistKey();

    abstract String getFailureMessagePreamble();

    private Set<String> getWhitelistedItems() {
        return this.healthCheckWhitelist.getWhitelistedItemsForHealthCheck(this.getWhitelistKey());
    }

    private PluginHealthCheckMode getMode() {
        if (this.configProvider.isEnabled(this.getDisableCheckPropertyName())) {
            return PluginHealthCheckMode.DISABLED;
        }
        if (this.configProvider.isEnabled(this.getForceNoFailPropertyName())) {
            return PluginHealthCheckMode.FORCE_NO_FAIL;
        }
        if (this.configProvider.isEnabled(this.getHardFailPropertyName())) {
            return PluginHealthCheckMode.HARD_FAIL;
        }
        return PluginHealthCheckMode.DEFAULT_NO_FAIL;
    }

    @Override
    public final HealthStatus check() {
        boolean hasFailuresThatAreNotWhitelisted;
        String whitelistedString;
        String failOrExcludeString;
        PluginHealthCheckMode mode = this.getMode();
        String modeString = "mode=" + mode.name();
        String restModeString = '(' + modeString + ")";
        if (mode == PluginHealthCheckMode.DISABLED) {
            this.logger.clearLastMessage(this.getClass(), "FAILED");
            this.logger.clearLastMessage(this.getClass(), "WHITELISTED");
            return this.healthStatusFactory.healthyWithWarning(restModeString);
        }
        Set<String> whitelist = this.getWhitelistedItems();
        Map<Boolean, List<String>> failuresPartitionedByWhitelistStatus = this.getItemsFailingCheck().stream().collect(Collectors.partitioningBy(whitelist::contains));
        List<String> failures = failuresPartitionedByWhitelistStatus.get(Boolean.FALSE);
        List<String> whitelistedFailures = failuresPartitionedByWhitelistStatus.get(Boolean.TRUE);
        if (mode == PluginHealthCheckMode.FORCE_NO_FAIL) {
            if (failures.isEmpty()) {
                failOrExcludeString = "PASS";
                this.logger.logWarningIfDifferentFromLastMessage(this.getClass(), "FAILED", this.getClass().getSimpleName() + " " + modeString + " - " + failOrExcludeString);
            } else {
                failOrExcludeString = "FAILED: " + failures;
                this.logger.logWarningIfDifferentFromLastMessage(this.getClass(), "FAILED", this.getClass().getSimpleName() + " " + modeString + " - " + failOrExcludeString);
            }
        } else if (failures.isEmpty()) {
            failOrExcludeString = "PASS";
            this.logger.clearLastMessage(this.getClass(), "FAILED");
        } else {
            failOrExcludeString = "FAILED: " + failures;
            this.logger.logWarningIfDifferentFromLastMessage(this.getClass(), "FAILED", this.getClass().getSimpleName() + " " + modeString + " - " + failOrExcludeString);
        }
        if (whitelistedFailures.isEmpty()) {
            whitelistedString = "";
            this.logger.clearLastMessage(this.getClass(), "WHITELISTED");
        } else {
            whitelistedString = " WHITELISTED: " + whitelistedFailures;
            this.logger.logWarningIfDifferentFromLastMessage(this.getClass(), "WHITELISTED", this.getClass().getSimpleName() + " " + modeString + " -" + whitelistedString);
        }
        if (failures.isEmpty() && whitelistedFailures.isEmpty()) {
            return this.healthStatusFactory.healthyWithWarning(restModeString + " - " + failOrExcludeString, mode == PluginHealthCheckMode.FORCE_NO_FAIL ? HealthStatusExtended.Severity.WARNING : HealthStatusExtended.Severity.UNDEFINED);
        }
        String failureReason = restModeString + " - " + this.getFailureMessagePreamble() + ": " + failOrExcludeString + whitelistedString;
        boolean bl = hasFailuresThatAreNotWhitelisted = !failures.isEmpty();
        if (hasFailuresThatAreNotWhitelisted && mode == PluginHealthCheckMode.HARD_FAIL) {
            return this.healthStatusFactory.failed(failureReason, HealthStatusExtended.Severity.CRITICAL);
        }
        return this.healthStatusFactory.healthyWithWarning(failureReason, HealthStatusExtended.Severity.WARNING);
    }
}


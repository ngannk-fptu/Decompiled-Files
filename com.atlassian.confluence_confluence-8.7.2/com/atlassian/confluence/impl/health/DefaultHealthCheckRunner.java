/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.johnson.JohnsonEventContainer
 *  com.atlassian.johnson.event.Event
 *  com.atlassian.johnson.event.EventLevel
 *  com.atlassian.util.concurrent.Supplier
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.health;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.impl.health.HealthCheckRunner;
import com.atlassian.confluence.impl.upgrade.UpgradeEventRegistry;
import com.atlassian.confluence.internal.health.HealthCheck;
import com.atlassian.confluence.internal.health.HealthCheckExecutor;
import com.atlassian.confluence.internal.health.HealthCheckRegistry;
import com.atlassian.confluence.internal.health.HealthCheckResult;
import com.atlassian.confluence.internal.health.JohnsonEventPredicates;
import com.atlassian.confluence.internal.health.LifecyclePhase;
import com.atlassian.confluence.internal.health.analytics.HealthCheckAnalyticsSender;
import com.atlassian.confluence.setup.johnson.JohnsonUtils;
import com.atlassian.johnson.JohnsonEventContainer;
import com.atlassian.johnson.event.Event;
import com.atlassian.johnson.event.EventLevel;
import com.atlassian.util.concurrent.Supplier;
import com.google.common.annotations.VisibleForTesting;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultHealthCheckRunner
implements HealthCheckRunner {
    @VisibleForTesting
    static final String HIDE_EVENT_DETAILS = "hide.system.error.details";
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHealthCheckRunner.class);
    private final HealthCheckExecutor healthCheckExecutor;
    private final HealthCheckRegistry healthCheckRegistry;
    private final JohnsonEventContainer johnsonEventContainer;
    private final UpgradeEventRegistry upgradeEventRegistry;
    private final ApplicationConfiguration applicationConfiguration;
    private final Supplier<HealthCheckAnalyticsSender> healthCheckAnalyticsSenderSupplier;
    private volatile boolean complete;

    public DefaultHealthCheckRunner(HealthCheckExecutor healthCheckExecutor, HealthCheckRegistry healthCheckRegistry, JohnsonEventContainer johnsonEventContainer, Supplier<HealthCheckAnalyticsSender> healthCheckAnalyticsSenderSupplier, UpgradeEventRegistry upgradeEventRegistry, ApplicationConfiguration applicationConfiguration) {
        this.healthCheckExecutor = Objects.requireNonNull(healthCheckExecutor);
        this.healthCheckRegistry = Objects.requireNonNull(healthCheckRegistry);
        this.johnsonEventContainer = Objects.requireNonNull(johnsonEventContainer);
        this.healthCheckAnalyticsSenderSupplier = Objects.requireNonNull(healthCheckAnalyticsSenderSupplier);
        this.upgradeEventRegistry = Objects.requireNonNull(upgradeEventRegistry);
        this.applicationConfiguration = Objects.requireNonNull(applicationConfiguration);
    }

    @Override
    public boolean isComplete() {
        return this.complete || this.anyJohnsonEventBlocksStartup();
    }

    @Override
    public void runHealthChecks(LifecyclePhase lifecyclePhase) {
        this.executeChecksAndRecordResults(this.healthCheckRegistry.getAll(), lifecyclePhase);
        if (lifecyclePhase.isLast() || this.anyJohnsonEventBlocksStartup()) {
            this.complete = true;
            this.clearDismissibleEventsBasedOnStartupMode();
        } else if (lifecyclePhase == LifecyclePhase.BOOTSTRAP_END && !this.applicationConfiguration.isSetupComplete()) {
            this.clearDismissibleEventsBasedOnStartupMode();
        }
    }

    private void clearDismissibleEventsBasedOnStartupMode() {
        boolean isNotUpgrading;
        boolean detailsShouldBeHidden = StringUtils.isNotBlank((CharSequence)System.getProperty(HIDE_EVENT_DETAILS));
        boolean bl = isNotUpgrading = !this.upgradeEventRegistry.hasUpgradeEventOccurred();
        if (JohnsonUtils.allEventsDismissible() && (detailsShouldBeHidden || isNotUpgrading)) {
            JohnsonUtils.dismissEvents();
        }
    }

    private boolean anyJohnsonEventBlocksStartup() {
        Collection events = this.johnsonEventContainer.getEvents();
        return events.stream().anyMatch(JohnsonEventPredicates.blocksStartup());
    }

    private void executeChecksAndRecordResults(Collection<HealthCheck> healthChecks, @Nullable LifecyclePhase phase) {
        Set<HealthCheckResult> healthCheckResults = this.healthCheckExecutor.performHealthChecks(healthChecks, phase);
        healthCheckResults.stream().filter(check -> this.isNew(check.getEvent())).forEach(this::recordResult);
    }

    private void recordResult(HealthCheckResult result) {
        Event event = result.getEvent();
        this.johnsonEventContainer.addEvent(event);
        this.sendHealthCheckResult(event);
        DefaultHealthCheckRunner.logEvent(event.getLevel(), event.getDesc(), result.getLogMessage());
    }

    private void sendHealthCheckResult(Event event) {
        ((HealthCheckAnalyticsSender)this.healthCheckAnalyticsSenderSupplier.get()).sendHealthCheckResult(event);
    }

    private boolean isNew(Event event) {
        return !this.johnsonEventContainer.getEvents().contains(event);
    }

    private static void logEvent(EventLevel eventLevel, String headline, String logMessage) {
        switch (eventLevel.getLevel()) {
            case "warning": {
                LOGGER.warn(headline);
                LOGGER.warn(logMessage);
                break;
            }
            case "fatal": 
            case "error": {
                LOGGER.error(headline);
                LOGGER.error(logMessage);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown EventLevel: " + eventLevel);
            }
        }
    }
}


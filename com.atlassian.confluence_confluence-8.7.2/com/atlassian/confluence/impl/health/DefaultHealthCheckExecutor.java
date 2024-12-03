/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.johnson.event.Event
 *  com.google.common.annotations.VisibleForTesting
 *  io.atlassian.fugue.Pair
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.health;

import com.atlassian.confluence.internal.health.HealthCheck;
import com.atlassian.confluence.internal.health.HealthCheckExecutor;
import com.atlassian.confluence.internal.health.HealthCheckResult;
import com.atlassian.confluence.internal.health.JohnsonEventPredicates;
import com.atlassian.confluence.internal.health.LifecyclePhase;
import com.atlassian.johnson.event.Event;
import com.google.common.annotations.VisibleForTesting;
import io.atlassian.fugue.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultHealthCheckExecutor
implements HealthCheckExecutor {
    private static final Logger log = LoggerFactory.getLogger(DefaultHealthCheckExecutor.class);
    private final Collection<HealthCheck> blockingChecks = new HashSet<HealthCheck>();

    @Override
    public synchronized Set<HealthCheckResult> performHealthChecks(Collection<HealthCheck> healthChecks, LifecyclePhase lifecyclePhase) {
        return (Set)this.applyAndCollectExceptions(healthChecks, lifecyclePhase).right();
    }

    @VisibleForTesting
    Pair<Collection<Exception>, Set<HealthCheckResult>> applyAndCollectExceptions(Collection<HealthCheck> healthChecks, LifecyclePhase lifecyclePhase) {
        this.blockingChecks.clear();
        LinkedHashSet results = new LinkedHashSet();
        HashSet healthChecksRun = new HashSet();
        ArrayList exceptions = new ArrayList();
        healthChecks.stream().filter(check -> check.isApplicableFor(lifecyclePhase)).forEach(check -> this.runCheck((HealthCheck)check, lifecyclePhase, results, healthChecksRun, exceptions));
        return Pair.pair(exceptions, results);
    }

    private boolean hasFailedPrerequisites(HealthCheck healthCheck) {
        return healthCheck.getPrerequisites().stream().anyMatch(this.blockingChecks::contains);
    }

    private void runCheck(HealthCheck healthCheck, LifecyclePhase lifecyclePhase, Set<HealthCheckResult> results, Set<HealthCheck> healthChecksRun, Collection<Exception> exceptions) {
        if (this.hasFailedPrerequisites(healthCheck)) {
            healthChecksRun.add(healthCheck);
            this.blockingChecks.add(healthCheck);
        } else {
            try {
                this.checkPrerequisitesAllRanInThisPhase(healthCheck, healthChecksRun, lifecyclePhase);
                healthCheck.perform(lifecyclePhase).forEach(result -> this.recordResult((HealthCheckResult)result, results));
                healthChecksRun.add(healthCheck);
            }
            catch (RuntimeException e) {
                log.error("Failure running HealthCheck " + healthCheck.getId(), (Throwable)e);
                exceptions.add(e);
            }
        }
    }

    private void checkPrerequisitesAllRanInThisPhase(HealthCheck healthCheck, Set<HealthCheck> healthChecksRun, LifecyclePhase lifecyclePhase) {
        Set<HealthCheck> missingPrerequisites = healthCheck.getPrerequisites().stream().filter(c -> !healthChecksRun.contains(c)).collect(Collectors.toSet());
        if (!missingPrerequisites.isEmpty()) {
            throw new PrerequisiteNotInPhaseException(healthCheck, lifecyclePhase, missingPrerequisites);
        }
    }

    private void recordResult(HealthCheckResult result, Set<HealthCheckResult> results) {
        Event johnsonEvent = result.getEvent();
        johnsonEvent.addAttribute((Object)"causeKey", (Object)result.getCause());
        johnsonEvent.addAttribute((Object)"idKey", (Object)result.getHealthCheck().getId());
        johnsonEvent.addAttribute((Object)"eventKey", (Object)UUID.randomUUID().toString());
        result.getKbUrl().ifPresent(url -> johnsonEvent.addAttribute((Object)"helpUrl", url));
        DefaultHealthCheckExecutor.markForRenderingInBetterJohnsonPage(johnsonEvent);
        results.add(result);
        if (DefaultHealthCheckExecutor.isBlocking(johnsonEvent)) {
            this.blockingChecks.add(result.getHealthCheck());
        }
    }

    private static boolean isBlocking(Event event) {
        return JohnsonEventPredicates.blocksStartup().test(event);
    }

    private static void markForRenderingInBetterJohnsonPage(Event event) {
        event.addAttribute((Object)"uiVersion", (Object)"CONFSRVDEV-2798");
    }

    @VisibleForTesting
    protected static class PrerequisiteNotInPhaseException
    extends RuntimeException {
        private static final String MESSAGE_FORMAT = "The following prerequisites for the HealthCheck '%s' were not run in the %s phase: %s";
        private final HealthCheck healthCheck;

        private static Object getIds(Collection<HealthCheck> healthChecks) {
            return healthChecks.stream().map(HealthCheck::getId).collect(Collectors.toSet());
        }

        PrerequisiteNotInPhaseException(HealthCheck healthCheck, LifecyclePhase lifecyclePhase, Collection<HealthCheck> missingPrerequisites) {
            this(String.format(MESSAGE_FORMAT, new Object[]{healthCheck.getId(), lifecyclePhase, PrerequisiteNotInPhaseException.getIds(missingPrerequisites)}), healthCheck);
        }

        PrerequisiteNotInPhaseException(String message, HealthCheck healthCheck) {
            super(message);
            this.healthCheck = Objects.requireNonNull(healthCheck);
        }

        public HealthCheck getHealthCheck() {
            return this.healthCheck;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            PrerequisiteNotInPhaseException that = (PrerequisiteNotInPhaseException)o;
            return this.healthCheck.equals(that.healthCheck) && this.getMessage().equals(that.getMessage());
        }

        public int hashCode() {
            return this.healthCheck.hashCode() * 42 + this.getMessage().hashCode();
        }
    }
}


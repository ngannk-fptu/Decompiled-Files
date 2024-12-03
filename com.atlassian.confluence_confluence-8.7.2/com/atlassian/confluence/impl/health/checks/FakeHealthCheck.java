/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.johnson.event.Event
 *  com.atlassian.johnson.event.EventType
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.health.checks;

import com.atlassian.confluence.impl.health.AbstractHealthCheck;
import com.atlassian.confluence.impl.health.checks.FakeHealthCheckEvent;
import com.atlassian.confluence.internal.health.HealthCheckResult;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.confluence.internal.health.LifecyclePhase;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.johnson.event.Event;
import com.atlassian.johnson.event.EventType;
import com.google.common.annotations.VisibleForTesting;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

public class FakeHealthCheck
extends AbstractHealthCheck {
    static final String SIMULATE_FAILURE_FOR_INTEGRATION_TESTS_SYSPROP = "confluence.johnson.simulate.failure";
    private final Map<LifecyclePhase, List<FakeHealthCheckEvent>> phases = FakeHealthCheck.extractPhases(System.getProperty("confluence.johnson.simulate.failure"));
    private static int errorIndex;

    public FakeHealthCheck() {
        super(Collections.emptyList());
    }

    @VisibleForTesting
    static Map<LifecyclePhase, List<FakeHealthCheckEvent>> extractPhases(String systemProperty) {
        if (StringUtils.isBlank((CharSequence)systemProperty)) {
            return Collections.emptyMap();
        }
        try {
            return Arrays.stream(systemProperty.split("\\|")).map(phase -> Arrays.asList(phase.split(":"))).collect(Collectors.toMap(phaseAndError -> LifecyclePhase.valueOf((String)phaseAndError.get(0)), phaseAndError -> Arrays.stream(((String)phaseAndError.get(1)).split(",")).map(FakeHealthCheckEvent::createPhase).collect(Collectors.toList())));
        }
        catch (Exception e) {
            throw new RuntimeException("Incorrect format for phase to error map, string should look like \"BOOTSTRAP_END:warning-dismissible,error|SETUP:warning,error-dismissible\"", e);
        }
    }

    @Override
    public @NonNull List<HealthCheckResult> perform(LifecyclePhase lifecyclePhase) {
        EventType eventType = this.getAppropriateErrorTypeForPhase(lifecyclePhase);
        return this.phases.getOrDefault((Object)lifecyclePhase, Collections.emptyList()).stream().map(fakeHealthCheckEvent -> this.createHealthCheckResultForEvent((FakeHealthCheckEvent)fakeHealthCheckEvent, eventType)).collect(Collectors.toList());
    }

    @Override
    public boolean isApplicableFor(LifecyclePhase phase) {
        return this.phases.containsKey((Object)phase);
    }

    private HealthCheckResult createHealthCheckResultForEvent(FakeHealthCheckEvent fakeHealthCheckEvent, EventType eventType) {
        Event event = new Event(eventType, "description" + errorIndex, "exception" + errorIndex, fakeHealthCheckEvent.getLevel());
        if (fakeHealthCheckEvent.isDismissible()) {
            event.addAttribute((Object)"dismissible", (Object)true);
        }
        HealthCheckResult result = HealthCheckResult.fail(this, event, UrlBuilder.createURL("http://kburl"), "cause", "logmessage").get(0);
        ++errorIndex;
        return result;
    }

    public EventType getAppropriateErrorTypeForPhase(LifecyclePhase phase) {
        return phase == LifecyclePhase.SETUP ? JohnsonEventType.STARTUP.eventType() : (phase == LifecyclePhase.BOOTSTRAP_END ? JohnsonEventType.BOOTSTRAP.eventType() : JohnsonEventType.DATABASE.eventType());
    }
}


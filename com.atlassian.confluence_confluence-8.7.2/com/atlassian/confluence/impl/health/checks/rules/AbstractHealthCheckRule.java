/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.johnson.event.Event
 */
package com.atlassian.confluence.impl.health.checks.rules;

import com.atlassian.confluence.impl.health.ErrorMessageProvider;
import com.atlassian.confluence.impl.health.checks.rules.HealthCheckRule;
import com.atlassian.confluence.internal.health.HealthCheck;
import com.atlassian.confluence.internal.health.HealthCheckResult;
import com.atlassian.confluence.internal.health.JohnsonEventLevel;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.johnson.event.Event;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractHealthCheckRule
implements HealthCheckRule {
    private final ErrorMessageProvider errorMessageProvider;
    private final URL kbUrl;
    private final String failureCause;
    private final JohnsonEventType johnsonEventType;

    protected AbstractHealthCheckRule(ErrorMessageProvider errorMessageProvider, URL kbUrl, String failureCause, JohnsonEventType johnsonEventType) {
        this.errorMessageProvider = Objects.requireNonNull(errorMessageProvider);
        this.kbUrl = Objects.requireNonNull(kbUrl);
        this.failureCause = Objects.requireNonNull(failureCause);
        this.johnsonEventType = Objects.requireNonNull(johnsonEventType);
    }

    @Override
    public List<HealthCheckResult> validate(HealthCheck parent) {
        Objects.requireNonNull(parent);
        return this.doValidation().map(error -> this.getResults((String)error, parent)).orElse(Collections.emptyList());
    }

    protected abstract Optional<String> doValidation();

    protected String getErrorMessage(String key, Object ... args) {
        return this.errorMessageProvider.getErrorMessage(key, args);
    }

    protected Event getFailureEvent(String errorMessage) {
        Event event = new Event(this.johnsonEventType.eventType(), errorMessage, JohnsonEventLevel.WARNING.level());
        event.addAttribute((Object)"dismissible", (Object)true);
        return event;
    }

    private List<HealthCheckResult> getResults(String errorMessage, HealthCheck parent) {
        return HealthCheckResult.fail(parent, this.getFailureEvent(errorMessage), this.kbUrl, this.failureCause, errorMessage);
    }
}


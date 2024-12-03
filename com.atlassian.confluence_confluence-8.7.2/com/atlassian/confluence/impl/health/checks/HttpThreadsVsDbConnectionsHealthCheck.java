/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.health.checks;

import com.atlassian.confluence.impl.health.HealthCheckTemplate;
import com.atlassian.confluence.impl.health.checks.rules.HealthCheckRule;
import com.atlassian.confluence.internal.health.HealthCheckResult;
import com.atlassian.confluence.internal.health.LifecyclePhase;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class HttpThreadsVsDbConnectionsHealthCheck
extends HealthCheckTemplate {
    private final List<HealthCheckRule> rules;

    public HttpThreadsVsDbConnectionsHealthCheck(List<HealthCheckRule> rules) {
        super(Collections.emptyList());
        this.rules = Objects.requireNonNull(rules);
    }

    @Override
    protected Set<LifecyclePhase> getApplicablePhases() {
        return Collections.singleton(LifecyclePhase.PLUGIN_FRAMEWORK_STARTED);
    }

    @Override
    protected List<HealthCheckResult> doPerform() {
        return this.rules.stream().flatMap(rule -> rule.validate(this).stream()).collect(Collectors.toList());
    }
}


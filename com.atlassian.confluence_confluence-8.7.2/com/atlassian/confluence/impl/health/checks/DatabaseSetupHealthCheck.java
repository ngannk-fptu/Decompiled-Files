/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.health.checks;

import com.atlassian.confluence.impl.health.HealthCheckTemplate;
import com.atlassian.confluence.impl.health.checks.rules.HealthCheckRule;
import com.atlassian.confluence.internal.health.HealthCheckResult;
import com.atlassian.confluence.internal.health.LifecyclePhase;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DatabaseSetupHealthCheck
extends HealthCheckTemplate {
    private final Collection<HealthCheckRule> databaseSetupRules;

    protected DatabaseSetupHealthCheck(HealthCheckRule ... databaseSetupRules) {
        super(Collections.emptyList());
        this.databaseSetupRules = Arrays.asList(databaseSetupRules);
    }

    @Override
    protected Set<LifecyclePhase> getApplicablePhases() {
        return Collections.singleton(LifecyclePhase.BOOTSTRAP_END);
    }

    @Override
    protected List<HealthCheckResult> doPerform() {
        return this.databaseSetupRules.stream().flatMap(rule -> rule.validate(this).stream()).collect(Collectors.toList());
    }
}


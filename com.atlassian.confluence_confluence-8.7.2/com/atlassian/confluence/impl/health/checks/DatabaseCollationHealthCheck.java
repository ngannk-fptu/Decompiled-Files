/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 */
package com.atlassian.confluence.impl.health.checks;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.confluence.impl.health.HealthCheckTemplate;
import com.atlassian.confluence.impl.health.checks.rules.HealthCheckRule;
import com.atlassian.confluence.internal.health.HealthCheckResult;
import com.atlassian.confluence.internal.health.LifecyclePhase;
import com.atlassian.confluence.util.db.DatabaseConfigHelper;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public class DatabaseCollationHealthCheck
extends HealthCheckTemplate {
    private final Map<String, HealthCheckRule> databaseRules;
    private final DatabaseConfigHelper databaseConfigHelper;

    protected DatabaseCollationHealthCheck(Map<String, HealthCheckRule> databaseRules, DatabaseConfigHelper databaseConfigHelper) {
        super(Collections.emptyList());
        this.databaseRules = Objects.requireNonNull(databaseRules);
        this.databaseConfigHelper = Objects.requireNonNull(databaseConfigHelper);
    }

    @Override
    protected Set<LifecyclePhase> getApplicablePhases() {
        return Collections.singleton(LifecyclePhase.BOOTSTRAP_END);
    }

    @Override
    protected List<HealthCheckResult> doPerform() {
        Optional<String> maybeDatabaseType = this.databaseConfigHelper.getProductName();
        return this.databaseRules.entrySet().stream().filter(entry -> ((String)entry.getKey()).equalsIgnoreCase(maybeDatabaseType.orElse(""))).flatMap(entry -> ((HealthCheckRule)entry.getValue()).validate(this).stream()).collect(Collectors.toList());
    }
}


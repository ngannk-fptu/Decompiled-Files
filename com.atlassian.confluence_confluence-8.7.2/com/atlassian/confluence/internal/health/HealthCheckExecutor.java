/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.health;

import com.atlassian.confluence.internal.health.HealthCheck;
import com.atlassian.confluence.internal.health.HealthCheckResult;
import com.atlassian.confluence.internal.health.LifecyclePhase;
import java.util.Collection;
import java.util.Set;

public interface HealthCheckExecutor {
    public Set<HealthCheckResult> performHealthChecks(Collection<HealthCheck> var1, LifecyclePhase var2);
}


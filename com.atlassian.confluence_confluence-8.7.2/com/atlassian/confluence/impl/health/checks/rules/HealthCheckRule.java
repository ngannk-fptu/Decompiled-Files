/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.health.checks.rules;

import com.atlassian.confluence.internal.health.HealthCheck;
import com.atlassian.confluence.internal.health.HealthCheckResult;
import java.util.List;

public interface HealthCheckRule {
    public List<HealthCheckResult> validate(HealthCheck var1);
}


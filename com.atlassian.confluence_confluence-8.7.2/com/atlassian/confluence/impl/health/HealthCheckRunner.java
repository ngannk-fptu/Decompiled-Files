/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.health;

import com.atlassian.confluence.internal.health.LifecyclePhase;

public interface HealthCheckRunner {
    public boolean isComplete();

    public void runHealthChecks(LifecyclePhase var1);
}

